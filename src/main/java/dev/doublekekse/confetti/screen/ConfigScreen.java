package dev.doublekekse.confetti.screen;

import dev.doublekekse.confetti.config.ConfettiConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class ConfigScreen extends Screen {
    Screen previousScreen;

    public ConfigScreen(Screen previousScreen) {
        super(Component.literal("Confetti Config"));

        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();

        final var buttonWidth = 150;
        final var buttonHeight = 20;

        var toggleParticlesButton = new Button(
            width / 2 - buttonWidth / 2, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight,
            getToggleString(),
            button -> {
                ConfettiConfig.ENABLED = !ConfettiConfig.ENABLED;

                button.setMessage(getToggleString());
            }
        );

        var doneButton = new Button(
            width / 2 - buttonWidth / 2, height - buttonHeight - 10, buttonWidth, buttonHeight,
            Component.literal("Done"),
            button -> {
                try {
                    ConfettiConfig.save();
                    onClose();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );

        this.addRenderableWidget(toggleParticlesButton);
        this.addRenderableWidget(doneButton);
    }

    Component getToggleString() {
        if (ConfettiConfig.ENABLED) {
            return Component.literal("Confetti: Enabled");
        } else {
            return Component.literal("Confetti: Disabled");
        }
    }

    @Override
    public void onClose() {
        popScreen();
    }

    public void popScreen() {
        assert this.minecraft != null;

        this.minecraft.setScreen(this.previousScreen);
    }
}
