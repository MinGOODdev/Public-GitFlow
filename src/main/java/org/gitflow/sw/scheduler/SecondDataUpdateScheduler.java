package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.EmailSender;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Component
@EnableScheduling
@Slf4j
public class SecondDataUpdateScheduler {

    private static String errorInfoOdd = null;
    private static String errorInfoEven = null;

    @Value("${scheduler.git.username1}")
    private String gitUserName;
    @Value("${scheduler.git.password1}")
    private String gitPassword;
    @Value("${scheduler.git.username2}")
    private String gitUserName1;
    @Value("${scheduler.git.password2}")
    private String gitPassword1;

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
    private SchedulerAssistant schedulerAssistant;
    @Autowired
    private FileReaderService fileReaderService;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private LibExpectService libExpectService;
    @Autowired
    private EmailSender emailSender;

    /**
     * Repo Table (userCommitCount, userCodeLine) - 홀수 번째 유저 스케줄링
     * GitUser Table (totalUserCommit, totalUserCodeLine)
     */
    @Scheduled(cron = "0 0 06 * * ?")
    private void repoOddUserCodeLineUpdate() {
        Map<String, List<Repo>> mailMap = new HashMap<>();

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            // Library 의심 목록을 추출하기 위해 필요한 Map
            Map<String, Integer> fileCommitCountMap = new HashMap<>();
            Map<String, Integer> fileCodeLineMap = new HashMap<>();

            List<GitUser> oddUserList = new ArrayList<>();
            for (GitUser user : userList) {
                if (user.getId() % 2 == 1) {
                    oddUserList.add(user);
                }
            }

            for (GitUser user : oddUserList) {
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GitUser gitUser = userService.findById(userId);
                GHUser ghUser = gitHub.getUser(userName);
                int totalUserCommit = 0;

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                // Contribute한 RepoList 순회
                for (String repoName : repoNameList) {
                    errorInfoOdd = userName + repoName + "Calling API 1";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
                    Repo repo = repoService.findByRepoNameAndUserId(repoName, userId);

                    // DB와 GitHub userCommit 값 비교
                    int dbUserCommit = repo.getUserCommitCount();
                    int githubUserCommit = schedulerAssistant.sumUserCommitCount(userName, ghRepo);

                    // 갖지 않으면 변동사항이 있는 RepoList에 추가
                    if (dbUserCommit != githubUserCommit) {
                        changedRepoList.add(repo);
                    }

                    // 변동사항 RepoList에 추가한 후, userCommitCount 갱신
                    totalUserCommit += githubUserCommit;
                    repo.setUserCommitCount(githubUserCommit);
                    repoService.updateRepo(repo);
                }

                // 변동 사항이 있는 RepoList 순회 - (userCodeLine 갱신)
                for (Repo repo : changedRepoList) {
                    String repoName = repo.getRepoName();
                    errorInfoOdd = userName + repoName + "Calling API 2";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repo.getRepoName());
                    int contributorCount = ghRepo.listContributors().asList().size();
                    int repoUserCodeLine = 0;

                    // Contributor가 2명 이상이라면 해당 User의 작업량을 산출하기 위해 API 활용
                    if (contributorCount != 0 && contributorCount != 1) {
                        List<GHCommit> commitList = ghRepo.listCommits().asList();

                        for (GHCommit commit : commitList) {
                            GHUser ghUser1 = commit.getCommitter();

                            if (!ObjectUtils.isEmpty(ghUser1) && ghUser1.getLogin().toLowerCase().equals(userName)) {
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

                                        // 라이브러리 의심 파일을 추출하기 위해 2가지 맵 데이터 채우는 작업
                                        String mixKeyName = userName + repoName + fileName;

                                        // file의 commit 횟수 계산
                                        schedulerAssistant.fileCommitCountCalculator(mixKeyName, fileCommitCountMap);

                                        // file의 코드 라인 수 계산
                                        schedulerAssistant.fileCodeLineCountCalculator(mixKeyName, changedLine, fileCodeLineMap);
                                    }
                                }
                            }
                        }

                        // 라이브러리 의심 파일 추리기
                        Map<String, Integer> libraryExpectationMap = schedulerAssistant.extractLibraryExpectation(fileCommitCountMap, fileCodeLineMap);

                        // library 의심 파일 갱신
                        schedulerService.insertLibraryExpectation(libraryExpectationMap);

                    } else {
                        // Contributor가 0 또는 1이라면 굳이 API를 활용할 필요 없음
                        repoUserCodeLine = repo.getTotalCodeLine();
                    }
                    repo.setUserCodeLine(repoUserCodeLine);
                    repoService.updateRepo(repo);
                }
                // 메일에 넣어 보낼 내용을 위한 map
                mailMap.put(userName, changedRepoList);

                // GitUser Table - totalUserCommit, totalUserCodeLine 갱신
                schedulerService.updateGitUserTotalUserCommitAndCodeLine(gitUser, totalUserCommit);
            }
            // 메일 전송
            emailSender.secondOddSchedulerSuccess(mailMap);

            // 라이브러리 의심 파일 목록 메일 전송
            emailSender.sendLibraryExpectation(libExpectService.findAll());

        } catch (IOException e) {
            emailSender.sendStringMsg(errorInfoOdd + "\n" + e.getMessage());
            log.error("### repoOddUserCodeLineUpdate 예외 발생!!!");
        }
    }

    /************************************************************************************************************/

    /**
     * Repo Table 갱신 (userCodeLine) - 짝수 번째 유저 스케줄링
     * GitUser Table 갱신 (totalUserCommit, totalUserCodeLine)
     */
    @Scheduled(cron = "0 0 06 * * ?")
    private void repoEvenUserCodeLineUpdate() {
        Map<String, List<Repo>> mailMap = new HashMap<>();

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName1, gitPassword1);
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            // Library 의심 목록을 추출하기 위해 필요한 Map
            Map<String, Integer> fileCommitCountMap = new HashMap<>();
            Map<String, Integer> fileCodeLineMap = new HashMap<>();

            List<GitUser> evenUserList = new ArrayList<>();
            for (GitUser user : userList) {
                if (user.getId() % 2 == 0) {
                    evenUserList.add(user);
                }
            }

            for (GitUser user : evenUserList) {
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GitUser gitUser = userService.findById(userId);
                GHUser ghUser = gitHub.getUser(userName);
                int totalUserCommit = 0;

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                // Contribute한 RepoList 순회
                for (String repoName : repoNameList) {
                    errorInfoEven = userName + repoName + "Calling API 1";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
                    Repo repo = repoService.findByRepoNameAndUserId(repoName, userId);

                    // DB와 GitHub userCommit 값 비교
                    int dbUserCommit = repo.getUserCommitCount();
                    int githubUserCommit = schedulerAssistant.sumUserCommitCount(userName, ghRepo);

                    // 갖지 않으면 변동사항이 있는 RepoList에 추가
                    if (dbUserCommit != githubUserCommit) {
                        changedRepoList.add(repo);
                    }

                    // 변동사항 RepoList에 추가한 후, userCommitCount 갱신
                    totalUserCommit += githubUserCommit;
                    repo.setUserCommitCount(githubUserCommit);
                    repoService.updateRepo(repo);
                }

                // 변동 사항이 있는 RepoList 순회 - (userCodeLine 갱신)
                for (Repo repo : changedRepoList) {
                    String repoName = repo.getRepoName();
                    errorInfoEven = userName + repoName + "Calling API 2";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repo.getRepoName());
                    int contributorCount = ghRepo.listContributors().asList().size();
                    int repoUserCodeLine = 0;

                    // Contributor가 2명 이상이라면 해당 User의 작업량을 산출하기 위해 API 활용
                    if (contributorCount != 0 && contributorCount != 1) {
                        List<GHCommit> commitList = ghRepo.listCommits().asList();

                        for (GHCommit commit : commitList) {
                            GHUser ghUser1 = commit.getCommitter();

                            if (!ObjectUtils.isEmpty(ghUser1) && ghUser1.getLogin().toLowerCase().equals(userName)) {
                                for (GHCommit.File file : commit.getFiles()) {
                                    String fileName = file.getFileName().toLowerCase();

                                    boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                                    boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                                    boolean ignorePathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);
                                    boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                                    // Commit Msg에 "Merge" 키워드가 포함되어 있는지 검사
                                    String commitMsg = commit.getCommitShortInfo().getMessage();
                                    boolean commitMsgFlag = schedulerAssistant.getCommitMsgFlag(commitMsg);

                                    // 최종적으로 파일을 읽은 것인지 판단하는 Boolean Flag
                                    boolean finalFlag = schedulerAssistant.getFinalFlag(ignorePathFlag, commitMsgFlag, includeFlag, excludeFlag, mustContainFlag);

                                    if (finalFlag) {
                                        int changedLine = file.getLinesAdded() - file.getLinesDeleted();
                                        repoUserCodeLine += changedLine;

                                        // 라이브러리 의심 파일을 추출하기 위해 2가지 맵 데이터 채우는 작업
                                        String mixKeyName = userName + repoName + fileName;

                                        // file의 commit 횟수 계산
                                        schedulerAssistant.fileCommitCountCalculator(mixKeyName, fileCommitCountMap);

                                        // file의 코드 라인 수 계산
                                        schedulerAssistant.fileCodeLineCountCalculator(mixKeyName, changedLine, fileCodeLineMap);
                                    }
                                }
                            }
                        }

                        // 라이브러리 의심 파일 추리기
                        Map<String, Integer> libraryExpectationMap = schedulerAssistant.extractLibraryExpectation(fileCommitCountMap, fileCodeLineMap);

                        // library 의심 파일 갱신
                        schedulerService.insertLibraryExpectation(libraryExpectationMap);

                    } else {
                        // Contributor가 0 또는 1이라면 굳이 API를 활용할 필요 없음
                        repoUserCodeLine = repo.getTotalCodeLine();
                    }
                    repo.setUserCodeLine(repoUserCodeLine);
                    repoService.updateRepo(repo);
                }
                // 메일에 넣어 보낼 내용을 위한 map
                mailMap.put(userName, changedRepoList);

                // GitUser Table - totalUserCommit, totalUserCodeLine 갱신
                schedulerService.updateGitUserTotalUserCommitAndCodeLine(gitUser, totalUserCommit);
            }
            // 메일 전송
            emailSender.secondEvenSchedulerSuccess(mailMap);

            // 라이브러리 의심 파일 목록 메일 전송
            emailSender.sendLibraryExpectation(libExpectService.findAll());

        } catch (IOException e) {
            emailSender.sendStringMsg(errorInfoEven + "\n" + e.getMessage());
            log.error("### repoEvenUserCodeLineUpdate 예외 발생!!!");
        }
    }

}
