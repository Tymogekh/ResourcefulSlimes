package io.github.tymogekh.resourcefulslimes.entity.particle;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ItemColoredParticleOption implements ParticleOptions {

    private static final Codec<ItemStack> ITEM_CODEC;
    private final int color;
    private final ItemStack stack;

    public ItemColoredParticleOption(int color, ItemStack stack) {
        this.color = color;
        this.stack = stack;
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ResourcefulSlimes.ITEM_COLORED_PARTICLE_TYPE.get();
    }

    public static MapCodec<ItemColoredParticleOption> codec() {
        return RecordCodecBuilder.mapCodec(x -> x.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter((ItemColoredParticleOption y) -> y.color),
                ITEM_CODEC.fieldOf("item").forGetter((ItemColoredParticleOption z) -> z.stack)).apply(x, ItemColoredParticleOption::new));
    }

    public static StreamCodec<? super RegistryFriendlyByteBuf, ItemColoredParticleOption> streamCodec() {
        return StreamCodec.composite(ByteBufCodecs.INT, y -> y.color, ItemStack.STREAM_CODEC, y -> y.stack, ItemColoredParticleOption::new);
    }

    public Vector3f getColor() {
        return ARGB.vector3fFromRGB24(this.color);
    }

    public ItemStack getItem() {
        return this.stack;
    }

    static {
        ITEM_CODEC = Codec.withAlternative(ItemStack.SINGLE_ITEM_CODEC, Item.CODEC, ItemStack::new);
    }
}
