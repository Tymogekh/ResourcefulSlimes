package io.github.tymogekh.resourcefulslimes.item;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

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
}
