package org.gitflow.sw.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.gitflow.sw.dto.Repo;

import java.util.List;

@Mapper
public interface RepoMapper {

    List<Repo> findAllByUserIdOrderByAllCommitCountDesc(int userId);

    Repo findByRepoNameAndUserId(@Param("repoName") String repoName, @Param("userId") int userId);

    void insertRepo(Repo repo);

    void updateRepo(Repo repo);

    void deleteRepo(@Param("repoName") String repoName, @Param("userId") int userId);

}
