package io.github.tymogekh.resourcefulslimes.entity.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ItemColoredParticle extends BreakingItemParticle {

    public ItemColoredParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, TextureAtlasSprite atlasSprite, ItemColoredParticleOption options) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, atlasSprite);
        Vector3f vector3f = options.getColor();
        this.rCol = vector3f.x();
        this.gCol = vector3f.y();
        this.bCol = vector3f.z();
    }

    public static class Provider extends ItemParticleProvider<@NotNull ItemColoredParticleOption> implements ParticleProvider<@NotNull ItemColoredParticleOption> {

        @Override
        public @org.jspecify.annotations.Nullable Particle createParticle(ItemColoredParticleOption itemColoredParticleOption, @NotNull ClientLevel clientLevel, double x, double y, double z, double v3, double v4, double v5, @NotNull RandomSource randomSource) {
            Vector3f color = itemColoredParticleOption.getColor();
            ItemColoredParticle particle = new ItemColoredParticle(clientLevel, x, y, z, v3, v4, v5, this.getSprite(itemColoredParticleOption.getItem(), clientLevel, randomSource), itemColoredParticleOption);
            particle.setColor(color.x(), color.y(), color.z());
            return particle;
        }
    }
}