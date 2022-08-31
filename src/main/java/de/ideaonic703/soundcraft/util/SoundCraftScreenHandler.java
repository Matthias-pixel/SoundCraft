package de.ideaonic703.soundcraft.util;

import net.minecraft.entity.player.PlayerEntity;

public interface SoundCraftScreenHandler {
    void onSoundCraftScreenPacket(PlayerEntity player, int buttonId, String[] data);
}
