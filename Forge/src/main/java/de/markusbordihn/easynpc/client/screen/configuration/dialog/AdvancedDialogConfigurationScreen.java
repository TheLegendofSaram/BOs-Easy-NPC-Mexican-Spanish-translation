/*
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

package de.markusbordihn.easynpc.client.screen.configuration.dialog;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.components.AddButton;
import de.markusbordihn.easynpc.client.screen.components.CopyButton;
import de.markusbordihn.easynpc.client.screen.components.EditButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextEditButton;
import de.markusbordihn.easynpc.data.dialog.DialogDataEntry;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationType;
import de.markusbordihn.easynpc.menu.configuration.dialog.AdvancedDialogConfigurationMenu;
import de.markusbordihn.easynpc.network.NetworkMessageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedDialogConfigurationScreen
    extends DialogConfigurationScreen<AdvancedDialogConfigurationMenu> {

  Button newDialogButton;
  AdvancedDialogConfigurationScreen.DialogList dialogList;

  public AdvancedDialogConfigurationScreen(
      AdvancedDialogConfigurationMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
  }

  @Override
  public void init() {
    super.init();

    // Default button stats
    this.advancedDialogButton.active = false;

    // Dialog List
    this.dialogList = new AdvancedDialogConfigurationScreen.DialogList();
    this.addWidget(this.dialogList);

    // Add new dialog button
    this.newDialogButton =
        this.addRenderableWidget(
            new AddButton(
                this.contentLeftPos,
                this.contentTopPos + 168,
                200,
                "dialog.add",
                onPress ->
                    NetworkMessageHandler.openDialogEditor(
                        AdvancedDialogConfigurationScreen.this.uuid,
                        ConfigurationType.ADVANCED_DIALOG)));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    super.render(guiGraphics, x, y, partialTicks);

    // White background for dialog list
    guiGraphics.fill(
        this.leftPos + 5, this.topPos + 40, this.leftPos + 314, this.topPos + 230, 0xffeeeeee);

    // Draw vertical separator line for entries
    guiGraphics.fill(
        this.leftPos + 109, this.topPos + 40, this.leftPos + 110, this.topPos + 210, 0xffbbbbbb);
    guiGraphics.fill(
        this.leftPos + 198, this.topPos + 40, this.leftPos + 199, this.topPos + 210, 0xffbbbbbb);

    // Render dialog list.
    this.dialogList.render(guiGraphics, x, y, partialTicks);

    // Header background
    guiGraphics.fill(
        this.leftPos + 5, this.topPos + 40, this.leftPos + 314, this.topPos + 59, 0xffaaaaaa);

    // Footer background
    guiGraphics.fill(
        this.leftPos + 5, this.topPos + 210, this.leftPos + 314, this.topPos + 230, 0xffaaaaaa);

    // Dialog Data Set header
    int headerLeft = this.leftPos + 10;
    Text.drawConfigString(
        guiGraphics,
        this.font,
        "label_id",
        headerLeft,
        this.topPos + 45,
        Constants.FONT_COLOR_BLACK);
    Text.drawString(
        guiGraphics,
        this.font,
        "Name",
        headerLeft + 103,
        this.topPos + 45,
        Constants.FONT_COLOR_BLACK);
    Text.drawString(
        guiGraphics,
        this.font,
        "Text",
        headerLeft + 191,
        this.topPos + 45,
        Constants.FONT_COLOR_BLACK);

    // Draw vertical separator line for headers
    guiGraphics.fill(
        this.leftPos + 109, this.topPos + 40, this.leftPos + 110, this.topPos + 59, 0xff666666);
    guiGraphics.fill(
        this.leftPos + 198, this.topPos + 40, this.leftPos + 199, this.topPos + 59, 0xff666666);

    // Re-render button for visibility
    this.newDialogButton.render(guiGraphics, x, y, partialTicks);
  }

  @Override
  public void containerTick() {
    super.containerTick();
  }

  @OnlyIn(Dist.CLIENT)
  class DialogList extends ObjectSelectionList<AdvancedDialogConfigurationScreen.DialogList.Entry> {

    DialogList() {
      super(
          AdvancedDialogConfigurationScreen.this.minecraft,
          AdvancedDialogConfigurationScreen.this.width + 50,
          AdvancedDialogConfigurationScreen.this.height - 60,
          AdvancedDialogConfigurationScreen.this.topPos + 56,
          AdvancedDialogConfigurationScreen.this.topPos + 211,
          19);
      this.setRenderHeader(false, 0);
      this.setRenderBackground(false);
      this.setRenderTopAndBottom(false);

      // Add all dialog data sets, sorted by label.
      for (DialogDataEntry dialogData :
          AdvancedDialogConfigurationScreen.this.dialogDataSet.getDialogsByLabel()) {
        if (dialogData == null || dialogData.getId() == null) {
          continue;
        }
        this.addEntry(new AdvancedDialogConfigurationScreen.DialogList.Entry(dialogData));
      }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
      if (this.getItemCount() > 0) {
        super.render(guiGraphics, x, y, partialTicks);
      }
    }

    @Override
    protected void renderSelection(
        GuiGraphics guiGraphics, int right, int top, int width, int height, int color) {
      // Show no selection
    }

    @OnlyIn(Dist.CLIENT)
    class Entry
        extends ObjectSelectionList.Entry<AdvancedDialogConfigurationScreen.DialogList.Entry> {

      final DialogDataEntry dialogData;
      final EditButton editButton;
      final CopyButton copyLabelButton;
      final TextEditButton textEditButton;
      final String defaultDialogLabel;

      public Entry(DialogDataEntry dialogData) {
        super();
        this.dialogData = dialogData;
        this.editButton =
            new EditButton(
                0,
                0,
                onPress ->
                    NetworkMessageHandler.openDialogEditor(
                        AdvancedDialogConfigurationScreen.this.uuid,
                        this.dialogData.getId(),
                        ConfigurationType.ADVANCED_DIALOG));
        this.copyLabelButton =
            new CopyButton(
                0,
                0,
                onPress -> {
                  Minecraft minecraft = Minecraft.getInstance();
                  minecraft.keyboardHandler.setClipboard(dialogData.getLabel());
                });
        this.textEditButton =
            new TextEditButton(
                0,
                0,
                onPress ->
                    NetworkMessageHandler.openDialogTextEditor(
                        AdvancedDialogConfigurationScreen.this.uuid,
                        this.dialogData.getId(),
                        ConfigurationType.ADVANCED_DIALOG));
        this.defaultDialogLabel =
            AdvancedDialogConfigurationScreen.this.dialogDataSet.getDefaultDialogLabel();
      }

      @Override
      public Component getNarration() {
        return Component.translatable(dialogData.getName());
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        this.copyLabelButton.mouseClicked(mouseX, mouseY, button);
        this.editButton.mouseClicked(mouseX, mouseY, button);
        this.textEditButton.mouseClicked(mouseX, mouseY, button);
        return button == 0;
      }

      @Override
      public void render(
          GuiGraphics guiGraphics,
          int entryId,
          int top,
          int left,
          int entryWidth,
          int entryHeight,
          int mouseX,
          int mouseY,
          boolean isSelected,
          float partialTicks) {

        // Position
        int leftPos = left - 75;

        // Render edit button and tooltip
        this.editButton.setX(leftPos + 75);
        this.editButton.setY(top);
        this.editButton.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.editButton.isHovered()) {
          guiGraphics.renderTooltip(
              AdvancedDialogConfigurationScreen.this.font,
              Component.translatable(
                  Constants.TEXT_CONFIG_PREFIX + "dialog.edit_dialog", dialogData.getName()),
              mouseX,
              mouseY);
        }

        // Render copy button and tooltip
        this.copyLabelButton.setX(this.editButton.getX() + this.editButton.getWidth());
        this.copyLabelButton.setY(top);
        this.copyLabelButton.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.copyLabelButton.isHovered()) {
          guiGraphics.renderTooltip(
              AdvancedDialogConfigurationScreen.this.font,
              Component.translatable(
                  Constants.TEXT_CONFIG_PREFIX + "dialog.copy_dialog_label", dialogData.getLabel()),
              mouseX,
              mouseY);
        }

        // Render edit text button and tooltip
        this.textEditButton.setX(leftPos + 200);
        this.textEditButton.setY(top);
        this.textEditButton.render(guiGraphics, mouseX, mouseY, partialTicks);
        if (this.textEditButton.isHovered()) {
          guiGraphics.renderTooltip(
              AdvancedDialogConfigurationScreen.this.font,
              Component.translatable(
                  Constants.TEXT_CONFIG_PREFIX + "dialog.edit_dialog_text", dialogData.getText()),
              mouseX,
              mouseY);
        }

        // Scale dialog text down
        float dialogDataScale = 0.75f;
        int dialogDataTopPos = Math.round((top + 5) / dialogDataScale);
        int fontColor =
            dialogData.getLabel().equals(this.defaultDialogLabel)
                ? Constants.FONT_COLOR_DARK_GREEN
                : Constants.FONT_COLOR_BLACK;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(dialogDataScale, dialogDataScale, dialogDataScale);
        Text.drawString(
            guiGraphics,
            AdvancedDialogConfigurationScreen.this.font,
            dialogData.getLabel(16),
            Math.round((leftPos + 7) / dialogDataScale),
            dialogDataTopPos,
            fontColor);
        Text.drawString(
            guiGraphics,
            AdvancedDialogConfigurationScreen.this.font,
            dialogData.getName(21),
            Math.round((leftPos + 112) / dialogDataScale),
            dialogDataTopPos,
            fontColor);
        Text.drawString(
            guiGraphics,
            AdvancedDialogConfigurationScreen.this.font,
            dialogData.getText(25),
            Math.round((leftPos + 220) / dialogDataScale),
            dialogDataTopPos,
            fontColor);
        guiGraphics.pose().popPose();

        // Draw separator line
        guiGraphics.fill(
            AdvancedDialogConfigurationScreen.this.leftPos + 5,
            top + 17,
            AdvancedDialogConfigurationScreen.this.leftPos + 314,
            top + 18,
            0xffaaaaaa);
      }
    }
  }
}
