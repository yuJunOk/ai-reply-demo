package com.example.aireply.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 全局时间格式化工具类
 * 基于 Java 8+ java.time API，支持 Long/Date/LocalDateTime 多种入参。
 */
public class DateTimeUtils {

    // --- 格式常量 ---
    private static final DateTimeFormatter F_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter F_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter F_NORMAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter F_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter F_DATE_CN = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    private static final DateTimeFormatter F_COMPACT = DateTimeFormatter.ofPattern("MM-dd HH:mm");
    private static final DateTimeFormatter F_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter F_ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    // ================= 公共格式化入口 (支持多种类型) =================

    public static String formatFull(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_FULL);
    }

    public static String formatFullWithMs(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_MS);
    }

    public static String formatNormal(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_NORMAL);
    }

    public static String formatDate(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_DATE);
    }

    public static String formatDateChinese(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_DATE_CN);
    }

    public static String formatCompact(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_COMPACT);
    }

    public static String formatTimeOnly(Object time) {
        LocalDateTime dt = toLocalDateTime(time);
        return dt == null ? "" : dt.format(F_TIME);
    }

    public static String formatIso(Object time) {
        if (time == null) {
            return "";
        }
        try {
            Instant instant = toInstant(time);
            if (instant == null) {
                return "";
            }
            ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, DEFAULT_ZONE);
            return zdt.format(F_ISO);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 人性化相对时间 (刚刚/分钟/小时/昨天/前天/日期)
     */
    public static String formatRelative(Object time) {
        Instant instant = toInstant(time);
        if (instant == null) {
            return "";
        }

        long nowMillis = System.currentTimeMillis();
        long targetMillis = instant.toEpochMilli();
        long diff = nowMillis - targetMillis;

        if (diff < 0) {
            diff = 0;
        }

        long minute = 60 * 1000;
        long hour = 60 * minute;
        long day = 24 * hour;

        if (diff < minute) {
            return "刚刚";
        }
        if (diff < hour) {
            return (diff / minute) + "分钟前";
        }
        if (diff < day) {
            return (diff / hour) + "小时前";
        }

        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        LocalDate target = LocalDate.ofInstant(instant, DEFAULT_ZONE);
        long daysDiff = ChronoUnit.DAYS.between(target, today);

        if (daysDiff == 1) {
            return "昨天";
        }
        if (daysDiff == 2) {
            return "前天";
        }
        if (daysDiff > 2 && daysDiff < 7) {
            return daysDiff + "天前";
        }

        DateTimeFormatter f = (target.getYear() == today.getYear())
                ? DateTimeFormatter.ofPattern("MM-dd")
                : F_DATE;
        return target.format(f);
    }

    // ================= 内部转换核心逻辑 =================

    /**
     * 统一转换为 LocalDateTime
     * 支持: Long, Date, Instant, LocalDateTime, null
     */
    private static LocalDateTime toLocalDateTime(Object time) {
        Instant instant = toInstant(time);
        return instant == null ? null : LocalDateTime.ofInstant(instant, DEFAULT_ZONE);
    }

    /**
     * 统一转换为 Instant
     * 支持: Long, Date, Instant, LocalDateTime, null
     */
    private static Instant toInstant(Object time) {
        if (time == null) {
            return null;
        }
        try {
            if (time instanceof Long) {
                long millis = (Long) time;
                return millis <= 0 ? null : Instant.ofEpochMilli(millis);
            }
            if (time instanceof Date) {
                return ((Date) time).toInstant();
            }
            if (time instanceof Instant) {
                return (Instant) time;
            }
            if (time instanceof LocalDateTime) {
                return ((LocalDateTime) time).atZone(DEFAULT_ZONE).toInstant();
            }
            if (time instanceof ZonedDateTime) {
                return ((ZonedDateTime) time).toInstant();
            }
        } catch (Exception e) {
            // 忽略转换异常，返回 null
        }
        return null;
    }
}