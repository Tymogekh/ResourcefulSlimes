package io.github.tymogekh.resourcefulslimes.item.tints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public record ResourceSlimeBallTint(int defaultColor) implements ItemTintSource {
    public static final MapCodec<ResourceSlimeBallTint> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(ResourceSlimeBallTint::defaultColor)
            ).apply(instance, ResourceSlimeBallTint::new));

    @Override
    public int calculate(@NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        ResourceSlime.Variant variant = itemStack.get(ResourcefulSlimes.RESOURCE_SLIME_VARIANT.get());
        return variant != null ? ARGB.opaque(variant.tint()) : this.defaultColor;
    }

    @Override
    public @NotNull MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
