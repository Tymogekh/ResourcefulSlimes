package io.github.tymogekh.resourcefulslimes.entity;

import io.github.tymogekh.resourcefulslimes.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.config.Config;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeMenu;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.IntFunction;

public class ResourceSlime extends Slime implements Bucketable, HasCustomInventoryScreen, MenuProvider {
    public static final EntityDataAccessor<Byte> RESOURCE = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Integer> SATURATION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Byte> GROWTH = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> SPLITTING = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> HUNGER_REDUCTION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Byte> PRODUCTIVENESS = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    private ParticleOptions particle;

    public ResourceSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RESOURCE, (byte) 0);
        builder.define(SATURATION, 0);
        builder.define(FROM_BUCKET, false);
        builder.define(GROWTH, (byte) 0);
        builder.define(SPLITTING, (byte) 0);
        builder.define(HUNGER_REDUCTION, (byte) 0);
        builder.define(PRODUCTIVENESS, (byte) 0);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("Resource", this.getVariant().getId());
        compound.putInt("Saturation", this.entityData.get(SATURATION));
        compound.putBoolean("FromBucket", this.fromBucket());
        compound.putByte("Growth", this.entityData.get(GROWTH));
        compound.putByte("HungerReduction", this.entityData.get(HUNGER_REDUCTION));
        compound.putByte("Splitting", this.entityData.get(SPLITTING));
        compound.putByte("Productiveness", this.entityData.get(PRODUCTIVENESS));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(SATURATION, compound.getInt("Saturation"));
        this.setVariant(ResourceSlime.Variant.byId(compound.getInt("Resource")));
        this.setFromBucket(compound.getBoolean("FromBucket"));
        this.entityData.set(GROWTH, compound.getByte("Growth"));
        this.entityData.set(HUNGER_REDUCTION, compound.getByte("HungerReduction"));
        this.entityData.set(SPLITTING, compound.getByte("Splitting"));
        this.entityData.set(PRODUCTIVENESS, compound.getByte("Productiveness"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new Slime.SlimeFloatGoal(this));
        this.goalSelector.addGoal(2, new ResourceSlimeFeederGoal(this));
        this.goalSelector.addGoal(3, new Slime.SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new Slime.SlimeKeepOnJumpingGoal(this));
    }

    public void setVariant(Variant variant){
        this.entityData.set(RESOURCE, variant.getId());
    }

    public ResourceSlime.@NotNull Variant getVariant(){
        return ResourceSlime.Variant.byId(this.entityData.get(RESOURCE));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSize() < 4 && this.random.nextInt(Config.GROW_CHANCE_DECREASE.get()) <= this.entityData.get(GROWTH)){
            this.setSize(this.getSize()+1, true);
        } else if(this.entityData.get(SATURATION) >= Config.FOOD_CONSUMPTION.get() - Config.FOOD_CONSUMPTION.get() / 20 * this.entityData.get(HUNGER_REDUCTION) && this.random.nextInt(Config.ITEM_DROP_CHANCE_DECREASE.get()) == 0){
            this.entityData.set(SATURATION, this.entityData.get(SATURATION) - (Config.FOOD_CONSUMPTION.get() - Config.FOOD_CONSUMPTION.get() / 20 * this.entityData.get(HUNGER_REDUCTION)));
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.5F);
            if(!this.level().isClientSide()) {
                this.spawnAtLocation((ServerLevel) this.level(), this.getVariant().getDropItem());
                if (this.random.nextInt(Config.ITEM_DROP_CHANCE_DECREASE.get()) <= this.entityData.get(PRODUCTIVENESS)){
                    this.spawnAtLocation((ServerLevel) this.level(), this.getVariant().getDropItem());
                }
            }
        }
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        if(this.particle == null) {
            this.particle = new ItemParticleOption(ParticleTypes.ITEM, this.getVariant().getIngotOrGem().getDefaultInstance());
        }
        return this.particle;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        if(foodProperties != null && this.entityData.get(SATURATION) < Config.MAX_SATURATION.get()) {
            stack.consume(1, player);
            this.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 1.5F);
            if (this.entityData.get(SATURATION) + foodProperties.nutrition() <= Config.MAX_SATURATION.get()) {
                this.entityData.set(SATURATION, this.entityData.get(SATURATION) + foodProperties.nutrition());
            } else {
                this.entityData.set(SATURATION, Config.MAX_SATURATION.get());
            }
            return InteractionResult.SUCCESS;
        } else if(stack.is(Items.BUCKET) && this.getSize() == 1) {
            this.slimePickup(player, hand);
            return InteractionResult.SUCCESS;
        } else if (stack.is(ItemInit.SLIMEPEDIA)){
            this.openCustomInventoryScreen(player);
            return InteractionResult.SUCCESS;
        } else if (this.getSize() == 1 && this.getVariant().equals(Variant.COBBLESTONE)) {
            List<Variant> present = presentValues();
            for (Variant variant : present) {
                if (stack.is(variant.getConvertItem())) {
                    stack.consume(1, player);
                    this.setVariant(variant);
                    this.particle = new ItemParticleOption(ParticleTypes.ITEM, this.getVariant().getIngotOrGem().getDefaultInstance());
                    this.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0F, 1.0F);
                    break;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_33601_, @NotNull DifficultyInstance p_33602_, @NotNull EntitySpawnReason p_361992_, @Nullable SpawnGroupData p_33604_) {
        if (p_361992_.equals(EntitySpawnReason.SPAWN_ITEM_USE)) {
            ArrayList<Variant> presentValues = presentValues();
            this.setVariant(presentValues.get(this.random.nextInt(presentValues.size())));
        } else if (p_361992_.equals(EntitySpawnReason.BUCKET)){
            this.setSize(1, false);
            return p_33604_;
        } else if (p_361992_.equals(EntitySpawnReason.NATURAL) || p_361992_.equals(EntitySpawnReason.CHUNK_GENERATION)) {
            this.setVariant(Variant.COBBLESTONE);
            if (!(p_33604_ instanceof ResourceSlimeGroupData)) {
                p_33604_ = new ResourceSlimeGroupData(this.getVariant());
            }
        }
        return super.finalizeSpawn(p_33601_, p_33602_, p_361992_, p_33604_);
    }

    public static ArrayList<Variant> presentValues(){
        ArrayList<Variant> list = new ArrayList<>();
        for(Variant variant : ResourceSlime.Variant.values()){
            Iterable<Holder<Item>> iterable = BuiltInRegistries.ITEM.getTagOrEmpty(variant.getResourceTag());
            if (iterable.spliterator().getExactSizeIfKnown() >= 1 || !variant.isModded()){
                list.add(variant);
            }
        }
        return list;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        int i = this.getSize();
        Variant variant = this.getVariant();
        byte growth = this.entityData.get(GROWTH);
        byte splitting = this.entityData.get(SPLITTING);
        byte hunger_reduction = this.entityData.get(HUNGER_REDUCTION);
        byte productiveness = this.entityData.get(PRODUCTIVENESS);
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            float f = this.getDimensions(this.getPose()).width();
            float f1 = f / 2.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3 + splitting / 5);
            PlayerTeam playerteam = this.getTeam();
            ArrayList<Mob> children = new ArrayList<>();
            this.preventConversionSpawns = true;

            for(int l = 0; l < k; ++l) {
                float f2 = ((float)(l % 2) - 0.5F) * f1;
                float f3 = ((float)(l / 2) - 0.5F) * f1;
                ResourceSlime slime = this.convertTo(ResourcefulSlimes.RESOURCE_SLIME.get(), new ConversionParams(ConversionType.SPLIT_ON_DEATH, false, false, playerteam), EntitySpawnReason.TRIGGERED, (p_381514_) -> {
                    p_381514_.setSize(j, true);
                    p_381514_.setVariant(variant);
                    p_381514_.entityData.set(GROWTH, growth);
                    p_381514_.entityData.set(HUNGER_REDUCTION, hunger_reduction);
                    p_381514_.entityData.set(SPLITTING, splitting);
                    p_381514_.entityData.set(PRODUCTIVENESS, productiveness);
                    p_381514_.moveTo(this.getX() + (double)f2, this.getY() + 0.5, this.getZ() + (double)f3, this.random.nextFloat() * 360.0F, 0.0F);
                    if (this.random.nextInt(Config.MUTATION_CHANCE_DECREASE.get()) == 0) {
                        int stat = this.random.nextInt(3);
                        switch (stat){
                            case 0:
                                if (growth < 10){
                                p_381514_.entityData.set(GROWTH, (byte) (growth + 1));
                                }
                                break;
                            case 1:
                                if (splitting < 10) {
                                    p_381514_.entityData.set(SPLITTING, (byte) (splitting + 1));
                                }
                                break;
                            case 2:
                                if (hunger_reduction < 10) {
                                    p_381514_.entityData.set(HUNGER_REDUCTION, (byte) (hunger_reduction + 1));
                                }
                                break;
                            case 3:
                                if (productiveness < 10) {
                                    p_381514_.entityData.set(PRODUCTIVENESS, (byte) (productiveness + 1));
                                }
                                break;
                        }
                    }
                });
                children.add(slime);
            }

            this.preventConversionSpawns = false;
            if (!EventHooks.onMobSplit(this, children).isCanceled()) {
                Level var10001 = this.level();
                Objects.requireNonNull(var10001);
                children.forEach(var10001::addFreshEntity);
            }
        }
        if ((reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED) && !this.level().isClientSide()) {
            this.triggerOnDeathMobEffects((ServerLevel) this.level(), reason);
        }
        this.setRemoved(reason);
        this.brain.clearMemories();
    }

    private void slimePickup(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && this.isAlive()) {
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = this.getBucketItemStack();
            this.saveToBucketTag(itemstack1);
            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
            player.setItemInHand(hand, itemstack2);
            this.discard();
        }
    }

    private static Optional<BlockPos> findNearestFeeder(BlockPos pos, Level level){
        int[] xyz = {pos.getX(), pos.getY(), pos.getZ()};
        ArrayList<int[]> visited = new ArrayList<>();
        ArrayList<int[]> queue = new ArrayList<>();
        BlockPos.MutableBlockPos checkedPos = new BlockPos.MutableBlockPos();
        queue.add(xyz.clone());
        while (!queue.isEmpty()) {
            for (int i = 0; i <= 2; ++i) {
                for (int j : new int[] {-1, 1}) {
                    final int[] current = queue.getFirst().clone();
                    current[i] += j;
                    checkedPos.set(current[0], current[1], current[2]);
                    if (pos.closerThan(checkedPos, 10)) {
                        if (level.getBlockEntity(checkedPos) instanceof SlimeFeederBlockEntity) {
                            return Optional.of(checkedPos);
                        } else if (visited.stream().noneMatch(x -> Arrays.equals(x, current)) && !level.getBlockState(checkedPos).isViewBlocking(level, checkedPos)) {
                            visited.add(current.clone());
                            queue.addLast(current.clone());
                        }
                    }
                }
            }
            queue.removeFirst();
        }
        return Optional.empty();
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected boolean isDealsDamage() {
        return false;
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean b) {
        this.entityData.set(FROM_BUCKET, b);
    }

    @Override
    public void saveToBucketTag(@NotNull ItemStack itemStack) {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, itemStack, compoundTag -> {
            compoundTag.putByte("Variant", this.getVariant().getId());
            compoundTag.putInt("Saturation", this.entityData.get(SATURATION));
            compoundTag.putByte("Growth", this.entityData.get(GROWTH));
            compoundTag.putByte("HungerReduction", this.entityData.get(HUNGER_REDUCTION));
            compoundTag.putByte("Splitting", this.entityData.get(SPLITTING));
            compoundTag.putByte("Productiveness", this.entityData.get(PRODUCTIVENESS));
        });
    }

    @Override
    public void loadFromBucketTag(@NotNull CompoundTag compoundTag) {
        if (compoundTag.getByte("Variant").isPresent()) {
            this.setVariant(Variant.byId(compoundTag.getByte("Variant").get()));
        }
        if (compoundTag.getInt("Saturation").isPresent()) {
            this.entityData.set(SATURATION, compoundTag.getInt("Saturation").get());
        }
        if (compoundTag.getByte("Growth").isPresent()) {
            this.entityData.set(GROWTH, compoundTag.getByte("Growth").get());
        }
        if (compoundTag.getByte("HungerReduction").isPresent()) {
            this.entityData.set(HUNGER_REDUCTION, compoundTag.getByte("HungerReduction").get());
        }
        if (compoundTag.getByte("Splitting").isPresent()) {
            this.entityData.set(SPLITTING, compoundTag.getByte("Splitting").get());
        }
        if (compoundTag.getByte("Productiveness").isPresent()) {
            this.entityData.set(PRODUCTIVENESS, compoundTag.getByte("Productiveness").get());
        };
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(ItemInit.RESOURCE_SLIME_BUCKET);
    }

    @Override
    public @NotNull SoundEvent getPickupSound() {
        return SoundEvents.SLIME_JUMP_SMALL;
    }

    @Override
    public void openCustomInventoryScreen(@NotNull Player player) {
        if (!this.level().isClientSide()) {
            player.openMenu(this, buf -> buf.writeInt(this.getId()));
        }
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ResourceSlimeMenu(ResourcefulSlimes.RESOURCE_SLIME_MENU.get(), i, this);
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor level, @NotNull EntitySpawnReason spawnReason) {
        if (spawnReason.equals(EntitySpawnReason.NATURAL) || spawnReason.equals(EntitySpawnReason.CHUNK_GENERATION)) {
            return (int) (Math.random()*Config.RESOURCE_SLIME_SPAWNS_RARITY.get()) == 0;
        }
        return super.checkSpawnRules(level, spawnReason);
    }

    public enum Variant implements StringRepresentable {
        COBBLESTONE((byte) 0, Tags.Items.COBBLESTONES, "cobblestone", 0x888788, ItemInit.COBBLESTONE_SLIME_BALL.get(), Tags.Items.COBBLESTONES, Items.COBBLESTONE, true),
        IRON((byte) 1, Tags.Items.INGOTS_IRON, "iron", 0xd8d8d8, ItemInit.IRON_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_IRON,  Items.IRON_INGOT, true),
        GOLD((byte) 2, Tags.Items.INGOTS_GOLD, "gold", 0xf6ea20, ItemInit.GOLD_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_GOLD, Items.GOLD_INGOT, true),
        COPPER((byte) 3, Tags.Items.INGOTS_COPPER, "copper", 0xe17c52, ItemInit.COPPER_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_COPPER, Items.COPPER_INGOT, true),
        NETHERITE((byte) 4, Tags.Items.ORES_NETHERITE_SCRAP, "netherite_scrap", 0x624740, ItemInit.NETHERITE_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_NETHERITE,  Items.NETHERITE_SCRAP, true),
        LAPIS((byte) 5, Tags.Items.GEMS_LAPIS, "lapis_lazuli", 0x425ec4, ItemInit.LAPIS_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_LAPIS, Items.LAPIS_LAZULI, true),
        REDSTONE((byte) 6, Tags.Items.DUSTS_REDSTONE, "redstone", 0xa31803, ItemInit.REDSTONE_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_REDSTONE, Items.REDSTONE, true),
        EMERALD((byte) 7, Tags.Items.GEMS_EMERALD, "emerald", 0x45dc5e, ItemInit.EMERALD_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_EMERALD, Items.EMERALD, true),
        DIAMOND((byte) 8, Tags.Items.GEMS_DIAMOND, "diamond", 0x68ecd8, ItemInit.DIAMOND_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_DIAMOND, Items.DIAMOND,true),
        QUARTZ((byte) 9, Tags.Items.GEMS_QUARTZ, "quartz", 0xe4dfd6, ItemInit.QUARTZ_SLIME_BALL.get(), Tags.Items.GEMS_QUARTZ, Items.QUARTZ, true),
        COAL((byte) 10, Tags.Items.ORES_COAL, "coal", 0x2e2e2e, ItemInit.COAL_SLIME_BALL.get(), Tags.Items.STORAGE_BLOCKS_COAL, Items.COAL,true),
        AMETHYST((byte) 11, Tags.Items.GEMS_AMETHYST, "amethyst", 0x8d6bcd, ItemInit.AMETHYST_SLIME_BALL.get(), Tags.Items.GEMS_AMETHYST, Items.AMETHYST_SHARD, true),
        NICKEL((byte) 12, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/nickel")), "nickel", 0xbabc94, ItemInit.NICKEL_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/nickel")), ItemInit.NICKEL_INGOT.get(), false),
        SILVER((byte) 13, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/silver")), "silver", 0x7ec3c3, ItemInit.SILVER_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/silver")), ItemInit.SILVER_INGOT.get(), false),
        LEAD((byte) 14, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/lead")), "lead", 0x4f8bb1, ItemInit.LEAD_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/lead")), ItemInit.LEAD_INGOT.get(), false),
        ZINC((byte) 15, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/zinc")), "zinc", 0xbfcece, ItemInit.ZINC_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/zinc")), ItemInit.ZINC_INGOT.get(), false),
        URANIUM((byte) 16, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/uranium")), "uranium", 0xbbba63, ItemInit.URANIUM_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/uranium")), ItemInit.URANIUM_INGOT.get(), false),
        TIN((byte) 17, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/tin")), "tin", 0x85c3ca, ItemInit.TIN_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/tin")), ItemInit.TIN_INGOT.get(), false),
        ALUMINIUM((byte) 18, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/aluminium")), "aluminium", 0xadadad, ItemInit.ALUMINIUM_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/aluminium")), ItemInit.ALUMINIUM_INGOT.get(), false),
        OSMIUM((byte) 19, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/osmium")), "osmium", 0x9ec6c7, ItemInit.OSMIUM_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:storage_blocks/osmium")), ItemInit.OSMIUM_INGOT.get(), false),
        CERTUS_QUARTZ((byte) 20, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:gems/certus_quartz")), "certus_quartz", 0x9df6ff, ItemInit.CERTUS_QUARTZ_SLIME_BALL.get(), TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:gems/certus_quartz")), ItemInit.CERTUS_QUARTZ.get(), false);


        private static final IntFunction<ResourceSlime.Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final TagKey<Item> resourceTag;
        private final byte id;
        private final String name;
        private final int color;
        private final Item dropItem;
        private final boolean isVanilla;
        private final TagKey<Item> converts;
        private final Item ingotOrGem;
        private final Component displayName;

        Variant(byte id, TagKey<Item> resource_tag, String name, int color, Item drop, TagKey<Item> converts, Item ingot_or_gem, boolean is_vanilla){
            this.resourceTag = resource_tag;
            this.id = id;
            this.name = name;
            this.color = color;
            this.dropItem = drop;
            this.converts = converts;
            this.isVanilla = is_vanilla;
            this.ingotOrGem = ingot_or_gem;
            this.displayName = Component.translatable("entity.resourcefulslimes.resource_slime.variant." + name);
        }

        public static Variant byId(int id){
            return BY_ID.apply(id);
        }

        public byte getId(){
            return this.id;
        }

        public TagKey<Item> getResourceTag(){
            return this.resourceTag;
        }

        public int getColor(){
            return this.color;
        }

        public Item getDropItem(){
            return this.dropItem;
        }

        public TagKey<Item> getConvertItem() {return this.converts;}

        public Item getIngotOrGem(){return this.ingotOrGem;}

        public boolean isModded(){
            return !this.isVanilla;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public Component getDisplayName(){
            return this.displayName;
        }
    }

    protected record ResourceSlimeGroupData(Variant variant) implements SpawnGroupData {}

    class ResourceSlimeFeederGoal extends Goal {

        private int giveUpTimer;
        private int cooldown = 1;
        private BlockPos nearestFeederPos;
        private SlimeFeederBlockEntity feeder;
        private final ResourceSlime slime;

        public ResourceSlimeFeederGoal(ResourceSlime slime){
            super();
            this.setFlags(EnumSet.of(Flag.LOOK));
            this.slime = slime;
        }

        @Override
        public boolean canUse() {
            --this.cooldown;
            if (this.cooldown <= 0) {
                this.cooldown = 1000;
                Optional<BlockPos> optional = findNearestFeeder(this.slime.blockPosition(), ResourceSlime.this.level());
                if (optional.isPresent()) {
                    this.nearestFeederPos = optional.get();
                    this.feeder = (SlimeFeederBlockEntity) this.slime.level().getBlockEntity(this.nearestFeederPos);
                    return this.slime.entityData.get(SATURATION) < Config.MAX_SATURATION.get() && this.feeder != null && this.feeder.getNutrition() > 0;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.slime.entityData.get(SATURATION) < Config.MAX_SATURATION.get() && this.giveUpTimer > 0 && this.feeder != null && this.feeder.getNutrition() > 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.giveUpTimer = Config.GIVE_UP_TIMER.get();
            super.start();
        }

        @Override
        public void tick() {
            --this.giveUpTimer;
            if(this.nearestFeederPos != null && this.feeder != null && this.feeder.getNutrition() > 0) {
                this.slime.lookAt(EntityAnchorArgument.Anchor.FEET, this.nearestFeederPos.getBottomCenter());
                ((Slime.SlimeMoveControl) this.slime.moveControl).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
                if (this.feeder != null && this.slime.blockPosition().closerThan(this.nearestFeederPos, 2)) {
                    int slimeHunger = Config.MAX_SATURATION.get() - this.slime.entityData.get(SATURATION);
                    if (this.feeder.getNutrition() - slimeHunger > 0) {
                        this.slime.entityData.set(SATURATION, this.slime.entityData.get(SATURATION) + slimeHunger);
                        this.feeder.shrinkNutrition(slimeHunger);
                    } else {
                        this.slime.entityData.set(SATURATION, this.slime.entityData.get(SATURATION) + this.feeder.getNutrition());
                        this.feeder.setNutrition(0);
                        SlimeFeederBlock.changeBlockState(this.slime.level(), this.feeder.getBlockState(), this.feeder.getBlockPos(), false);
                        Objects.requireNonNull(this.feeder.getLevel()).invalidateCapabilities(this.feeder.getBlockPos());
                    }
                    this.slime.playSound(SoundEvents.GENERIC_EAT.value(), 1.0F, 1.5F);
                    this.feeder.setChanged();
                    Objects.requireNonNull(this.feeder.getLevel()).sendBlockUpdated(this.feeder.getBlockPos(), this.feeder.getBlockState(), this.feeder.getBlockState(), 0);
                }
            }
        }
    }
}
