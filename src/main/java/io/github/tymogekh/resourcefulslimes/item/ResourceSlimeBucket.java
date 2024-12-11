package io.github.tymogekh.resourcefulslimes.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class ResourceSlimeBucket extends MobBucketItem {

    public ResourceSlimeBucket() {
        super(ResourcefulSlimes.RESOURCE_SLIME.get(), Fluids.EMPTY, SoundEvents.SLIME_SQUISH_SMALL, new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "resource_slime_bucket"))));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if(!context.getLevel().isClientSide()) {
            Player player = context.getPlayer();
            this.spawn((ServerLevel) context.getLevel(), context.getItemInHand(), context.getClickedPos());
            if(player != null) {
                context.getItemInHand().shrink(1);
                player.addItem(new ItemStack(Items.BUCKET));
                player.swing(context.getHand());
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void spawn(ServerLevel serverLevel, ItemStack bucketedMobStack, BlockPos pos) {
        ResourceSlime slime = ResourcefulSlimes.RESOURCE_SLIME.get().spawn(serverLevel, bucketedMobStack, null, pos, EntitySpawnReason.BUCKET, true, false);
        CustomData customdata = bucketedMobStack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
        if(slime != null) {
            slime.loadFromBucketTag(customdata.copyTag());
            slime.setFromBucket(true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        CustomData customData = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if(tag.contains("Variant")){
            ResourceSlime.Variant variant = ResourceSlime.Variant.byId(tag.getByte("Variant"));
            ChatFormatting[] formatting = new ChatFormatting[]{ChatFormatting.GRAY};
            tooltipComponents.add(((MutableComponent) variant.getDisplayName()).withStyle(formatting));
        }
    }

    public record VariantTint(int defaultColor) implements ItemTintSource {

        public static final MapCodec<VariantTint> MAP_CODEC = RecordCodecBuilder.mapCodec(variantTintInstance -> variantTintInstance.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(VariantTint::defaultColor)).apply(variantTintInstance, VariantTint::new)
        );

        @Override
        public int calculate(@NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
            CompoundTag tag = Objects.requireNonNull(itemStack.get(DataComponents.BUCKET_ENTITY_DATA)).copyTag();
            return tag.contains("Variant") ? ARGB.opaque(ResourceSlime.Variant.byId(tag.getByte("Variant")).getColor()) : ARGB.opaque(defaultColor);
        }

        @Override
        public @NotNull MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }
}
