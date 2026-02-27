package com.example.aireply.component.tyme;

import com.example.aireply.component.tyme.model.TymeInfo;
import com.tyme.culture.ren.MinorRen;
import com.tyme.sixtycycle.SixtyCycle;
import com.tyme.solar.SolarDay;
import com.tyme.solar.SolarTime;
import com.tyme.lunar.LunarDay;
import com.tyme.lunar.LunarHour;
import com.tyme.lunar.LunarMonth;
import com.tyme.lunar.LunarYear;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 历法数据采集组件
 * 基于 tyme4j 库提取丰富的传统历法信息
 */
@Component
public class TymeCollector {

    public TymeInfo collect() {
        LocalDateTime now = LocalDateTime.now();
        SolarTime solarTime = SolarTime.fromYmdHms(now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond());
        SolarDay solarDay = solarTime.getSolarDay();
        LunarHour lunarHour = solarTime.getLunarHour();
        LunarDay lunarDay = lunarHour.getLunarDay();
        LunarMonth lunarMonth = lunarDay.getLunarMonth();
        LunarYear lunarYear = lunarMonth.getLunarYear();

        // 干支 SixtyCycle
        SixtyCycle sixtyCycle = lunarDay.getSixtyCycle();

        // 获取小六壬 (从时辰获取)
        MinorRen minorRen = lunarHour.getMinorRen();

        // 获取节日聚合 (公历 + 农历)
        List<String> festivalList = new ArrayList<>();
        if (solarDay.getFestival() != null) {
            festivalList.add(solarDay.getFestival().getName());
        }
        if (lunarDay.getFestival() != null) {
            festivalList.add(lunarDay.getFestival().getName());
        }
        String festivals = festivalList.stream()
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .collect(Collectors.joining(" · "));

        return TymeInfo.builder()
                .lunarYear(lunarYear.getName())
                .lunarMonth(lunarMonth.getName())
                .lunarDay(lunarDay.getName())
                .lunarSeason(lunarMonth.getSeason().getName())
                .animal(lunarYear.getSixtyCycle().getEarthBranch().getZodiac().getName())
                .constellation(solarDay.getConstellation().getName())
                .moonPhase(lunarDay.getPhase().getName())
                .solarTerm(solarDay.getTerm() != null ? solarDay.getTerm().getName() : "无")

                .currentHour(lunarHour.getName())
                .currentHourRange(String.format("%02d:00 - %02d:00",
                        (now.getHour() / 2 * 2 + 23) % 24, (now.getHour() / 2 * 2 + 1) % 24))
                .liuRen(minorRen.getName())
                .liuRenLuck(minorRen.getLuck().getName())
                .liuRenElement(minorRen.getElement().getName())

                .dayYi(lunarDay.getRecommends().stream().map(Object::toString).collect(Collectors.toList()))
                .dayJi(lunarDay.getAvoids().stream().map(Object::toString).collect(Collectors.toList()))
                .hourYi(lunarHour.getRecommends().stream().map(Object::toString).collect(Collectors.toList()))
                .hourJi(lunarHour.getAvoids().stream().map(Object::toString).collect(Collectors.toList()))

                // 彭祖百忌
                .pengZu(sixtyCycle.getPengZu().getPengZuHeavenStem() + "，" + sixtyCycle.getPengZu().getPengZuEarthBranch())
                // 五行：这里取“纳音五行”，更有传统味道
                .wuXing(sixtyCycle.getEarthBranch().getElement().getName())
                .baZi(lunarHour.getEightChar().getName())
                .festivals(festivals.isEmpty() ? "无" : festivals)
                .week("星期" + solarDay.getWeek().getName())
                .build();
    }
}