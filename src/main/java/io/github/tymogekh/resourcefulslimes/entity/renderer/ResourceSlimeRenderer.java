package io.github.tymogekh.resourcefulslimes.entity.renderer;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.layer.ResourceSlimeLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.resources.ResourceLocation;
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
    public @NotNull ResourceLocation getTextureLocation(@NotNull Slime entity) {
        return ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/entity/resource_slime.png");
    }
}
