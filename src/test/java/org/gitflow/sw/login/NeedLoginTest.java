package org.gitflow.sw.login;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NeedLoginTest {

    @Test
    public void getGHUser_Test() throws Exception {
        GitHub gitHub = GitHub.connectAnonymously();
        GHUser ghUser1 = gitHub.getUser("mingooddev");
        GHUser ghUser2 = gitHub.getUser("MinGOODdev");

        log.info("* {}", ghUser1);
        log.info("* {}", ghUser2);
        Assert.assertNotNull(ghUser1);
        Assert.assertNotNull(ghUser2);
    }

}
