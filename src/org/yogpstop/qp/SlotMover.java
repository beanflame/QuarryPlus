package org.yogpstop.qp;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotMover extends Slot {
    Container parentsC;

    public SlotMover(IInventory par1iInventory, int par2, int par3, int par4, Container c) {
        super(par1iInventory, par2, par3, par4);
        this.parentsC = c;

    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        switch (this.slotNumber) {
        case 0:
            if (par1ItemStack.itemID == Item.pickaxeDiamond.itemID) { return true; }
            return false;
        case 1:
            if (par1ItemStack.itemID == QuarryPlus.blockQuarry.blockID) { return true; }
        }
        return false;
    }

    @Override
    public void onSlotChanged() {
        this.parentsC.onCraftMatrixChanged(this.inventory);
        super.onSlotChanged();
    }
}