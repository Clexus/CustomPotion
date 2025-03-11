package cn.clexus.customPotion.events;

import cn.clexus.customPotion.effects.CustomEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomPotionApplyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private LivingEntity entity;
    private CustomEffect effect;

    public CustomPotionApplyEvent(LivingEntity entity, CustomEffect effect) {
        this.entity = entity;
        this.effect = effect;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CustomEffect getEffect() {
        return effect;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public void setEffect(CustomEffect effect) {
        this.effect = effect;
    }
}
