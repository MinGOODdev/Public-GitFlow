package org.gitflow.sw.controller;

import org.gitflow.sw.dto.*;
import org.gitflow.sw.service.*;
import org.gitflow.sw.util.reader.DetailFileReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("analysis")
public class RepoAnalysisController {

    private IncludeService includeService;
    private ExcludeService excludeService;
    private IgnorePathService ignorePathService;
    private MustContainService mustContainService;
    private AdminService adminService;
    private DetailFileReader detailFileReader;
    private UserService userService;
    private CommonService commonService;

    public RepoAnalysisController(IncludeService includeService,
                                  ExcludeService excludeService,
                                  IgnorePathService ignorePathService,
                                  MustContainService mustContainService,
                                  AdminService adminService,
                                  DetailFileReader detailFileReader,
                                  UserService userService,
                                  CommonService commonService) {
        this.includeService = includeService;
        this.excludeService = excludeService;
        this.ignorePathService = ignorePathService;
        this.mustContainService = mustContainService;
        this.adminService = adminService;
        this.detailFileReader = detailFileReader;
        this.userService = userService;
        this.commonService = commonService;
    }

    @GetMapping("window")
    public String getInputGitUrlWindow(Model model) {
        commonService.commonAttributeSetting(model);
        return "analysis/windowInput";
    }

    @GetMapping("linux")
    public String getInputGitUrlLinux(Model model) {
        commonService.commonAttributeSetting(model);
        return "analysis/linuxInput";
    }

    /**
     * git clone 경로 삭제 (로컬 - 윈도우)
     *
     * @return
     */
    @GetMapping("window/reset")
    public String removeDumpWindow() {
        adminService.runWindowDirectoryRemove();
        return "redirect:/analysis/window";
    }

    /**
     * git clone 경로 삭제 (배포 서버 - 리눅스)
     *
     * @return
     */
    @GetMapping("linux/reset")
    public String removeDumpLinux() {
        adminService.runLinuxDirectoryRemove();
        return "redirect:/analysis/linux";
    }

    /**
     * 로컬(윈도우) 확장자 별 코드 라인 계산
     *
     * @param model
     * @param gitUrl
     * @return
     */
    @PostMapping("window/line")
    public String getWindowCodeLine(Model model,
                                    @RequestParam("gitUrl") String gitUrl,
                                    @RequestParam("path") String path) {
        List<Include> includes = includeService.findAll();
        List<Exclude> excludes = excludeService.findAll();
        List<IgnorePath> ignorePaths = ignorePathService.findAll();
        List<MustContain> mustContains = mustContainService.findAll();

        Map<String, Integer> logMap = new HashMap<>();

        adminService.runWindowGitClone(gitUrl, path);
        Map<String, Integer> codeMap = adminService.makeBaseCodeMap(includes);
        detailFileReader.countTotalLine("dump/" + path, includes, excludes, ignorePaths, mustContains, codeMap, logMap);
        Map<String, Integer> resultMap = adminService.makeResultCodeMap(codeMap);

        model.addAttribute("gitUrl", gitUrl);
        model.addAttribute("resultMap", resultMap);
        model.addAttribute("logMap", adminService.sortByValues(logMap, true));
        return "analysis/codeLine";
    }

    /**
     * 배포 서버(리눅스) 확장자 별 코드 라인 계산
     *
     * @param model
     * @param gitUrl
     * @return
     */
    @PostMapping("linux/line")
    public String getLinuxCodeLine(Model model,
                                   @RequestParam("gitUrl") String gitUrl) {
        List<Include> includes = includeService.findAll();
        List<Exclude> excludes = excludeService.findAll();
        List<IgnorePath> ignorePaths = ignorePathService.findAll();
        List<MustContain> mustContains = mustContainService.findAll();

        String currentUserName = userService.getPrincipalUserName();
        GitUser currentUser = userService.findByUserName(currentUserName);
        boolean authFlag = userService.userAuthCheck(currentUserName);

        String tempPath = gitUrl.replaceFirst("https://github.com/", "")
                .replaceFirst("/", "")
                .replaceFirst(".git", "");

        Map<String, Integer> logMap = new HashMap<>();

        adminService.runLinuxGitClone(gitUrl, tempPath);
        Map<String, Integer> codeMap = adminService.makeBaseCodeMap(includes);
        detailFileReader.countTotalLine("dump/" + tempPath, includes, excludes, ignorePaths, mustContains, codeMap, logMap);
        Map<String, Integer> resultMap = adminService.makeResultCodeMap(codeMap);

        commonService.commonAttributeSetting(model);
        model.addAttribute("gitUrl", gitUrl);
        model.addAttribute("resultMap", resultMap);
        model.addAttribute("logMap", adminService.sortByValues(logMap, true));
        return "analysis/codeLine";
    }

}
