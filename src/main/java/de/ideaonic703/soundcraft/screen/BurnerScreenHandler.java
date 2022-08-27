package de.ideaonic703.soundcraft.screen;

import com.mojang.brigadier.StringReader;
import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.item.ModItems;
import de.ideaonic703.soundcraft.screen.slot.ModCDSlot;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;

public class BurnerScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final Path MUSIC_INDEX = SoundCraft.MUSIC_DIRECTORY.resolve("index.dat");
    private HashMap<String, NbtCompound> playlist = new HashMap<>();
    private PlaylistUpdateHandler updateHandler = null;
    // /give @s soundcraft:compact_disc{songs:[{name:"Song A", path:"123456"}, {name:"Song B", path:"162543"}, {name:"Song C u", path:"000000"}]}
    // /give @s soundcraft:compact_disc{songs:[{name:"Colorblind", path:"100983"}, {name:"Invincible", path:"346634"}]}
    //
    // {songs:[{name:"Colorblind", path:"100983"}, {name:"Invincible", path:"346634"},{name:"SAVED 1", path:"199384"}, {name:"SAVED b", path:"010929"}, {name:"savedSong 3", path:"111325"},{name:"Song A", path:"123456"}, {name:"Song B", path:"162543"}, {name:"Song C u", path:"000000"}, {name:"Song S", path:"967964"}, {name:"Song C u", path:"000000"}, {name:"Song Last", path:"667788"}]}
    public BurnerScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreenHandlers.BURNER_SCREEN_HANDLER, syncId);
        this.inventory = new SimpleInventory(1);
        this.inventory.onOpen(playerInventory.player);
        this.addSlot(new ModCDSlot(inventory, 0, 17, 13));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    private void checkPlaylist() {
        for(String path : this.playlist.keySet()) {
            NbtElement element = this.playlist.get(path);
            if(element instanceof NbtCompound song) {
                boolean invalid = song.getKeys().size() != 4;
                invalid = invalid || !song.contains("name", NbtType.STRING);
                invalid = invalid || !song.contains("path", NbtType.STRING);
                invalid = invalid || !song.contains("available", NbtType.BYTE);
                invalid = invalid || song.getByte("available") > 1;
                invalid = invalid || song.getByte("available") < 0;
                invalid = invalid || !song.contains("added", NbtType.BYTE);
                invalid = invalid || song.getByte("added") > 1;
                invalid = invalid || song.getByte("added") < 0;
                if(invalid) {
                    SoundCraft.LOGGER.warn("Playlist item did not match structure. removed.");
                    SoundCraft.LOGGER.info(String.format("Item: %s", song));
                    this.playlist.remove(path);
                }
            } else {
                SoundCraft.LOGGER.warn("Playlist item can only be of type NbtCompound. removed.");
                this.playlist.remove(path);
            }
        }
    }
    private ItemStack cd() {
        return this.inventory.getStack(0);
    }
    public void updatePlaylist() {
        this.updatePlaylist(false);
    }
    public void updatePlaylist(boolean force) {
        NbtCompound cdNbt = this.cd().getNbt();
        if(cdNbt == null) {
            cdNbt = new NbtCompound();
            cdNbt.put("songs", new NbtList());
        }
        NbtList addedSongs = cdNbt.getList("songs", NbtType.COMPOUND).copy();
        NbtList songs = this.getAvailable();
        HashMap<String, NbtCompound> playlist = new HashMap<>(songs.size());
        for(NbtElement e : songs) {
            NbtCompound song = (NbtCompound) e;
            song.putBoolean("added", false);
            song.putBoolean("available", true);
            String path = song.getString("path");
            playlist.put(path, song);
        }
        for(NbtElement e : addedSongs) {
            NbtCompound song = (NbtCompound) e;
            String path = song.getString("path");
            if(playlist.containsKey(path)) {
                NbtCompound other = playlist.get(path);
                other.putBoolean("added", true);
                other.putBoolean("available", true);
            } else {
                song.putBoolean("added", false);
                song.putBoolean("available", false);
                songs.add(song);
            }
        }
        if(!force) {
            boolean unchanged = playlist.equals(this.playlist);
            if (unchanged) {
                return;
            }
        }
        this.playlist = playlist;
        this.checkPlaylist();
        if(this.updateHandler != null)
            this.updateHandler.onUpdate(this.getPlaylist());
    }
    private void saveChanges() {
        NbtList addedSongs = new NbtList();
        for(NbtCompound song : this.playlist.values()) {
            NbtCompound cleanSong = song.copy();
            cleanSong.remove("added");
            cleanSong.remove("available");
            if(song.getBoolean("added")) {
                addedSongs.add(cleanSong);
            }
        }
        NbtCompound newCdNbt = new NbtCompound();
        newCdNbt.put("songs", addedSongs.copy());
        //this.slots.get(0).getStack().setNbt(newCdNbt);
        //this.slots.get(0).getStack().setCount(2);
        this.slots.get(0).setStack(new ItemStack(ModItems.PORTABLE_RADIO));
        // Notify server please !!!!!!!!!!!!!!!!!!!!!!!!!!
        // Notify server please !!!!!!!!!!!!!!!!!!!!!!!!!!
        // Notify server please !!!!!!!!!!!!!!!!!!!!!!!!!!
        // Notify server please !!!!!!!!!!!!!!!!!!!!!!!!!!
        // Notify server please !!!!!!!!!!!!!!!!!!!!!!!!!!
        this.sendContentUpdates();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.MUSIC_INDEX.toFile()));
            NbtCompound newIndex = new NbtCompound();
            NbtList playlistList = new NbtList();
            playlistList.addAll(this.playlist.values());
            newIndex.put("songs", playlistList);
            StringNbtWriter nbtWriter = new StringNbtWriter();
            String toExport = nbtWriter.apply(newIndex);
            writer.write(toExport);
            writer.close();
        } catch(Exception e1) {
            e1.printStackTrace();
        }
        this.updatePlaylist(true);
    }
    public void registerUpdateHandler(PlaylistUpdateHandler handler) {
        this.updateHandler = handler;
    }
    public boolean hasUpdateHandler() {
        return this.updateHandler != null;
    }
    public HashMap<String, NbtCompound> getPlaylist() {
        return this.playlist;
    }

    public NbtList getAvailable() {
        try {
            BufferedReader index = new BufferedReader(new FileReader(this.MUSIC_INDEX.toFile()));
            StringBuilder data = new StringBuilder();
            while(true) {
                String line = index.readLine();
                data.append(line).append('\n');
                if(line == null)
                    break;
            }
            String cleanedData = data.toString().replaceAll("\n", "").replaceAll("\r", "").strip();
            NbtCompound element = new StringNbtReader(new StringReader(cleanedData)).parseCompound();
            return element.getList("songs", NbtType.COMPOUND);
        } catch(Exception e) {
            SoundCraft.LOGGER.error("Message: " + e.getMessage());
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(this.MUSIC_INDEX.toFile()));
                NbtCompound empty = new NbtCompound();
                empty.put("songs", new NbtList());
                StringNbtWriter nbtWriter = new StringNbtWriter();
                String toExport = nbtWriter.apply(empty);
                writer.write(toExport);
                writer.close();
            } catch(Exception e1) {
                e1.printStackTrace();
            }
            return new NbtList();
        }
    }
    //***Screen***
    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean canInsertItem = super.insertItem(stack, startIndex, endIndex, fromLast);
        this.updatePlaylist();
        return canInsertItem;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        this.updatePlaylist();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        this.updatePlaylist();
        return newStack;
    }
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 90 + i * 18));
            }
        }
    }
    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 148));
        }
    }
    @Override
    public void close(PlayerEntity player) {
        ItemStack cdSlot = this.slots.get(0).getStack();
        if(!cdSlot.isEmpty()) {
            player.getInventory().offerOrDrop(cdSlot);
        }
        super.close(player);
    }

    public interface PlaylistUpdateHandler {
        void onUpdate(HashMap<String, NbtCompound> playlist);
    }
    public boolean hasCd() {
        return !this.cd().isEmpty();
    }
    public void deleteSong(String path) {
        this.updatePlaylist();
        this.playlist.remove(path);
        this.saveChanges();
    }
    public void removeSong(String path) {
        this.updatePlaylist();
        if(this.playlist.containsKey(path)) {
            this.playlist.get(path).putBoolean("added", false);
        }
        this.saveChanges();
    }
    public void addSong(String path) {
        this.updatePlaylist();
        if(this.playlist.containsKey(path)) {
            this.playlist.get(path).putBoolean("added", true);
        }
        this.saveChanges();
    }
}
