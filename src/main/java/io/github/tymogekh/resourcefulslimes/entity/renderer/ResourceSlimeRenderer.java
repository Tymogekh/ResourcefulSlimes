package io.github.tymogekh.resourcefulslimes.entity.renderer;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.entity.layer.ResourceSlimeLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.monster.Slime;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ResourceSlimeRenderer extends SlimeRenderer {

    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/entity/resource_slime.png");

    public ResourceSlimeRenderer(EntityRendererProvider.Context p_174391_) {
        super(p_174391_);
        this.layers.removeLast();
        this.addLayer(new ResourceSlimeLayer(this, p_174391_.getModelSet()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SlimeRenderState p_365351_) {
        return ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/entity/resource_slime.png");
    }

    @Override
    protected int getModelTint(@NotNull SlimeRenderState p_360502_) {
        return ARGB.color(100, ((ResourceSlimeRenderState) p_360502_).color);
    }

    @Override
    public @NotNull SlimeRenderState createRenderState() {
        return new ResourceSlimeRenderState();
    }

    @Override
    public void extractRenderState(@NotNull Slime p_362664_, @NotNull SlimeRenderState p_365237_, float p_361099_) {
        super.extractRenderState(p_362664_, p_365237_, p_361099_);
        ((ResourceSlimeRenderState) p_365237_).color = ((ResourceSlime) p_362664_).getVariant().getColor();
    }
}
