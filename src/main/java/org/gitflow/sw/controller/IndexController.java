package org.gitflow.sw.controller;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.Newbie;
import org.gitflow.sw.service.AsyncService;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.DepartmentService;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.thread.NewbieRepoInfoCallableThread;
import org.gitflow.sw.util.GitHubApiUtil;
import org.gitflow.sw.util.security.SignUpValidator;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class IndexController {

    private UserService userService;
    private DepartmentService departmentService;
    private SignUpValidator signUpValidator;
    private GitHubApiUtil gitHubApiUtil;
    private AsyncService asyncService;
    private CommonService commonService;

    public IndexController(UserService userService,
                           DepartmentService departmentService,
                           SignUpValidator signUpValidator,
                           GitHubApiUtil gitHubApiUtil,
                           AsyncService asyncService,
                           CommonService commonService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.signUpValidator = signUpValidator;
        this.gitHubApiUtil = gitHubApiUtil;
        this.asyncService = asyncService;
        this.commonService = commonService;
    }

    /**
     * 메인 페이지
     * - 사용자 권한 Flag
     *
     * @param model
     * @return
     */
    @GetMapping("/")
    public String home(Model model) {
        commonService.commonAttributeSetting(model);
        return "home";
    }

    /**
     * 소속 별 사용자 목록 조회 + 랭킹
     *
     * @param model
     * @param departmentId
     * @return
     */
    @GetMapping("users")
    public String index(Model model,
                        @RequestParam(value = "departmentId", defaultValue = "1") int departmentId) {
        commonService.commonAttributeSetting(model);
        String currentUserName = userService.getPrincipalUserName();
        String departmentName = departmentService.findById(departmentId).getDepartmentName();
        List<GitUser> gitUserList = userService.findAllByDepartmentIdOrderByTotalUserCodeLineDesc(departmentId);
        int totalCodeLineRank = userService.getUserRankByTotalCodeLine(currentUserName, gitUserList);
        boolean rankFlag = userService.checkUserRankValue(totalCodeLineRank);

        model.addAttribute("gitUserList", gitUserList);
        model.addAttribute("departmentName", departmentName);
        model.addAttribute("totalCodeLineRank", totalCodeLineRank);
        model.addAttribute("rankFlag", rankFlag);
        return "index";
    }

    /**
     * 로그인
     * - 로그인 과정은 login_processing이 처리한다.
     *
     * @return
     */
    @GetMapping("login")
    public String login() {
        return "login";
    }

    /**
     * 회원가입
     * GitHub 계정 도용을 방지하기 위해 Password까지 GitHub과 일치해야 가입 가능
     *
     * @param newbie
     * @param bindingResult
     * @param model
     * @param httpServletRequest: get, post 판단
     * @return
     */
    @RequestMapping("signUp")
    public String signUp(@ModelAttribute("newbie") @Validated Newbie newbie,
                         BindingResult bindingResult,
                         Model model,
                         HttpServletRequest httpServletRequest) {
        try {
            model.addAttribute("departments", departmentService.findAll());

            if ("POST".equals(httpServletRequest.getMethod())) {
                GitHub gitHub = GitHub.connectUsingPassword(newbie.getUserName(), newbie.getPassword1());
                gitHub.getMyself();
                signUpValidator.validate(newbie, bindingResult);

                if (bindingResult.hasErrors()) {
                    model.addAttribute("msg", "ID와 PW를 확인하세요.");
                    model.addAttribute("departmentId", newbie.getDepartmentId());
                    return "signUp";
                }

                gitHubApiUtil.setGitHubMap(newbie.getUserName().toLowerCase());
                userService.userInsert(newbie);
                gitHubApiUtil.refreshGitHub();

                //thread, 비동기 신규회원 repo info insert.
                NewbieRepoInfoCallableThread thread =
                        new NewbieRepoInfoCallableThread(asyncService, newbie.getUserName(), newbie.getPassword1());

                asyncService.runThread(thread);
                return "redirect:/";

            }

            return "signUp";

        } catch (IOException e) {
            model.addAttribute("msg", "ID와 PW를 GitHub 계정과 일치하도록 입력하세요.");
            return "signUp";
        }
    }

}