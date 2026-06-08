package dev.doublekekse.confetti;

import dev.doublekekse.confetti.command.ExtendedParticleCommand;
import dev.doublekekse.confetti.config.ConfettiConfig;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class Confetti implements ModInitializer {
    public static final String MOD_ID = "confetti";
    public static final SimpleParticleType CONFETTI = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier("confetti"), CONFETTI);
        PayloadTypeRegistry.clientboundPlay().register(ExtendedParticlePacket.TYPE, ExtendedParticlePacket.STREAM_CODEC);

        try {
            ConfettiConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        CommandRegistrationCallback.EVENT.register(
            (dispatcher, commandBuildContext, environment) -> {
                ExtendedParticleCommand.register(dispatcher, commandBuildContext);
            }
        );
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
