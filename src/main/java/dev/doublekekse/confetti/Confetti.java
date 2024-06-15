package dev.doublekekse.confetti;

import dev.doublekekse.confetti.config.ConfettiConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class Confetti implements ModInitializer {
    public static final String MOD_ID = "confetti";
    public static final SimpleParticleType CONFETTI = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier("confetti"), CONFETTI);

        try {
            ConfettiConfig.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
