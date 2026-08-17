package io.wispforest.owo.mixin.braid;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.owo.braid.display.BraidDisplayBinding;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    // as of 26.2 block entity submission happens in submitFeatures rather than inside
    // the main pass lambda, and the collector to submit into is passed in directly
    @Inject(
        method = "submitFeatures",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;submitBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            shift = At.Shift.AFTER
        )
    )
    private void renderBraidDisplays(LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, boolean renderOutline, CallbackInfo ci, @Local PoseStack matrixStack) {
        BraidDisplayBinding.renderAutomaticDisplays(matrixStack, levelRenderState.cameraRenderState, submitNodeCollector);
    }
}
