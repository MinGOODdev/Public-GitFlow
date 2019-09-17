package org.gitflow.sw.service;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserServiceTest {

    @Resource
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    public void 회원_목록_조회() throws Exception {
        List<GitUser> list = userMapper.findAllOrderByTotalUserCodeLineDesc();
        for (GitUser user : list) {
            log.info("* {}", user.getLogin());
        }
    }

    @Test
    public void findAllOrderByAllCommitCountDesc() throws Exception {
        List<GitUser> list = userMapper.findAllOrderByTotalUserCodeLineDesc();
        log.info("* {}", list);
    }

    @Test
    public void 소속별_사용자_목록_조회() throws Exception {
        List<GitUser> list = userMapper.findAllByDepartmentIdOrderByTotalUserCodeLineDesc(2);
        log.info("* {}", list);
    }

    @Test
    public void findById() throws Exception {
        log.info("* {}", userMapper.findById(7));
    }

    @Test
    public void userUpdateTest() {
        GitUser gitUser = userMapper.findById(10);
        gitUser.setTotalUserCodeLine(1);
        userMapper.userUpdate(gitUser);
    }

    @Test
    public void userAuthCheckTest() {
        log.info("** {}", userService.userAuthCheck("mingooddev"));
        log.info("** {}", userService.userAuthCheck("bghgu"));
        Assert.assertTrue(userService.userAuthCheck("mingooddev"));
        Assert.assertFalse(userService.userAuthCheck("bghgu"));
    }

}
