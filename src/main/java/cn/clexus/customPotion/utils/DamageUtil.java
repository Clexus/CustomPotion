package cn.clexus.customPotion.utils;

import cn.clexus.customPotion.effects.CustomEffect;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

public class DamageUtil {
    public static void damage(double damage, DamageType damageType, LivingEntity target, LivingEntity source, CustomEffect effect, Consumer<CustomEffect> effectConsumer, Consumer<LivingEntity> targetConsumer, Consumer<LivingEntity> sourceConsumer) {
        DamageSource damageSource;
        if(source != null) {
            damageSource = DamageSource.builder(damageType).withDamageLocation(target.getLocation()).withCausingEntity(source).withDirectEntity(source).build();
        }else{
            damageSource = DamageSource.builder(damageType).withDamageLocation(target.getLocation()).build();
        }
        if(target.getNoDamageTicks()==0) {
            if(effectConsumer != null) {
                effectConsumer.accept(effect);
            }
            if(targetConsumer != null) {
                targetConsumer.accept(target);
            }
            if(sourceConsumer != null) {
                sourceConsumer.accept(source);
            }
            target.damage(damage,damageSource);
        }
    }
    public static void damage(double damage, DamageType damageType, LivingEntity target, LivingEntity source, CustomEffect effect) {
        damage(damage, damageType, target, source, effect, null, null, null);
    }
}
