package io.github.tymogekh.resourcefulslimes.item;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;


public class ResourceSlimeBucket extends MobBucketItem {

    public ResourceSlimeBucket() {
        super(ResourcefulSlimes.RESOURCE_SLIME.get(), Fluids.EMPTY, SoundEvents.SLIME_SQUISH_SMALL, new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if(!context.getLevel().isClientSide()) {
            Player player = context.getPlayer();
            this.spawn((ServerLevel) context.getLevel(), context.getItemInHand(), context.getClickedPos());
            if(player != null && !player.isCreative()) {
                context.getItemInHand().shrink(1);
                player.addItem(new ItemStack(Items.BUCKET));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    private void spawn(ServerLevel serverLevel, ItemStack bucketedMobStack, BlockPos pos) {
        ResourceSlime slime = ResourcefulSlimes.RESOURCE_SLIME.get().spawn(serverLevel, bucketedMobStack, null, pos, MobSpawnType.BUCKET, true, false);
        CustomData customdata = bucketedMobStack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
        if(slime != null) {
            slime.loadFromBucketTag(customdata.copyTag());
            slime.setFromBucket(true);
        }
    }
}
