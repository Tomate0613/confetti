package dev.doublekekse.confetti.client;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.particle.ConfettiParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ConfettiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ParticleFactoryRegistry.getInstance().register(Confetti.CONFETTI, ConfettiParticle.Provider::new);
    }
}
