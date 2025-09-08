package com.eams.config.websocket;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 啟用內存中的消息代理，用於向客戶端發送消息
        config.enableSimpleBroker("/topic", "/queue", "/user");
        
        // 設置客戶端向服務器發送消息的前綴
        config.setApplicationDestinationPrefixes("/app");
        
        // 設置用戶目的地前綴
        config.setUserDestinationPrefix("/user");
        
        // 調試輸出
        System.out.println("✅ WebSocket Message Broker 配置完成");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊STOMP端點 - 確保路徑正確
        registry.addEndpoint("/ws")
        .setAllowedOriginPatterns(
                "https://vue-teal-ten.vercel.app",   // Vercel 正式站
                "https://campusplus.xyz",             // 你的後端 domain
                "http://localhost:5173",              // Vue/Vite 本地開發用
                "http://localhost:8080"               // 若有直接前後端同 port，本地 Java
            )  
                .withSockJS();
        
        // 調試輸出
        System.out.println("✅ WebSocket STOMP 端點註冊: /ws");
        System.out.println("🔗 完整的 WebSocket URL: http://localhost:8080/EAMS/ws");
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        
        System.out.println("✅ WebSocket Message Converter 配置完成");
        return false;
    }
}