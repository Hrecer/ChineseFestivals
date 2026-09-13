package dev.dubhe.chinesefestivals.features.impl;

import com.nlf.calendar.Lunar;
import dev.dubhe.chinesefestivals.features.Feature;
import dev.dubhe.chinesefestivals.festivals.Festivals;
import dev.dubhe.chinesefestivals.festivals.IFestival;
import dev.dubhe.chinesefestivals.util.BitMap;
import dev.dubhe.chinesefestivals.util.LunarUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Fireworks extends Feature {

    public Fireworks(String id, IFestival... enableTimes) {
        super(id, Festivals.CHINESE_SPRING_FESTIVAL);
        if (enableTimes.length > 0) {
            super.enableTimes.clear();
            super.enableTimes.addAll(List.of(enableTimes));
        }
    }

    @Override
    public double[][] getFireworkParticle() {
        LunarUtil.ShenXiao shenXiao = getShenXiao();
        //noinspection SwitchStatementWithTooFewBranches
        return switch (shenXiao) {
            case HORSE -> BitMap.HORSE;
            default -> BitMap.LOONG;
        };
    }

    private LunarUtil.ShenXiao getShenXiao() {
        Lunar lunar = new Lunar();
        LunarUtil.ShenXiao shenXiao = LunarUtil.getShenXiao();
        if (lunar.getMonth() == 12) return shenXiao.next();
        return shenXiao;
    }

    @Override
    public Map<String, Supplier<String>> getTranslationReplace() {
        LunarUtil.ShenXiao shenXiao = getShenXiao();
        Map<String, Supplier<String>> map = Collections.synchronizedMap(new HashMap<>());
        map.put("item.minecraft.firework_star.shape.creeper", () -> "item.firework_star.shape.%s".formatted(shenXiao.name().toLowerCase()));
        return map;
    }
}
