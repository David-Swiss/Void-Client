package dev.void.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public final class VOIDClient implements ClientModInitializer {
    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath("void", "hud");
    private static boolean menuOpen = false;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("void", "main")
    );

    private static final KeyMapping TOGGLE_MENU = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.void.toggle_menu",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_RIGHT_SHIFT,
                    CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_MENU.consumeClick()) {
                menuOpen = !menuOpen;
            }
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                HUD_ID,
                VOIDClient::renderHud
        );
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) {
            return;
        }

        int x = 8;
        int y = 8;
        graphics.fill(x - 3, y - 3, x + 124, y + 43, 0xB510101A);
        graphics.fill(x - 3, y - 3, x + 124, y - 1, 0xFFB56CFF);
        graphics.text(client.font, "VOID", x + 3, y + 2, 0xFFFFB8FF, true);
        graphics.text(client.font, "FPS  " + client.getFps(), x + 3, y + 15, 0xFFFFFFFF, true);

        if (client.player != null) {
            String coords = String.format("XYZ  %.1f / %.1f / %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ());
            graphics.text(client.font, coords, x + 3, y + 28, 0xFFD8C9E8, true);
        }

        if (menuOpen) {
            int panelX = 8;
            int panelY = 62;
            graphics.fill(panelX, panelY, panelX + 166, panelY + 126, 0xE510101A);
            graphics.fill(panelX, panelY, panelX + 166, panelY + 2, 0xFFB56CFF);
            graphics.text(client.font, "VOID  /  CLIENT MENU", panelX + 9, panelY + 10, 0xFFFFB8FF, true);
            graphics.text(client.font, "HUD                 ON", panelX + 9, panelY + 30, 0xFFFFFFFF, true);
            graphics.text(client.font, "Coordinates         ON", panelX + 9, panelY + 46, 0xFFFFFFFF, true);
            graphics.text(client.font, "FPS Counter         ON", panelX + 9, panelY + 62, 0xFFFFFFFF, true);
            graphics.text(client.font, "More client options coming soon", panelX + 9, panelY + 91, 0xFFB9A8C9, false);
            graphics.text(client.font, "Right Shift  -  close", panelX + 9, panelY + 107, 0xFFB56CFF, false);
        }
    }
}
