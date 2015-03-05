/*
 * Copyright (C) 2012,2013 yogpstop This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.yogpc.qp;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import com.yogpc.qp.item.IEnchantableItem;

public class SlotMover extends Slot {
  public SlotMover(final IInventory par1iInventory, final int par2, final int par3, final int par4) {
    super(par1iInventory, par2, par3, par4);
  }

  @Override
  public boolean isItemValid(final ItemStack is) {
    switch (this.slotNumber) {
      case 0:
        final NBTTagList nbttl = is.getEnchantmentTagList();
        if (nbttl != null
            && (is.getItem() instanceof net.minecraft.item.ItemTool
                && ((net.minecraft.item.ItemTool) is.getItem()).getToolMaterialName() == Item.ToolMaterial.EMERALD
                    .toString() || is.getItem() instanceof ItemBow))
          return true;
        return false;
      case 1:
        if (is.getItem() instanceof IEnchantableItem)
          return true;
    }
    return false;
  }

  @Override
  public int getSlotStackLimit() {
    return 1;
  }
}
