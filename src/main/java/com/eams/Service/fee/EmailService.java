package com.eams.Service.fee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service("feeEmailService")
public class EmailService {

	
    @Autowired
    private JavaMailSender mailSender;

    public void sendPaymentLinkEmail(String toEmail, String noticeNo, String paymentUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("💰 學費繳費通知單 - " + noticeNo);
        message.setText("您好，您的繳費單已建立，請點擊以下連結完成付款：\n\n" +
                paymentUrl + "\n\n" +
                "※ 此連結僅限使用一次，有效時間為 1 小時。");

        mailSender.send(message);
    }
    
    
    /**
     * 發送 HTML 格式的繳費連結郵件
     * @param toEmail 收件人 Email
     * @param noticeNo 通知單編號
     * @param paymentUrl 完整的繳費連結 URL
     */
    public void sendPaymentLinkHtmlEmail(String toEmail, String noticeNo, String paymentUrl) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            // true 表示啟用 multipart 模式 (可以包含附件或 HTML 內容)
            // "utf-8" 設定字符編碼
            helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setFrom("eeit202.05@gmail.com"); // 建議設定發件人
            helper.setTo(toEmail);
            helper.setSubject("💰 學費繳費通知單 - " + noticeNo);

            // HTML 內容
            String htmlContent = String.format(
                "<!DOCTYPE html>" +
                "<html lang=\"zh-TW\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>繳費通知單</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9; }" +
                "        .header { background-color: #4CAF50; color: white; padding: 10px 20px; text-align: center; border-radius: 8px 8px 0 0; }" +
                "        .content { padding: 20px; }" +
                "        .button { display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }" +
                "        .footer { margin-top: 30px; font-size: 0.8em; color: #777; text-align: center; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"container\">" +
                "        <div class=\"header\">" +
                "            <h2>學費繳費通知</h2>" +
                "        </div>" +
                "        <div class=\"content\">" +
                "            <p>您好，</p>" +
                "            <p>您的繳費單已建立，通知單編號：<strong>%s</strong>。</p>" +
                "            <p>請點擊以下連結完成付款：</p>" +
                "            <p style=\"text-align: center;\"><a href=\"%s\" class=\"button\">點此前往繳費</a></p>" +
                "            <p>※ 此連結僅限使用一次，有效時間為 1 小時。</p>" +
                "            <p>如果您有任何問題，請隨時聯繫我們。</p>" +
                "            <p>謝謝！</p>" +
                "            <p>智慧校園事務平台</p>" +
                "        </div>" +
                "        <div class=\"footer\">" +
                "            <p>&copy; %d 版權所有</p>" + // %d 會被替換為當前年份
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>",
                noticeNo, paymentUrl, java.time.Year.now().getValue() // 傳入 noticeNo, paymentUrl 和當前年份
            );

            helper.setText(htmlContent, true); // true 表示內容是 HTML

            mailSender.send(mimeMessage);
            System.out.println("HTML 繳費連結郵件已成功發送至 Mailtrap!");
        } catch (MessagingException | MailException e) {
            System.err.println("HTML 繳費連結郵件發送失敗: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PostConstruct
    public void init() {
        System.out.println("mailSender 注入結果: " + mailSender);
    }
    
}