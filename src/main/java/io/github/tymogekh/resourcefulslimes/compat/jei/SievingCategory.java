package io.github.tymogekh.resourcefulslimes.compat.jei;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SievingCategory implements IRecipeCategory<Sieving> {

    private final IDrawable icon;
    private final IDrawableStatic progress_sprite;
    private final IGuiHelper helper;

    public SievingCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemLike(ResourcefulSlimes.SLIME_SIEVE_BLOCK);
        this.progress_sprite = helper.drawableBuilder(Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/sprites/container/slime_sieve/sieving_progress.png"),
                0, 0, 21, 17).setTextureSize(21, 17).build();
        this.helper = helper;
    }

    @Override
    public @NotNull IRecipeType<Sieving> getRecipeType() {
        return SlimesJEIPlugin.SIEVING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("recipe." + ResourcefulSlimes.MOD_ID + ".sieving");
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 166;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(@NotNull Sieving recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_sieve_no_inv.png"),
                0, 0, 0, 0, 176, 82, 256, 256);
        guiGraphics.text(Minecraft.getInstance().font, "Chance: " + recipe.getChance(), 65, 20, 0xFF4a4a4a, false);
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull Sieving recipe, @NotNull IFocusGroup focuses) {
        IDrawableAnimated animated_progress = this.helper.createAnimatedDrawable(this.progress_sprite, recipe.getTicks(), IDrawableAnimated.StartDirection.LEFT, false);
        builder.addDrawable(animated_progress, 81, 34);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Sieving recipe, @NotNull IFocusGroup focuses) {
        builder.addInputSlot(55, 35).addItemStacks(List.of(recipe.getIngredient().getValues().get(0).value().getDefaultInstance()));
        builder.addOutputSlot(116, 35).addItemStacks(List.of(recipe.getResult().create().copy()));
    }
}
