/*
 * Copyright 2022 Markus Bordihn
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

package de.markusbordihn.easynpc.utils;

import de.markusbordihn.easynpc.data.scale.CustomScale;
import net.minecraft.nbt.CompoundTag;

public class CompoundTagUtils {

  public static final String X_TAG = "X";
  public static final String Y_TAG = "Y";
  public static final String Z_TAG = "Z";

  private CompoundTagUtils() {}

  public static CompoundTag writeScale(float x, float y, float z) {
    CompoundTag compoundTag = new CompoundTag();
    compoundTag.putFloat(X_TAG, x);
    compoundTag.putFloat(Y_TAG, y);
    compoundTag.putFloat(Z_TAG, z);
    return compoundTag;
  }

  public static CompoundTag writeCustomScale(CustomScale customScale) {
    return writeScale(customScale.x(), customScale.y(), customScale.z());
  }

  public static CustomScale readCustomScale(CompoundTag compoundTag) {
    if (compoundTag == null) {
      return null;
    }
    return new CustomScale(
        compoundTag.getFloat(X_TAG), compoundTag.getFloat(Y_TAG), compoundTag.getFloat(Z_TAG));
  }
}
