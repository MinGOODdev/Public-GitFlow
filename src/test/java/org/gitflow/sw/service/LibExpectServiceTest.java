package org.gitflow.sw.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LibExpectServiceTest {

    @Autowired
    private LibExpectService libExpectService;

    @Test
    public void deleteAll_TEST() {
        libExpectService.deleteAll();
    }

}
