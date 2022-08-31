/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package de.ideaonic703.soundcraft.network.packet.c2s;

import de.ideaonic703.soundcraft.network.ModPackets;
import de.ideaonic703.soundcraft.network.SoundCraftPacket;
import de.ideaonic703.soundcraft.util.SoundCraftScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.nio.charset.StandardCharsets;

public class SoundCraftScreenC2SPacket implements SoundCraftPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int buttonId = buf.readInt();
        int dataLength = buf.readInt();
        String[] data = new String[dataLength];
        for(int i = 0; i < dataLength; i++) {
            data[i] = buf.readString();
        }
        if(player.currentScreenHandler instanceof SoundCraftScreenHandler) {
            ((SoundCraftScreenHandler) player.currentScreenHandler).onSoundCraftScreenPacket(player, buttonId, data);
        }
    }
    public static void send(int buttonId, String[] data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(buttonId);
        buf.writeInt(data.length);
        for(String str : data) {
            buf.writeString(str);
        }
        ClientPlayNetworking.send(ModPackets.SCREEN_ACTION_ID, buf);
    }
}

