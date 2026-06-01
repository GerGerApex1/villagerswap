package me.gergerapex1.villagerswap.client.ui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import me.gergerapex1.villagerswap.client.states.StateManager
import net.minecraft.client.gui.components.Button
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.enchantment.Enchantment
import java.awt.Color


class ItemPriorityScreen(val registryAccess: RegistryAccess) : WindowScreen(ElementaVersion.V10) {
    var scrollComponent: ScrollComponent? = null
    init {
        val container = UIContainer().constrain {
            x = 0.pixels()
            y = 15.percent()
            width = 100.percent()
            height = 90.percent()
        } childOf window
        val searchBarContainer = UIContainer().constrain {
            x = CenterConstraint()
            y = CenterConstraint() - 20.percent()
            width = 210.pixels()
            height = ChildBasedSizeConstraint()

        } childOf window
        val searchBarBg = UIRoundedRectangle(5f).constrain {
            x = SiblingConstraint()
            y = SiblingConstraint()

            width = 180.pixels()
            height = 13.pixels()
            color = Color(128, 127, 127, 127).toConstraint()
        } childOf searchBarContainer
        val searchBar= UITextInput("Enchantment Name").constrain {
            x = CenterConstraint()
            y = CenterConstraint()

            width = 150.pixels()
        } childOf searchBarBg
        searchBar.onKeyType { _, _ ->
            val query = searchBar.getText().lowercase()

            scrollComponent?.filterChildren { (it as UIText).getText().contains(query, true) }
        }
        searchBar.onMouseClick {
            searchBar.grabWindowFocus()
        }
        scrollComponent = ScrollComponent().constrain {
            x = CenterConstraint()
            y = CenterConstraint() - 5.percent()

            width = 200.pixels()
            height = 40.percentOfWindow()
        } childOf container
        Inspector(window).constrain {
            x = 10.pixels(true)
            y = 10.pixels(true)
        } childOf window

        // VANILLA SHI
        val autoCloseButton: Button = Button.builder(Component.literal(String.format("Auto Close Ui: %s",
            StateManager.getAutoCloseUi().toString()))
        ) { btn ->
            // When the button is clicked, we can display a toast to the screen.
            val newValue = !StateManager.getAutoCloseUi()
            StateManager.setAutoCloseUi(newValue)
            btn.setMessage(
                Component.literal(
                    String.format(
                        "Auto Close Ui: %s",
                        newValue.toString()
                    )
                )
            )
        }.bounds(20, 20, 120, 20).build()
        this.addRenderableWidget(autoCloseButton)
        getRegistry()

    }

    fun getRegistry() {
        val prioritizedEnchants = StateManager.getPrioritizedEnchants()
        val enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT)
        val enchantmentKeySet = enchantmentRegistry.keySet()
        for (enchantment in enchantmentKeySet) {
            val enchantmentKey = enchantment.toString()
            val enchantmentValue: Enchantment = enchantmentRegistry.getValue(enchantment)!!
            val uiText = UIText(enchantmentValue.description.string).constrain {
                x = CenterConstraint()
                y = SiblingConstraint(padding = 2f) + 3.pixels
                textScale = 1.25f.pixels()
            } childOf scrollComponent!!
             if(prioritizedEnchants.contains(enchantmentKey)) {
                uiText.setColor(Color.GREEN)
             } else {
                uiText.setColor(Color.WHITE)
             }
            uiText.onMouseClick {
                minecraft.player?.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
                if(prioritizedEnchants.contains(enchantmentKey)) {
                    uiText.setColor(Color.WHITE)
                    prioritizedEnchants.remove(enchantmentKey)
                } else {
                    prioritizedEnchants.add(enchantmentKey)
                    uiText.setColor(Color.GREEN)
                }
            }
        }
    }
}