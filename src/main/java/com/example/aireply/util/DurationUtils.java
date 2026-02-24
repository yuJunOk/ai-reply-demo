package com.example.aireply.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * 时长/耗时 可视化工具类
 * 专注于：运行时长、接口耗时、倒计时、视频进度等场景
 * @author 13758
 */
public class DurationUtils {

    /**
     * 场景 1: 智能性能耗时 (Smart Performance)
     * 规则：
     * < 1000ms -> 显示毫秒 (e.g., "125ms")
     * < 60s    -> 显示秒，保留1位小数 (e.g., "1.2s", "59.5s")
     * >= 60s   -> 显示分:秒 (e.g., "1:30", "2:05")
     * 
     * @param millis 毫秒数
     * @return 格式化字符串
     */
    public static String formatSmartDuration(long millis) {
        if (millis < 0) {
            return "0ms";
        }
        
        if (millis < 1000) {
            return millis + "ms";
        } else if (millis < 60000) {
            // 除以1000.0转为秒，保留1位小数
            double seconds = millis / 1000.0;
            // 去掉末尾多余的 .0 (例如 2.0s -> 2s)
            String str = String.format("%.1f", seconds);
            if (str.endsWith(".0")) {
                str = str.substring(0, str.length() - 2);
            }
            return str + "s";
        } else {
            // 超过1分钟，使用 mm:ss 格式
            return DurationFormatUtils.formatDuration(millis, "mm:ss");
        }
    }

    /**
     * 场景 2: 标准视频/任务进度 (Standard HH:MM:SS)
     * 规则：总是显示 时:分:秒，不足补零。
     * 如果超过24小时，会自动累加小时数 (e.g., 25:00:00)，不会进位到天。
     * 
     * @param millis 毫秒数
     * @return e.g., "01:05:30", "00:45:00"
     */
    public static String formatHMS(long millis) {
        if (millis < 0) {
            return "00:00:00";
        }
        // H 表示累计小时 (0-999...)，HH 表示补零
        return DurationFormatUtils.formatDuration(millis, "HH:mm:ss");
    }

    /**
     * 场景 3: 包含天的长时长 (Long Running Time)
     * 规则：显示 "X天 H小时 m分"，通常省略秒，适合展示服务器运行时间等。
     * 
     * @param millis 毫秒数
     * @return e.g., "3天 4小时 15分", "0天 2小时 5分"
     */
    public static String formatDayHm(long millis) {
        if (millis < 0) {
            return "0天 0小时 0分";
        }
        // d:天, H:小时, m:分
        // 注意：DurationFormatUtils 的 d 是总天数，H 是剩余小时
        return DurationFormatUtils.formatDuration(millis, "d天 H小时 m分", true);
    }
    
    /**
     * 场景 3-B: 智能长时长 (Smart Long Duration)
     * 规则：自动隐藏为0的大单位，让字符串更简洁。
     * > 1天 -> "3天 4小时"
     * < 1天 -> "4小时 15分"
     * < 1小时 -> "15分 30秒"
     * 
     * @param millis 毫秒数
     * @return e.g., "3天 4小时", "15分 30秒"
     */
    public static String formatSmartLong(long millis) {
        if (millis < 0) {
            return "0秒";
        }
        if (millis < 1000) {
            return millis + "ms";
        }
        if (millis < 60000) {
            return (millis / 1000) + "秒";
        }

        long days = millis / (24 * 60 * 60 * 1000);
        long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (millis % (60 * 1000)) / 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天 ");
        }
        if (hours > 0 || days > 0) {
            // 如果有天，小时即使是0也显示(可选)，或者只>0显示
            sb.append(hours).append("小时 ");
        }
        if (minutes > 0 || hours > 0 || days > 0) {
            sb.append(minutes).append("分 ");
        }
        
        // 如果前面都没有单位（理论上不会，因为上面有秒的判断），或者为了精确显示秒
        if (sb.isEmpty()) {
            return seconds + "秒";
        }
        
        // 简单处理：如果秒不为0且前面已有内容，可以追加，但通常长时长忽略秒
        // 这里为了简洁，如果已经有分，通常不再显示秒，除非非常精确的需求
        return sb.toString().trim();
    }

    /**
     * 场景 4: 紧凑英文格式 (Compact English)
     * 规则：用于UI空间极小的情况，如 "2h 15m", "3d 4h"
     * 
     * @param millis 毫秒数
     * @return e.g., "2h 15m", "1d 3h 20m"
     */
    public static String formatCompactEn(long millis) {
        if (millis < 0) {
            return "0s";
        }
        
        long days = millis / (24 * 60 * 60 * 1000);
        long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (millis % (60 * 1000)) / 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        
        // 如果没有任何大单位，显示秒
        if (sb.isEmpty()) {
            return seconds + "s";
        }
        
        return sb.toString().trim();
    }

    /**
     * 场景 5: 自然语言描述 (Natural Language)
     * 规则：更口语化，适合提示语。
     * 
     * @param millis 毫秒数
     * @return e.g., "大约3小时", "1小时20分", "刚刚"
     */
    public static String formatNatural(long millis) {
        if (millis < 0) {
            return "0秒";
        }
        if (millis < 5000) {
            // 小于5秒算刚刚
            return "刚刚";
        }

        long days = millis / (24 * 60 * 60 * 1000);
        long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0) {
            sb.append(minutes).append("分");
        }
        
        if (sb.isEmpty()) {
            long seconds = millis / 1000;
            return seconds + "秒";
        }
        
        return sb.toString();
    }
}