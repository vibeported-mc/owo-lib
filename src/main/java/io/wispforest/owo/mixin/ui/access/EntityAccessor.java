package io.wispforest.owo.mixin.ui.access;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * As of 26.2 {@link Entity#getId()} throws when no id has been assigned, which is the
 * case for every entity built client-side - reading the raw field lets us tell whether
 * an entity still needs a synthetic one
 */
@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("id")
    int owo$getRawId();
}
