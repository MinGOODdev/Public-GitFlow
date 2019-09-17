package org.gitflow.sw.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private OptionFlagService optionFlagService;
    @Autowired
    private RepoService repoService;
    @Autowired
    private SchedulerAssistant schedulerAssistant;
    @Autowired
    private LibExpectService libExpectService;
    @Autowired
    private UserService userService;

    /**
     * 스케줄러 작동 여부 체크
     *
     * @return 1: 작동 true & 0: 작동X false
     */
    @Deprecated
    @Override
    public boolean schedulerRunningCheck() {
        int schedulerActive = optionFlagService.findById(1).getSchedulerActive();
        if (schedulerActive == 1) return true;
        else return false;
    }

    /**
     * MySQL: repo table 갱신
     * - repo != null 이고 현재 github의 커밋 수와 db의 커밋 수가 다르면 update
     * - repo == null 이면 insert
     *
     * @param repo
     * @param userId
     * @param userName
     * @param ghRepo
     * @param changedRepoList
     */
    @Override
    public void insertOrUpdateRepoData(Repo repo,
                                       int userId, String userName,
                                       GHRepository ghRepo, List<Repo> changedRepoList) {
        int gitCommits = ghRepo.listCommits().asList().size();  // 전체 커밋 수
        int dbCommits = 0;
        if (repo != null) dbCommits = repo.getAllCommitCount();

        if (repo != null && (gitCommits != dbCommits)) {
            if (ghRepo.getLanguage() == null) repo.setRepoLanguage("Markdown");
            else repo.setRepoLanguage(ghRepo.getLanguage());
            repo.setAllCommitCount(gitCommits);
            repo.setContributors(schedulerAssistant.repoContributorCount(ghRepo));
            repoService.updateRepo(repo);

            changedRepoList.add(repo);
            log.info("update** {}", repo);
        } else if (repo == null) {
            Repo tempRepo = new Repo();
            tempRepo.setUserId(userId);
            tempRepo.setRepoName(ghRepo.getName());
            if (ghRepo.getLanguage() == null) tempRepo.setRepoLanguage("Markdown");
            else tempRepo.setRepoLanguage(ghRepo.getLanguage());
            tempRepo.setRepoUrl(ghRepo.getHttpTransportUrl());
            tempRepo.setAllCommitCount(gitCommits);
            tempRepo.setContributors(schedulerAssistant.repoContributorCount(ghRepo));
            repoService.insertRepo(tempRepo);

            changedRepoList.add(tempRepo);
            log.info("insert** {}", tempRepo);
        }
    }

    /**
     * 라이브러리 의심 파일 LibExpect Table에 저장
     *
     * @param libraryExpectationMap
     */
    @Override
    public void insertLibraryExpectation(Map<String, Integer> libraryExpectationMap) {
        for (String key : libraryExpectationMap.keySet()) {
            log.info("### library 의심 목록: {} / {}", key, libraryExpectationMap.get(key));
            libExpectService.insert(key, libraryExpectationMap.get(key));
        }
    }

    /**
     * GitUser Table - totalUserCommit, totalUserCodeLine 갱신
     *
     * @param gitUser
     * @param totalUserCommit
     */
    @Override
    public void updateGitUserTotalUserCommitAndCodeLine(GitUser gitUser, int totalUserCommit) {
        List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(gitUser.getId());
        int totalUserCodeLine = schedulerAssistant.getTotalUserCodeLine(repoList);
        gitUser.setTotalUserCommit(totalUserCommit);
        gitUser.setTotalUserCodeLine(totalUserCodeLine);
        userService.userUpdate(gitUser);
    }

}
