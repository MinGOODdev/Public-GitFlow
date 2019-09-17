package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EvenSchedulerTest {

    private static final int LIBRARY_JUDGE_CODE_LINE = 200;
    private static final int PER_COMMIT_LIBRARY_JUDGE_CODE_LINE = 100;

    @Value("${scheduler.git.username2}")
    private String gitUserName2;
    @Value("${scheduler.git.password2}")
    private String gitPassword2;

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
    private OptionFlagService optionFlagService;
    @Autowired
    private FileReaderService fileReaderService;
    @Autowired
    private LibExpectService libExpectService;

    @Test
    public void 짝수_TEST() throws Exception {
        optionFlagService.tempEvenUpdate(0);

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName2, gitPassword2);
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            // Library 의심 목록을 추출하기 위해 필요한 Map
            Map<String, Integer> fileCommitCountMap = new HashMap<>();
            Map<String, Integer> fileCodeLineMap = new HashMap<>();
            Map<String, Integer> libraryExpectationMap = new HashMap<>();

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

                log.info("*** {}", userName);

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                // Contribute한 RepoList 순회
                for (String repoName : repoNameList) {
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
                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
                    int contributorCount = ghRepo.listContributors().asList().size();
                    int repoUserCodeLine = 0;

                    log.info("***** {}", repoName);

                    // Contributor가 2명 이상이라면 해당 User의 작업량을 산출하기 위해 API 활용
                    if (contributorCount != 0 && contributorCount != 1) {
                        List<GHCommit> commitList = ghRepo.listCommits().asList();

                        for (GHCommit commit : commitList) {
                            GHUser ghUser1 = commit.getCommitter();
                            if (ghUser1 != null && ghUser1.getLogin().toLowerCase().equals(userName)) {
                                for (GHCommit.File file : commit.getFiles()) {
                                    int fileCodeLine = 0;
                                    String fileName = file.getFileName().toLowerCase();
                                    boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                                    boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                                    boolean ignorePathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);
                                    boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                                    String commitMsg = commit.getCommitShortInfo().getMessage();
                                    boolean commitMsgFlag = !commitMsg.startsWith("Merge branch")
                                            && !commitMsg.startsWith("Merge pull")
                                            && !commitMsg.startsWith("Merge remote")
                                            && !commitMsg.startsWith("merge");
                                    if (ignorePathFlag && commitMsgFlag) {
                                        if (includeFlag && excludeFlag || mustContainFlag) {
                                            int changedLine = file.getLinesAdded() - file.getLinesDeleted();
                                            repoUserCodeLine += changedLine;
                                            fileCodeLine += changedLine;

                      /*
                      라이브러리 의심 파일을 추출하기 위한 사전 작업
                       */

                                            String mixKeyName = userName + repoName + fileName;

                                            // file의 commit 횟수 계산
                                            if (fileCommitCountMap.get(mixKeyName) == null) {
                                                fileCommitCountMap.put(mixKeyName, 1);
                                            } else {
                                                fileCommitCountMap.put(mixKeyName, fileCommitCountMap.get(mixKeyName) + 1);
                                            }

                                            // file의 코드 라인 수 계산
                                            if (fileCodeLineMap.get(mixKeyName) == null) {
                                                fileCodeLineMap.put(mixKeyName, fileCodeLine);
                                            } else {
                                                fileCodeLineMap.put(mixKeyName, fileCodeLineMap.get(mixKeyName) + fileCodeLine);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 라이브러리 의심 파일 추리기
                        for (String key : fileCommitCountMap.keySet()) {
                            int fileCommitCount = fileCommitCountMap.get(key);
                            int fileCodeLine = fileCodeLineMap.get(key);
                            int perCommitCodeLine = fileCodeLine / fileCommitCount;

                            if (fileCommitCount == 1 && fileCodeLine >= LIBRARY_JUDGE_CODE_LINE) {
                                libraryExpectationMap.put(key, fileCodeLine);
                            } else if (fileCommitCount > 1 && perCommitCodeLine >= PER_COMMIT_LIBRARY_JUDGE_CODE_LINE) {
                                libraryExpectationMap.put(key, fileCodeLine);
                            }
                        }

                        // library 의심 파일 갱신
                        libExpectService.deleteAll();
                        for (String key : libraryExpectationMap.keySet()) {
                            log.info("### library 의심 목록: {} / {}", key, libraryExpectationMap.get(key));
                            libExpectService.insert(key, libraryExpectationMap.get(key));
                        }

                    } else {
                        // Contributor가 0 또는 1이라면 굳이 API를 활용할 필요 없음
                        repoUserCodeLine = repo.getTotalCodeLine();
                    }
                    repo.setUserCodeLine(repoUserCodeLine);
                    repoService.updateRepo(repo);
                }

                // GitUser Table - totalUserCodeLine 갱신
                List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(userId);
                int totalUserCodeLine = schedulerAssistant.getTotalUserCodeLine(repoList);
                gitUser.setTotalUserCommit(totalUserCommit);
                gitUser.setTotalUserCodeLine(totalUserCodeLine);
                userService.userUpdate(gitUser);
            }
            int rateLimit = gitHub.getRateLimit().remaining;
            optionFlagService.tempEvenUpdate(rateLimit);

        } catch (IOException e) {
            log.error("### repoEvenUserCodeLineUpdate 예외 발생!!!");
        }
    }

}
