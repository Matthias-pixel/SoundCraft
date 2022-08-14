package de.ideaonic703.soundcraft.block;

import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.block.custom.BurnerBlock;
import de.ideaonic703.soundcraft.block.custom.SpeakerBlock;
import de.ideaonic703.soundcraft.item.ModItemGroup;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static final Block SPEAKER = registerBlock("speaker", new SpeakerBlock(FabricBlockSettings.of(Material.WOOD)));
    public static final Item SPEAKER_ITEM = registerBlockItem("speaker", new BlockItem(SPEAKER, new FabricItemSettings().group(ModItemGroup.SOUND_CRAFT).maxCount(1)));
    public static final Block BURNER = registerBlock("cd_burner", new BurnerBlock(FabricBlockSettings.of(Material.STONE)));
    public static final Item BURNER_ITEM = registerBlockItem("cd_burner", new BlockItem(BURNER, new FabricItemSettings().group(ModItemGroup.SOUND_CRAFT).maxCount(1)));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier("soundcraft", name), block);
    }
    private static Item registerBlockItem(String name, BlockItem blockItem) {
        return Registry.register(Registry.ITEM, new Identifier("soundcraft", name), blockItem);
    }
    public static void registerBlocks() {
        SoundCraft.LOGGER.debug("Registering Blocks for Sound Craft");
    }
}
