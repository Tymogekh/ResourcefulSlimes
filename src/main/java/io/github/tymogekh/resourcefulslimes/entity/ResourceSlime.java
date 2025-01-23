package io.github.tymogekh.resourcefulslimes.entity;

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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

public class ResourceSlime extends Slime implements Bucketable, VariantHolder<ResourceSlime.Variant>, HasCustomInventoryScreen, MenuProvider {
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
            this.particle = new ItemParticleOption(ParticleTypes.ITEM, this.getVariant().getDropItem().getDefaultInstance());
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
        } else if (stack.is(ResourcefulSlimes.SLIMEPEDIA)){
            this.openCustomInventoryScreen(player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_33601_, @NotNull DifficultyInstance p_33602_, @NotNull EntitySpawnReason p_361992_, @Nullable SpawnGroupData p_33604_) {
        if(p_361992_.equals(EntitySpawnReason.SPAWN_ITEM_USE)) {
            ArrayList<Variant> presentValues = presentValues();
            this.setVariant(presentValues.get(this.random.nextInt(presentValues.size())));
        } else if(p_361992_.equals(EntitySpawnReason.BUCKET)){
            this.setSize(1, false);
            return p_33604_;
        } else if(p_361992_.equals(EntitySpawnReason.NATURAL) || p_361992_.equals(EntitySpawnReason.CHUNK_GENERATION)) {
            Holder<Biome> holder = p_33601_.getBiome(this.blockPosition());
            ArrayList<Variant> variants = new ArrayList<>();
            for(Variant variant : presentValues()){
                if(holder.is(variant.getSpawnBiomeTag())){
                    variants.add(variant);
                }
            }
            if(!variants.isEmpty()) {
                Variant setVariant = spawnTie(variants);
                p_33604_ = new ResourceSlimeGroupData(setVariant);
                this.setVariant(setVariant);
            } else {
                this.remove(RemovalReason.DISCARDED);
            }
        }
        return super.finalizeSpawn(p_33601_, p_33602_, p_361992_, p_33604_);
    }

    public static ArrayList<Variant> presentValues(){
        ArrayList<Variant> list = new ArrayList<>();
        for(Variant variant : ResourceSlime.Variant.values()){
            Iterable<Holder<Item>> iterable = BuiltInRegistries.ITEM.getTagOrEmpty(variant.getResourceTag());
            if (iterable.spliterator().getExactSizeIfKnown() > 1 || !variant.isModded()){
                list.add(variant);
            }
        }
        return list;
    }

    private static Variant spawnTie(ArrayList<Variant> variants){
        Variant variant1 = variants.getFirst();
        Variant variant2 = variants.getLast();
        if(variant2.equals(variant1)){
            return variant1;
        } else if((int)(Math.random()*2) == 0){
            return variant2;
        } else {
            return variant1;
        }
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
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for(int i = x - 10; i <= x + 10; ++i){
            for(int j = z - 10; j <= z + 10; ++j){
                for(int k = y - 1; k <= y + 5; ++k){
                    mutableBlockPos.set(i, k, j);
                    if(pos.closerThan(mutableBlockPos, 10) && level.getBlockEntity(mutableBlockPos) instanceof SlimeFeederBlockEntity){
                        return Optional.of(mutableBlockPos);
                    }
                }
            }
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
        this.setVariant(Variant.byId(compoundTag.getByte("Variant")));
        this.entityData.set(SATURATION, compoundTag.getInt("Saturation"));
        this.entityData.set(GROWTH, compoundTag.getByte("Growth"));
        this.entityData.set(HUNGER_REDUCTION, compoundTag.getByte("HungerReduction"));
        this.entityData.set(SPLITTING, compoundTag.getByte("Splitting"));
        this.entityData.set(PRODUCTIVENESS, compoundTag.getByte("Productiveness"));
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(ResourcefulSlimes.RESOURCE_SLIME_BUCKET);
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


    public enum Variant implements StringRepresentable {
        IRON((byte) 0, Tags.Items.INGOTS_IRON, "iron", 0xd8d8d8, Items.IRON_INGOT, Tags.Biomes.IS_MOUNTAIN,  true),
        GOLD((byte) 1, Tags.Items.INGOTS_GOLD, "gold", 0xf6ea20, Items.GOLD_INGOT, Tags.Biomes.IS_TAIGA, true),
        COPPER((byte) 2, Tags.Items.INGOTS_COPPER, "copper", 0xe17c52, Items.COPPER_INGOT, Tags.Biomes.IS_PLAINS, true),
        NETHERITE((byte) 3, Tags.Items.ORES_NETHERITE_SCRAP, "netherite_scrap", 0x624740, Items.NETHERITE_SCRAP, Tags.Biomes.IS_DRY_NETHER,  true),
        LAPIS((byte) 4, Tags.Items.GEMS_LAPIS, "lapis_lazuli", 0x425ec4, Items.LAPIS_LAZULI, Tags.Biomes.IS_SNOWY, true),
        REDSTONE((byte) 5, Tags.Items.DUSTS_REDSTONE, "redstone", 0xa31803, Items.REDSTONE, Tags.Biomes.IS_BADLANDS, true),
        EMERALD((byte) 6, Tags.Items.GEMS_EMERALD, "emerald", 0x45dc5e, Items.EMERALD, Tags.Biomes.IS_JUNGLE, true),
        DIAMOND((byte) 7, Tags.Items.GEMS_DIAMOND, "diamond", 0x68ecd8, Items.DIAMOND, Tags.Biomes.IS_ICY,true),
        QUARTZ((byte) 8, Tags.Items.GEMS_QUARTZ, "quartz", 0xe4dfd6, Items.QUARTZ, Tags.Biomes.IS_WET_NETHER, true),
        COAL((byte) 9, Tags.Items.ORES_COAL, "coal", 0x2e2e2e, Items.COAL, Tags.Biomes.IS_DESERT,true),
        AMETHYST((byte) 10, Tags.Items.GEMS_AMETHYST, "amethyst", 0x8d6bcd, Items.AMETHYST_SHARD, Tags.Biomes.IS_CAVE, true),
        NICKEL((byte) 11, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/nickel")), "nickel", 0xbabc94, ResourcefulSlimes.NICKEL_INGOT.get(), Tags.Biomes.IS_DESERT,false),
        SILVER((byte) 12, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/silver")), "silver", 0x7ec3c3, ResourcefulSlimes.SILVER_INGOT.get(), Tags.Biomes.IS_ICY,false),
        LEAD((byte) 13, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/lead")), "lead", 0x4f8bb1, ResourcefulSlimes.LEAD_INGOT.get(), Tags.Biomes.IS_SNOWY,false),
        ZINC((byte) 14, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/zinc")), "zinc", 0xbfcece, ResourcefulSlimes.ZINC_INGOT.get(), Tags.Biomes.IS_PLAINS, false),
        URANIUM((byte) 15, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/uranium")), "uranium", 0xbbba63, ResourcefulSlimes.URANIUM_INGOT.get(), Tags.Biomes.IS_JUNGLE,false),
        TIN((byte) 16, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/tin")), "tin", 0x85c3ca, ResourcefulSlimes.TIN_INGOT.get(), Tags.Biomes.IS_MOUNTAIN, false),
        ALUMINIUM((byte) 17, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/aluminium")), "aluminium", 0xadadad, ResourcefulSlimes.ALUMINIUM_INGOT.get(), Tags.Biomes.IS_TAIGA, false),
        OSMIUM((byte) 18, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/osmium")), "osmium", 0x9ec6c7, ResourcefulSlimes.OSMIUM_INGOT.get(), Tags.Biomes.IS_BADLANDS,false),
        CERTUS_QUARTZ((byte) 19, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:gems/certus_quartz")), "certus_quartz", 0x9df6ff, ResourcefulSlimes.CERTUS_QUARTZ.get(), Tags.Biomes.IS_CAVE, false);


        private static final IntFunction<ResourceSlime.Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final TagKey<Item> resourceTag;
        private final byte id;
        private final String name;
        private final int color;
        private final Item dropItem;
        private final boolean isVanilla;
        private final TagKey<Biome> spawnsIn;
        private final Component displayName;

        Variant(byte id, TagKey<Item> resource_tag, String name, int color, Item drop, TagKey<Biome> spawnsIn, boolean is_vanilla){
            this.resourceTag = resource_tag;
            this.id = id;
            this.name = name;
            this.color = color;
            this.dropItem = drop;
            this.spawnsIn = spawnsIn;
            this.isVanilla = is_vanilla;
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

        public TagKey<Biome> getSpawnBiomeTag(){
            return this.spawnsIn;
        }

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

    public record ResourceSlimeGroupData(Variant variant) implements SpawnGroupData {}

    class ResourceSlimeFeederGoal extends Goal {

        private int giveUpTimer;
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
            Optional<BlockPos> optional = findNearestFeeder(this.slime.blockPosition(), ResourceSlime.this.level());
            if(optional.isPresent()) {
                this.nearestFeederPos = optional.get();
                this.feeder = (SlimeFeederBlockEntity) this.slime.level().getBlockEntity(this.nearestFeederPos);
                return this.slime.entityData.get(SATURATION) <= Config.MAX_SATURATION.get() && this.feeder != null && this.feeder.getNutrition() > 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.slime.entityData.get(SATURATION) <= Config.MAX_SATURATION.get() && this.giveUpTimer > 0 && this.feeder != null && this.feeder.getNutrition() > 0;
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
