package io.github.tymogekh.resourcefulslimes.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderState;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.slime.SlimeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public class ResourceSlimeLayer extends RenderLayer<@NotNull SlimeRenderState, @NotNull SlimeModel> {
    private final SlimeModel slimeModel;

    public ResourceSlimeLayer(ResourceSlimeRenderer renderer, EntityModelSet modelSet) {
        super(renderer);
        this.slimeModel = new SlimeModel(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void submit(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector submitNodeCollector, int i, SlimeRenderState slimeRenderState, float v, float v1) {
        boolean flag = slimeRenderState.isInvisible && slimeRenderState.appearsGlowing();
        if (!slimeRenderState.isInvisible || flag) {
            RenderType renderType = flag ? RenderTypes.outline(ResourceSlimeRenderer.TEXTURE_LOCATION) : RenderTypes.entityTranslucent(ResourceSlimeRenderer.TEXTURE_LOCATION);
            int tint = slimeRenderState.isInvisible ? -1 : ARGB.opaque(((ResourceSlimeRenderState) slimeRenderState).color);
            this.slimeModel.setupAnim(slimeRenderState);
            submitNodeCollector.order(1).submitModel(this.slimeModel,
                    slimeRenderState, poseStack, renderType, i, LivingEntityRenderer.getOverlayCoords(slimeRenderState, 0.0F),
                    tint, null, slimeRenderState.outlineColor, null);
        }
    }
}

