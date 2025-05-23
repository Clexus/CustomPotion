package cn.clexus.customPotion.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerCollideEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Entity collidedEntity;

    public PlayerCollideEvent(Player player, LivingEntity collidedEntity) {
        this.player = player;
        this.collidedEntity = collidedEntity;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getCollidedEntity() {
        return collidedEntity;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
