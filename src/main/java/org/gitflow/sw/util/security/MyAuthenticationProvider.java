package org.gitflow.sw.util.security;

import org.gitflow.sw.dto.GitUser;
import org.gitflow.sw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();
        return authenticate(login, password);
    }

    public Authentication authenticate(String login, String password) throws AuthenticationException {
        GitUser gitUser = userService.checkExist(login, password);
        if (gitUser == null) return null;

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String role = "";
        switch (gitUser.getAuthorization()) {
            case "1":
                role = "ROLE_STUDENT";
                break;
            case "2":
                role = "ROLE_ADMIN";
                break;
        }
        grantedAuthorities.add(new SimpleGrantedAuthority(role));

        return new MyAuthentication(login, password, grantedAuthorities, gitUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public class MyAuthentication extends UsernamePasswordAuthenticationToken {
        private static final long serialVersionUID = 1L;
        GitUser gitUser;

        public MyAuthentication(String login,
                                String password,
                                List<GrantedAuthority> grantedAuthorities,
                                GitUser gitUser) {
            super(login, password, grantedAuthorities);
            this.gitUser = gitUser;
        }

        public GitUser getGitUser() {
            return gitUser;
        }

        public void setGitUser(GitUser gitUser) {
            this.gitUser = gitUser;
        }
    }

}
