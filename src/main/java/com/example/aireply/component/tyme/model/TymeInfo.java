package com.example.aireply.component.tyme.model;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * 历法信息业务对象
 * 封装农历、时辰、吉凶、宜忌等信息
 */
@Getter
@Builder
public class TymeInfo {
    // --- 天时背景 ---
    private String lunarYear;        // 甲辰年
    private String lunarMonth;       // 丙寅月
    private String lunarDay;         // 壬申日
    private String lunarSeason;      // 孟春
    private String animal;           // 生肖: 龙
    private String constellation;    // 星座: 双鱼
    private String moonPhase;        // 月相: 弦月
    private String solarTerm;        // 节气: 惊蛰

    // --- 此时此刻 (当前时辰) ---
    private String currentHour;      // 辰时
    private String currentHourRange; // 07:00 - 09:00
    private String liuRen;           // 小六壬: 大安
    private String liuRenLuck;       // 小六壬吉凶: 吉
    private String liuRenElement;    // 小六壬五行: 木
    
    // --- 宜忌决策 ---
    private List<String> dayYi;      // 今日宜
    private List<String> dayJi;      // 今日忌
    private List<String> hourYi;     // 此时宜
    private List<String> hourJi;     // 此时忌
    private String pengZu;           // 彭祖百忌

    // --- 文化属性 ---
    private String wuXing;           // 五行: 剑锋金
    private String baZi;             // 八字: 甲辰 丙寅 壬申
    private String festivals;        // 节日聚合
    private String week;             // 星期
}