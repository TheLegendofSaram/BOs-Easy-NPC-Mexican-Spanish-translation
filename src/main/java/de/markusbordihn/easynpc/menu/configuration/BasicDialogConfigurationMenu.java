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

package de.markusbordihn.easynpc.menu.configuration;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import de.markusbordihn.easynpc.menu.ModMenuTypes;

public class BasicDialogConfigurationMenu extends DialogConfigurationMenu {

  public BasicDialogConfigurationMenu(int windowId, Inventory playerInventory, UUID uuid) {
    this(ModMenuTypes.BASIC_DIALOG_CONFIGURATION_MENU.get(), windowId, playerInventory, uuid);
  }

  public BasicDialogConfigurationMenu(int windowId, Inventory playerInventory,
      FriendlyByteBuf data) {
    this(windowId, playerInventory, data.readUUID());
  }

  public BasicDialogConfigurationMenu(final MenuType<?> menuType, final int windowId,
      final Inventory playerInventory, UUID uuid) {
    super(menuType, windowId, playerInventory, uuid);
    log.debug("Open basic dialog configuration menu for {}: {}", this.uuid, this.entity);
  }

  @Override
  public boolean stillValid(Player player) {
    return player != null && player.isAlive() && entity != null && entity.isAlive();
  }

}