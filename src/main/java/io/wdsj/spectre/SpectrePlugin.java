package io.wdsj.spectre;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@IFMLLoadingPlugin.Name("SpectrePlugin")
public class SpectrePlugin implements IEarlyMixinLoader, IFMLLoadingPlugin {
    private static final boolean isClient = FMLLaunchHandler.side().isClient();
    private static final Map<String, Supplier<Boolean>> clientsideMixinConfigs = ImmutableMap.copyOf(new LinkedHashMap<String, Supplier<Boolean>>()
    {
        {
            put("mixins.spectre.json", () -> true);
        }
    });

    @Override
    public List<String> getMixinConfigs() {
        List<String> mixins = new ArrayList<>();
        if (!isClient) return mixins;
        mixins.addAll(clientsideMixinConfigs.keySet());
        return mixins;
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        Supplier<Boolean> side = isClient ? clientsideMixinConfigs.get(mixinConfig) : null;
        if (side != null) {
            return side.get();
        }
        return true;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
