package com.eams.utils;

import org.springframework.stereotype.Component;

@Component
public class MailUtil {
	
    /** 假發送驗證信（console顯示） */
    public void fakeSendVerifyMail(String email, String token) {
        System.out.println("=== 假驗證信 ===");
        System.out.println("收件者：" + email);
        System.out.println("驗證連結：http://localhost:8080/EduManage/verifyEmail?token=" + token);
        System.out.println("================");
    }

    /** 假發送重設密碼信（console顯示） */
    public void fakeSendResetPwdMail(String email, String token, int id) {
        System.out.println("=== 假重設密碼信 ===");
        System.out.println("收件者：" + email);
        System.out.println("重設連結：http://localhost:8080/EduManage/resetPassword?token=" + token + "&id=" + id);
        System.out.println("===================");
    }

}
