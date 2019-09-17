package org.gitflow.sw.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.EmailSender;
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

import java.util.List;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SimpleSecondSchedulerTest {

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
    private SchedulerAssistant schedulerAssistant;
    @Autowired
    private OptionFlagService optionFlagService;
    @Autowired
    private FileReaderService fileReaderService;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private EmailSender emailSender;

    @Test
    public void test() throws Exception {
        GitHub gitHub = GitHub.connectUsingPassword(gitUserName, gitPassword);
        List<Include> includes = includeService.findAll();
        List<Exclude> excludes = excludeService.findAll();
        List<IgnorePath> ignorePaths = ignorePathService.findAll();
        List<MustContain> mustContains = mustContainService.findAll();

        String userName = "mingooddev";
        String repoName = "springboot-batch-demo";

        GHRepository ghRepo = gitHub.getRepository(userName + "/" + repoName);
        int contributorCount = ghRepo.listContributors().asList().size();

        if (contributorCount != 0 && contributorCount != 1) {
            List<GHCommit> commitList = ghRepo.listCommits().asList();

            for (GHCommit commit : commitList) {
                GHUser ghUser1 = commit.getCommitter();
                log.info("*** {} / {}", commit.getCommitter().getLogin(), commit.getAuthor().getLogin());

                if (ghUser1 != null && ghUser1.getLogin().toLowerCase().equals(userName)) {
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

                        }
                    }
                }
            }
        }

    }

}
