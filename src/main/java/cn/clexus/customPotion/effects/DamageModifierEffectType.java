package cn.clexus.customPotion.effects;

import org.bukkit.event.entity.EntityDamageEvent;

public interface DamageModifierEffectType {
    /**
     * 修改伤害值
     *
     * @param event 伤害事件
     */
    void modifyDamage(EntityDamageEvent event, CustomEffect effect);
}
