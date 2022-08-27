package de.ideaonic703.soundcraft.block.entity;

import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {
    public static BlockEntityType<BurnerBlockEntity> BURNER_BLOCK;

    public static void registerBlockEntities() {
        BURNER_BLOCK = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SoundCraft.MOD_ID, "burner_block"), FabricBlockEntityTypeBuilder.create(BurnerBlockEntity::new, ModBlocks.BURNER).build(null));
    }
}
