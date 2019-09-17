package org.gitflow.sw.util.security;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.Newbie;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.util.GitHubApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Resource;

@Component
public class SignUpValidator implements Validator {

    @Autowired
    private UserService userService;
    @Resource
    private GitHubApiUtil gitHubApiUtil;

    /**
     * validate에 object로 넘어온 객체가 검증 가능한 객체인지 판단
     *
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return GitUser.class.equals(aClass);
    }

    /**
     * 유효성 검사 진행
     * - error msg 출력: ValidationUtils, errors 활용
     *
     * @param target
     * @param errors
     */
    @Override
    public void validate(Object target, Errors errors) {
        Newbie newbie = (Newbie) target;

        String userName = newbie.getUserName();
        String password1 = newbie.getPassword1();
        String password2 = newbie.getPassword2();

        if (userName.equals("") || userName == null) {
            errors.rejectValue("userName", "required.userName", "Field userName Is Required");
        } else if (!gitHubApiUtil.isGitHubUserName(userName)) {
            errors.rejectValue("userName", "required.userName", "Field userName is Not Valid");
        } else if (userService.findByUserName(userName) != null) {
            errors.rejectValue("userName", "required.userName", "Field userName Is Used");
        }

        int pwLength = password1.length();
        if (pwLength < 3 || pwLength > 17) {
            errors.rejectValue("password1", "required.password1", "Field Password Is Not Valid");
        }

        if (!password1.equals(password2)) {
            errors.rejectValue("password2", "required.password2", "Field Password Not Matched");
        }
    }

}
