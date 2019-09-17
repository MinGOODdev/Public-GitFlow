package org.gitflow.sw.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.model.Login;
import org.gitflow.sw.model.ResponseModel;
import org.gitflow.sw.service.UserService;
import org.gitflow.sw.util.security.MyAuthenticationProvider;
import org.gitflow.sw.util.security.SHA256Encryption;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("api")
@Slf4j
public class RestIndexController {

    private MyAuthenticationProvider myAuthenticationProvider;
    private UserService userService;

    /**
     * 로그인
     *
     * @param login
     * @param httpServletRequest
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<ResponseModel> signUp(@RequestBody Login login,
                                                HttpServletRequest httpServletRequest) {
        ResponseModel responseModel = new ResponseModel();
        String inputLogin = login.getLogin().toLowerCase();
        GitUser gitUser = userService.findByUserName(inputLogin);

        if (gitUser == null) {
            // UserName이 틀린 경우
            responseModel.setMsg("Invalid UserName");
            return new ResponseEntity<>(responseModel, HttpStatus.OK);
        } else if (gitUser != null && !gitUser.getPassword().equals(SHA256Encryption.encrypt(login.getPassword()))) {
            // Password가 틀린 경우
            responseModel.setMsg("Invalid Password");
            return new ResponseEntity<>(responseModel, HttpStatus.OK);
        } else {
            // 로그인 성공
            myAuthenticationProvider.authenticate(inputLogin, login.getPassword());
            Map<String, Object> map = new HashMap<>();
            map.put("id", httpServletRequest.getSession(true).getId());
            map.put("requestedId", httpServletRequest.getRequestedSessionId());
            responseModel.setData(map);
            responseModel.setMsg("Login Success");
            return new ResponseEntity<>(responseModel, HttpStatus.OK);
        }
    }

}
