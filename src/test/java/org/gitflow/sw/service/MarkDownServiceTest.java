package org.gitflow.sw.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MarkDownServiceTest {

    @Test
    public void markdownTest() {
        try {
            GitHub gitHub = GitHub.connectAnonymously();

            log.info(gitHub.getRateLimit().toString());

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }
}
