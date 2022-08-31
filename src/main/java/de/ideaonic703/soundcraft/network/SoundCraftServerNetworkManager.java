package de.ideaonic703.soundcraft.network;

import de.ideaonic703.soundcraft.network.packet.s2c.CacheDataS2CPacket;
import de.ideaonic703.soundcraft.util.DataProvider;
import de.ideaonic703.soundcraft.util.MinecraftServerProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

@Environment(EnvType.SERVER)
public class SoundCraftServerNetworkManager {
    private static SoundCraftServerNetworkManager instance;
    public static SoundCraftServerNetworkManager getInstance() {
        if(instance == null)
            instance = new SoundCraftServerNetworkManager();
        return instance;
    }
    private MinecraftServer server;
    private SoundCraftServerNetworkManager() {
        this.server = MinecraftServerProvider.instance;
    }
    private HashMap<String, DataProvider> dataProviders = new HashMap<>();

    public void registerDataProvider(String name, DataProvider provider) {
        this.dataProviders.put(name, provider);
    }
    public void broadcastCacheData(String name) {
        DataProvider provider = this.dataProviders.get(name);
        byte[] data = null;
        if(provider != null) {
            data = provider.onRequest().getWrittenBytes();
        }
        CacheDataS2CPacket.broadcast(this.server, name, data);
    }
    public void onCacheRequest(int syncId, String name, ServerPlayerEntity player) {
        DataProvider provider = this.dataProviders.get(name);
        byte[] data = null;
        if(provider != null) {
            data = provider.onRequest().getWrittenBytes();
        }
        CacheDataS2CPacket.send(syncId, player, name, data);
    }
}
