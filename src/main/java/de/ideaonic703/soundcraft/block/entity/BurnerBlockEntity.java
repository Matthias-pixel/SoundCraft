package de.ideaonic703.soundcraft.block.entity;

import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.item.inventory.ImplementedInventory;
import de.ideaonic703.soundcraft.screen.BurnerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BurnerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    public BurnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BURNER_BLOCK, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("CD Burner");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        SoundCraft.LOGGER.info("creating new Screen Handler");
        return new BurnerScreenHandler(syncId, inv);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }
    public static void tick(World world, BlockPos pos, BlockState state, BurnerBlockEntity burner) {

    }

    /**
     * Retrieves the item list of this inventory.
     * Must return the same instance every time it's called.
     */
    @Override
    public DefaultedList<ItemStack> getItems() {
        return null;
    }
}
