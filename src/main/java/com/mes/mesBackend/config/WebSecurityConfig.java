package com.mes.mesBackend.config;

import com.mes.mesBackend.auth.JwtAccessDeniedHandler;
import com.mes.mesBackend.auth.JwtAuthenticationEntryPoint;
import com.mes.mesBackend.auth.JwtAuthenticationFilter;
import com.mes.mesBackend.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                // custom exception 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and().cors().configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        config.setAllowedOrigins(
                                Arrays.asList(
                                        "http://localhost:3000",
                                        "http://dev-mes-grid.s3-website.ap-northeast-2.amazonaws.com",
                                        "http://prod-mes-grid.s3-website.ap-northeast-2.amazonaws.com",
                                        "http://prod-mes.s3-website.ap-northeast-2.amazonaws.com"
                                )
                        );
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList(AUTHORIZATION));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }).and()
                // 시큐리티는 세션을 사용하지만 jwt로 인해서 세션을 사용하지 않기 때문에 stateless로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 요청에 대한 사용권한 체크
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers("/auth/signin").permitAll()
                .antMatchers("/auth/signup").permitAll()
                .antMatchers("/auth/reissue").permitAll()
                .antMatchers("/pop/work-processes").permitAll()
                .antMatchers("/label-prints/**").permitAll()
                .antMatchers("/**").authenticated()
//                .anyRequest().permitAll()       // 그 외 나머지 요청은 누구나 접근
                // JwtFilter 를 addFilterBefore 로 등록했던 JwtAuthenticationFilter 클래스를 적용
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}
