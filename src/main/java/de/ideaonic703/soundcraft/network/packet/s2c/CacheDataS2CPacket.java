package de.ideaonic703.soundcraft.network.packet.s2c;

import de.ideaonic703.soundcraft.network.ModPackets;
import de.ideaonic703.soundcraft.network.SoundCraftClientNetworkManager;
import de.ideaonic703.soundcraft.network.SoundCraftPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class CacheDataS2CPacket implements SoundCraftPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int syncId = buf.readInt();
        String name = buf.readString();
        byte[] data = buf.readByteArray();
        SoundCraftClientNetworkManager.getInstance().onCacheResponse(syncId, name, data);
    }
    public static void send(int syncId, ServerPlayerEntity player, String name, byte[] data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeString(name);
        buf.writeByteArray(Objects.requireNonNullElseGet(data, () -> new byte[0]));
        ServerPlayNetworking.send(player, ModPackets.CACHE_DATA_ID, buf);
    }
    public static void broadcast(MinecraftServer server, String name, byte[] data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name);
        buf.writeByteArray(data);
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            send(-1, player, name, data);
        }
    }
}
