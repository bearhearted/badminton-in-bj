package cc.bearvalley.badminton.conf;

import cc.bearvalley.badminton.product.service.BadmintonUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * spring security的配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //开启权限注解功能
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .antMatchers(PERMIT_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll).csrf().disable();
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(badmintonUserDetailService)
                .passwordEncoder(new BCryptPasswordEncoder())
                .and()
                .build();
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 构造方法，注入需要使用的组件
     *
     * @param badmintonUserDetailService 加载后台用户的服务类
     */
    public WebSecurityConfig(BadmintonUserDetailService badmintonUserDetailService) {
        this.badmintonUserDetailService = badmintonUserDetailService;
    }

    private final BadmintonUserDetailService badmintonUserDetailService;

    // 不需要登录的页面url列表
    private final String[] PERMIT_URLS = {
            "/api/**",          // 对外api
            "/css/**",          // css文件
            "/js/**",           // js文件
            "/fonts/**"         // 字体文件
    };
}
