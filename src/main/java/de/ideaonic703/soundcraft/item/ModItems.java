package de.ideaonic703.soundcraft.item;

import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.item.custom.CDItem;
import de.ideaonic703.soundcraft.item.custom.RadioItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item PORTABLE_RADIO = registerItem("radio", new RadioItem(new FabricItemSettings().group(ModItemGroup.SOUND_CRAFT).maxCount(1)));
    public static final Item COMPACT_DISC = registerItem("compact_disc", new CDItem(new FabricItemSettings().group(ModItemGroup.SOUND_CRAFT).maxCount(1)));

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier("soundcraft", name), item);
    }
    public static void registerItems() {
        SoundCraft.LOGGER.debug("Registering Items for Sound Craft");
    }
}
