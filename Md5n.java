package dev.md5n;

import dev.md5n.config.ConfigManager;
import dev.md5n.gui.ClickGuiScreen;
import dev.md5n.module.ModuleManager;
import dev.md5n.module.modules.ThemeSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class Md5n implements ClientModInitializer {
    public static final String NAME = "md5n";
    public static final String VERSION = "1.0";
    public static ModuleManager modules;
    private static boolean menuWasHeld;

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        ConfigManager.load(modules);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ThemeSettings theme = modules.get(ThemeSettings.class);
            long window = client.getWindow().getHandle();

            // مفتاح القائمة (الافتراضي: Right Shift + Space)
            boolean held = theme.menuHeld(window);
            if (held && !menuWasHeld && client.currentScreen == null && client.player != null) {
                client.setScreen(new ClickGuiScreen());
            }
            menuWasHeld = held;

            if (client.currentScreen == null && client.player != null) modules.pollKeys();
            if (client.player != null && client.world != null) modules.onTick();
        });

        HudRenderCallback.EVENT.register(modules::onHudRender);

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(modules));
    }
}
