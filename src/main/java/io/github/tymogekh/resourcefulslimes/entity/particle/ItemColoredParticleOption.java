package io.github.tymogekh.resourcefulslimes.entity.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStackTemplate;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ItemColoredParticleOption implements ParticleOptions {

    private final int color;
    private final ItemStackTemplate item;

    public ItemColoredParticleOption(int color, ItemStackTemplate item) {
        this.color = color;
        this.item = item;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ResourcefulSlimes.ITEM_COLORED_PARTICLE_TYPE.get();
    }

    public static MapCodec<ItemColoredParticleOption> codec() {
        return RecordCodecBuilder.mapCodec(x -> x.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter((ItemColoredParticleOption y) -> y.color),
                ItemStackTemplate.CODEC.fieldOf("item").forGetter((ItemColoredParticleOption z) -> z.item)).apply(x, ItemColoredParticleOption::new));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ItemColoredParticleOption> streamCodec() {
        return StreamCodec.composite(ByteBufCodecs.INT, x -> x.color, ItemStackTemplate.STREAM_CODEC, x -> x.item, ItemColoredParticleOption::new);
    }

    public Vector3f getColor() {
        return ARGB.vector3fFromRGB24(this.color);
    }

    public ItemStackTemplate getItem() {
        return this.item;
    }
}
