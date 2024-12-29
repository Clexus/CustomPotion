package cn.clexus.customPotion.Events;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.StackingModes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomPotionAddEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private CustomEffect effect;
    private LivingEntity entity;
    private StackingModes mode;

    public CustomPotionAddEvent(LivingEntity entity, CustomEffect effect, StackingModes mode) {
        this.effect = effect;
        this.entity = entity;
        this.mode = mode;
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

    public StackingModes getMode() {
        return mode;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }
    public void setEffect(CustomEffect effect) {
        this.effect = effect;
    }
    public void setMode(StackingModes mode) {
        this.mode = mode;
    }
}
