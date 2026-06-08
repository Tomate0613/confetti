package dev.doublekekse.confetti.packet;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ExtendedParticlePacket(
    Vec3Dist posDist,
    Vec3Dist velocityDist,

    int count,
    boolean overrideLimiter,
    boolean alwaysShow,
    ParticleOptions particle
) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtendedParticlePacket> STREAM_CODEC = StreamCodec.composite(
        Vec3Dist.STREAM_CODEC, ExtendedParticlePacket::posDist,
        Vec3Dist.STREAM_CODEC, ExtendedParticlePacket::velocityDist,
        ByteBufCodecs.INT, ExtendedParticlePacket::count,
        ByteBufCodecs.BOOL, ExtendedParticlePacket::overrideLimiter,
        ByteBufCodecs.BOOL, ExtendedParticlePacket::alwaysShow,
        ParticleTypes.STREAM_CODEC, ExtendedParticlePacket::particle,
        ExtendedParticlePacket::new
    );

    public static final Type<ExtendedParticlePacket> TYPE = new Type<>(Confetti.identifier("extended_particle_packet"));

    public ExtendedParticlePacket(
        Vec3Dist posDist,
        Vec3Dist velocityDist,

        int count,
        boolean overrideLimiter,
        ParticleOptions particle
    ) {
        this(
            posDist, velocityDist, count, overrideLimiter, false, particle
        );
    }

    public static void handle(ExtendedParticlePacket payload, ClientPlayNetworking.Context context) {
        for (int i = 0; i < payload.count; i++) {
            context.player().level().addParticle(
                payload.particle,
                payload.overrideLimiter,
                payload.alwaysShow,

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
