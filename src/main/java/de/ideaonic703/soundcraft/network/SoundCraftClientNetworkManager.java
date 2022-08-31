package de.ideaonic703.soundcraft.network;

import de.ideaonic703.soundcraft.network.packet.c2s.RenewCacheC2SPacket;
import de.ideaonic703.soundcraft.network.packet.c2s.SoundCraftScreenC2SPacket;
import de.ideaonic703.soundcraft.util.Pending;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class SoundCraftClientNetworkManager {
    private static SoundCraftClientNetworkManager instance;
    public static SoundCraftClientNetworkManager getInstance() {
        if(instance == null)
            instance = new SoundCraftClientNetworkManager();
        return instance;
    }
    private final HashMap<String, byte[]> cache = new HashMap<>();
    private final HashMap<String, Integer> cacheAges = new HashMap<>();
    private final HashMap<Integer, Pending<PacketByteBuf>> queue = new HashMap<>();
    public Pending<PacketByteBuf> getCached(String name) {
        Pending<PacketByteBuf> pending = new Pending<>(false, PacketByteBufs.create());
        if(MinecraftClient.getInstance().player == null) {
            pending.complete();
            return pending;
        }
        if(!this.cache.containsKey(name)) {
            this.cachePacket(name, pending);
            return pending;
        }
        byte[] data = this.cache.get(name);
        if(data == null)
            data = new byte[0];
        pending.set(new PacketByteBuf(PacketByteBufs.create().writeBytes(data)));
        if(LocalDateTime.now().getSecond() - this.cacheAges.get(name) > 0) {
            this.cachePacket(name, pending);
        } else {
            pending.complete();
        }
        return pending;
    }
    private void cachePacket(String name, Pending<PacketByteBuf> pending) {
        int syncId;
        do {
            syncId = new Random().nextInt();
        } while(this.queue.containsKey(syncId));
        this.queue.put(syncId, pending);
        RenewCacheC2SPacket.send(syncId, name);
    }
    public void onCacheResponse(int syncId, String name, byte[] data) {
        this.cache.put(name, data);
        this.cacheAges.put(name, LocalDateTime.now().getSecond());
        if(this.queue.containsKey(syncId)) {
            this.queue.remove(syncId).complete(new PacketByteBuf(PacketByteBufs.create().writeBytes(data)));
        }
    }
    public void sendScreenAction2S(int buttonId, String[] data) {
        SoundCraftScreenC2SPacket.send(buttonId, data);
    }
    public void sendScreenAction2S(int buttonId, String data) {
        SoundCraftScreenC2SPacket.send(buttonId, new String[]{data});
    }
}
