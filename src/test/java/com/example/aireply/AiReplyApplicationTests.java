package com.example.aireply;

import com.example.aireply.component.notification.EmailSender;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiReplyApplicationTests {

    @Resource
    private EmailSender emailSender;

    @Test
    void contextLoads() {
        emailSender.sendBugReport(new Exception("hello"));
    }

}
