package org.gitflow.sw.controller;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.MyInfo;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.DepartmentService;
import org.gitflow.sw.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user")
@Slf4j
public class UserController {

    private UserService userService;
    private DepartmentService departmentService;
    private CommonService commonService;

    public UserController(UserService userService,
                          DepartmentService departmentService,
                          CommonService commonService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.commonService = commonService;
    }

    /**
     * 회원 정보 수정 (GET)
     *
     * @param model
     * @return
     */
    @GetMapping("myInfo")
    public String myInfo(Model model) {
        GitUser currentUser = userService.findByUserName(userService.getPrincipalUserName());
        model.addAttribute("currentUser", currentUser);

        if (currentUser == null) {
            return "/signUp";
        }

        int departmentId = currentUser.getDepartmentId();
        commonService.commonAttributeSetting(model);
        model.addAttribute("department", departmentService.findById(departmentId).getDepartmentName());
        return "user/myInfo";
    }

    /**
     * 회원 정보 수정 (POST)
     *
     * @return
     */
    @PostMapping("myInfo")
    public String postMyInfo(@ModelAttribute MyInfo myInfo) {
        GitUser gitUser = userService.findByUserName(myInfo.getUserName());
        gitUser.setDepartmentId(myInfo.getDepartmentId());
        userService.userUpdate(gitUser);
        return "redirect:/";
    }

}
