package io.wispforest.owo.util;

import io.wispforest.owo.mixin.ui.access.EntityAccessor;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Support for showing entities which are not part of a world inside a UI
 */
public final class DisplayEntities {

    private static final AtomicInteger SYNTHETIC_IDS = new AtomicInteger();

    private DisplayEntities() {}

    /**
     * Entities built client-side never get an id assigned - only {@code ServerLevel}
     * hands those out - and as of 26.2 {@link Entity#getId()} throws instead of
     * returning {@code 0}. Renderers call it while extracting render state, where it
     * seeds item model variation, so any display-only entity needs an id of its own.
     * <p>
     * Ids handed out here are negative, so they can never collide with a
     * server-assigned one. Entities which already carry an id are left alone.
     */
    @ApiStatus.Internal
    public static <E extends Entity> E ensureRenderable(E entity) {
        if (entity != null && ((EntityAccessor) entity).owo$getRawId() == 0) {
            entity.setId(SYNTHETIC_IDS.decrementAndGet());
        }

        return entity;
    }
}
