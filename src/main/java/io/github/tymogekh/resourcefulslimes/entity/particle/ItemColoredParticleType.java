package io.github.tymogekh.resourcefulslimes.entity.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class ItemColoredParticleType extends ParticleType<@NotNull ItemColoredParticleOption> {
    public ItemColoredParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public @NotNull MapCodec<ItemColoredParticleOption> codec() {
        return ItemColoredParticleOption.codec();
    }

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ItemColoredParticleOption> streamCodec() {
        return ItemColoredParticleOption.streamCodec();
    }
}
