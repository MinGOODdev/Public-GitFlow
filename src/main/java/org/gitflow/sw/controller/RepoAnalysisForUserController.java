package org.gitflow.sw.controller;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.Exclude;
import org.gitflow.sw.dto.IgnorePath;
import org.gitflow.sw.dto.Include;
import org.gitflow.sw.dto.MustContain;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.scheduler.SchedulerAssistant;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Slf4j
@Controller
@RequestMapping("user")
public class RepoAnalysisForUserController {

    private static final String WEB_FLOW = "web-flow";

    private IncludeService includeService;
    private ExcludeService excludeService;
    private IgnorePathService ignorePathService;
    private MustContainService mustContainService;
    private FileReaderService fileReaderService;
    private AdminService adminService;
    private SchedulerAssistant schedulerAssistant;
    private CommonService commonService;

    public RepoAnalysisForUserController(IncludeService includeService,
                                         ExcludeService excludeService,
                                         IgnorePathService ignorePathService,
                                         MustContainService mustContainService,
                                         FileReaderService fileReaderService,
                                         AdminService adminService,
                                         SchedulerAssistant schedulerAssistant,
                                         CommonService commonService) {
        this.includeService = includeService;
        this.excludeService = excludeService;
        this.ignorePathService = ignorePathService;
        this.mustContainService = mustContainService;
        this.fileReaderService = fileReaderService;
        this.adminService = adminService;
        this.schedulerAssistant = schedulerAssistant;
        this.commonService = commonService;
    }

    /**
     * 협업 Repo 분석 - For 일반 사용자 (GET)
     *
     * @param model
     * @return
     */
    @GetMapping("analysis")
    public String analysis(Model model) {
        commonService.commonAttributeSetting(model);
        return "user/analysis";
    }

    /**
     * 협업 Repo 분석 - For 일반 사용자
     *
     * @param login:     각 사용자의 userName
     * @param password:  각 사용자의 password
     * @param repoOwner: 협업 repo 소유자
     * @param repoName:  협업 repo 이름
     * @param model
     * @return
     */
    @PostMapping("analysis/result")
    public String result(@RequestParam("login") String login,
                         @RequestParam("password") String password,
                         @RequestParam("repoOwner") String repoOwner,
                         @RequestParam("repoName") String repoName,
                         Model model) {
        commonService.commonAttributeSetting(model);

        try {
            GitHub gitHub = GitHub.connectUsingPassword(login, password);
            List<Include> includes = includeService.findAll();
            List<Exclude> excludes = excludeService.findAll();
            List<IgnorePath> ignorePaths = ignorePathService.findAll();
            List<MustContain> mustContains = mustContainService.findAll();

            GHRepository ghRepo = gitHub.getRepository(repoOwner + "/" + repoName);
            List<GHRepository.Contributor> contributorList = ghRepo.listContributors().asList();
            List<GHCommit> commitList = ghRepo.listCommits().asList();

            // Contributor 별 분류를 위해 필요한 Map
            Map<String, Map<String, Integer>> contributorLogMap = new HashMap<>();
            Map<String, Map<String, Integer>> contributorCodeMap = new HashMap<>();
            Map<String, Integer> contributorWriteCodeMap = new HashMap<>();

            // Library 의심 목록을 추출하기 위해 필요한 Map
            Map<String, Integer> fileCommitCountMap = new HashMap<>();
            Map<String, Integer> fileCodeLineMap = new HashMap<>();

            // contributor 별 LogMap과 CodeMap 만들기
            adminService.makeLogAndCodeMapByContributor(contributorList, contributorLogMap, contributorCodeMap, includes);

            for (GHCommit commit : commitList) {
                GHUser ghUser = commit.getCommitter();

                if (ghUser != null && !ghUser.getLogin().equals(WEB_FLOW)) {
                    String userName = ghUser.getLogin().toLowerCase();

                    for (GHCommit.File file : commit.getFiles()) {
                        String fileName = file.getFileName().toLowerCase();

                        boolean includeFlag = fileReaderService.includeCheck(fileName, includes);
                        boolean excludeFlag = fileReaderService.excludeCheck(fileName, excludes);
                        boolean ignorePathFlag = fileReaderService.ignorePathCheck(fileName, ignorePaths);
                        boolean mustContainFlag = fileReaderService.mustContainCheck(fileName, mustContains);

                        // Commit Msg에 "Merge" 키워드가 포함되어 있는지 검사
                        String commitMsg = commit.getCommitShortInfo().getMessage();
                        boolean commitMsgFlag = schedulerAssistant.getCommitMsgFlag(commitMsg);

                        // 최종적으로 파일을 읽은 것인지 판단하는 Boolean Flag
                        boolean finalFlag = schedulerAssistant.getFinalFlag(ignorePathFlag, commitMsgFlag, includeFlag, excludeFlag, mustContainFlag);

                        if (finalFlag) {
                            int userCodeLine = 0;
                            int changedLine = file.getLinesAdded() - file.getLinesDeleted();
                            userCodeLine += changedLine;

                            // contributor 별 작업량 계산
                            adminService.contributorWriteCodeLineCalculator(contributorWriteCodeMap, userName, userCodeLine);

                            // contributor 별 작업 로그 정리
                            adminService.contributorLogArrange(contributorLogMap, userName, fileName, userCodeLine);

                            // contributor 별 extension 별 작업량 계산
                            adminService.extensionWorkloadByContributor(contributorCodeMap, userName, fileName, userCodeLine);

                            // 라이브러리 의심 파일을 추출하기 위해 2가지 맵 데이터를 채우는 작업
                            // file의 commit 횟수 계산
                            schedulerAssistant.fileCommitCountCalculator(fileName, fileCommitCountMap);

                            // file의 코드 라인 수 계산
                            schedulerAssistant.fileCodeLineCountCalculator(fileName, userCodeLine, fileCodeLineMap);
                        }
                    }
                }
            }

            // Contributor 별 Log 목록 내림차순 정렬
            Map<Object, Object> contributorOrderedLogMap = adminService.orderByDescContributorLogMap(contributorLogMap);

            // 라이브러리 의심 파일 추리기
            Map<String, Integer> libraryExpectationMap = schedulerAssistant.extractLibraryExpectation(fileCommitCountMap, fileCodeLineMap);

            model.addAttribute("repoOwner", repoOwner);
            model.addAttribute("repoName", repoName);
            model.addAttribute("logMap", contributorOrderedLogMap);
            model.addAttribute("codeMap", adminService.makeDeepCodeMap(contributorCodeMap));
            model.addAttribute("contributorWriteCodeMap", contributorWriteCodeMap);
            model.addAttribute("libraryMap", libraryExpectationMap);

        } catch (IOException e) {
            log.error("### For User Collaboration Repository Analysis 예외 발생!!!");
        }
        return "user/result";
    }


}
