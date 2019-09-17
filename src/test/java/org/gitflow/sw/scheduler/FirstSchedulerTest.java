package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.EmailSender;
import org.gitflow.sw.util.reader.FileReader;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class FirstSchedulerTest {

    @Value("${git.account.username}")
    private String gitUserName;
    @Value("${git.account.password}")
    private String gitPassword;

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

    @Test
    public void first_Scheduler_TEST() throws Exception {
        Map<String, List<Repo>> mailMap = new HashMap<>();

        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            for (GitUser user : userList) {
                // START Repo Table 갱신, 사용자 주 언어 검색 부분
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GHUser ghUser = gitHub.getUser(userName);

                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                List<Repo> changedRepoList = new ArrayList<>();

                for (String repoName : repoNameList) {
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
                    schedulerAssistant.gitCloneCommand(repo, userName);
                    int repoCodeLine = fileReader.countTotalLine("dump/" + userName + repo.getRepoName(), includes, excludes, ignorePaths, mustContains);
                    repo.setTotalCodeLine(repoCodeLine);
                    repoService.updateRepo(repo);
                }
                user.setUserLanguage(primaryLanguage);
                userService.userUpdate(user);
                // END gituser userLanguage 갱신, repo table totalCodeLine 갱신

                mailMap.put(userName, changedRepoList);
            }
            emailSender.firstSchedulerSuccess(mailMap);

            schedulerAssistant.dumpRemoveCommand();

        } catch (IOException e) {
            log.error("### repoAndGitUserDataUpdate 예외 발생!!!");
        }
    }
}
