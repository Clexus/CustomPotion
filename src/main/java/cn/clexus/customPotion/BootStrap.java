package cn.clexus.customPotion;

import cn.clexus.customPotion.utils.EffectLoader;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;

public class BootStrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        EffectLoader.registerAllEffects();
    }
}
