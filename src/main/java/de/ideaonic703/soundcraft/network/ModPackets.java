package de.ideaonic703.soundcraft.network;

import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.network.packet.c2s.RenewCacheC2SPacket;
import de.ideaonic703.soundcraft.network.packet.c2s.SoundCraftScreenC2SPacket;
import de.ideaonic703.soundcraft.network.packet.s2c.CacheDataS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier SCREEN_ACTION_ID = new Identifier(SoundCraft.MOD_ID, "screen_action");
    public static final Identifier RENEW_CACHE_ID = new Identifier(SoundCraft.MOD_ID, "renew_cache");
    public static final Identifier CACHE_DATA_ID = new Identifier(SoundCraft.MOD_ID, "cache_data");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SCREEN_ACTION_ID, SoundCraftScreenC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(RENEW_CACHE_ID, RenewCacheC2SPacket::receive);
    }
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CACHE_DATA_ID, CacheDataS2CPacket::receive);
    }
}
