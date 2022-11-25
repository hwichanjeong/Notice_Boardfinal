package Study.Board.config;

import Study.Board.auth.CustomAccessDeniedHandler;
import Study.Board.auth.CustomAuthenticationEntryPoint;
import Study.Board.auth.LoginFailureHandler;
import Study.Board.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URLEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다.
@RequiredArgsConstructor
public class SecurityConfig{

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();//보안수준 향상을 위해 위조 요청 방지를 하는 csrf를 일단 disable함
        http.authorizeRequests()
                //특정 리소스의 접근 허용 또는 특정 권한을 가진 사용자만 접근을 가능하게 하는 메서드
                .antMatchers("/content/list/1").access("hasAuthority('ADMIN')")
                .antMatchers("/content/list/3/**", "/content/3/write")
                .access("hasAuthority('GOLD') or hasAuthority('ADMIN')")
                .antMatchers("/", "/mycss.css", "/user/login", "/user/signup", "/message", "/error",
                        "/oauth2/**")
                //antMatchers에서 설정한 리소스의 접근을 인증절차 없이 허용한다는 의미 입니다.
                .permitAll()
                //모든 리소스를 의미하며 접근허용 리소스 및 인증후 특정 레벨의 권한을 가진 사용자만 접근가능한 리소스를 설정하고
                // 그외 나머지 리소스들은 무조건 인증을 완료해야 접근이 가능하다는 의미입니다.
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("loginId")
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")// 이 URL이 호출되면 스프링 시큐리티가 대신 로그인을 진행해줍니다.
                .defaultSuccessUrl("/message?url=/&msg="+ URLEncoder.encode("로그인에 성공했습니다!", "UTF-8"))
                .failureHandler(new LoginFailureHandler())
                .and()
                .oauth2Login()
                .loginPage("/user/login")
                .defaultSuccessUrl("/message?url=/&msg="+ URLEncoder.encode("로그인에 성공했습니다!", "UTF-8"))
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());
        return http.build();
    }
}
