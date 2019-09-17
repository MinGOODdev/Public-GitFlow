package org.gitflow.sw.util.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.RepoService;
import org.gitflow.sw.util.command.Cmd;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class SchedulerAssistant {

    private static final int LIBRARY_JUDGE_CODE_LINE = 200;
    private static final int PER_COMMIT_LIBRARY_JUDGE_CODE_LINE = 100;

    @Autowired
    private RepoService repoService;

    /**
     * git clone 실행
     *
     * @param repo
     * @param userName
     */
    public void gitCloneCommand(Repo repo, String userName) {
        Cmd cmd = new Cmd();
        String command = cmd.inputCommand("git clone ")
                .concat(repo.getRepoUrl())
                .concat(" dump/" + userName + repo.getRepoName());
        cmd.execCommand(command);
        log.info("* {} / cmd 실행", repo.getRepoName());
    }

    /**
     * git clone 저장 및 파일을 읽은 후 dump 경로 삭제
     */
    public void dumpRemoveCommand() {
        Cmd cmd = new Cmd();
//    String command = cmd.inputCommand("rmdir /s /q dump");
        String command = cmd.inputCommand("rm -r dump");
        cmd.execCommand(command);
    }

    /**
     * DB repo 데이터와 Github Repo 데이터 비교를 위한 base map 생성
     *
     * @param repoList
     * @return
     */
    public Map<String, Boolean> makeCompareMap(List<Repo> repoList) {
        Map<String, Boolean> compareMap = new HashMap<>();

        for (Repo repo : repoList) {
            compareMap.put(repo.getRepoName(), false);
        }
        return compareMap;
    }

    /**
     * DB repo 데이터가 Github에도 존재하면 compareMap의 boolean 값을 true로 갱신
     *
     * @param compareMap
     * @param repoNameList
     */
    public void updateCompareMapByRepoExist(Map<String, Boolean> compareMap,
                                            List<String> repoNameList) {
        for (String repoName : repoNameList) {
            if (compareMap.get(repoName) != null) {
                compareMap.put(repoName, true);
            }
        }
    }

    /**
     * DB 데이터가 스케줄링 할 때도 존재하는지 판별을 위한 repoNameList 만들기
     *
     * @param userName
     * @param ghUser
     * @return
     */
    public List<String> makeRepoNameList(String userName, GHUser ghUser) {
        List<String> repoNameList = new ArrayList<>();

        try {
            Map<String, GHRepository> ghRepoMap = ghUser.getRepositories();
            Set<String> keys = ghRepoMap.keySet();
            for (String repoName : keys) {
                GHRepository ghRepo = ghRepoMap.get(repoName);
                if (ghRepo.getSize() != 0 && ghRepo.isPrivate() == false) {
                    List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
                    String repoSimpleName = ghRepo.getName();
                    for (GHRepository.Contributor contributor : contributorList) {
                        if (contributor.getLogin().toLowerCase().equals(userName)) {
                            repoNameList.add(repoSimpleName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("### makeRepoFullNameList 예외 발생");
        }
        return repoNameList;
    }

    /**
     * 해당 Repo에 대한 사용자의 Total Commit 수 구하기
     *
     * @param userName
     * @param ghRepo
     * @return
     */
    public int sumUserCommitCount(String userName, GHRepository ghRepo) {
        int userCommitCount = 0;

        try {
            List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
            for (GHRepository.Contributor contributor : contributorList) {
                if (contributor.getLogin().toLowerCase().equals(userName)) {
                    userCommitCount += contributor.getContributions();
                }
            }
        } catch (IOException e) {
            log.error("### sumUserCommitCount 예외 발생");
        }
        return userCommitCount;
    }

    /**
     * Repository의 Contributor 수 구하기
     *
     * @param ghRepo
     * @return
     */
    public int repoContributorCount(GHRepository ghRepo) {
        int contributors = 0;

        try {
            List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
            contributors = contributorList.size();
        } catch (IOException e) {
            log.error("### repoContributorCount 예외 발생");
        }
        return contributors;
    }

    /**
     * 사용자의 주 언어 도출
     *
     * @param userId
     * @return
     */
    public String getUserPrimaryLanguage(int userId) {
        Map<String, Integer> languageMap = new HashMap<>();
        List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(userId);

        for (Repo repo : repoList) {
            String repoLanguage = repo.getRepoLanguage();
            if (languageMap.get(repoLanguage) == null) languageMap.put(repoLanguage, 1);
            else languageMap.put(repoLanguage, languageMap.get(repoLanguage) + 1);
        }

        String maxLanguage = null;
        int maxCount = 0;

        for (String language : languageMap.keySet()) {
            if (maxCount < languageMap.get(language)) {
                maxLanguage = language;
                maxCount = languageMap.get(language);
            }
        }
        return maxLanguage;
    }

    /**
     * gituser table - totalUserCodeLine 값 구하기
     *
     * @param repoList
     * @return
     */
    public int getTotalUserCodeLine(List<Repo> repoList) {
        int totalUserCodeLine = 0;

        for (Repo repo : repoList) {
            if (repo.getContributors() == 0 || repo.getContributors() == 1) {
                totalUserCodeLine += repo.getTotalCodeLine();
            } else {
                totalUserCodeLine += repo.getUserCodeLine();
            }
        }
        return totalUserCodeLine;
    }

    /**
     * 라이브러리 의심 파일이 저장된 Map 만들기
     *
     * @param fileCommitCountMap
     * @param fileCodeLineMap
     * @return
     */
    public Map<String, Integer> extractLibraryExpectation(Map<String, Integer> fileCommitCountMap,
                                                          Map<String, Integer> fileCodeLineMap) {
        Map<String, Integer> libraryExpectationMap = new HashMap<>();

        for (String key : fileCommitCountMap.keySet()) {
            int fileCommitCount = fileCommitCountMap.get(key);
            int fileCodeLine = fileCodeLineMap.get(key);
            int perCommitCodeLine = fileCodeLine / fileCommitCount;

            if (fileCommitCount == 1 && fileCodeLine >= LIBRARY_JUDGE_CODE_LINE) {
                libraryExpectationMap.put(key, fileCodeLine);
            } else if (fileCommitCount > 1 && perCommitCodeLine >= PER_COMMIT_LIBRARY_JUDGE_CODE_LINE) {
                libraryExpectationMap.put(key, fileCodeLine);
            }
        }
        return libraryExpectationMap;
    }

    /**
     * 파일의 Commit 횟수 계산
     *
     * @param mixKeyName
     * @param fileCommitCountMap
     */
    public void fileCommitCountCalculator(String mixKeyName, Map<String, Integer> fileCommitCountMap) {
        if (fileCommitCountMap.get(mixKeyName) == null) {
            fileCommitCountMap.put(mixKeyName, 1);
        } else {
            fileCommitCountMap.put(mixKeyName, fileCommitCountMap.get(mixKeyName) + 1);
        }
    }

    /**
     * 파일의 코드 라인 수 계산
     *
     * @param mixKeyName
     * @param fileCodeLine
     * @param fileCodeLineMap
     */
    public void fileCodeLineCountCalculator(String mixKeyName, int fileCodeLine, Map<String, Integer> fileCodeLineMap) {
        if (fileCodeLineMap.get(mixKeyName) == null) {
            fileCodeLineMap.put(mixKeyName, fileCodeLine);
        } else {
            fileCodeLineMap.put(mixKeyName, fileCodeLineMap.get(mixKeyName) + fileCodeLine);
        }
    }

    /**
     * Commit Msg에 "Merge" 키워드가 포함되어 있는지 검사
     *
     * @param commitMsg
     * @return
     */
    public boolean getCommitMsgFlag(String commitMsg) {
        boolean commitMsgFlag = !commitMsg.startsWith("Merge branch")
                && !commitMsg.startsWith("Merge pull")
                && !commitMsg.startsWith("Merge remote")
                && !commitMsg.startsWith("merge");
        return commitMsgFlag;
    }

    /**
     * 최종적으로 파일을 읽은 것인지 여부를 판단하는 Boolean Flag
     *
     * @param ignorePathFlag
     * @param commitMsgFlag
     * @param includeFlag
     * @param excludeFlag
     * @param mustContainFlag
     * @return
     */
    public boolean getFinalFlag(boolean ignorePathFlag, boolean commitMsgFlag,
                                boolean includeFlag, boolean excludeFlag, boolean mustContainFlag) {
        boolean firstFlag = ignorePathFlag && commitMsgFlag;
        boolean secondFlag = includeFlag && excludeFlag || mustContainFlag;
        boolean finalFlag = firstFlag && secondFlag;
        return finalFlag;
    }

}
