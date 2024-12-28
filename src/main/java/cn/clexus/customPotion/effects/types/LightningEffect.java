package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;

import static cn.clexus.customPotion.CustomPotion.plugin;

public class LightningEffect extends CustomEffectType {
    public LightningEffect() {
        super("lightning","雷霆");
    }
    @Override
    public void applyEffect(LivingEntity target, CustomEffect effect) {
        int amplifier = effect.getAmplifier();
        LivingEntity source = effect.getSource();
        double damage = amplifier * 5.0;
        DamageSource damageSource = DamageSource.builder(DamageType.LIGHTNING_BOLT).withDamageLocation(target.getLocation()).build();
        if(target.getNoDamageTicks()==0){
            target.getWorld().strikeLightningEffect(target.getLocation());
            if(source != null){
                DamageSource sourceSource = DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(source).withCausingEntity(source).build();
                target.damage(1,sourceSource);
            }
            Bukkit.getScheduler().runTaskLater(plugin,()->{
                target.damage(damage, damageSource);
            },20L);
        }
    }
}
