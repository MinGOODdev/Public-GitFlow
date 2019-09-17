package org.gitflow.sw.controller;

import org.gitflow.sw.dto.Include;
import org.gitflow.sw.service.AdminService;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.IncludeService;
import org.gitflow.sw.util.reader.NoFilterFileReader;
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
@RequestMapping("nofilter/analysis")
public class NoFilterAnalysisController {

    private NoFilterFileReader noFilterFileReader;
    private AdminService adminService;
    private IncludeService includeService;
    private CommonService commonService;

    public NoFilterAnalysisController(NoFilterFileReader noFilterFileReader,
                                      AdminService adminService,
                                      IncludeService includeService,
                                      CommonService commonService) {
        this.noFilterFileReader = noFilterFileReader;
        this.adminService = adminService;
        this.includeService = includeService;
        this.commonService = commonService;
    }

    /**
     * No Filtering GET
     *
     * @param model
     * @return
     */
    @GetMapping("linux")
    public String getInputGitUrlLinux(Model model) {
        commonService.commonAttributeSetting(model);
        return "analysis/nofilter/linux";
    }

    /**
     * No Filtering 분석
     *
     * @param model
     * @param gitUrl
     * @return
     */
    @PostMapping("linux/line")
    public String getLinuxCodeLine(Model model,
                                   @RequestParam("gitUrl") String gitUrl) {
        List<Include> includes = includeService.findAll();

        String tempPath = gitUrl.replaceFirst("https://github.com/", "")
                .replaceFirst("/", "")
                .replaceFirst(".git", "");

        Map<String, Integer> logMap = new HashMap<>();

        adminService.runLinuxGitClone(gitUrl, tempPath);
        Map<String, Integer> codeMap = adminService.makeBaseCodeMap(includes);
        noFilterFileReader.countTotalLine("dump/" + tempPath, codeMap, logMap);
        Map<String, Integer> resultMap = adminService.makeResultCodeMap(codeMap);

        model.addAttribute("gitUrl", gitUrl);
        model.addAttribute("resultMap", resultMap);
        model.addAttribute("logMap", adminService.sortByValues(logMap, true));
        return "analysis/nofilter/codeLine";
    }

}
