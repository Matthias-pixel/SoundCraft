package de.ideaonic703.soundcraft;

import de.ideaonic703.soundcraft.network.ModPackets;
import de.ideaonic703.soundcraft.screen.BurnerScreen;
import de.ideaonic703.soundcraft.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SoundCraftClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BURNER_SCREEN_HANDLER, BurnerScreen::new);
        ModPackets.registerS2CPackets();
    }
}
