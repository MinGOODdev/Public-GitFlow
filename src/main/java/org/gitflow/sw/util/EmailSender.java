package org.gitflow.sw.util;

import org.gitflow.sw.dto.LibExpect;
import org.gitflow.sw.dto.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Component
public class EmailSender {

    private static final String MAIL = "mingood92@gmail.com";

    @Autowired
    private JavaMailSender javaMailSender;

    public void firstSchedulerSuccess(Map<String, List<Repo>> mailMap) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(MAIL);
        mailMessage.setSubject("첫번째 스케줄러 동작 - SUCCESS");
        mailMessage.setText(this.makeSendMessage(mailMap));
        javaMailSender.send(mailMessage);
    }

    public void secondOddSchedulerSuccess(Map<String, List<Repo>> mailMap) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(MAIL);
        mailMessage.setSubject("두번째 Odd 스케줄러 동작 - SUCCESS");
        mailMessage.setText(this.makeSendMessage(mailMap));
        javaMailSender.send(mailMessage);
    }

    public void secondEvenSchedulerSuccess(Map<String, List<Repo>> mailMap) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(MAIL);
        mailMessage.setSubject("두번째 Even 스케줄러 동작 - SUCCESS");
        mailMessage.setText(this.makeSendMessage(mailMap));
        javaMailSender.send(mailMessage);
    }

    public void sendLibraryExpectation(List<LibExpect> libExpects) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(MAIL);
        mailMessage.setSubject("라이브러리 의심 파일 목록");
        mailMessage.setText(this.makeLibExpectMsg(libExpects));
        javaMailSender.send(mailMessage);
    }

    public void sendStringMsg(String msg) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(MAIL);
        mailMessage.setSubject("에러 발생 알림");
        mailMessage.setText(msg);
        javaMailSender.send(mailMessage);
    }

    /**
     * 지원 완료 시 지원 완료 확인 이메일 전송
     *
     * @param userName
     * @param email
     */
    public void applyConfirmEmail(String userName, String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        String htmlMsg = "<img src=\"https://t1.daumcdn.net/cfile/tistory/99085E355BE81E732D\">";

        mimeMessage.setContent(htmlMsg, "text/html; charset=UTF-8");
        helper.setTo(email);
        helper.setSubject("[안내]" + userName + "님 GitFlow 지원서 접수 완료");
        helper.setFrom(MAIL);
        javaMailSender.send(mimeMessage);
    }

    /**
     * 전송할 메시지 만들기
     *
     * @param mailMap
     * @return
     */
    private String makeSendMessage(Map<String, List<Repo>> mailMap) {
        StringBuilder sb = new StringBuilder();

        for (String userName : mailMap.keySet()) {
            List<Repo> repoList = mailMap.get(userName);

            if (!repoList.isEmpty()) {
                sb.append(userName.toUpperCase() + "\n");

                for (Repo repo : repoList) {
                    sb.append(repo.getId() + " ")
                            .append(repo.getRepoName() + " ")
                            .append(repo.getRepoUrl() + "\n");
                }
                sb.append("-------------------------\n");
            }
        }
        return sb.toString();
    }

    /**
     * 라이브러리 의심 파일 목록 메시지 만들기
     *
     * @param libExpects
     * @return
     */
    private String makeLibExpectMsg(List<LibExpect> libExpects) {
        StringBuilder sb = new StringBuilder();

        for (LibExpect libExpect : libExpects) {
            sb.append(libExpect.getFileName() + " ")
                    .append(libExpect.getCodeLine() + "\n");
        }
        return sb.toString();
    }

}
