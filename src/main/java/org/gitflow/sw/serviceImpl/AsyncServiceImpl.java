package org.gitflow.sw.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.thread.NewbieRepoInfoCallableThread;
import org.gitflow.sw.util.reader.FileReader;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@SuppressWarnings("Duplicates")
@Service
@Slf4j
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private UserService userService;

    @Autowired
    private RepoService repoService;

    @Autowired
    private IncludeService includeService;

    @Autowired
    private ExcludeService excludeService;

    @Autowired
    private IgnorePathService ignorePathService;

    @Autowired
    private MustContainService mustContainService;

    @Autowired
    private FileReader fileReader;

    @Autowired
    private SchedulerAssistant schedulerAssistant;

    @Autowired
    private FileReaderService fileReaderService;

    @Autowired
    private SchedulerService schedulerService;

    /**
     * 비동기처리 - 회원가입 시 (새로운 유저) 사용자 Repository 정보 DB에 저장.
     * <p>
     * - Repo Table, User Table (TotalUserCode, TotalUserCommitCount) set
     *
     * @param username
     */
    @Override
    public boolean insertNewbieInfo(String username, String password) {

        boolean isSuccessed = false;  //비동기 INSERT 성패여부.

        try {
            GitHub gitHub = GitHub.connectUsingPassword(username, password);

            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            // Library 의심 목록을 추출하기 위해 필요한 Map
            Map<String, Integer> fileCommitCountMap = new HashMap<>();
            Map<String, Integer> fileCodeLineMap = new HashMap<>();

            GHUser ghUser = gitHub.getUser(username);

            int userId = userService.findByUserName(username).getId();  // Repo - 1) Id

            int totalUserCommitCount = 0;
            int totalUserCodeLine = 0;

            // 사용자의 Repo 전체를 돌면서 필요한 info를 뽑아낸다.
            List<String> repoNameList = schedulerAssistant.makeRepoNameList(username, ghUser);

            for (String repoName : repoNameList) {  // Repo - 2) Name

                GHRepository ghRepo = gitHub.getRepository(username + "/" + repoName);
                int gitCommits = ghRepo.listCommits().asList().size();  // Repo - 3) AllCommitCount(user + contributor)

                // Repo - 4) Language
                String Language = (ghRepo.getLanguage() == null) ? "Markdown" : ghRepo.getLanguage();

                Repo repo = new Repo();

                repo.setUserId(userId);
                repo.setRepoName(ghRepo.getName());
                repo.setRepoLanguage(Language);
                repo.setRepoUrl(ghRepo.getHttpTransportUrl());  // Repo - 5) URL
                repo.setAllCommitCount(gitCommits);
                repo.setContributors(schedulerAssistant.repoContributorCount(ghRepo));  // Repo - 6) Contributors

                int userCommitCount = schedulerAssistant.sumUserCommitCount(username, ghRepo);  // Repo - 7) UserCommitCount
                totalUserCommitCount += userCommitCount;  // User - 1) TotalUserCommit
                repo.setUserCommitCount(userCommitCount);

                //total code line
                schedulerAssistant.gitCloneCommand(repo, username);
                int repoCodeLine = fileReader.countTotalLine("dump/" + username + repo.getRepoName(),
                        includes, excludes, ignorePaths, mustContains);

                repo.setTotalCodeLine(repoCodeLine);  // Repo - 8) TotalCodeLine

                //user code line (if 'contributor == 0' -> user code line = total code line)
                int contributorCount = ghRepo.listContributors().asList().size();
                int repoUserCodeLine = 0;

                // Contributor가 2명 이상이라면 해당 User의 작업량을 산출하기 위해 API 활용
                if (contributorCount != 0 && contributorCount != 1) {
                    List<GHCommit> commitList = ghRepo.listCommits().asList();

                    for (GHCommit commit : commitList) {
                        GHUser ghUser1 = commit.getCommitter();

                        if (!ObjectUtils.isEmpty(ghUser1) && ghUser1.getLogin().toLowerCase().equals(username)) {
                            for (GHCommit.File file : commit.getFiles()) {

                                String fileName = file.getFileName().toLowerCase();

                                boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                                boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                                boolean ignorePathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);
                                boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                                // Commit Msg에 "Merge" 키워드가 포함되어 있는지 검사
                                String commitMsg = commit.getCommitShortInfo().getMessage();
                                boolean commitMsgFlag = schedulerAssistant.getCommitMsgFlag(commitMsg);

                                // 최종적으로 파일을 읽을 것인지 판단하는 Boolean Flag
                                boolean finalFlag = schedulerAssistant.getFinalFlag(ignorePathFlag, commitMsgFlag, includeFlag, excludeFlag, mustContainFlag);

                                if (finalFlag) {
                                    int changedLine = file.getLinesAdded() - file.getLinesDeleted();
                                    repoUserCodeLine += changedLine;

                                    // 라이브러리 의심 파일을 추출하기 위해 2가지 맵 데이터를 채우는 작업
                                    String mixKey = username + repoName + fileName;
                                    // file의 commit 횟수 계산
                                    schedulerAssistant.fileCommitCountCalculator(mixKey, fileCommitCountMap);

                                    // file의 코드 라인 수 계산
                                    schedulerAssistant.fileCodeLineCountCalculator(mixKey, changedLine, fileCodeLineMap);
                                }
                            }
                        }
                    }

                    // 라이브러리 의심 파일 추리기
                    Map<String, Integer> libraryExpectationMap = schedulerAssistant.extractLibraryExpectation(fileCommitCountMap, fileCodeLineMap);

                    // 라이브러리 의심 파일 저장
                    schedulerService.insertLibraryExpectation(libraryExpectationMap);

                } else {
                    // Contributor가 0 또는 1이라면 굳이 API를 활용할 필요 없음
                    repoUserCodeLine = repo.getTotalCodeLine();
                }

                repo.setUserCodeLine(repoUserCodeLine); // Repo - 9) UserCodeLine
                totalUserCodeLine += repoUserCodeLine;   // User - 2) UserCodeline

                repoService.insertRepo(repo);
            }

            // User 테이블.
            GitUser gitUser = new GitUser();
            gitUser.setId(userId);
            gitUser.setUserLanguage(schedulerAssistant.getUserPrimaryLanguage(userId)); // User - 3) Language
            gitUser.setTotalUserCodeLine(totalUserCodeLine);
            gitUser.setTotalUserCommit(totalUserCommitCount);

            userService.userUpdate(gitUser);
            isSuccessed = true;

        } catch (IOException e) {
            log.error("### AsyncServiceImpl 예외 발생 !!!");
        }

        return isSuccessed;
    }

    /**
     * Thread 실행을 따로 두었는데, 결과값을 리턴 받아서 사용자에게 알림을 띄우기 위한 조치.
     *
     * @param thread
     * @return
     */
    @Override
    public void runThread(NewbieRepoInfoCallableThread thread) {

        ExecutorService executor = thread.executorService;  //스레드 동작 시켜주는 클래스.

        Future<String> resultThread = executor.submit(thread);  //Callable 스레드의 결과가 Future 객체로 나옴.

        /**
         * 과정이 비동기이기 때문에 결과도 얻으려면 기다려야 한다.
         * 결과 받는 것도 스레드를 돌려서 비동기 처리.
         * 스레드는 만든 ThreadPool에서 가져다가 쓴다.
         */
        thread.executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = resultThread.get();

                    if (resultThread.isCancelled()) log.info(result + " 작업 취소");
                    else log.info(result + " 작업 완료");

                    log.info("[" + Thread.currentThread().getName() + "]" + "비동기 처리 된 user : " + result);

                } catch (Exception e) {
                    log.error(e.getCause().getMessage());
                }
            }
        });

    }
}
