package org.gitflow.sw.service;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.mapper.RepoMapper;
import org.gitflow.sw.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SuppressWarnings("Duplicates")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RepoServiceTest {

    @Value("${git.account.username}")
    private String gitUserName;
    @Value("${git.account.password}")
    private String gitPassword;

    @Resource
    private RepoMapper repoMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    @Test
    public void findByRepoNameAndUserId_Test() throws Exception {
        log.info("* {}", repoMapper.findByRepoNameAndUserId("Algorithm", 7));
    }

    @Test
    public void updateRepo_Test() throws Exception {
        Repo repo = repoMapper.findByRepoNameAndUserId("Algorithm", 7);
        repo.setRepoLanguage("jominkuk");
        repo.setAllCommitCount(777);
        repoMapper.updateRepo(repo);
    }

}
