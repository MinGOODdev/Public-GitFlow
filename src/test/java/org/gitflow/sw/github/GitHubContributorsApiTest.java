package org.gitflow.sw.github;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GitHubContributorsApiTest {

    @Value("${git.account.username}")
    private String gitUserName;
    @Value("${git.account.password}")
    private String gitPassword;

    @Test
    public void contributor수_구하기() throws IOException {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        GHRepository ghRepo = gitHub.getRepository("bghgu/algossstudy");
        List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
        for (GHRepository.Contributor contributor : contributorList) {
            log.info("### {}", contributor.getLogin());
        }

//    int contributorCount = ghRepo.listContributors().asList().size();
//    for (GHCommit commit : ghRepo.listCommits().asList()) {
//      log.info("### {}", commit.getCommitter().getLogin());
//    }
//    log.info("*** {}", contributorCount);
    }

    @Test
    public void contributors에서_특정_사용자의_특정_레포_커밋수_조회() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        GHRepository ghRepo = gitHub.getRepository("wkdtndgns/SM_Mentoring_Server");
        List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
        int sum = 0;

        for (GHCommit commit : ghRepo.listCommits().asList()) {
            if (commit.getCommitter().getLogin().toLowerCase().equals("tails5555")) {
                for (GHCommit.File file : commit.getFiles()) {
                    String str = file.getFileName();
//          if (str.startsWith(".idea") || str.contains(".gitignore")) {
//
//          } else {
                    int addLine = file.getLinesAdded();
                    int subLine = file.getLinesDeleted();
                    int cal = addLine - subLine;
                    log.info("**** {} / {}", file.getFileName(), cal);
                    sum += cal;
//          }
                }
            }
        }
        log.info("**** sum 15516 : {} / {}", sum, gitHub.getRateLimit());
//    log.info("**4661 sum : {}", sum);

//    if (ghRepo.getSize() != 0) {
//      for (GHRepository.Contributor contributor : contributorList) {
//        String contributorName = contributor.getLogin().toLowerCase();
//        if (contributorName.equals("mingooddev")) {
//          log.info("* {} / {}", contributorName, contributor.getContributions());
//        }
//      }
//    }
    }

}
