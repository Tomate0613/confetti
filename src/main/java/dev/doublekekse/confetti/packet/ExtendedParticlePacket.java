package dev.doublekekse.confetti.packet;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ExtendedParticlePacket(
    Vec3Dist posDist,
    Vec3Dist velocityDist,

    int count,
    boolean overrideLimiter,
    ParticleOptions particle
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtendedParticlePacket> STREAM_CODEC = CustomPacketPayload.codec(ExtendedParticlePacket::write, ExtendedParticlePacket::new);
    public static final Type<ExtendedParticlePacket> TYPE = new Type<>(Confetti.identifier("extended_particle_packet"));

    private ExtendedParticlePacket(RegistryFriendlyByteBuf friendlyByteBuf) {
        this(
            Vec3Dist.read(friendlyByteBuf),
            Vec3Dist.read(friendlyByteBuf),

            friendlyByteBuf.readInt(),
            friendlyByteBuf.readBoolean(),
            ParticleTypes.STREAM_CODEC.decode(friendlyByteBuf)
        );
    }

    private void write(RegistryFriendlyByteBuf friendlyByteBuf) {
        posDist.write(friendlyByteBuf);
        velocityDist.write(friendlyByteBuf);

        friendlyByteBuf.writeInt(count);
        friendlyByteBuf.writeBoolean(overrideLimiter);
        ParticleTypes.STREAM_CODEC.encode(friendlyByteBuf, this.particle);
    }


    public static void handle(ExtendedParticlePacket payload, ClientPlayNetworking.Context context) {
        for (int i = 0; i < payload.count; i++) {
            context.player().level().addParticle(
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


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
