package de.ideaonic703.soundcraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.PacketByteBuf;

public abstract class SoundCraftSTools {
    public PacketByteBuf writeStringArray(PacketByteBuf buf, String[] array) {
        buf.writeVarInt(array.length);
        for(String item : array) {
            buf.writeString(item);
        }
        return buf;
    }
    public String[] readStringArray(PacketByteBuf buf, int maxSize) {
        int length = buf.readVarInt();
        if (length > maxSize) {
            throw new DecoderException("StringArray with size " + length + " is bigger than allowed " + maxSize);
        }
        String[] data = new String[length];
        for(int i = 0; i < length; i++) {
            data[i] = buf.readString();
        }
        return data;
    }
}
