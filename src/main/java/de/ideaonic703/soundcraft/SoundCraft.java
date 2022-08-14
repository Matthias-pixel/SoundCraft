package de.ideaonic703.soundcraft;

import de.ideaonic703.soundcraft.block.ModBlocks;
import de.ideaonic703.soundcraft.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

public class SoundCraft implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("SoundCraft");
	public static final Path MOD_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("SoundCraft");
	public static final Path MUSIC_DIRECTORY = MOD_DIRECTORY.resolve("music");
	public static boolean ensureDirectory(Path directory) {
		if(directory.toFile().exists())
			return true;
		return directory.toFile().mkdirs();
	}
	@Override
	public void onInitialize() {
		if(!ensureDirectory(MUSIC_DIRECTORY)) {
			throw new RuntimeException(String.format("Could not create music directory: \"%s\"", MUSIC_DIRECTORY));
		}
		LOGGER.info(String.format("Music directory: %s", MUSIC_DIRECTORY));
		ModItems.registerItems();
		ModBlocks.registerBlocks();
	}
}
