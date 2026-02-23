package com.example.aireply.component.notification;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @email: pengyujun53@163.com
 * @author: peng_YuJun
 * @date: 2023/1/9
 * @time: 22:07
 */
@Slf4j
@Component
public class EmailSender {
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.application.name}")
    private String systemName;

    private static final String BUG_MAIL_TITLE = "[BUG] 系统异常";
    private static final String MANAGE_EMAIL = "1375841038@qq.com";

    public void sendMail(String mailTitle, String context, String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(mailTitle);
            message.setText(context);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送异常", e);
        }
    }

    public void sendHtmlMail(String mailTitle, String htmlContent, String to) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(mailTitle);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("HTML邮件发送异常", e);
        }
    }

    public void sendBugReport(Exception e) {
        String content = "发生未处理异常，请及时排查：\n\n" + ExceptionUtils.getStackTrace(e);
        sendMail(systemName + BUG_MAIL_TITLE, content, MANAGE_EMAIL);
    }
}
