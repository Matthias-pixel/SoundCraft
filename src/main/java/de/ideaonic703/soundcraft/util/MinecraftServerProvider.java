package de.ideaonic703.soundcraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.SERVER)
public abstract class MinecraftServerProvider {
    public static MinecraftServer instance;
}
