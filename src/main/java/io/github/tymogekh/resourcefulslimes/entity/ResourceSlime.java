package io.github.tymogekh.resourcefulslimes.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.config.Config;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeMenu;
import io.github.tymogekh.resourcefulslimes.entity.particle.ItemColoredParticleOption;
import io.github.tymogekh.resourcefulslimes.init.MenuInit;
import io.github.tymogekh.resourcefulslimes.util.SlimeUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ResourceSlime extends Slime implements Bucketable, HasCustomInventoryScreen, MenuProvider {
    public static final EntityDataAccessor<Holder<Variant>> RESOURCE_VARIANT = SynchedEntityData.defineId(ResourceSlime.class, ResourcefulSlimes.RESOURCE_SLIME_VARIANT_SERIALIZER.get());
    public static final EntityDataAccessor<Integer> SATURATION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> GROWTH = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SPLITTING = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> HUNGER_REDUCTION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> PRODUCTIVENESS = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    private ParticleOptions particle;

    public ResourceSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RESOURCE_VARIANT, VariantUtils.getDefaultOrAny(this.level().registryAccess(), Variant.EMPTY));
        builder.define(SATURATION, 0);
        builder.define(FROM_BUCKET, false);
        builder.define(GROWTH, 0);
        builder.define(SPLITTING, 0);
        builder.define(HUNGER_REDUCTION, 0);
        builder.define(PRODUCTIVENESS, 0);
    }


    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        VariantUtils.writeVariant(valueOutput, this.getVariant());
        valueOutput.putInt("Saturation", this.entityData.get(SATURATION));
        valueOutput.putBoolean("FromBucket", this.fromBucket());
        valueOutput.putInt("Growth", this.entityData.get(GROWTH));
        valueOutput.putInt("HungerReduction", this.entityData.get(HUNGER_REDUCTION));
        valueOutput.putInt("Splitting", this.entityData.get(SPLITTING));
        valueOutput.putInt("Productiveness", this.entityData.get(PRODUCTIVENESS));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        VariantUtils.readVariant(valueInput, Variant.REGISTRY_KEY).ifPresent(this::setVariant);
        if (valueInput.getInt("Saturation").isPresent()) {
            this.entityData.set(SATURATION, valueInput.getInt("Saturation").get());
        }

        if (valueInput.getInt("Growth").isPresent()) {
            this.entityData.set(GROWTH, valueInput.getInt("Growth").get());
        }
        if (valueInput.getInt("HungerReduction").isPresent()) {
            this.entityData.set(HUNGER_REDUCTION, valueInput.getInt("HungerReduction").get());
        }
        if (valueInput.getInt("Splitting").isPresent()) {
            this.entityData.set(SPLITTING, valueInput.getInt("Splitting").get());
        }
        if (valueInput.getInt("Productiveness").isPresent()) {
            this.entityData.set(PRODUCTIVENESS, valueInput.getInt("Productiveness").get());
        }
        this.setFromBucket(valueInput.getBooleanOr("FromBucket", false));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AbstractCubeMob.CubeMobFloatGoal(this));
        this.goalSelector.addGoal(2, new ResourceSlimeFeederGoal(this));
        this.goalSelector.addGoal(3, new AbstractCubeMob.CubeMobRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new AbstractCubeMob.CubeMobKeepOnJumpingGoal(this));
    }

    public void setVariant(Holder<Variant> variant){
        this.entityData.set(RESOURCE_VARIANT, variant);
    }

    public Holder<Variant> getVariant(){
        return this.entityData.get(RESOURCE_VARIANT);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSize() < 4 && this.random.nextInt(Config.GROW_CHANCE_DECREASE.get()) <= this.entityData.get(GROWTH)){
            this.setSize(this.getSize()+1, true);
        } else if(this.entityData.get(SATURATION) >= Config.FOOD_CONSUMPTION.get() - Config.FOOD_CONSUMPTION.get() / 20 * this.entityData.get(HUNGER_REDUCTION) && this.random.nextInt(Config.ITEM_DROP_CHANCE_DECREASE.get()) == 0){
            this.entityData.set(SATURATION, this.entityData.get(SATURATION) - (Config.FOOD_CONSUMPTION.get() - Config.FOOD_CONSUMPTION.get() / 20 * this.entityData.get(HUNGER_REDUCTION)));
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.5F);
            ItemStack dropItem = new ItemStack(ItemInit.RESOURCE_SLIME_BALL.get());
            dropItem.set(ResourcefulSlimes.RESOURCE_SLIME_VARIANT.get(), this.getVariant().value());
            if(!this.level().isClientSide()) {
                this.spawnAtLocation((ServerLevel) this.level(), dropItem);
                if (this.random.nextInt(Config.ITEM_DROP_CHANCE_DECREASE.get()) <= this.entityData.get(PRODUCTIVENESS)){
                    this.spawnAtLocation((ServerLevel) this.level(), dropItem);
                }
            }
        }
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        if(this.particle == null) {
            this.particle = new ItemColoredParticleOption(this.getVariant().value().tint(), new ItemStackTemplate(ItemInit.RESOURCE_SLIME_BALL));
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
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_33601_, @NotNull DifficultyInstance p_33602_, @NotNull EntitySpawnReason p_361992_, @Nullable SpawnGroupData p_33604_) {
        if (p_361992_.equals(EntitySpawnReason.SPAWN_ITEM_USE)) {
            Registry<Variant> registry = p_33601_.registryAccess().lookupOrThrow(Variant.REGISTRY_KEY);
            Optional<Holder.Reference<Variant>> variantOptional = registry.getRandom(this.random);
            while (variantOptional.isPresent() && variantOptional.get().is(Variant.EMPTY)) {
                variantOptional = registry.getRandom(this.random);
            }
            variantOptional.ifPresent(this::setVariant);
        } else if (p_361992_.equals(EntitySpawnReason.BUCKET)){
            this.setSize(1, false);
            return p_33604_;
        }
        return super.finalizeSpawn(p_33601_, p_33602_, p_361992_, p_33604_);
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentGetter components) {
        this.applyImplicitComponentIfPresent(components, ResourcefulSlimes.RESOURCE_SLIME_VARIANT.get());
        super.applyImplicitComponents(components);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        int i = this.getSize();
        Holder<Variant> variant = this.getVariant();
        int growth = this.entityData.get(GROWTH);
        int splitting = this.entityData.get(SPLITTING);
        int hunger_reduction = this.entityData.get(HUNGER_REDUCTION);
        int productiveness = this.entityData.get(PRODUCTIVENESS);
        if (!this.level().isClientSide() && i > 1 && this.isDeadOrDying()) {
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
                    p_381514_.snapTo(this.getX() + (double)f2, this.getY() + 0.5, this.getZ() + (double)f3, this.random.nextFloat() * 360.0F, 0.0F);
                    if (this.random.nextInt(Config.MUTATION_CHANCE_DECREASE.get()) == 0) {
                        int stat = this.random.nextInt(3);
                        switch (stat){
                            case 0:
                                if (growth < 10){
                                p_381514_.entityData.set(GROWTH, growth + 1);
                                }
                                break;
                            case 1:
                                if (splitting < 10) {
                                    p_381514_.entityData.set(SPLITTING, splitting + 1);
                                }
                                break;
                            case 2:
                                if (hunger_reduction < 10) {
                                    p_381514_.entityData.set(HUNGER_REDUCTION, hunger_reduction + 1);
                                }
                                break;
                            case 3:
                                if (productiveness < 10) {
                                    p_381514_.entityData.set(PRODUCTIVENESS, productiveness + 1);
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
            ItemStack itemStack1 = this.getBucketItemStack();
            this.saveToBucketTag(itemStack1);
            ItemStack itemStack2 = ItemUtils.createFilledResult(itemstack, player, itemStack1, false);
            player.setItemInHand(hand, itemStack2);
            this.discard();
        }
    }

    private static Optional<BlockPos> findClosestFeeder(BlockPos pos, Level level){
        int[] xyz = {pos.getX(), pos.getY(), pos.getZ()};
        List<int[]> visited = new ArrayList<>();
        List<int[]> queue = new ArrayList<>();
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
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, itemStack, valueInputTag -> {
            this.getVariant().unwrapKey().ifPresent(key -> valueInputTag.store("variant", Identifier.CODEC, key.identifier()));
            valueInputTag.putInt("Saturation", this.entityData.get(SATURATION));
            valueInputTag.putInt("Growth", this.entityData.get(GROWTH));
            valueInputTag.putInt("HungerReduction", this.entityData.get(HUNGER_REDUCTION));
            valueInputTag.putInt("Splitting", this.entityData.get(SPLITTING));
            valueInputTag.putInt("Productiveness", this.entityData.get(PRODUCTIVENESS));
        });
    }

    @Override
    public void loadFromBucketTag(@NotNull CompoundTag valueInputTag) {
        valueInputTag.read("variant", Identifier.CODEC).ifPresent(identifier ->
                this.entityData.set(RESOURCE_VARIANT, this.level().registryAccess().getOrThrow(ResourceKey.create(Variant.REGISTRY_KEY, identifier))));
        if (valueInputTag.getInt("Saturation").isPresent()) {
            this.entityData.set(SATURATION, valueInputTag.getInt("Saturation").get());
        }
        if (valueInputTag.getInt("Growth").isPresent()) {
            this.entityData.set(GROWTH, valueInputTag.getInt("Growth").get());
        }
        if (valueInputTag.getInt("HungerReduction").isPresent()) {
            this.entityData.set(HUNGER_REDUCTION, valueInputTag.getInt("HungerReduction").get());
        }
        if (valueInputTag.getInt("Splitting").isPresent()) {
            this.entityData.set(SPLITTING, valueInputTag.getInt("Splitting").get());
        }
        if (valueInputTag.getInt("Productiveness").isPresent()) {
            this.entityData.set(PRODUCTIVENESS, valueInputTag.getInt("Productiveness").get());
        }
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(ItemInit.RESOURCE_SLIME_BUCKET.asItem());
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
        return new ResourceSlimeMenu(MenuInit.RESOURCE_SLIME_MENU.get(), i, this);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("gui." + ResourcefulSlimes.MOD_ID + ".resourceSlime");
    }

    public record Variant(String name, int tint) {
        public static final ResourceKey<Registry<Variant>> REGISTRY_KEY = ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "slime_variant"));

        public static final Codec<Variant> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(ExtraCodecs.NON_EMPTY_STRING.fieldOf("name").forGetter(Variant::name),
                        ExtraCodecs.RGB_COLOR_CODEC.fieldOf("tint").forGetter(Variant::tint))
                        .apply(instance, Variant::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Variant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY_KEY);

        public static final ResourceKey<Variant> EMPTY = SlimeUtils.createVariantResourceKey("empty");
        public static final ResourceKey<Variant> COBBLESTONE = SlimeUtils.createVariantResourceKey("cobblestone");
        public static final ResourceKey<Variant> IRON = SlimeUtils.createVariantResourceKey("iron");
        public static final ResourceKey<Variant> GOLD = SlimeUtils.createVariantResourceKey("gold");
        public static final ResourceKey<Variant> COPPER = SlimeUtils.createVariantResourceKey("copper");
        public static final ResourceKey<Variant> NETHERITE = SlimeUtils.createVariantResourceKey("netherite");
        public static final ResourceKey<Variant> LAPIS = SlimeUtils.createVariantResourceKey("lapis");
        public static final ResourceKey<Variant> REDSTONE = SlimeUtils.createVariantResourceKey("redstone");
        public static final ResourceKey<Variant> EMERALD = SlimeUtils.createVariantResourceKey("emerald");
        public static final ResourceKey<Variant> DIAMOND = SlimeUtils.createVariantResourceKey("diamond");
        public static final ResourceKey<Variant> QUARTZ = SlimeUtils.createVariantResourceKey("quartz");
        public static final ResourceKey<Variant> COAL = SlimeUtils.createVariantResourceKey("coal");
        public static final ResourceKey<Variant> AMETHYST = SlimeUtils.createVariantResourceKey("amethyst");
    }

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
                this.cooldown = 500;
                Optional<BlockPos> optional = findClosestFeeder(this.slime.blockPosition(), ResourceSlime.this.level());
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
                this.slime.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(this.nearestFeederPos).add(0.5F, 1.0F, 0.5F));
                ((AbstractCubeMob.CubeMobMoveControl<?>) this.slime.moveControl).setDirection(this.slime.getYRot(), this.slime.isDealsDamage());
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
