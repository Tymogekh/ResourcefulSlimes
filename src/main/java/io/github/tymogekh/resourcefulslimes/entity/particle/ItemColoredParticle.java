package io.github.tymogekh.resourcefulslimes.entity.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ItemColoredParticle extends BreakingItemParticle {

    public ItemColoredParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ItemStackRenderState renderState, ItemColoredParticleOption options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, renderState);
        Vector3f vector3f = options.getColor();
        this.rCol = vector3f.x();
        this.gCol = vector3f.y();
        this.bCol = vector3f.z();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider extends ItemParticleProvider<ItemColoredParticleOption> implements ParticleProvider<ItemColoredParticleOption> {

        @Override
        public @Nullable Particle createParticle(@NotNull ItemColoredParticleOption itemColoredParticleOption, @NotNull ClientLevel clientLevel, double x, double y, double z, double v3, double v4, double v5) {
            Vector3f color = itemColoredParticleOption.getColor();
            ItemColoredParticle particle = new ItemColoredParticle(clientLevel, x, y, z, v3, v4, v5, this.calculateState(itemColoredParticleOption.getItem(), clientLevel), itemColoredParticleOption);
            particle.setColor(color.x(), color.y(), color.z());
            return particle;
        }
    }
}