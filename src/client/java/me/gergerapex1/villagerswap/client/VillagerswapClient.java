package me.gergerapex1.villagerswap.client;

import com.mojang.blaze3d.platform.InputConstants;
import me.gergerapex1.villagerswap.client.states.StateManager;
import me.gergerapex1.villagerswap.client.ui.ItemPriorityScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffers;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class VillagerswapClient implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("villagerswap");
    private Screen lastScreen = null;
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Use the client instance passed by the event for thread-safety
            Screen current = client.screen;
            if (current != lastScreen) {
                boolean found = false;
                if(current instanceof MerchantScreen) {
                    MerchantScreen merchantScreen = (MerchantScreen) current;
                    MerchantOffers offers =  merchantScreen.getMenu().getOffers();
                    for (int i = 0; i < offers.size(); i++) {
                        ItemStack item = offers.get(i).getResult();
                        if(item.is(Items.ENCHANTED_BOOK)) {
                            for (var e : item.getComponents().get(DataComponents.STORED_ENCHANTMENTS).entrySet()) {
                                if(StateManager.getPrioritizedEnchants().contains(e.getKey().unwrapKey().orElseThrow().identifier().toString())) {
                                    client.player.playSound(SoundEvents.EXPERIENCE_BOTTLE_THROW, 1.0F, 1.0F);
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println(!found + "" +     StateManager.getAutoCloseUi());

                    if(!found && StateManager.getAutoCloseUi()) {
                        client.getToastManager().addToast(
                            SystemToast.multiline(client, SystemToastId.CHUNK_LOAD_FAILURE, Component.nullToEmpty("VillagerSwap"), Component.nullToEmpty("Closed screen due to no selected enchantments are present."))
                        );

                        client.setScreen(lastScreen);
                    }
                }
                found = false;
                lastScreen = current;
            }
        });

        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("villagerswap", "main")
        );
        KeyMapping sendToChatKey = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                "key.villagerswap.openscreen", // The translation key for the key mapping.
                InputConstants.Type.KEYSYM, // // The type of the keybinding; KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_J, // The GLFW keycode of the key.
                CATEGORY // The category of the mapping.
            ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (sendToChatKey.consumeClick()) {
                if (client.player != null) {
                    Minecraft.getInstance().setScreen(new ItemPriorityScreen(client.player.connection.registryAccess()));
                }
            }
        });
    }
}
