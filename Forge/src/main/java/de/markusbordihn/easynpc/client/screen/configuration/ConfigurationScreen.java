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

package de.markusbordihn.easynpc.client.screen.configuration;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.client.screen.components.CloseButton;
import de.markusbordihn.easynpc.client.screen.components.Text;
import de.markusbordihn.easynpc.client.screen.components.TextButton;
import de.markusbordihn.easynpc.config.CommonConfig;
import de.markusbordihn.easynpc.data.skin.SkinModel;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ConfigurationData;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerData;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationMenu;
import de.markusbordihn.easynpc.menu.configuration.ConfigurationType;
import de.markusbordihn.easynpc.network.NetworkMessageHandler;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ConfigurationScreen<T extends ConfigurationMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  protected final ClientLevel clientLevel;
  protected final LocalPlayer localPlayer;
  protected final Minecraft minecraftInstance;
  protected final EasyNPC<?> easyNPC;
  protected final SkinModel skinModel;
  protected final UUID uuid;
  protected final LivingEntity owner;
  protected final String ownerName;
  protected final boolean hasOwner;
  protected final boolean supportsPoseConfiguration;
  protected final boolean supportsStandardPoseConfiguration;
  protected final boolean supportsAdvancedPoseConfiguration;
  protected final boolean supportsCustomPoseConfiguration;
  protected final boolean supportsScalingConfiguration;
  protected final boolean supportsSkinConfiguration;
  protected final boolean supportsNoneSkinConfiguration;
  protected final boolean supportsDefaultSkinConfiguration;
  protected final boolean supportsUrlSkinConfiguration;
  protected final boolean supportsPlayerSkinConfiguration;
  protected final boolean supportsCustomSkinConfiguration;
  protected Button closeButton = null;
  protected Button homeButton = null;
  protected float xMouse;
  protected float yMouse;
  protected int bottomPos;
  protected int buttonLeftPos;
  protected int buttonTopPos;
  protected int contentLeftPos;
  protected int contentTopPos;
  protected int rightPos;

  public ConfigurationScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);

    // NPC Entity Data
    this.easyNPC = menu.getEasyNPC();
    this.skinModel = this.easyNPC.getEasyNPCSkinData().getSkinModel();
    this.uuid = this.easyNPC.getUUID();

    // Owner Data
    OwnerData<?> ownerData = this.easyNPC.getEasyNPCOwnerData();
    this.hasOwner = ownerData.hasOwner();
    this.owner = this.hasOwner ? ownerData.getOwner() : null;
    this.ownerName = this.hasOwner ? ownerData.getOwnerName() : "";

    // General environment Data
    this.minecraftInstance = Minecraft.getInstance();
    this.localPlayer = this.minecraftInstance.player;
    this.clientLevel = this.minecraftInstance.level;

    // Supported configuration types
    ConfigurationData<?> configurationData = this.easyNPC.getEasyNPCConfigurationData();
    this.supportsPoseConfiguration = configurationData.supportsPoseConfiguration();
    this.supportsStandardPoseConfiguration = configurationData.supportsStandardPoseConfiguration();
    this.supportsAdvancedPoseConfiguration = configurationData.supportsAdvancedPoseConfiguration();
    this.supportsCustomPoseConfiguration = configurationData.supportsCustomPoseConfiguration();
    this.supportsScalingConfiguration = configurationData.supportsScalingConfiguration();
    this.supportsSkinConfiguration = configurationData.supportsSkinConfiguration();
    this.supportsNoneSkinConfiguration = configurationData.supportsNoneSkinConfiguration();
    this.supportsCustomSkinConfiguration = configurationData.supportsCustomSkinConfiguration();
    this.supportsDefaultSkinConfiguration = configurationData.supportsDefaultSkinConfiguration();
    this.supportsPlayerSkinConfiguration = configurationData.supportsPlayerSkinConfiguration();
    this.supportsUrlSkinConfiguration = configurationData.supportsUrlSkinConfiguration();
  }

  protected static Double getDoubleValue(String value) {
    if (value != null && !value.isEmpty()) {
      try {
        return Double.parseDouble(value);
      } catch (NumberFormatException e) {
        log.error("Failed to parse double value: {}", value);
      }
    }
    return null;
  }

  protected static boolean isFloatValue(String text) {
    return text != null
        && (text.isEmpty()
            || (text.matches("^\\d+(\\.?\\d*)?$") && Float.parseFloat(text) >= 0.0F));
  }

  protected static boolean isPositiveNumericValue(String text) {
    return text != null
        && (text.isEmpty() || (text.matches("^\\d+$") && Integer.parseInt(text) > 0));
  }

  protected static boolean isNumericValue(String text) {
    return text != null
        && (text.isEmpty() || (text.matches("^\\d+$") && Integer.parseInt(text) >= 0));
  }

  public void closeScreen() {
    if (this.minecraftInstance != null) {
      this.minecraftInstance.setScreen(null);
    }
  }

  public void showMainScreen() {
    NetworkMessageHandler.openConfiguration(uuid, ConfigurationType.MAIN);
  }

  protected boolean hasPermissions(Boolean enabled, Boolean allowInCreative, int permissionLevel) {
    if (Boolean.FALSE.equals(enabled)) {
      return false;
    } else if (Boolean.TRUE.equals(allowInCreative && this.localPlayer != null)
        && this.localPlayer.isCreative()) {
      return true;
    }
    return this.localPlayer != null && this.localPlayer.hasPermissions(permissionLevel);
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageHeight = 243;
    this.imageWidth = 318;

    // Core Positions
    this.titleLabelX = 8;
    this.titleLabelY = 7;
    this.topPos = ((this.height - this.imageHeight) / 2) + 2;
    this.leftPos = (this.width - this.imageWidth) / 2;
    this.rightPos = this.leftPos + this.imageWidth;
    this.bottomPos = this.topPos + this.imageHeight;
    this.buttonLeftPos = this.leftPos + 17;
    this.buttonTopPos = this.topPos + 17;
    this.contentLeftPos = this.leftPos + 7;
    this.contentTopPos = this.topPos + 43;

    // Close Button
    this.closeButton =
        this.addRenderableWidget(
            new CloseButton(this.rightPos - 15, this.topPos + 4, onPress -> closeScreen()));

    // Home Button
    this.homeButton =
        this.addRenderableWidget(
            new TextButton(
                this.leftPos + 7,
                this.buttonTopPos,
                10,
                "<",
                onPress -> NetworkMessageHandler.openConfiguration(uuid, ConfigurationType.MAIN)));
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, x, y, partialTicks);
    this.xMouse = x;
    this.yMouse = y;
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
    Text.drawString(
        guiGraphics, this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    // Main screen: top left
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, leftPos, topPos, 0, 0, 210, 160);

    // Main screen: top right
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, leftPos + 203, topPos, 132, 0, 120, 160);

    // Main screen: bottom left
    guiGraphics.blit(Constants.TEXTURE_DEMO_BACKGROUND, leftPos, topPos + 77, 0, 5, 210, 170);

    // Main screen: bottom right
    guiGraphics.blit(
        Constants.TEXTURE_DEMO_BACKGROUND, leftPos + 203, topPos + 77, 132, 5, 120, 170);
  }

  @Override
  public boolean keyPressed(int keyCode, int unused1, int unused2) {
    if (keyCode != 257 && keyCode != 335 && keyCode != 69 && keyCode != 73) {
      return super.keyPressed(keyCode, unused1, unused2);
    }
    return keyCode == 257 || keyCode == 335 || keyCode == 73;
  }
}
