package io.github.tymogekh.resourcefulslimes.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ResourceSlimeLayer<T extends LivingEntity> extends RenderLayer<T, SlimeModel<T>> {
    private final SlimeModel<T> slimeModel;

    public ResourceSlimeLayer(RenderLayerParent<T, SlimeModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.slimeModel = new SlimeModel<>(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int color = ((ResourceSlime) livingEntity).getVariant().getColor();
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(livingEntity) && livingEntity.isInvisible();
        if (!livingEntity.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(livingEntity)));
            } else {
                vertexconsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(livingEntity)));
            }
            this.getParentModel().copyPropertiesTo(this.slimeModel);
            this.slimeModel.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
            this.slimeModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.slimeModel.renderToBuffer(poseStack, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F), FastColor.ARGB32.opaque(color));
        }
    }
}

