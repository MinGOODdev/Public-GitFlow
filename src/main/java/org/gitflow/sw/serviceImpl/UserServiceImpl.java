package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.mapper.UserMapper;
import org.gitflow.sw.model.Newbie;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.util.GitHubApiUtil;
import org.gitflow.sw.util.security.SHA256Encryption;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * For MySQL
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * GitUser 검색
     *
     * @param id
     * @return
     */
    @Override
    public GitUser findById(int id) {
        return userMapper.findById(id);
    }

    /**
     * GitUser 검색
     *
     * @param login
     * @return
     */
    public GitUser findByUserName(String login) {
        return userMapper.findByUserName(login);
    }

    /**
     * GitUser 전체 조회 - 총 코드 라인 수 오름차순
     * (데이터가 없는 유저부터 스케줄러가 갱신하는게 나을 것 같아서 만듬)
     *
     * @return
     */
    @Override
    public List<GitUser> findAllOrderByTotalUserCodeLineAsc() {
        return userMapper.findAllOrderByTotalUserCodeLineAsc();
    }

    /**
     * GitUser 전체 조회 - 총 코드 라인 수 내림차순
     *
     * @return
     */
    @Override
    public List<GitUser> findAllOrderByTotalUserCodeLineDesc() {
        return userMapper.findAllOrderByTotalUserCodeLineDesc();
    }

    /**
     * 소속 별 GitUser 전체 조회 - 총 코드 라인 수 내림차순
     *
     * @param departmentId
     * @return
     */
    @Override
    public List<GitUser> findAllByDepartmentIdOrderByTotalUserCodeLineDesc(int departmentId) {
        if (departmentId == 1) {
            return this.findAllOrderByTotalUserCodeLineDesc();
        } else {
            return userMapper.findAllByDepartmentIdOrderByTotalUserCodeLineDesc(departmentId);
        }
    }

    /**
     * MySQL: 신규 사용자 저장
     *
     * @param newbie
     */
    @Override
    public void userInsert(Newbie newbie) {
        GitUser gitUser = new GitUser();
        String userName = newbie.getUserName().toLowerCase();
        gitUser.setLogin(userName);
        gitUser.setDepartmentId(newbie.getDepartmentId());
//    gitUser.setPassword(sha256Encryption.encode(newbie.getPassword1()));
        gitUser.setPassword(SHA256Encryption.encrypt(newbie.getPassword1()));
        gitUser.setProfileUrl(GitHubApiUtil.gitHubMap.get(userName).getAvatarUrl());
        userMapper.userInsert(gitUser);
    }

    /**
     * MySQL: gituser 수정
     *
     * @param gitUser
     */
    @Override
    public void userUpdate(GitUser gitUser) {
        userMapper.userUpdate(gitUser);
    }

    /**
     * 로그인 여부 판단
     *
     * @return
     */
    public Boolean checkExistPrincipalUserName() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        String userName = principal.getName();
        if ("anonymousUser".equals(userName)) {
            return false;
        }
        return true;
    }

    /**
     * MySQL: GitUser 존재 여부 체크
     *
     * @param login
     * @param password
     * @return
     */
    @Override
    public GitUser checkExist(String login, String password) {
        GitUser gitUser = userMapper.findByUserName(login);
        if (ObjectUtils.isEmpty(gitUser)) {     // 신규 or 사용자가 ID를 잘못 입력한 경우
            return null;
        }

//    String encryptPassword = sha256Encryption.encode(password);   // PW 암호화
        String encryptPassword = SHA256Encryption.encrypt(password);

//    if (!sha256Encryption.matches(password, encryptPassword)) return null;
        if (gitUser.getPassword().equals(encryptPassword) == false) {
            return null;
        }
        return gitUser;
    }

    /**
     * 현재 사용자의 랭킹이 0보다 큰지 판별 (All, 본인 소속에서만 보여주기 위함)
     *
     * @param totalCodeLineRank
     * @return
     */
    @Override
    public boolean checkUserRankValue(int totalCodeLineRank) {
        boolean flag = false;
        if (totalCodeLineRank > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 현재 로그인 사용자의 UserName 꺼내기
     *
     * @return
     */
    @Override
    public String getPrincipalUserName() {
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        String userName = principal.getName();
        return userName;
    }

    /**
     * 현재 로그인 사용자의 랭킹 - TotalCodeLineCount
     *
     * @param userName
     * @param gitUsers
     * @return
     */
    @Override
    public int getUserRankByTotalCodeLine(String userName, List<GitUser> gitUsers) {
        List<String> userNameList = new ArrayList<>();
        for (GitUser user : gitUsers) {
            userNameList.add(user.getLogin());
        }
        int totalCodeLineRank = userNameList.indexOf(userName) + 1;
        return totalCodeLineRank;
    }

    /**
     * 사용자의 권한 검사
     *
     * @param userName
     * @return 2: 관리자 true & 1: 일반 false
     */
    @Override
    public boolean userAuthCheck(String userName) {
        GitUser gitUser = userMapper.findByUserName(userName);
        if (ObjectUtils.isEmpty(gitUser)) {
            return false;
        } else if ("2".equals(gitUser.getAuthorization())) {
            return true;
        }
        return false;
    }

    @Override
    public void userDelete(String login) {
        userMapper.userDelete(login);
    }
}
