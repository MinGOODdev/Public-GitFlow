package org.gitflow.sw.controller;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.dto.Repo;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.RepoService;
import org.gitflow.sw.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class RepoController {

    private RepoService repoService;
    private UserService userService;
    private CommonService commonService;

    public RepoController(RepoService repoService,
                          UserService userService,
                          CommonService commonService) {
        this.repoService = repoService;
        this.userService = userService;
        this.commonService = commonService;
    }

    /**
     * 사용자의 repository 목록 조회
     *
     * @param model
     * @param userId
     * @return
     */
    @GetMapping("user/{userId}/repos")
    public String repos(Model model,
                        @PathVariable("userId") int userId) {
        commonService.commonAttributeSetting(model);
        GitUser gitUser = userService.findById(userId);
        List<Repo> repoList = repoService.findAllByUserIdOrderByAllCommitCountDesc(userId);

        model.addAttribute("user", gitUser);
        model.addAttribute("repoList", repoList);
        return "user/repoSet";
    }

}
