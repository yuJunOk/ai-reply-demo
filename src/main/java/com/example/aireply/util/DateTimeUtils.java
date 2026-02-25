package com.example.aireply.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 全局时间格式化工具类
 * <p>
 * 覆盖场景：日志、展示、相对时间、紧凑格式、时长转换等。
 * 1. 工具类禁止实例化（私有构造器）
 * 2. 避免魔法值（统一使用静态常量）
 * 3. 时间处理推荐使用 java.time (Java 8+)
 * 4. 完善的 Javadoc 注释
 * </p>
 *
 * @author AI Assistant
 */
public class DateTimeUtils {

    // --- 常用格式定义 (避免魔法值) ---

    /**
     * 精确时间 (不含毫秒): yyyy-MM-dd HH:mm:ss
     * 场景: 一般业务展示、列表时间
     */
    private static final DateTimeFormatter FORMAT_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 精确时间 (含毫秒): yyyy-MM-dd HH:mm:ss.SSS
     * 场景: 日志、调试、高精度记录
     */
    private static final DateTimeFormatter FORMAT_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * 标准展示时间: yyyy-MM-dd HH:mm
     * 场景: 移动端列表、非精确时间展示
     */
    private static final DateTimeFormatter FORMAT_NORMAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 纯日期: yyyy-MM-dd
     * 场景: 生日、归档、日期选择器
     */
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 中文纯日期: yyyy年MM月dd日
     * 场景: 证书、正式文档、中文界面
     */
    private static final DateTimeFormatter FORMAT_DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    /**
     * 紧凑格式: MM-dd HH:mm
     * 场景: 通知栏、空间受限处
     */
    private static final DateTimeFormatter FORMAT_COMPACT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    /**
     * 仅时间格式: HH:mm
     * 场景: 图表 X 轴
     */
    private static final DateTimeFormatter FORMAT_TIME_ONLY = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * ISO 8601 格式
     * 场景: 前后端交互、标准 API 响应
     */
    private static final DateTimeFormatter FORMAT_ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 默认时区
     * 建议：生产环境明确指定，如 ZoneId.of("Asia/Shanghai")
     * 此处使用系统默认时区以适配服务器环境
     */
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    /**
     * 私有构造器，防止实例化
     */
    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    // ================= 核心格式化方法 =================

    /**
     * 场景 1: 标准精确时间 (不含毫秒)
     * <p>
     * 格式: yyyy-MM-dd HH:mm:ss
     * 示例: 2026-02-24 20:23:15
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串，若时间戳无效则返回空字符串
     */
    public static String formatFull(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_FULL);
        } catch (Exception e) {
            // 记录日志或返回空，视具体业务而定，这里返回空保证健壮性
            return "";
        }
    }

    /**
     * 场景 1-B: 高精度时间 (含毫秒)
     * <p>
     * 格式: yyyy-MM-dd HH:mm:ss.SSS
     * 示例: 2026-02-24 20:23:15.123
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串，若时间戳无效则返回空字符串
     */
    public static String formatFullWithMs(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_MS);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 场景 2: 标准展示时间
     * <p>
     * 格式: yyyy-MM-dd HH:mm
     * 示例: 2026-02-24 20:23
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串，若时间戳无效则返回空字符串
     */
    public static String formatNormal(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_NORMAL);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 场景 3: 纯日期
     * <p>
     * 格式: yyyy-MM-dd
     * 示例: 2026-02-24
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串，若时间戳无效则返回空字符串
     */
    public static String formatDate(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDate dt = LocalDate.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_DATE);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 场景 3-B: 中文纯日期
     * <p>
     * 格式: yyyy年MM月dd日
     * 示例: 2026年02月24日
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串，若时间戳无效则返回空字符串
     */
    public static String formatDateChinese(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDate dt = LocalDate.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_DATE_CN);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 场景 4: 相对时间/人性化时间
     * <p>
     * 规则:
     * < 1分钟 -> "刚刚"
     * < 1小时 -> "X分钟前"
     * < 24小时 -> "X小时前"
     * = 昨天 -> "昨天"
     * = 前天 -> "前天"
     * 3-7天 -> "X天前"
     * > 7天 -> "MM-dd" (今年) 或 "yyyy-MM-dd" (往年)
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 人性化时间字符串
     */
    public static String formatRelative(long millis) {
        if (millis <= 0) {
            return "";
        }

        long now = System.currentTimeMillis();
        long diff = now - millis;

        // 处理未来时间
        if (diff < 0) {
            diff = 0;
        }

        long minute = 60 * 1000;
        long hour = 60 * minute;
        long day = 24 * hour;

        if (diff < minute) {
            return "刚刚";
        } else if (diff < hour) {
            return (diff / minute) + "分钟前";
        } else if (diff < day) {
            return (diff / hour) + "小时前";
        }

        // 计算自然天差异
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        LocalDate targetDate;
        try {
            targetDate = LocalDate.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
        } catch (Exception e) {
            return "";
        }

        long daysDiff = ChronoUnit.DAYS.between(targetDate, today);

        if (daysDiff == 1) {
            return "昨天";
        } else if (daysDiff == 2) {
            return "前天";
        } else if (daysDiff > 2 && daysDiff < 7) {
            return daysDiff + "天前";
        } else {
            // 超过一周或当天（极端情况）
            if (targetDate.getYear() == today.getYear()) {
                return targetDate.format(DateTimeFormatter.ofPattern("MM-dd"));
            } else {
                return targetDate.format(FORMAT_DATE);
            }
        }
    }

    /**
     * 场景 5: 紧凑格式
     * <p>
     * 格式: MM-dd HH:mm
     * 示例: 02-24 20:23
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的字符串
     */
    public static String formatCompact(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return dt.format(FORMAT_COMPACT);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化 LocalDateTime 为 HH:mm
     *
     * @param time LocalDateTime 对象
     * @return 格式化后的字符串
     */
    public static String formatTimeOnly(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(FORMAT_TIME_ONLY);
    }

    /**
     * 场景 6: ISO 8601 格式
     * <p>
     * 格式: 2026-02-24T20:23:15+08:00
     * </p>
     *
     * @param millis 毫秒时间戳
     * @return ISO 格式字符串
     */
    public static String formatIso(long millis) {
        if (millis <= 0) {
            return "";
        }
        try {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), DEFAULT_ZONE);
            return zdt.format(FORMAT_ISO);
        } catch (Exception e) {
            return "";
        }
    }
}