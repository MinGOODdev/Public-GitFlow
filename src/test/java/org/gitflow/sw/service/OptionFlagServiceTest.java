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
public class OptionFlagServiceTest {

    @Autowired
    private OptionFlagService optionFlagService;

    @Test
    public void findById() {
        log.info("** {}", optionFlagService.findById(1));
    }

    @Test
    public void update_TEST() {
        optionFlagService.update();
    }

    @Test
    public void optionFlagUpdateTest() {
        optionFlagService.optionFlagUpdate();
        log.info("** {}", optionFlagService.findById(1));
    }

    @Test
    public void tempUpdateTest() {
        optionFlagService.tempEvenUpdate(0);
        optionFlagService.tempEvenUpdate(4900);
        optionFlagService.tempOddUpdate(0);
        optionFlagService.tempOddUpdate(4000);
    }

}
