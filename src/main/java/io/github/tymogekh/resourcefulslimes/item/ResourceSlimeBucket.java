package io.github.tymogekh.resourcefulslimes.item;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;


public class ResourceSlimeBucket extends MobBucketItem {

    public ResourceSlimeBucket(Item.Properties properties) {
        super(ResourcefulSlimes.RESOURCE_SLIME.get(), Fluids.EMPTY, SoundEvents.SLIME_SQUISH_SMALL, properties.stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            Player player = context.getPlayer();
            this.spawn((ServerLevel) context.getLevel(), context.getItemInHand(), context.getClickedPos());
            if (player != null) {
                if (!player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
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
}
