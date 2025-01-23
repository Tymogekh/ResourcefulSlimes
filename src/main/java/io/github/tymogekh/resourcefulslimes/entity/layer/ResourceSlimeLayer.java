package io.github.tymogekh.resourcefulslimes.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderState;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderer;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.SlimeRenderState;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;

public class ResourceSlimeLayer extends RenderLayer<SlimeRenderState, SlimeModel> {
    private final SlimeModel slimeModel;

    public ResourceSlimeLayer(ResourceSlimeRenderer renderer, EntityModelSet modelSet) {
        super(renderer);
        this.slimeModel = new SlimeModel(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i, @NotNull SlimeRenderState slimeRenderState, float v, float v1) {
        boolean flag = slimeRenderState.appearsGlowing && slimeRenderState.isInvisible;
        if (!slimeRenderState.isInvisible || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = multiBufferSource.getBuffer(RenderType.outline(ResourceSlimeRenderer.TEXTURE_LOCATION));
            } else {
                vertexconsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(ResourceSlimeRenderer.TEXTURE_LOCATION));
            }
            this.slimeModel.setupAnim(slimeRenderState);
            this.slimeModel.renderToBuffer(poseStack, vertexconsumer, i, LivingEntityRenderer.getOverlayCoords(slimeRenderState, 0.0F), ARGB.opaque(((ResourceSlimeRenderState) slimeRenderState).color));
        }
    }
}

