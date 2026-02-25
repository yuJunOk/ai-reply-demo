package com.example.aireply.component.notification;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @Value("${spring.application.display-name:${spring.application.name}}")
    private String appDisplayName;

    @Value("${mail.manager}")
    private String manageEmail;

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

    public void sendHtmlMailWithImages(String mailTitle, String htmlContent, Map<String, byte[]> inlineImages, String to) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(mailTitle);
            helper.setText(htmlContent, true);

            if (inlineImages != null) {
                for (Map.Entry<String, byte[]> entry : inlineImages.entrySet()) {
                    helper.addInline(entry.getKey(), new ByteArrayResource(entry.getValue()), "image/png");
                }
            }

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送带图片的 HTML 邮件失败", e);
        }
    }

    /**
     * 发送系统异常报告
     */
    public void sendBugReport(Exception e) {
        String title = String.format("%s - 异常上报", appDisplayName);
        String context = String.format("系统名称：%s\n异常详情：%s", appDisplayName, ExceptionUtils.getStackTrace(e));
        sendMail(title, context, manageEmail);
    }
}
