package dev.doublekekse.confetti.packet;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ExtendedParticlePacket(
    Vec3Dist posDist,
    Vec3Dist velocityDist,

    int count,
    boolean overrideLimiter,
    ParticleOptions particle
) {
    public static final ResourceLocation TYPE = Confetti.identifier("extended_particle_packet");

    private ExtendedParticlePacket(FriendlyByteBuf friendlyByteBuf) {
        this(
            Vec3Dist.read(friendlyByteBuf),
            Vec3Dist.read(friendlyByteBuf),

            friendlyByteBuf.readInt(),
            friendlyByteBuf.readBoolean(),
            friendlyByteBuf.readWithCodec(ParticleTypes.CODEC)
        );
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        posDist.write(friendlyByteBuf);
        velocityDist.write(friendlyByteBuf);

        friendlyByteBuf.writeInt(count);
        friendlyByteBuf.writeBoolean(overrideLimiter);
        friendlyByteBuf.writeWithCodec(ParticleTypes.CODEC, this.particle);
    }


    public static void handle(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        var payload = new ExtendedParticlePacket(buf);

        for (int i = 0; i < payload.count; i++) {
            client.player.level.addParticle(
                payload.particle,
                payload.overrideLimiter,

                payload.posDist.randomX(),
                payload.posDist.randomY(),
                payload.posDist.randomZ(),

                payload.velocityDist.randomX(),
                payload.velocityDist.randomY(),
                payload.velocityDist.randomZ()
            );
        }
    }
}
