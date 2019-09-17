package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.RepoService;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NotExistDataRemoveTest {

    @Value("${git.account.username}")
    private String gitUserName;
    @Value("${git.account.password}")
    private String gitPassword;

    @Autowired
    private UserService userService;
    @Autowired
    private RepoService repoService;
    @Autowired
    private SchedulerAssistant schedulerAssistant;

    @Test
    public void TEST() {
        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
            List<GitUser> userList = userService.findAllOrderByTotalUserCodeLineDesc();

            for (GitUser user : userList) {
                String userName = user.getLogin();
                int userId = userService.findByUserName(userName).getId();
                GHUser ghUser = gitHub.getUser(userName);

                List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(userId);
                List<String> repoNameList = schedulerAssistant.makeRepoNameList(userName, ghUser);
                Map<String, Boolean> compareMap = schedulerAssistant.makeCompareMap(repoList);
                schedulerAssistant.updateCompareMapByRepoExist(compareMap, repoNameList);

                for (String key : compareMap.keySet()) {
                    if (compareMap.get(key) == false) {
                        log.info("*** 존재하지 않는 Repo Data 삭제 시도");
                        repoService.deleteRepo(key, userId);
                        log.info("*** 존재하지 않는 Repo Data 삭제 성공");
                    }
                }
            }
        } catch (IOException e) {
            log.error("### notExistRepoDataRemove 예외 발생!!!");
        }
    }

}
