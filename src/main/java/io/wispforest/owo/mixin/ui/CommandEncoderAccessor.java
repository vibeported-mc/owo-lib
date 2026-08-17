package io.wispforest.owo.mixin.ui;

import com.mojang.blaze3d.systems.CommandEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * As of 26.2 the render pass guard lives on {@link CommandEncoder} itself rather
 * than on the OpenGL backend, so it can be toggled directly here
 */
@Mixin(CommandEncoder.class)
public interface CommandEncoderAccessor {
    @Accessor("isInRenderPass")
    void owo$setInRenderPass(boolean inRenderPass);
}
