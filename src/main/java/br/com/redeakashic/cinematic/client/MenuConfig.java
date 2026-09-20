package br.com.redeakashic.cinematic.client;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import br.com.redeakashic.cinematic.core.Numbers;

public final class MenuConfig {
    public final boolean enabled, reducedMotion, replaceDummyCore, particles;
    public final double parallax;
    private MenuConfig(boolean enabled, boolean reduced, boolean dummy, boolean particles, double parallax) {
        this.enabled = enabled; this.reducedMotion = reduced; this.replaceDummyCore = dummy;
        this.particles = particles; this.parallax = Numbers.clamp(parallax, 0, 2);
    }
    public static MenuConfig read(File file) {
        Configuration c = new Configuration(file);
        c.load();
        MenuConfig result = new MenuConfig(
                c.get("general", "enabled", true).getBoolean(true),
                c.get("motion", "reducedMotion", false).getBoolean(false),
                c.get("compatibility", "replaceKnownDummyCoreMenus", true).getBoolean(true),
                c.get("motion", "particles", true).getBoolean(true),
                c.get("motion", "parallaxStrength", 1.0).getDouble(1.0));
        if (c.hasChanged()) { c.save(); }
        return result;
    }
}
