package io.wdsj.spectre.config;

import com.cleanroommc.configanytime.ConfigAnytime;
import io.wdsj.spectre.Tags;
import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class Settings {
    public static PlayerSpectreSettings PlayerSpectreSettings = new PlayerSpectreSettings();
    public static class PlayerSpectreSettings {
        @Config.Comment("The transparency of the ghost player")
        public float playerGhostAlpha = 0.35F;
        @Config.Comment("The maximum distance for the player to be ghosted")
        public double ghostDistance = 0.75D;
        @Config.Comment("Skip players that are riding entities")
        public boolean skipRidingPlayer = false;

    }

    public static EntitySpectreSettings EntitySpectreSettings = new EntitySpectreSettings();
    public static class EntitySpectreSettings {
        @Config.Comment("Enable Spectre for entities")
        @Config.RequiresWorldRestart
        public boolean enableEntitySpectre = false;
        @Config.Comment("The transparency of the ghost entity")
        public float entityGhostAlpha = 0.20F;
        @Config.Comment("The maximum distance for the entity to be ghosted")
        public double ghostDistance = 1.0D;
        @Config.Comment("A whitelist for entities that should be ghosted")
        public String[] ghostEntityWhitelist = new String[]{};
        @Config.Comment("Invert whitelist to blacklist, listed entites won't be ghosted")
        public boolean invertWhitelist = false;
        @Config.Name("Enable Density Check")
        @Config.Comment("If true, entities will only become ghosts if the entity density around them is high enough.")
        public boolean enableDensityCheck = false;
        @Config.Name("Minimum Entity Density")
        @Config.Comment("The minimum number of entities required within the density check radius to trigger the ghost effect.")
        @Config.RangeInt(min = 1)
        public int minEntityDensity = 12;
        @Config.Name("Density Check Radius")
        @Config.Comment("The radius (in blocks) around an entity to check for other entities to determine density.")
        @Config.RangeDouble(min = 0.01)
        public double densityCheckRadius = 8.0;
        @Config.Name("Skip Target Entity")
        @Config.Comment("If true, the target entity will not be ghosted.")
        public boolean skipTargetEntity = true;
    }

    static {
        ConfigAnytime.register(Settings.class);
    }
}
