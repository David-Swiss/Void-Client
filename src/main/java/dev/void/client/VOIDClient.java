package dev.voidclient;

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
    private static boolean menuOpen;
    private static boolean hudEnabled = true;
    private static boolean fpsEnabled = true;
    private static boolean coordinatesEnabled = true;

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("void", "main")
    );

    private static KeyMapping key(String translation, int keyCode) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(
                translation, InputConstants.Type.KEYSYM, keyCode, CATEGORY
        ));
    }

    private static final KeyMapping TOGGLE_MENU = key("key.void.toggle_menu", InputConstants.KEY_RIGHT_SHIFT);
    private static final KeyMapping TOGGLE_HUD = key("key.void.toggle_hud", InputConstants.KEY_F8);
    private static final KeyMapping TOGGLE_FPS = key("key.void.toggle_fps", InputConstants.KEY_F6);
    private static final KeyMapping TOGGLE_COORDS = key("key.void.toggle_coords", InputConstants.KEY_F7);

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_MENU.consumeClick()) menuOpen = !menuOpen;
            while (TOGGLE_HUD.consumeClick()) hudEnabled = !hudEnabled;
            while (TOGGLE_FPS.consumeClick()) fpsEnabled = !fpsEnabled;
            while (TOGGLE_COORDS.consumeClick()) coordinatesEnabled = !coordinatesEnabled;
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT, HUD_ID, VOIDClient::renderHud
        );
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;

        if (hudEnabled) {
            int x = 8;
            int y = 8;
            graphics.fill(x - 3, y - 3, x + 145, y + 43, 0xB510101A);
            graphics.fill(x - 3, y - 3, x + 145, y - 1, 0xFFB56CFF);
            graphics.text(client.font, "VOID", x + 3, y + 2, 0xFFFFB8FF, true);

            if (fpsEnabled) {
                graphics.text(client.font, "FPS  " + client.getFps(), x + 3, y + 15, 0xFFFFFFFF, true);
            }
            if (coordinatesEnabled) {
                String coords = String.format("XYZ  %.1f / %.1f / %.1f",
                        client.player.getX(), client.player.getY(), client.player.getZ());
                graphics.text(client.font, coords, x + 3, y + 28, 0xFFD8C9E8, true);
            }
        }

        if (menuOpen) {
            int panelX = 8;
            int panelY = 62;
            graphics.fill(panelX, panelY, panelX + 190, panelY + 132, 0xE510101A);
            graphics.fill(panelX, panelY, panelX + 190, panelY + 2, 0xFFB56CFF);
            graphics.text(client.font, "VOID  /  CLIENT MENU", panelX + 9, panelY + 10, 0xFFFFB8FF, true);
            graphics.text(client.font, "HUD overlay       " + state(hudEnabled), panelX + 9, panelY + 31, 0xFFFFFFFF, true);
            graphics.text(client.font, "FPS counter       " + state(fpsEnabled), panelX + 9, panelY + 48, 0xFFFFFFFF, true);
            graphics.text(client.font, "Coordinates       " + state(coordinatesEnabled), panelX + 9, panelY + 65, 0xFFFFFFFF, true);
            graphics.text(client.font, "F8  HUD   |   F6  FPS   |   F7  XYZ", panelX + 9, panelY + 94, 0xFFD8C9E8, false);
            graphics.text(client.font, "Right Shift  -  close menu", panelX + 9, panelY + 112, 0xFFB56CFF, false);
        }
    }

    private static String state(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }
}
