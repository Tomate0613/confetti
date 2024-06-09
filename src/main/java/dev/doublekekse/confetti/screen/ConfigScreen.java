package dev.doublekekse.confetti.screen;

import dev.doublekekse.confetti.config.ConfettiConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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

        var toggleParticlesButton = Button.builder(
                getToggleString(),
                button -> {
                    ConfettiConfig.ENABLED = !ConfettiConfig.ENABLED;

                    button.setMessage(getToggleString());
                }
            )
            .bounds(width / 2 - buttonWidth / 2, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight);

        var doneButton = Button.builder(
                Component.literal("Done"),
                button -> onClose()
            )
            .bounds(width / 2 - buttonWidth / 2, height - buttonHeight - 10, buttonWidth, buttonHeight);

        this.addRenderableWidget(toggleParticlesButton.build());
        this.addRenderableWidget(doneButton.build());
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
