package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.LibExpectService;
import org.gitflow.sw.service.RepoService;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
@Slf4j
public class DataCheckScheduler {

    @Value("${scheduler.git.username1}")
    private String gitUserName;
    @Value("${scheduler.git.password1}")
    private String gitPassword;

    @Autowired
    private UserService userService;
    @Autowired
    private RepoService repoService;
    @Autowired
    private SchedulerAssistant schedulerAssistant;
    @Autowired
    private LibExpectService libExpectService;

    /**
     * DB의 데이터를 갱신하기 전, Repository 이름을 변경했거나, 삭제하여 존재하지 않을 경우를 대비
     * 데이터를 갱신하기 전, 미리 없는 DB 데이터를 삭제
     */
    @Scheduled(cron = "0 0 03 * * ?")
    private void notExistRepoDataRemove() {
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
            // 라이브러리 의심 파일 저장 Table 비우기
            libExpectService.deleteAll();

            // 다음 스케줄러의 정확한 갱신을 위해 dump 폴더 삭제
            schedulerAssistant.dumpRemoveCommand();

        } catch (IOException e) {
            log.error("### notExistRepoDataRemove 예외 발생!!!");
        }
    }

}
