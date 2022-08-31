package de.ideaonic703.soundcraft.screen;

import de.ideaonic703.soundcraft.network.SoundCraftClientNetworkManager;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public abstract class SoundCraftScreen<T extends ScreenHandler> extends HandledScreen<T> {
    public SoundCraftScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected void soundCraftScreenAction(int buttonId, String[] data) {
        SoundCraftClientNetworkManager.getInstance().sendScreenAction2S(buttonId, data);
    }
    protected void soundCraftScreenAction(int buttonId, String data) {
        SoundCraftClientNetworkManager.getInstance().sendScreenAction2S(buttonId, data);
    }
}
