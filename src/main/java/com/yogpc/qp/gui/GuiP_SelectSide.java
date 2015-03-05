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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.yogpc.qp.PacketHandler;
import com.yogpc.qp.YogpstopPacket;
import com.yogpc.qp.tile.TilePump;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiP_SelectSide extends GuiScreenA {
  private final TilePump tile;
  private final boolean copy;
  private final byte to;

  public GuiP_SelectSide(final TilePump ptile, final GuiP_List pparent, final boolean pcopy) {
    super(pparent);
    this.tile = ptile;
    this.copy = pcopy;
    this.to = pparent.dir;
  }

  @Override
  public void initGui() {
    super.initGui();
    this.buttonList.add(new GuiButton(ForgeDirection.UP.ordinal(), this.width / 2 - 50,
        this.height / 2 - 60, 100, 20, StatCollector.translateToLocal("FD.UP")));
    this.buttonList.add(new GuiButton(ForgeDirection.DOWN.ordinal(), this.width / 2 - 50,
        this.height / 2 + 40, 100, 20, StatCollector.translateToLocal("FD.DOWN")));
    this.buttonList.add(new GuiButton(ForgeDirection.SOUTH.ordinal(), this.width / 2 - 50,
        this.height / 2 + 15, 100, 20, StatCollector.translateToLocal("FD.SOUTH")));
    this.buttonList.add(new GuiButton(ForgeDirection.NORTH.ordinal(), this.width / 2 - 50,
        this.height / 2 - 35, 100, 20, StatCollector.translateToLocal("FD.NORTH")));
    this.buttonList.add(new GuiButton(ForgeDirection.EAST.ordinal(), this.width / 2 + 40,
        this.height / 2 - 10, 100, 20, StatCollector.translateToLocal("FD.EAST")));
    this.buttonList.add(new GuiButton(ForgeDirection.WEST.ordinal(), this.width / 2 - 140,
        this.height / 2 - 10, 100, 20, StatCollector.translateToLocal("FD.WEST")));
  }

  @Override
  public void actionPerformed(final GuiButton par1) {
    try {
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      final DataOutputStream dos = new DataOutputStream(bos);
      byte c;
      if (this.copy) {
        c = PacketHandler.CtS_COPY_MAPPING;
        dos.writeByte(par1.id);
        dos.writeByte(this.to);
      } else {
        c = PacketHandler.CtS_RENEW_DIRECTION;
        dos.writeByte(par1.id);
      }
      PacketHandler.sendPacketToServer(new YogpstopPacket(bos.toByteArray(), this.tile, c));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void drawScreen(final int i, final int j, final float k) {
    drawDefaultBackground();
    drawCenteredString(this.fontRendererObj,
        StatCollector.translateToLocal(this.copy ? "pp.copy.select" : "pp.set.select"),
        this.width / 2, 8, 0xFFFFFF);
    super.drawScreen(i, j, k);
  }

}
