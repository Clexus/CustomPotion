package cn.clexus.customPotion.Events;

import cn.clexus.customPotion.effects.CustomEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomPotionRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final CustomEffect effect;
    private final LivingEntity entity;

    public CustomPotionRemoveEvent(LivingEntity entity, CustomEffect effect) {
        this.effect = effect;
        this.entity = entity;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public CustomEffect getEffect() {
        return effect;
    }

    public LivingEntity getEntity() {
        return entity;
    }

}
