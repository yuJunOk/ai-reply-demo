package com.example.aireply.component.camera;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 树莓派摄像头采集组件
 * 基于 libcamera-still 命令行工具实现
 */
@Slf4j
@Component
public class RaspberryPiCameraCollector {

    /**
     * 判断是否为树莓派环境且摄像头可用
     */
    public boolean isCameraAvailable() {
        if (!isLinux()) {
            return false;
        }

        try {
            // 检查 libcamera 是否能识别到摄像头
            Process process = Runtime.getRuntime().exec("libcamera-still --list-cameras");
            process.waitFor(5, TimeUnit.SECONDS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Available cameras")) {
                    // 如果列表不为空，则认为摄像头可用
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to check camera availability: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 采集照片并返回字节数组
     */
    public byte[] capturePhoto() {
        if (!isCameraAvailable()) {
            log.info("Raspberry Pi camera is not available, skip capturing.");
            return null;
        }

        File tempFile = new File(System.getProperty("java.io.tmpdir"), "rpi_camera_" + UUID.randomUUID() + ".jpg");
        try {
            // 使用 libcamera-still 拍照
            // -t 1000: 等待 1 秒(曝光/对焦)
            // --width 1280 --height 720: 指定分辨率，防止内存溢出或邮件过大
            // --immediate: 立即拍照（如果不需要预热）
            // -o: 输出文件
            String command = String.format("libcamera-still -t 1000 --width 1280 --height 720 -o %s", tempFile.getAbsolutePath());
            log.info("Executing camera command: {}", command);
            
            Process process = Runtime.getRuntime().exec(command);
            boolean finished = process.waitFor(15, TimeUnit.SECONDS);
            
            if (finished && process.exitValue() == 0 && tempFile.exists()) {
                byte[] data = Files.readAllBytes(tempFile.toPath());
                log.info("Photo captured successfully, size: {} bytes", data.length);
                return data;
            } else {
                log.error("Camera capture failed or timed out. Exit value: {}", process.exitValue());
            }
        } catch (Exception e) {
            log.error("Error during camera capture: ", e);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        return null;
    }

    private boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}