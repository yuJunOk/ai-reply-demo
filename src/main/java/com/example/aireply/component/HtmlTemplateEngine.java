package com.example.aireply.component;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 模板渲染组件：支持变量替换和简单的 [IF:key] 条件渲染
 */
@Component
public class HtmlTemplateEngine {

    public String render(String templatePath, Map<String, String> variables) {
        String template = loadTemplate(templatePath);

        // 1. 处理 IF 标签逻辑
        for (String key : variables.keySet()) {
            String ifTag = "[IF:" + key + "]";
            String endTag = "[/IF]";
            boolean keep = "true".equalsIgnoreCase(variables.get(key));

            while (template.contains(ifTag)) {
                int start = template.indexOf(ifTag);
                int end = template.indexOf(endTag, start);
                if (end == -1) break;

                String content = keep ? template.substring(start + ifTag.length(), end) : "";
                template = template.substring(0, start) + content + template.substring(end + endTag.length());
            }
        }

        // 2. 替换常规变量
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
        }
        return template;
    }

    private String loadTemplate(String path) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new IllegalStateException("Template not found: " + path);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load template", e);
        }
    }
}