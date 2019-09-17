package org.gitflow.sw.service;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.Newbie;

import java.util.List;

public interface UserService {

    List<GitUser> findAllOrderByTotalUserCodeLineAsc();

    List<GitUser> findAllOrderByTotalUserCodeLineDesc();

    List<GitUser> findAllByDepartmentIdOrderByTotalUserCodeLineDesc(int departmentId);

    GitUser findById(int id);

    GitUser findByUserName(String userName);

    void userInsert(Newbie newbie);

    void userUpdate(GitUser gitUser);

    Boolean checkExistPrincipalUserName();

    GitUser checkExist(String login, String password);

    String getPrincipalUserName();

    int getUserRankByTotalCodeLine(String userName, List<GitUser> gitUsers);

    boolean checkUserRankValue(int totalCodeLineRank);

    boolean userAuthCheck(String userName);

    void userDelete(String login);
}
