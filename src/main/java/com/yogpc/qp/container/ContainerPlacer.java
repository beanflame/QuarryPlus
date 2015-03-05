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

package com.yogpc.qp.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.yogpc.qp.tile.TilePlacer;

public class ContainerPlacer extends Container {
  private final TilePlacer tile;

  public ContainerPlacer(final IInventory par1IInventory, final TilePlacer par2TileEntityDispenser) {
    this.tile = par2TileEntityDispenser;
    int row;
    int col;

    for (row = 0; row < 3; ++row)
      for (col = 0; col < 3; ++col)
        addSlotToContainer(new Slot(par2TileEntityDispenser, col + row * 3, 62 + col * 18,
            17 + row * 18));

    for (row = 0; row < 3; ++row)
      for (col = 0; col < 9; ++col)
        addSlotToContainer(new Slot(par1IInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

    for (col = 0; col < 9; ++col)
      addSlotToContainer(new Slot(par1IInventory, col, 8 + col * 18, 142));
  }

  @Override
  public boolean canInteractWith(final EntityPlayer par1EntityPlayer) {
    return this.tile.isUseableByPlayer(par1EntityPlayer);
  }

  @Override
  public ItemStack transferStackInSlot(final EntityPlayer ep, final int i) {
    ItemStack src = null;
    final Slot slot = (Slot) this.inventorySlots.get(i);
    if (slot != null && slot.getHasStack()) {
      final ItemStack remain = slot.getStack();
      src = remain.copy();
      if (i < 9) {
        if (!mergeItemStack(remain, 9, 45, true))
          return null;
      } else if (!mergeItemStack(remain, 0, 9, false))
        return null;
      if (remain.stackSize == 0)
        slot.putStack((ItemStack) null);
      else
        slot.onSlotChanged();
      if (remain.stackSize == src.stackSize)
        return null;
      slot.onPickupFromSlot(ep, remain);
    }
    return src;
  }
}
