package io.wdsj.spectre;

import io.wdsj.spectre.handler.ConfigSyncher;
import io.wdsj.spectre.handler.EntityRenderHandler;
import io.wdsj.spectre.handler.PlayerRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, clientSideOnly = true, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = Spectre.DEPENDENCY)
public class Spectre {
    public static final String DEPENDENCY = "required-after:mixinbooter@[10.1,);required-after:configanytime;";
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_ID);

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
        MinecraftForge.EVENT_BUS.register(new EntityRenderHandler());
        MinecraftForge.EVENT_BUS.register(new ConfigSyncher());
    }
}
