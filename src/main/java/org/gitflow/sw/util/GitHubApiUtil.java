package org.gitflow.sw.util;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GitHubApiUtil {

    private GitHub gitHub;

    @Value("${scheduler.git.username1}")
    private String gitUserName;
    @Value("${scheduler.git.password1}")
    private String gitPassword;

    /**
     * [ githubMap ]
     * GHUser 객체를 계속 사용하기 위한 방법.
     * <p>
     * - GitHub 객체로는 'github'에 연결만 합니다.
     * -- 사용자의 repo 정보, avatarUrl 등의 정보 접근에는 GHUser 객체가 필요합니다.
     * <p>
     * - 회원가입 시 입력받은 'userName'으로 'GHUser' 객체를 생성하고, 그 객체를 계속 사용합니다.
     * - Map으로 만든 이유는, 입력받은 'userName' 사용자와 'GHUser'가 상응하는지 확인하기 위함입니다.
     * <p>
     * - 이 방법은 재배포할 때마다 객체가 사라져도 무방한 경우에 사용합니다.
     * -- 이 프로젝트의 경우, 회원가입 시에만 객체가 필요하고, 이후에는 필요가 없습니다. 유지할 필요도 없습니다.
     */
    public static Map<String, GHUser> gitHubMap = new HashMap<>();

    public GitHubApiUtil() {
        try {
            this.gitHub = GitHub.connectAnonymously();
        } catch (IOException e) {
            log.info("* {}", e);
        }
    }

    private GHUser getGHUser(GitHub gitHub, String userName) {
        try {
            return gitHub.getUser(userName);
        } catch (IOException e) {
            log.info("* {}", e);
            return null;
        }
    }

    /**
     * 회원가입 시 'GitHub' 사용자의 'userName'으로 회원가입을 받기 위해
     * github api를 통해 'GitHub'의 'userName'이 맞는지 검증합니다.
     * <p>
     * - 회원가입 시에는 검증하는 단계이기 때문에 'GHUser' 객체를 생성하지 않습니다.
     *
     * @param userName
     * @return
     */
    public boolean isGitHubUserName(String userName) {
        try {
//      GitHub.connectAnonymously().getUser(userName);
            GitHub.connectUsingPassword(gitUserName, gitPassword).getUser(userName);
            return true;
        } catch (IOException e) {
            log.info("* {}", e);
            return false;
        }
    }

    public Map<String, GHUser> setGitHubMap(String userName) {
        if (!gitHubMap.containsKey(userName)) {
            gitHubMap.put(userName, getGHUser(this.gitHub, userName));
        }
        return gitHubMap;
    }

    public void refreshGitHub() {
        this.gitHub.refreshCache();
    }

}
