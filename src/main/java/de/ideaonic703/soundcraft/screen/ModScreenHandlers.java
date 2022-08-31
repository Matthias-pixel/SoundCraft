package de.ideaonic703.soundcraft.screen;

import de.ideaonic703.soundcraft.SoundCraft;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static ScreenHandlerType<BurnerScreenHandler> BURNER_SCREEN_HANDLER;

    static {
        BURNER_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(SoundCraft.MOD_ID, "cd_burner"), BurnerScreenHandler::new);
    }

    public static void register() {}
}
