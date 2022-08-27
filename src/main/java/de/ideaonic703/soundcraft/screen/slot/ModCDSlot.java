package de.ideaonic703.soundcraft.screen.slot;

import de.ideaonic703.soundcraft.item.ModItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ModCDSlot extends Slot {
    public ModCDSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(ModItems.COMPACT_DISC);
    }
    @Override
    public int getMaxItemCount() {
        return 1;
    }
}
