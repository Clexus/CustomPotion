package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DeathEffect extends CustomEffectType {
    public DeathEffect() {
        super("death","终焉");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        ParticleBuilder particleBuilder = new ParticleBuilder(Particle.ENTITY_EFFECT);
        particleBuilder.color(0,0,0).count(5).offset(0.2,0.5,0.2).location(entity.getLocation().add(0,0.5,0)).allPlayers().spawn();
        if(effect.getDuration()%20==0&&effect.getDuration()<=200){
            if(entity instanceof Player){
                ((Player) entity).sendTitle("§4"+effect.getDuration()/20,"",5,10,5);
            }
        }
        if(effect.getDuration()%40==0&&effect.getDuration()<=200) {
            entity.getWorld().playSound(entity,Sound.BLOCK_BELL_USE,0.12f - (float) effect.getDuration() / 2000,0);
        }
        if(effect.getDuration()==1){
            entity.getWorld().playSound(entity,Sound.ENTITY_WARDEN_DEATH,1,1);
            ParticleBuilder particleBuilder2 = new ParticleBuilder(Particle.SCULK_SOUL);
            particleBuilder2.extra(0.1).count(50).offset(0.2,0.5,0.2).location(entity.getLocation().add(0,0.5,0)).allPlayers().spawn();
            if(effect.getSource()!=null){
                entity.setNoDamageTicks(0);
                entity.damage(entity.getAttribute(Attribute.MAX_HEALTH).getValue()*100,effect.getSource());
                if(!entity.isDead()){
                    entity.setHealth(0);
                }
            }else{
                entity.setHealth(0);
            }
        }
    }

    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {

    }
}
