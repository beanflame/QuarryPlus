package com.yogpc.qp.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerDrawHandler;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;

import com.yogpc.qp.WorkbenchRecipe;
import com.yogpc.qp.gui.GuiWorkbench;

public class WBPRecipeHandler extends TemplateRecipeHandler {
  // All offset is (x, y) = (5, 11)
  private class WBPRecipe extends TemplateRecipeHandler.CachedRecipe {
    final ArrayList<PositionedStack> input = new ArrayList<PositionedStack>();
    private final PositionedStack output;
    final double energy;

    WBPRecipe(final WorkbenchRecipe wbr) {
      this.energy = wbr.power;
      this.output = new PositionedStack(wbr.output, 3, 79);
      int row = 0;
      int col = 0;
      for (final WorkbenchRecipe.WBIS is : wbr.input) {
        if (is.getAmount() <= 0)
          continue;
        this.input.add(new PositionedStack(is.getItemStack(), 3 + col * 18, 7 + row * 18));
        col++;
        if (col >= 9) {
          row++;
          col = 0;
        }
      }
    }

    @Override
    public PositionedStack getResult() {
      return this.output;
    }

    @Override
    public List<PositionedStack> getIngredients() {
      return this.input;
    }
  }

  @Override
  public String getRecipeName() {
    return StatCollector.translateToLocal("tile.WorkbenchPlus.name");
  }

  @Override
  public String getGuiTexture() {
    return "yogpstop_qp:textures/gui/workbench.png";
  }

  @Override
  public Class<? extends GuiContainer> getGuiClass() {
    return GuiWorkbench.class;
  }

  @Override
  public int recipiesPerPage() {
    return 1;
  }

  @Override
  public void drawBackground(final int recipe) {
    GL11.glColor4f(1, 1, 1, 1);
    GuiDraw.changeTexture(getGuiTexture());
    GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 121);
  }

  @Override
  public void loadCraftingRecipes(final String outputId, final Object... results) {
    if (outputId.equals("workbenchPlus") && getClass() == WBPRecipeHandler.class)
      for (final WorkbenchRecipe wbr : WorkbenchRecipe.getRecipes())
        this.arecipes.add(new WBPRecipe(wbr));
    else
      super.loadCraftingRecipes(outputId, results);
  }

  @Override
  public void loadCraftingRecipes(final ItemStack result) {
    for (final WorkbenchRecipe wbr : WorkbenchRecipe.getRecipes())
      if (NEIServerUtils.areStacksSameTypeCrafting(wbr.output, result))
        this.arecipes.add(new WBPRecipe(wbr));
  }

  @Override
  public void loadUsageRecipes(final ItemStack ingredient) {
    for (final WorkbenchRecipe wbr : WorkbenchRecipe.getRecipes()) {
      final WBPRecipe recipe = new WBPRecipe(wbr);
      if (recipe.contains(recipe.input, ingredient))
        this.arecipes.add(recipe);
    }
  }

  @Override
  public void drawExtras(final int recipeIndex) {
    drawProgressBar(3, 67, 0, 222, 160, 4, 40, 0);
    final WBPRecipe recipe = (WBPRecipe) this.arecipes.get(recipeIndex);
    Minecraft.getMinecraft().fontRenderer.drawString(Double.toString(recipe.energy) + "MJ", 3, 121,
        0x404040);
  }

  @Override
  public void loadTransferRects() {
    this.transferRects.add(new RecipeTransferRect(new Rectangle(2, 66, 162, 6), "workbenchPlus"));
  }

  static {
    GuiContainerManager.addDrawHandler(new IContainerDrawHandler() {
      @Override
      public void onPreDraw(final GuiContainer arg0) {}

      @Override
      public void postRenderObjects(final GuiContainer arg0, final int arg1, final int arg2) {}

      @Override
      public void renderObjects(final GuiContainer arg0, final int arg1, final int arg2) {}

      @Override
      public void renderSlotUnderlay(final GuiContainer arg0, final Slot arg1) {
        if (arg0 instanceof GuiRecipe
            && ((GuiRecipe) arg0).getCurrentRecipeHandlers().get(((GuiRecipe) arg0).recipetype) instanceof WBPRecipeHandler
            && arg1.getHasStack())
          GuiWorkbench.handlePre();
      }

      @Override
      public void renderSlotOverlay(final GuiContainer arg0, final Slot arg1) {
        if (arg0 instanceof GuiRecipe
            && ((GuiRecipe) arg0).getCurrentRecipeHandlers().get(((GuiRecipe) arg0).recipetype) instanceof WBPRecipeHandler
            && arg1.getHasStack())
          GuiWorkbench.handlePost();
      }
    });
  }
}
