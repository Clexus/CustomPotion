package cn.clexus.customPotion.effects.types;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.utils.DamageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.potion.PotionTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PoisonEffect extends CustomEffectType {

    public PoisonEffect() {
        super("poison");
    }

    @Override
    public void applyEffect(LivingEntity entity, CustomEffect effect) {
        int amplifier = effect.getAmplifier();
        LivingEntity source = effect.getSource();
        if(entity instanceof Player){
            sendPacket(entity);
        }
        DamageUtil.damage(amplifier, DamageType.INDIRECT_MAGIC, entity, source, effect, effect1 -> effect1.setAmplifier(amplifier-1),null,null);
    }
    @Override
    public void onRemove(LivingEntity entity, CustomEffect effect) {
        if(entity instanceof Player){
            WrapperPlayServerRemoveEntityEffect removePacket = new WrapperPlayServerRemoveEntityEffect(entity.getEntityId(), PotionTypes.POISON);
            PacketEvents.getAPI().getPlayerManager().sendPacket(entity, removePacket);
        }
    }
    public void sendPacket(LivingEntity entity) {
        WrapperPlayServerEntityEffect addPacket = new WrapperPlayServerEntityEffect(entity.getEntityId(), PotionTypes.POISON,0,10, (byte) 0);
        PacketEvents.getAPI().getPlayerManager().sendPacket(entity, addPacket);
    }
}
