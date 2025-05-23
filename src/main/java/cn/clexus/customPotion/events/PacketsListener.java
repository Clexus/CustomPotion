package cn.clexus.customPotion.events;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;

public class PacketsListener implements PacketListener {
    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.PARTICLE) {
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);
            if (packet.getParticle().getType() == ParticleTypes.DAMAGE_INDICATOR) {
                event.setCancelled(true);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
            WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(event);
            if (packet.getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.CRITICAL_HIT) {
                event.setCancelled(true);
            }
        }
    }
}
