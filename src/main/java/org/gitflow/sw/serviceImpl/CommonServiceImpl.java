package org.gitflow.sw.serviceImpl;

import org.gitflow.sw.dto.Department;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.service.CommonService;
import org.gitflow.sw.service.DepartmentService;
import org.gitflow.sw.service.OptionFlagService;
import org.gitflow.sw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private OptionFlagService optionFlagService;

    @Override
    public void commonAttributeSetting(Model model) {
        String currentUserName = userService.getPrincipalUserName();
        GitUser currentUser = userService.findByUserName(currentUserName);
        boolean userCheckExist = userService.checkExistPrincipalUserName();
        boolean authFlag = userService.userAuthCheck(currentUserName);
        boolean optionFlag = optionFlagService.optionFlagCheck();
        List<Department> departmentList = departmentService.findAll();

        model.addAttribute("authFlag", authFlag);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userCheckExist", userCheckExist);
        model.addAttribute("departments", departmentList);
        model.addAttribute("optionFlag", optionFlag);
    }

}
