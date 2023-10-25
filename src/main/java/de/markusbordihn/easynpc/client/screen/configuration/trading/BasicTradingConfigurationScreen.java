/**
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.client.screen.configuration.trading;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.menu.configuration.trading.BasicTradingConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandler;

@OnlyIn(Dist.CLIENT)
public class BasicTradingConfigurationScreen
    extends TradingConfigurationScreen<BasicTradingConfigurationMenu> {

  // Trading Options
  protected EditBox resetsEveryMinEditBox;
  protected EditBox maxUsesEditBox;
  protected EditBox rewardExpEditBox;

  public BasicTradingConfigurationScreen(BasicTradingConfigurationMenu menu, Inventory inventory,
      Component component) {
    super(menu, inventory, component);
  }

  private void onResetsEveryMinEditBoxChanged(String text) {
    if (isNumericValue(text) && !text.isEmpty()) {
      NetworkMessageHandler.setBasicTradingResetsEveryMin(uuid, Integer.parseInt(text));
    }
  }

  private void onMaxUsesEditBoxChanged(String text) {
    if (isPositiveNumericValue(text) && !text.isEmpty()) {
      NetworkMessageHandler.setBasicTradingMaxUses(uuid, Integer.parseInt(text));
    }
  }

  private void onRewardExpEditBoxChanged(String text) {
    if (isNumericValue(text) && !text.isEmpty()) {
      NetworkMessageHandler.setBasicTradingRewardExp(uuid, Integer.parseInt(text));
    }
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.basicTradesButton.active = false;

    // Reset Every Min Edit Box
    this.resetsEveryMinEditBox = new EditBox(this.font, this.contentLeftPos + 166,
        this.contentTopPos + 122, 32, 16, Component.nullToEmpty(""));
    this.resetsEveryMinEditBox.setMaxLength(3);
    this.resetsEveryMinEditBox.setValue(this.entity.getTradingResetsEveryMin() + "");
    this.resetsEveryMinEditBox.setResponder(this::onResetsEveryMinEditBoxChanged);
    this.resetsEveryMinEditBox.setFilter(TradingConfigurationScreen::isNumericValue);
    this.addRenderableWidget(this.resetsEveryMinEditBox);

    // Max Uses Edit Box
    this.maxUsesEditBox = new EditBox(this.font, this.contentLeftPos + 166,
        this.contentTopPos + 145, 32, 16, Component.nullToEmpty(""));
    this.maxUsesEditBox.setMaxLength(4);
    this.maxUsesEditBox.setValue(this.entity.getBasicTradingMaxUses() + "");
    this.maxUsesEditBox.setResponder(this::onMaxUsesEditBoxChanged);
    this.maxUsesEditBox.setFilter(TradingConfigurationScreen::isPositiveNumericValue);
    this.addRenderableWidget(this.maxUsesEditBox);

    // Experience Edit Box
    this.rewardExpEditBox = new EditBox(this.font, this.contentLeftPos + 166,
        this.contentTopPos + 168, 32, 16, Component.nullToEmpty(""));
    this.rewardExpEditBox.setMaxLength(3);
    this.rewardExpEditBox.setValue(this.entity.getBasicTradingRewardExp() + "");
    this.rewardExpEditBox.setResponder(this::onRewardExpEditBoxChanged);
    this.rewardExpEditBox.setFilter(TradingConfigurationScreen::isNumericValue);
    this.addRenderableWidget(this.rewardExpEditBox);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

    // Render Trading Slots
    int slotPositionX = this.leftPos + BasicTradingConfigurationMenu.TRADING_START_POSITION_X - 1;
    int slotPositionY = this.topPos + BasicTradingConfigurationMenu.TRADING_START_POSITION_Y - 1;
    for (int tradingOffer =
        0; tradingOffer < BasicTradingConfigurationMenu.TRADING_OFFERS; tradingOffer++) {

      // Position for Second row
      if (tradingOffer == 6) {
        slotPositionX =
            this.leftPos + BasicTradingConfigurationMenu.TRADING_START_POSITION_SECOND_ROW_X - 1;
        slotPositionY = this.topPos + BasicTradingConfigurationMenu.TRADING_START_POSITION_Y - 1;
      }

      // Offer Label
      guiGraphics.drawString(this.font, (tradingOffer < 9 ? " " : "") + (tradingOffer + 1) + ".",
          slotPositionX - 15, slotPositionY + 5, 0x404040, false);

      // Item A Slot
      int itemASlotLeftPosition = slotPositionX;
      int itemASlotTopPosition = slotPositionY;
      guiGraphics.blit(Constants.TEXTURE_INVENTORY, itemASlotLeftPosition, itemASlotTopPosition, 7,
          7, 18, 18);

      // "+" Label
      guiGraphics.drawString(this.font, "+",
          itemASlotLeftPosition + BasicTradingConfigurationMenu.TRADING_SLOT_SIZE + 6,
          itemASlotTopPosition + 5, 0x404040, false);

      // Item B Slot
      int itemBSlotLeftPosition = slotPositionX + BasicTradingConfigurationMenu.TRADING_SLOT_SIZE
          + BasicTradingConfigurationMenu.TRADING_SLOT_SIZE;
      int itemBSlotTopPosition = slotPositionY;
      guiGraphics.blit(Constants.TEXTURE_INVENTORY, itemBSlotLeftPosition, itemBSlotTopPosition, 7,
          7, 18, 18);

      // "=" Label
      guiGraphics.drawString(this.font, "=",
          itemBSlotLeftPosition + BasicTradingConfigurationMenu.TRADING_SLOT_SIZE + 12,
          itemBSlotTopPosition + 5, 0x404040, false);

      // Result Slot
      guiGraphics.blit(Constants.TEXTURE_INVENTORY,
          slotPositionX + ((BasicTradingConfigurationMenu.TRADING_SLOT_SIZE
              + BasicTradingConfigurationMenu.TRADING_SLOT_SIZE + 5) * 2),
          slotPositionY, 7, 7, 18, 18);

      slotPositionY += BasicTradingConfigurationMenu.TRADING_SLOT_SIZE + 1;

    }

    // Player Inventory Slots
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, this.contentLeftPos, this.contentTopPos + 115, 7,
        83, 162, 54);

    // Player Hotbar Slots
    guiGraphics.blit(Constants.TEXTURE_INVENTORY, this.contentLeftPos, this.contentTopPos + 171, 7,
        141, 162, 18);

    // Render Reset Every Min Label
    guiGraphics.drawString(this.font,
        Component.translatable(Constants.TEXT_CONFIG_PREFIX + "trading.mins_for_trade_reset"),
        this.contentLeftPos + 202, this.contentTopPos + 127, 0x404040, false);

    // Render Max Uses Label
    guiGraphics.drawString(this.font,
        Component.translatable(Constants.TEXT_CONFIG_PREFIX + "trading.max_uses_per_trade"),
        this.contentLeftPos + 202, this.contentTopPos + 150, 0x404040, false);

    // Render Reward Exp Label
    guiGraphics.drawString(this.font,
        Component.translatable(Constants.TEXT_CONFIG_PREFIX + "trading.rewarded_exp_per_trade"),
        this.contentLeftPos + 202, this.contentTopPos + 173, 0x404040, false);

  }

}
