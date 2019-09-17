package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.EmailSender;
import org.gitflow.sw.util.reader.FileReader;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Component
@EnableScheduling
@Slf4j
public class FirstDataUpdateScheduler {

    private static String errorInfo = null;

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
    private FileReader fileReader;
    @Autowired
    private SchedulerAssistant schedulerAssistant;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private EmailSender emailSender;

    /**
     * Repo Table (allCommitCount, contributors, totalCodeLine)
     * GitUser Table (userLanguage)
     */
    @Scheduled(cron = "0 0 04 * * ?")
    private void oddRepoAndGitUserDataUpdate() {
        Map<String, List<Repo>> mailMap = new HashMap<>();

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
//      List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineAsc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            List<GitUser> oddUserList = new ArrayList<>();
            for (GitUser user : userList) {
                if (user.getId() % 2 == 1) {
                    oddUserList.add(user);
                }
            }

            for (GitUser user : oddUserList) {
                // START Repo Table 갱신, 사용자 주 언어 검색 부분
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GHUser ghUser = gitHub.getUser(userName);

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                for (String repoName : repoNameList) {
                    errorInfo = userName + repoName + "Calling API";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
                    Repo repo = repoService.findByRepoNameAndUserId(repoName, userId);

                    // repo table allCommitCount, contributors를 갱신
                    schedulerService.insertOrUpdateRepoData(repo, userId, userName, ghRepo, changedRepoList);
                }
                String primaryLanguage = schedulerAssistant.getUserPrimaryLanguage(userId);
                // END Repo Table 갱신, 사용자 주 언어 검색 부분

                // START gituser userLanguage 갱신, repo table totalCodeLine 갱신
                // 변동이 있는 Repo만 Clone
                for (Repo repo : changedRepoList) {
                    errorInfo = userName + repo.getRepoName() + "fileReader";

                    schedulerAssistant.gitCloneCommand(repo, userName);
                    int repoCodeLine = fileReader.countTotalLine("dump/" + userName + repo.getRepoName(), includes, excludes, ignorePaths, mustContains);
                    repo.setTotalCodeLine(repoCodeLine);
                    repoService.updateRepo(repo);
                }
                user.setUserLanguage(primaryLanguage);
                userService.userUpdate(user);
                // END gituser userLanguage 갱신, repo table totalCodeLine 갱신

                // 메일에 넣어 보낼 내용을 위한 map
                mailMap.put(userName, changedRepoList);
            }
            // 메일 전송
            emailSender.firstSchedulerSuccess(mailMap);

            // 업데이트 목록 공지사항 작성
//      autoNoticeWriter.updatingListWriter(mailMap);

            // dump 폴더 삭제
            schedulerAssistant.dumpRemoveCommand();

        } catch (IOException e) {
            emailSender.sendStringMsg(errorInfo + "\n" + e.getMessage());
            log.error("### oddRepoAndGitUserDataUpdate 예외 발생!!!");
        }
    }

    @Scheduled(cron = "0 0 04 * * ?")
    private void evenRepoAndGitUserDataUpdate() {
        Map<String, List<Repo>> mailMap = new HashMap<>();

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName1, gitPassword1);
//      List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineAsc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            List<GitUser> evenUserList = new ArrayList<>();
            for (GitUser user : userList) {
                if (user.getId() % 2 == 0) {
                    evenUserList.add(user);
                }
            }

            for (GitUser user : evenUserList) {
                // START Repo Table 갱신, 사용자 주 언어 검색 부분
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GHUser ghUser = gitHub.getUser(userName);

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                for (String repoName : repoNameList) {
                    errorInfo = userName + repoName + "Calling API";

                    GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
                    Repo repo = repoService.findByRepoNameAndUserId(repoName, userId);

                    // repo table allCommitCount, contributors를 갱신
                    schedulerService.insertOrUpdateRepoData(repo, userId, userName, ghRepo, changedRepoList);
                }
                String primaryLanguage = schedulerAssistant.getUserPrimaryLanguage(userId);
                // END Repo Table 갱신, 사용자 주 언어 검색 부분

                // START gituser userLanguage 갱신, repo table totalCodeLine 갱신
                // 변동이 있는 Repo만 Clone
                for (Repo repo : changedRepoList) {
                    errorInfo = userName + repo.getRepoName() + "fileReader";

                    schedulerAssistant.gitCloneCommand(repo, userName);
                    int repoCodeLine = fileReader.countTotalLine("dump/" + userName + repo.getRepoName(), includes, excludes, ignorePaths, mustContains);
                    repo.setTotalCodeLine(repoCodeLine);
                    repoService.updateRepo(repo);
                }
                user.setUserLanguage(primaryLanguage);
                userService.userUpdate(user);
                // END gituser userLanguage 갱신, repo table totalCodeLine 갱신

                // 메일에 넣어 보낼 내용을 위한 map
                mailMap.put(userName, changedRepoList);
            }
            // 메일 전송
            emailSender.firstSchedulerSuccess(mailMap);

            // 업데이트 목록 공지사항 작성
//      autoNoticeWriter.updatingListWriter(mailMap);

            // dump 폴더 삭제
            schedulerAssistant.dumpRemoveCommand();

        } catch (IOException e) {
            emailSender.sendStringMsg(errorInfo + "\n" + e.getMessage());
            log.error("### evenRepoAndGitUserDataUpdate 예외 발생!!!");
        }
    }

}
