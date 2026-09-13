package dev.dubhe.chinesefestivals.util;

import com.nlf.calendar.Lunar;

public class LunarUtil {
    public static ShenXiao getShenXiao() {
        Lunar lunar = new Lunar();
        String shenXiao = lunar.getYearShengXiao();
        for (ShenXiao value : ShenXiao.values()) {
            if (value.chinese.equals(shenXiao)) return value;
        }
        return ShenXiao.LOONG;
    }

    public enum ShenXiao {
        RAT("鼠"),
        OX("牛"),
        TIGER("虎"),
        RABBIT("兔"),
        LOONG("龙"),
        SNAKE("蛇"),
        HORSE("马"),
        GOAT("羊"),
        MONKEY("猴"),
        ROOSTER("鸡"),
        DOG("狗"),
        PIG("猪");

        public final String chinese;

        ShenXiao(String chinese) {
            this.chinese = chinese;
        }

        public ShenXiao next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
