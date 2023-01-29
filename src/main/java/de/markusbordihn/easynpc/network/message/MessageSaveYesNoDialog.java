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

package de.markusbordihn.easynpc.network.message;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.dialog.DialogType;
import de.markusbordihn.easynpc.entity.EasyNPCEntity;
import de.markusbordihn.easynpc.entity.EntityManager;

public class MessageSaveYesNoDialog {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final String uuid;
  protected final String dialog;
  protected final String yesDialog;
  protected final String noDialog;
  protected final String yesButtonText;
  protected final String noButtonText;

  public MessageSaveYesNoDialog(String uuid, String dialog, String yesDialog, String noDialog,
      String yesButtonText, String noButtonText) {
    this.uuid = uuid;
    this.dialog = dialog;
    this.yesDialog = yesDialog;
    this.noDialog = noDialog;
    this.yesButtonText = yesButtonText;
    this.noButtonText = noButtonText;
  }

  public String getDialog() {
    return this.dialog;
  }

  public String getYesDialog() {
    return this.yesDialog;
  }

  public String getNoDialog() {
    return this.noDialog;
  }

  public String getYesButtonText() {
    return this.yesButtonText;
  }

  public String getNoButtonText() {
    return this.noButtonText;
  }

  public String getUUID() {
    return this.uuid;
  }

  public static void handle(MessageSaveYesNoDialog message,
      Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> handlePacket(message, context));
    context.setPacketHandled(true);
  }

  public static void handlePacket(MessageSaveYesNoDialog message, NetworkEvent.Context context) {
    ServerPlayer serverPlayer = context.getSender();
    if (serverPlayer == null) {
      log.error("Unable to get server player for message {} from {}", message, context);
      return;
    }

    // Check for access.
    UUID uuid = UUID.fromString(message.getUUID());
    if (!EntityManager.hasAccess(uuid, serverPlayer)) {
      log.warn("User {} has no access to Easy NPC with uuid {}.", serverPlayer, uuid);
      return;
    }

    // Validate dialog.
    String dialog = message.getDialog();
    if (dialog == null) {
      log.error("Invalid dialog {} for yes/no dialog and {} from {}", dialog, message,
          serverPlayer);
      return;
    }

    // Validate Yes dialog.
    String yesDialog = message.getYesDialog();
    if (yesDialog == null) {
      log.error("Invalid yes dialog {} for yes/no dialog and {} from {}", yesDialog, message,
          serverPlayer);
      return;
    }

    // Validate No dialog.
    String noDialog = message.getNoDialog();
    if (noDialog == null) {
      log.error("Invalid no dialog {} for yes/no dialog and {} from {}", noDialog, message,
          serverPlayer);
      return;
    }

    // Validate entity.
    EasyNPCEntity easyNPCEntity = EntityManager.getEasyNPCEntityByUUID(uuid, serverPlayer);
    if (easyNPCEntity == null) {
      log.error("Unable to get valid entity with UUID {} for {}", uuid, serverPlayer);
      return;
    }

    // Perform action.
    String yesButtonText = message.getYesButtonText();
    String noButtonText = message.getNoButtonText();

    log.debug("Saving yes/no dialog {} [{}] -> {} [{}] -> {}: {} for {} from {}", dialog,
        yesButtonText, yesDialog, noButtonText, noDialog, easyNPCEntity, serverPlayer);
    easyNPCEntity.setDialogType(DialogType.YES_NO);
    easyNPCEntity.setDialog(dialog);
    easyNPCEntity.setYesDialog(yesDialog);
    easyNPCEntity.setNoDialog(noDialog);
    if (yesButtonText != null && !yesButtonText.isBlank()) {
      easyNPCEntity.setYesDialogButton(yesButtonText);
    }
    if (noButtonText != null && !noButtonText.isBlank()) {
      easyNPCEntity.setNoDialogButton(noButtonText);
    }
  }

}