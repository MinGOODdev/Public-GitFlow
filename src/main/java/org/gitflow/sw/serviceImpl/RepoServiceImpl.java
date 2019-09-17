package org.gitflow.sw.serviceImpl;

import lombok.AllArgsConstructor;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.mapper.RepoMapper;
import org.gitflow.sw.service.RepoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RepoServiceImpl implements RepoService {

    private RepoMapper repoMapper;

    /**
     * 사용자의 repo list 조회 - 커밋수 내림차순
     *
     * @param userId
     * @return
     */
    @Override
    public List<Repo> findAllByUserIdOrderByAllCommitCountDesc(int userId) {
        return repoMapper.findAllByUserIdOrderByAllCommitCountDesc(userId);
    }

    /**
     * repoName과 userId로 repo 조회
     *
     * @param repoName
     * @param userId
     * @return
     */
    @Override
    public Repo findByRepoNameAndUserId(String repoName, int userId) {
        return repoMapper.findByRepoNameAndUserId(repoName, userId);
    }

    /**
     * MySQL: repo 저장
     *
     * @param repo
     */
    @Override
    public void insertRepo(Repo repo) {
        repoMapper.insertRepo(repo);
    }

    /**
     * MySQL: repo 수정
     *
     * @param repo
     */
    @Override
    public void updateRepo(Repo repo) {
        repoMapper.updateRepo(repo);
    }

    /**
     * 해당 Repo 삭제
     *
     * @param repoName
     * @param userId
     */
    @Override
    public void deleteRepo(String repoName, int userId) {
        repoMapper.deleteRepo(repoName, userId);
    }

}
