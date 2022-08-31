package de.ideaonic703.soundcraft.util;

import net.minecraft.network.PacketByteBuf;

public interface DataProvider {
    PacketByteBuf onRequest();
}
