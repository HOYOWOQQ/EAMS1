//package com.eams.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(authorize -> authorize
//                .anyRequest().permitAll()   // 所有路徑都放行
//            )
//            .csrf(csrf -> csrf.disable())   // 關掉 CSRF，前端測試比較方便
//            .formLogin(form -> form.disable()) // 關掉登入表單
//            .httpBasic(basic -> basic.disable()) // 關掉 Basic 認證
//            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
//        return http.build();
//    }
//}
////