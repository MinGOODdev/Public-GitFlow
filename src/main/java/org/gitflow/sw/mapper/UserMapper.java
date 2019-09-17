package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gitflow.sw.dto.GitUser;

import java.util.List;

@Mapper
public interface UserMapper {

    List<GitUser> findAllOrderByTotalUserCodeLineAsc();

    List<GitUser> findAllOrderByTotalUserCodeLineDesc();

    List<GitUser> findAllByDepartmentIdOrderByTotalUserCodeLineDesc(int departmentId);

    GitUser findById(int id);

    GitUser findByUserName(String login);

    void userInsert(GitUser gitUser);

    void userUpdate(GitUser gitUser);

    void userDelete(String login);

}
