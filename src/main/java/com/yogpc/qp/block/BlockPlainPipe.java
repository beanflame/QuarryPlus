package com.yogpc.qp.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class BlockPlainPipe extends Block {
  public BlockPlainPipe() {
    super(Material.glass);
    this.minX = 0.25;
    this.minY = 0.0;
    this.minZ = 0.25;
    this.maxX = 0.75;
    this.maxY = 1.0;
    this.maxZ = 0.75;
    setBlockName("qpPlainPipe");
    setBlockTextureName("yogpstop_qp:blockPlainPipe");
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public Item getItemDropped(final int i, final Random random, final int fortune) {
    return null;
  }
}
