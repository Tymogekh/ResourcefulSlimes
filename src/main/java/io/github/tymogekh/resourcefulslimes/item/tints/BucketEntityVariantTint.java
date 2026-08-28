package io.github.tymogekh.resourcefulslimes.item.tints;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record BucketEntityVariantTint(int defaultColor) implements ItemTintSource {

    public static final MapCodec<BucketEntityVariantTint> MAP_CODEC = RecordCodecBuilder.mapCodec(variantTintInstance -> variantTintInstance.group(
            ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(BucketEntityVariantTint::defaultColor)).apply(variantTintInstance, BucketEntityVariantTint::new)
    );

    @Override
    public int calculate(@NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        CompoundTag tag = itemStack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
        RegistryAccess registryAccess = Objects.requireNonNull(Minecraft.getInstance().getConnection()).registryAccess();
        return tag.contains("variant") && tag.read("variant", Identifier.CODEC).isPresent() ? ARGB.opaque(registryAccess.getOrThrow(ResourceKey.create(ResourceSlime.Variant.REGISTRY_KEY, tag.read("variant", Identifier.CODEC).get())).value().tint()) : ARGB.opaque(this.defaultColor);
    }

    @Override
    public @NotNull MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
