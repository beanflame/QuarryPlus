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

package com.yogpc.qp.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.yogpc.qp.QuarryPlusI;
import com.yogpc.qp.container.ContainerEnchList;
import com.yogpc.qp.container.ContainerMover;
import com.yogpc.qp.container.ContainerPlacer;
import com.yogpc.qp.container.ContainerWorkbench;
import com.yogpc.qp.tile.TileBasic;
import com.yogpc.qp.tile.TilePlacer;
import com.yogpc.qp.tile.TilePump;
import com.yogpc.qp.tile.TileWorkbench;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
  @Override
  public Object getClientGuiElement(final int ID, final EntityPlayer p, final World w, final int x,
      final int y, final int z) {
    switch (ID) {
      case QuarryPlusI.guiIdMover:
        return new GuiMover(p, w, x, y, z);
      case QuarryPlusI.guiIdFList:
        return new GuiEnchList((byte) 0, (TileBasic) w.getTileEntity(x, y, z));
      case QuarryPlusI.guiIdSList:
        return new GuiEnchList((byte) 1, (TileBasic) w.getTileEntity(x, y, z));
      case QuarryPlusI.guiIdPlacer:
        return new GuiPlacer(p.inventory, (TilePlacer) w.getTileEntity(x, y, z));
      case QuarryPlusI.guiIdPump:
      case QuarryPlusI.guiIdPump + 1:
      case QuarryPlusI.guiIdPump + 2:
      case QuarryPlusI.guiIdPump + 3:
      case QuarryPlusI.guiIdPump + 4:
      case QuarryPlusI.guiIdPump + 5:
        return new GuiP_List((byte) (ID - QuarryPlusI.guiIdPump), (TilePump) w.getTileEntity(x, y,
            z));
      case QuarryPlusI.guiIdWorkbench:
        return new GuiWorkbench(p.inventory, (TileWorkbench) w.getTileEntity(x, y, z));
    }

    return null;
  }

  @Override
  public Object getServerGuiElement(final int ID, final EntityPlayer p, final World w, final int x,
      final int y, final int z) {
    switch (ID) {
      case QuarryPlusI.guiIdMover:
        return new ContainerMover(p.inventory, w, x, y, z);
      case QuarryPlusI.guiIdFList:
      case QuarryPlusI.guiIdSList:
        return new ContainerEnchList((TileBasic) w.getTileEntity(x, y, z));
      case QuarryPlusI.guiIdPlacer:
        return new ContainerPlacer(p.inventory, (TilePlacer) w.getTileEntity(x, y, z));
      case QuarryPlusI.guiIdWorkbench:
        return new ContainerWorkbench(p.inventory, (TileWorkbench) w.getTileEntity(x, y, z));
    }
    return null;
  }
}
