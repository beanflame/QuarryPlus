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

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import com.yogpc.qp.container.ContainerPlacer;
import com.yogpc.qp.tile.TilePlacer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlacer extends GuiContainer {
  private static final ResourceLocation tex = new ResourceLocation(
      "textures/gui/container/dispenser.png");
  public TilePlacer tile;

  public GuiPlacer(final InventoryPlayer par1InventoryPlayer,
      final TilePlacer par2TileEntityDispenser) {
    super(new ContainerPlacer(par1InventoryPlayer, par2TileEntityDispenser));
    this.tile = par2TileEntityDispenser;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
    final String s =
        this.tile.hasCustomInventoryName() ? this.tile.getInventoryName() : StatCollector
            .translateToLocal(this.tile.getInventoryName());
    this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2,
        6, 4210752);
    this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8,
        this.ySize - 96 + 2, 4210752);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(tex);
    final int k = (this.width - this.xSize) / 2;
    final int l = (this.height - this.ySize) / 2;
    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }
}
