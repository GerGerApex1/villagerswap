package me.gergerapex1.villagerswap.client.mixins;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import me.gergerapex1.villagerswap.client.states.StateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.client.gui.screens.inventory.MerchantScreen.class)
public class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu> {
    public MerchantScreenMixin(MerchantMenu menu, Inventory inventory,
        Component title) {
        super(menu, inventory, title);
    }
    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
    }
    @Inject(method = "extractContents", at = @At("HEAD"))
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        int centerX = this.width/2;
        int centerY = this.height/2;

        graphics.text(this.font, "Enchantments Found:", centerX - 250 ,centerY - 75, 0xFFFFFFFF, false);

        MerchantOffers offers = this.menu.getOffers();
        var ref = new Object() {
            int i = 0;
        };
        for (MerchantOffer offer : offers) {
            ItemStack item = offer.getResult();
            if (item.is(Items.ENCHANTED_BOOK)) {
                for (Entry<Holder<Enchantment>> e : item.getComponents().get(DataComponents.STORED_ENCHANTMENTS)
                    .entrySet()) {
                    int itemY = centerY - (60 - 20 * ref.i);
                    int color = 0xFFFF0000;
                    int level = e.getIntValue();
                    //System.out.println(StateManager.getEnchantIndex() + " " + e.getKey().unwrapKey().orElseThrow().identifier());
                    if(StateManager.getPrioritizedEnchants().contains(e.getKey().unwrapKey().orElseThrow().identifier().toString())) {
                        color = 0xFF00FF00;
                    }

                    graphics.text(this.font, e.getKey().value().description().getString() + " "
                        + level , centerX - 230 ,itemY + 3 , color, false);
                    graphics.fakeItem(new ItemStack(Items.ENCHANTED_BOOK), centerX - 250, itemY );
                    ref.i++;
                }
            }
        }
    }

    /*
    private void addEnchantButton() {
        ResourceKey<Registry<Enchantment>> enchantments =
            Registries.ENCHANTMENT;

        if (enchantments.isEmpty()) return;

        Button button = Button.builder(
            enchantments.get(0).getName(1),
            btn -> {
                enchantIndex = (enchantIndex + 1) % enchantments.size();
                Enchantment enchantment = enchantments.get(enchantIndex);

                btn.setMessage(enchantment.getName(1));
            }
        ).dimensions(
            this.width / 2 + 90,  // right side of trades
            this.height / 2 - 70,
            130,
            20
        ).build();

        this.addDrawableChild(button);
    }

     */
}
