package com.eams.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // 列出前端可跨域的來源（可加更多）
        cfg.setAllowedOrigins(List.of("http://localhost:5173",
        							"https://vue-teal-ten.vercel.app",
        							" https://b95c41441ed6.ngrok-free.app",
        							"https://payment-stage.ecpay.com.tw"));
        // 如需萬用字元，改用 patterns（且仍可與 allowCredentials=true 並存）
        // cfg.setAllowedOriginPatterns(List.of("http://localhost:5173", "https://*.yourdomain.com"));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        // 視需要曝露自訂標頭
        // cfg.setExposedHeaders(List.of("Content-Disposition"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg); // 或 "/EAMS/**" 視你的 contextPath
        return source;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 靜態資源處理 - 讓頭像可以直接訪問
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    
}
