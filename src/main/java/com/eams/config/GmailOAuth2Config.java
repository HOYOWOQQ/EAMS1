//package com.eams.config;
//
//import org.springframework.context.annotation.Configuration;
//
///**
// * Gmail OAuth2 配置類
// * 
// * 注意：Gmail 服務現在透過 GmailOAuth2Service 的懶加載方式初始化，
// * 不再需要在這裡建立 Bean。
// * 
// * 所有的 OAuth2 配置參數都透過 @Value 註解在 GmailOAuth2Service 中直接注入。
// */
//@Configuration
//public class GmailOAuth2Config {
//    
//    // 這個類別現在只是一個標記，實際的 Gmail 服務初始化
//    // 已移至 GmailOAuth2Service 中的懶加載方式
//    
//}