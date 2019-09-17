package org.gitflow.sw.service;

import org.gitflow.sw.dto.Repo;

import java.util.List;

public interface RepoService {

    List<Repo> findAllByUserIdOrderByAllCommitCountDesc(int userId);

    Repo findByRepoNameAndUserId(String repoName, int userId);

    void insertRepo(Repo repo);

    void updateRepo(Repo repo);

    void deleteRepo(String repoName, int userId);

}
