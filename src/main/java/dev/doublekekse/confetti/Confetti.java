package dev.doublekekse.confetti;

import dev.doublekekse.confetti.command.ExtendedParticleCommand;
import dev.doublekekse.confetti.config.ConfettiConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Confetti implements ModInitializer {
    public static final String MOD_ID = "confetti";
    public static final SimpleParticleType CONFETTI = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(Registry.PARTICLE_TYPE, identifier("confetti"), CONFETTI);

        try {
            ConfettiConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        CommandRegistrationCallback.EVENT.register(
            (dispatcher, commandBuildContext, environment) -> {
                ExtendedParticleCommand.register(dispatcher);
            }
        );
    }

    public static ResourceLocation identifier(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
