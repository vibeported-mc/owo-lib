package io.wispforest.owo.mixin.ui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import io.wispforest.owo.ui.renderstate.BlurQuadElementRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @Shadow
    @Nullable
    private TextureSetup previousTextureSetup;

    // as of 26.2, StagedVertexBuffer#getExecuteInfo picks the sequential index buffer
    // based on the draw's primitive topology - the workaround we used to apply here
    // for non-quad owo pipelines is now redundant

    @Inject(
        method = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIIII)V")
    )
    private void drawBlur(GuiRenderer.Draw draw, RenderPass pass, CallbackInfo ci) {
        var blurSetup = BlurQuadElementRenderState.getBlurSetupOf(draw.textureSetup());
        if (blurSetup == null) return;

        var mainBuffer = Minecraft.getInstance().gameRenderer.mainRenderTarget();
        var inputSize = new Vector2i(mainBuffer.width, mainBuffer.height);

        var encoder = RenderSystem.getDevice().createCommandEncoder();

        ((CommandEncoderAccessor) encoder).owo$setInRenderPass(false);
        encoder.copyTextureToTexture(
            Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTexture(),
            BlurQuadElementRenderState.input.getColorTexture(),
            0, 0, 0, 0, 0, inputSize.x, inputSize.y
        );

        var uniforms = BlurQuadElementRenderState.uniforms.write(inputSize, blurSetup.directions(), blurSetup.quality(), blurSetup.size());
        ((CommandEncoderAccessor) encoder).owo$setInRenderPass(true);

        pass.setUniform("BlurSettings", uniforms);
        pass.bindTexture("InputSampler", BlurQuadElementRenderState.inputView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
    }

    @ModifyExpressionValue(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/TextureSetup;equals(Ljava/lang/Object;)Z"))
    private boolean adjustCheckForBlurElements(boolean original, @Local(argsOnly = true) GuiElementRenderState state) {
        return original && !(state instanceof BlurQuadElementRenderState || BlurQuadElementRenderState.hasBlurSetupFor(previousTextureSetup));
    }
}
