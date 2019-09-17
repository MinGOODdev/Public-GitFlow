package org.gitflow.sw.config;

import org.gitflow.sw.util.security.MyAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationProvider myAuthenticationProvider;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 회원가입을 해야 세부 메뉴들을 사용할 수 있도록 설정
        http.authorizeRequests()
                .antMatchers("/swagger-ui.html", "/admin/**", "/gitflow/**").access("hasRole('ROLE_ADMIN')")
//            .antMatchers("/users/**", "/user/**", "/question/**", "/notice/**").authenticated()
                .antMatchers("/analysis/**", "/nofilter/**", "/apply/**").authenticated()
                .antMatchers("/**").permitAll();

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_processing")
                .failureUrl("/login")
                .defaultSuccessUrl("/", true)
                .usernameParameter("login")
                .passwordParameter("password");

        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout_processing"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);

        http.authenticationProvider(myAuthenticationProvider);
    }

}
