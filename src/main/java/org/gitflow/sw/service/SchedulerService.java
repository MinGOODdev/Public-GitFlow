package org.gitflow.sw.service;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.kohsuke.github.GHRepository;

import java.util.List;
import java.util.Map;

public interface SchedulerService {

    @Deprecated
    boolean schedulerRunningCheck();

    void insertOrUpdateRepoData(Repo repo, int userId, String userName, GHRepository ghRepo, List<Repo> changedRepoList);

    void insertLibraryExpectation(Map<String, Integer> libraryExpectationMap);

    void updateGitUserTotalUserCommitAndCodeLine(GitUser gitUser, int totalUserCommit);

}
