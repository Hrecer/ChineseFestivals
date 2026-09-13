package dev.dubhe.chinesefestivals.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import dev.dubhe.chinesefestivals.ChineseFestivals;
import dev.dubhe.chinesefestivals.client.model.LoongBoatModel;
import dev.dubhe.chinesefestivals.features.Features;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BoatRenderer.class)
public class BoatRendererMixin {
    @Unique
    private static final String DEFAULT_LAYER = "main";
    @Unique
    private static ListModel<Boat> chineseFestivals$model = null;
    @Unique
    private boolean chineseFestivals$hasChest = false;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void init(EntityRendererProvider.@NotNull Context context, boolean bl, CallbackInfo ci) {
        ModelLayerLocation modelLayerLocation = LoongBoatModel.LAYER_LOCATION;
        ModelPart modelPart = context.bakeLayer(modelLayerLocation);
        BoatRendererMixin.chineseFestivals$model = new LoongBoatModel(modelPart);
        this.chineseFestivals$hasChest = bl;
    }

    /**
     * Forge 对 {@code BoatRenderer} 打了补丁：把 "取模型与贴图" 抽成了公开方法
     * {@code BoatRenderer#getModelWithLocation(Boat)}，原版 {@code render} 里内联的那条
     * {@code Map#get} 指令在 Forge 上被搬进了这个方法，因此必须在 Forge 上注入这个方法。
     * 原版/Fabric 没有该方法，{@code require = 0} 保证静默跳过而不是崩溃。
     */
    @Inject(
        method = "getModelWithLocation",
        at = @At("HEAD"),
        cancellable = true,
        require = 0,
        remap = false
    )
    private void chineseFestivals$getModelWithLocation(Boat boat, CallbackInfoReturnable<Pair<ResourceLocation, ListModel<Boat>>> cir) {
        Pair<ResourceLocation, ListModel<Boat>> replace = this.chineseFestivals$getLoongBoatModel(boat.getVariant());
        if (replace != null) cir.setReturnValue(replace);
    }

    /**
     * 原版/Fabric 的 {@code render} 直接内联了 {@code boatResources.get(...)}，这里保持原实现。
     * Forge 上该指令已不存在，{@code require = 0} 保证静默跳过，而不是抛出 InjectionError 让游戏崩溃。
     */
    @SuppressWarnings("unchecked")
    @Redirect(
        method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
        remap = false,
        require = 0
    )
    private <K, V> V get(Map<K, V> instance, K key) {
        Pair<ResourceLocation, ListModel<Boat>> replace = this.chineseFestivals$getLoongBoatModel(key);
        if (replace != null) return (V) replace;
        return instance.get(key);
    }

    @Inject(
        method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V")
    )
    private void render(@NotNull Boat boat, float f, float g, @NotNull PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!this.chineseFestivals$hasChest && boat.getVariant() != Boat.Type.BAMBOO && Features.LOONG_BOAT.get().isNow()) {
            poseStack.translate(0.0, 1.0, 0.0);
            poseStack.rotateAround(Axis.YP.rotationDegrees(90), 0.0f, 0.0f, 0.0f);
        }
    }

    @Unique
    private @Nullable Pair<ResourceLocation, ListModel<Boat>> chineseFestivals$getLoongBoatModel(@Nullable Object key) {
        if (this.chineseFestivals$hasChest || BoatRendererMixin.chineseFestivals$model == null) return null;
        if (!(key instanceof Boat.Type type) || type == Boat.Type.BAMBOO) return null;
        if (!Features.LOONG_BOAT.get().isNow()) return null;
        return new Pair<>(ChineseFestivals.of("textures/entity/loong_boat.png"), BoatRendererMixin.chineseFestivals$model);
    }
}
