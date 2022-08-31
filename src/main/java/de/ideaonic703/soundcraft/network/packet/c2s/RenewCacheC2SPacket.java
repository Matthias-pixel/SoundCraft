package de.ideaonic703.soundcraft.network.packet.c2s;

import de.ideaonic703.soundcraft.network.ModPackets;
import de.ideaonic703.soundcraft.network.SoundCraftPacket;
import de.ideaonic703.soundcraft.network.SoundCraftServerNetworkManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class RenewCacheC2SPacket implements SoundCraftPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int syncId = buf.readInt();
        String name = buf.readString();
        SoundCraftServerNetworkManager.getInstance().onCacheRequest(syncId, name, player);
    }
    public static void send(int syncId, String name) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeString(name);
        ClientPlayNetworking.send(ModPackets.RENEW_CACHE_ID, buf);
    }
}
