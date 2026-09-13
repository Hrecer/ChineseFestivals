package dev.dubhe.chinesefestivals.features.impl;

import dev.dubhe.chinesefestivals.features.Feature;
import dev.dubhe.chinesefestivals.features.IFeature;
import dev.dubhe.chinesefestivals.festivals.Festivals;
import dev.dubhe.chinesefestivals.festivals.IFestival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Couplets extends Feature {
    private static final PaintingVariant COUPLET_LEFT = IFeature.registerPainting("couplet_left", 16, 32);
    private static final PaintingVariant COUPLET_RIGHT = IFeature.registerPainting("couplet_right", 16, 32);
    private static final PaintingVariant COUPLET_TOP = IFeature.registerPainting("couplet_top", 32, 16);
    private static final PaintingVariant COUPLET_FU = IFeature.registerPainting("couplet_fu", 32, 32);
    public Couplets(String id, IFestival... enableTimes) {
        super(id, Festivals.CHINESE_SPRING_FESTIVAL);
        if (enableTimes.length > 0) {
            super.enableTimes.clear();
            super.enableTimes.addAll(List.of(enableTimes));
        }
    }

    @Override
    public PaintingVariant getPaintingReplace(Painting painting) {
        ResourceLocation location = painting.getVariant().unwrapKey().orElse(PaintingVariants.KEBAB).location();
        if ("minecraft".equals(location.getNamespace())) {
            switch (location.getPath()) {
                case "graham":
                    return COUPLET_LEFT;
                case "wanderer":
                    return COUPLET_RIGHT;
                case "creebet":
                    return COUPLET_TOP;
                case "bust":
                    return COUPLET_FU;
            }
        }
        return null;
    }

    @Override
    public Map<String, Supplier<String>> getTranslationReplace() {
        Map<String, Supplier<String>> map = new HashMap<>();
        map.put("painting.minecraft.graham.title", () -> "painting.chinesefestivals.couplet_left.title");
        map.put("painting.minecraft.graham.author", () -> "painting.chinesefestivals.couplet_left.author");
        map.put("painting.minecraft.wanderer.title", () -> "painting.chinesefestivals.couplet_right.title");
        map.put("painting.minecraft.wanderer.author", () -> "painting.chinesefestivals.couplet_right.author");
        map.put("painting.minecraft.creebet.title", () -> "painting.chinesefestivals.couplet_top.title");
        map.put("painting.minecraft.creebet.author", () -> "painting.chinesefestivals.couplet_top.author");
        map.put("painting.minecraft.bust.title", () -> "painting.chinesefestivals.couplet_fu.title");
        map.put("painting.minecraft.bust.author", () -> "painting.chinesefestivals.couplet_fu.author");
        return map;
    }
}
