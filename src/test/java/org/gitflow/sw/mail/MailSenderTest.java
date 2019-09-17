package org.gitflow.sw.mail;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.LibExpect;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.LibExpectService;
import org.gitflow.sw.service.RepoService;
import org.gitflow.sw.util.EmailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MailSenderTest {

    @Autowired
    private EmailSender emailSender;
    @Autowired
    private RepoService repoService;
    @Autowired
    private LibExpectService libExpectService;

    @Test
    public void 메일_보내기_TEST() {
        Map<String, List<Repo>> map = new HashMap<>();
        List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(1);
        map.put("mingooddev", repoList);
        emailSender.firstSchedulerSuccess(map);
        emailSender.secondOddSchedulerSuccess(map);
        emailSender.secondEvenSchedulerSuccess(map);
    }

    @Test
    public void 라이브러리_의심_파일_목록_메일_전송_TEST() {
        List<LibExpect> libExpects = libExpectService.findAll();
        emailSender.sendLibraryExpectation(libExpects);
    }

    @Test
    public void 날짜_시간_출력_TEST() {
        String localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("***" + localTime);
    }

    @Test
    public void 지원완료_메일_전송_TEST() throws MessagingException {
        emailSender.applyConfirmEmail("조민국", "jo1010m@naver.com");
    }

}
