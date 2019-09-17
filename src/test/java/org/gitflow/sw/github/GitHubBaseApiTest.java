package org.gitflow.sw.github;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GitHubBaseApiTest {

    @Value("${git.account.username}")
    private String gitUserName;
    @Value("${git.account.password}")
    private String gitPassword;

    @Test
    public void api_호출_횟수() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        log.info("* {}", gitHub.getRateLimit());
        System.out.println(gitHub.getRateLimit().remaining);
    }

    @Test
    public void gitHub_유저_존재여부_체크() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        try {
//      String userName = gitHub.getUser(gitUserName).getLogin();
            String userName = gitHub.getUser("jominmingood").getLogin();
            log.info("* {}", userName);
        } catch (FileNotFoundException e) {
            log.info("해당 GitHub User가 존재하지 않습니다.");
        }
    }

    @Test
    public void anonymous_Test() throws Exception {
        GHUser ghUser = GitHub.connectAnonymously().getUser("parkyounghwan");
        log.info("* {}", ghUser.getLogin());
    }

    @Test
    public void get_Avatar() throws Exception {
        GHUser ghUser = GitHub.connectAnonymously().getUser("meeansub");
        log.info(ghUser.getAvatarUrl());
    }

    @Test
    public void GitHub객체_생성_확인() throws Exception {
        try {
            GitHub gitHub = GitHub.connectUsingPassword(gitUserName, "aaa");
            Assert.assertNotNull(gitHub);
            log.info("* {}", gitHub.getMyself());
        } catch (IOException e) {
            log.info("null");
        }
    }

    @Test
    public void 퍼블릭_레포지토리_개수_조회() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        int publicRepoCount = gitHub.getUser("parkyounghwan").getPublicRepoCount();
        Assert.assertNotEquals(publicRepoCount, 0);
        log.info("* {}", publicRepoCount);
    }

    @Test
    public void 나의_레포지토리_리스트_조회() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        String userName = gitHub.getMyself().getLogin();
        Assert.assertEquals(userName, "MinGOODdev");

        Map<String, GHRepository> repoMap = gitHub.getMyself().getAllRepositories();
        for (String repoName : repoMap.keySet()) {
            GHRepository ghRepo = repoMap.get(repoName);
            if (ghRepo.getFullName().contains(userName) && ghRepo.isPrivate() == false) {
                log.info("* {} / {}", ghRepo.getName(), ghRepo.listCommits().asList().size());
            }
        }
    }

    @Test
    public void 다른_사용자_레포지토리_리스트_조회() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        Map<String, GHRepository> ghRepoMap = gitHub.getUser("tails5555").getRepositories();
        log.info("* {}", gitHub.getUser("tails5555").getPublicRepoCount());

        Set<String> keys = ghRepoMap.keySet();
        for (String repoName : keys) {
            GHRepository ghRepo = ghRepoMap.get(repoName);
            List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
            String repoFullName = ghRepo.getFullName();
            for (GHRepository.Contributor contributor : contributorList) {
                if (contributor.getLogin().toLowerCase().equals("tails5555")) {
                    log.info("* {}", repoFullName);
                }
            }
        }
    }

    @Test
    public void test() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        GHRepository ghRepo = gitHub.getRepository("mingooddev/hotcomments");
        log.info("**{}", ghRepo);
    }

}
