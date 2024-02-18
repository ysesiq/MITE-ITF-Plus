package net.oilcake.mitelros.mixins.entity.player;

import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.oilcake.mitelros.achivements.AchievementExtend;
import net.oilcake.mitelros.block.enchantreserver.EnchantReserverSlots;
import net.oilcake.mitelros.entity.EntityUndeadGuard;
import net.oilcake.mitelros.item.ItemTotem;
import net.oilcake.mitelros.item.Items;
import net.oilcake.mitelros.item.Materials;
import net.oilcake.mitelros.item.enchantment.Enchantments;
import net.oilcake.mitelros.item.potion.PotionExtend;
import net.oilcake.mitelros.util.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static java.lang.Math.max;
import static net.xiaoyu233.fml.util.ReflectHelper.dyCast;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLiving implements ICommandListener {

//    private static int getWaterLimit(int level) {
//        return Math.max(Math.min(6 + level / 5 * 2, 20), 6);
//    }
//    public float getWaterLimit() {
//        return (float)getWaterLimit(this.getExperienceLevel());
//
    public boolean isNewPlayer = true;
    @Shadow
    private int insulin_resistance;
    @Shadow
    public EnumInsulinResistanceLevel insulin_resistance_level;
    @Shadow
    private int field_82249_h;
    private int hunt_counter = 0;
    public int getHunt_counter(){
        return this.hunt_counter;
    }
    public void setHunt_counter(int counter){
        this.hunt_counter = counter;
    }
    public boolean hunt_cap = false;

    public int getCurrent_insulin_resistance_lvl(){
        if(this.insulin_resistance_level == null)
            return 0;
        else{
            return insulin_resistance_level.ordinal() + 1;
        }
    }
    @Overwrite
    protected void jump() {
        if(!((this.getHealth() <= 5.0F || this.capabilities.getWalkSpeed() < 0.05F || !this.hasFoodEnergy()) && ExperimentalConfig.TagConfig.Realistic.ConfigValue)){
            super.jump();
            this.addStat(StatisticList.jumpStat, 1);
            if (this.getAsPlayer() instanceof ClientPlayer) {
                this.getAsPlayer().getAsEntityClientPlayerMP().sendPacket((new Packet85SimpleSignal(EnumSignal.increment_stat_for_this_world_only)).setInteger(StatisticList.jumpStat.statId));
            }
        }
    }
    @Override
    public boolean isOnLadder()
    {
        if(ExperimentalConfig.TagConfig.Realistic.ConfigValue && (this.getHealth() <= 5.0F || this.capabilities.getWalkSpeed() < 0.05F || !this.hasFoodEnergy())){
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.boundingBox.minY);
            int z = MathHelper.floor_double(this.posZ);
            int var0 = this.worldObj.getBlockId(x, y, z);
            if(var0 == Block.ladder.blockID || var0 == Block.vine.blockID){
                return true;
            }
            float yaw = this.rotationYaw % 360F;
            if(yaw < -45F){
                yaw += 360F;
            }
            int towards = (int) ((yaw + 45.0F) % 360F) / 90;
            switch (towards){
                case 0:
                    z += 1;
                    break;
                case 1:
                    x -= 1;
                    break;
                case 2:
                    z -= 1;
                    break;
                case 3:
                    x += 1;
                    break;
                default:
                    Minecraft.setErrorMessage("isOnLadder: Undefined Facing : " + towards + ".");
            }
            Block var1 = this.worldObj.getBlock(x, y, z);
            Block var2 = this.worldObj.getBlock(x, y + 1, z);
            return this.fallDistance == 0F && var1 != null && var1.isSolid(0) && var2 == null || (var2 != null && !var2.isSolid(0));
        }else{
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.boundingBox.minY);
            int var3 = MathHelper.floor_double(this.posZ);
            int var4 = this.worldObj.getBlockId(var1, var2, var3);
            return var4 == Block.ladder.blockID || var4 == Block.vine.blockID;
        }
    }
    @Overwrite
    public void attackTargetEntityWithCurrentItem(Entity target) {
        if (!this.isImmuneByGrace()) {
            if (target.canAttackWithItem()) {
                boolean critical = this.willDeliverCriticalStrike();
                float damage = this.calcRawMeleeDamageVs(target, critical, this.isSuspendedInLiquid());
                if (damage <= 0.0F) {
                    return;
                }
                ItemStack heldItemStack = this.getHeldItemStack();
                if(EnchantmentManager.hasEnchantment(heldItemStack, Enchantments.enchantmentDestroying)){
                    int destorying = EnchantmentManager.getEnchantmentLevel(Enchantments.enchantmentDestroying, heldItemStack);
                    target.worldObj.createExplosion(this, target.posX, target.posY, target.posZ, 0F, destorying * 0.5F, true);
                    //System.out.println("判断为enchantmentDestorying player");
                    //target.setFire(120);
                }

                int knockback = 0;
                if (target instanceof EntityLiving) {
                    knockback += EnchantmentManager.getKnockbackModifier(this, (EntityLiving)target);
                }

                if (this.isSprinting()) {
                    ++knockback;
                }

                boolean was_set_on_fire = false;
                int fire_aspect = EnchantmentManager.getFireAspectModifier(this);
                if (target instanceof EntityLiving && fire_aspect > 0 && !target.isBurning()) {
                    was_set_on_fire = true;
                    target.setFire(1);
                }

                if (this.onServer() && target instanceof EntityLiving) {
                    EntityLiving entity_living_base = (EntityLiving)target;
                    ItemStack item_stack_to_drop = entity_living_base.getHeldItemStack();
                    if (item_stack_to_drop != null && this.rand.nextFloat() < EnchantmentManager.getEnchantmentLevelFraction(Enchantment.disarming, this.getHeldItemStack()) && entity_living_base instanceof EntityInsentient) {
                        EntityInsentient entity_living = (EntityInsentient)entity_living_base;
                        entity_living.dropItemStack(item_stack_to_drop, entity_living.height / 2.0F);
                        entity_living.clearMatchingEquipmentSlot(item_stack_to_drop);
                        entity_living.ticks_disarmed = 40;
                    }
                }
                if (this.onServer() && target instanceof EntityLiving) {
                    EntityLiving entity_living_base = (EntityLiving)target;
                    ItemStack[] item_stack_to_drop = entity_living_base.getWornItems();
                    int rand = this.rand.nextInt(item_stack_to_drop.length);
                    if (item_stack_to_drop[rand] != null && this.rand.nextFloat() < EnchantmentManager.getEnchantmentLevelFraction(Enchantments.enchantmentThresher, this.getHeldItemStack()) && entity_living_base instanceof EntityInsentient) {
                        EntityInsentient entity_living = (EntityInsentient)entity_living_base;
                        entity_living.dropItemStack(item_stack_to_drop[rand], entity_living.height / 2.0F);
                        entity_living.clearMatchingEquipmentSlot(item_stack_to_drop[rand]);
                        entity_living.ticks_disarmed = 40;
                    }
                }

                EntityDamageResult result = target.attackEntityFrom(new Damage(DamageSource.causePlayerDamage(dyCast(this)).setFireAspectC(fire_aspect > 0), damage));
                boolean target_was_harmed = result != null && result.entityWasNegativelyAffected();
                target.onMeleeAttacked(this, result);
                if (target_was_harmed) {
                    if (target instanceof EntityLiving) {
                        int stunning = EnchantmentManager.getStunModifier(this, (EntityLiving)target);
                        if ((double)stunning > Math.random() * 10.0) {
                            ((EntityLiving)target).addPotionEffect(new MobEffect(MobEffectList.moveSlowdown.id, stunning * 50, stunning * 5));
                        }
                        this.heal((float)EnchantmentManager.getVampiricTransfer(this, (EntityLiving)target, damage), EnumEntityFX.vampiric_gain);
                        if(EnchantmentManager.hasEnchantment(heldItemStack, Enchantments.enchantmentSweeping)){
                            List <Entity>targets  = this.getNearbyEntities(5.0F, 5.0F);
                            this.attackMonsters(targets, damage * EnchantmentManager.getEnchantmentLevelFraction(Enchantments.enchantmentSweeping,heldItemStack));
                        }
                    }

                    if (knockback > 0) {
                        target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback * 0.5F), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927F / 180.0F) * (float)knockback * 0.5F));
                        this.motionX *= 0.6;
                        this.motionZ *= 0.6;
                        this.setSprinting(false);
                    }

                    if (critical) {
                        this.onCriticalHit(target);
                    }

                    if (target instanceof EntityLiving && EnchantmentWeaponDamage.getDamageModifiers(this.getHeldItemStack(), (EntityLiving)target) > 0.0F) {
                        this.onEnchantmentCritical(target);
                    }

                    if (damage >= 40.0F) {
                        this.triggerAchievement(AchievementList.overkill);
                    }

                    this.setLastAttackTarget(target);
                    if (target instanceof EntityLiving) {
                        if (this.worldObj.isRemote) {
                            System.out.println("EntityPlayer.attackTargetEntityWithCurrentItem() is calling EnchantmentThorns.func_92096_a() on client");
                            Minecraft.temp_debug = "player";
                        }

                        EnchantmentThorns.func_92096_a(this, (EntityLiving)target, this.rand);
//                        EnchantmentProtectFire.PerformProtect(this,(EntityLiving)target,this.rand);
                    }
                }

                ItemStack held_item_stack = this.getHeldItemStack();
                Object var10 = target;
                if (target instanceof EntityComplexPart) {
                    IComplex var11 = ((EntityComplexPart)target).entityDragonObj;
                    if (var11 != null && var11 instanceof EntityLiving) {
                        var10 = (EntityLiving)var11;
                    }
                }

                if (target_was_harmed && held_item_stack != null && var10 instanceof EntityLiving) {
                    held_item_stack.hitEntity((EntityLiving)var10, dyCast(this));
                }

                if (target instanceof EntityLiving) {
                    this.addStat(StatisticList.damageDealtStat, Math.round(damage * 10.0F));
                    if (fire_aspect > 0 && target_was_harmed) {
                        target.setFire(fire_aspect * 4);
                    } else if (was_set_on_fire) {
                        target.extinguish();
                    }
                }

                if (this.onServer()) {
                    this.addHungerServerSide(0.3F * EnchantmentManager.getEnduranceModifier(this));
                }
            }

        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE"))
    public void onDeath(DamageSource par1DamageSource, CallbackInfo callbackInfo) {
        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory")) {
            this.inventory.vanishingItems();
        }
    }

    public InventoryEnderChest getTheInventoryEnderChest(){
        return this.theInventoryEnderChest;
    }
    public int getWater() {
        return this.getFoodStats().getWater();
    }
    public int addWater(int water){
        return this.getFoodStats().addWater(water);
    }

    public void decreaseWaterServerSide(float hungerWater)
    {
        if (!this.capabilities.isCreativeMode && !this.capabilities.disableDamage)
        {
            this.foodStats.decreaseWaterServerSide(hungerWater);
            //System.out.println("发送buff数据 player");
        }
    }
    @Overwrite
    public boolean isStarving() {
        return this.getNutrition() == 0;
    }

    public boolean DuringDehydration() {
        return this.getWater() == 0;
    }
    @Overwrite
    public boolean hasFoodEnergy() {
        return this.getSatiation() + this.getNutrition() != 0 && this.getWater() != 0;
    }
    public boolean UnderArrogance() {
        boolean Hel_Arro = false;
        boolean Cst_Arro = false;
        boolean Lgs_Arro = false;
        boolean Bts_Arro = false;
        boolean Hnd_Arro = false;
        ItemStack Helmet = this.getHelmet();
        ItemStack Cuirass = this.getCuirass();
        ItemStack Leggings = this.getLeggings();
        ItemStack Boots = this.getBoots();
        ItemStack Holding = this.getHeldItemStack();
        if(Helmet != null)Hel_Arro = EnchantmentManager.hasEnchantment(Helmet, Enchantments.enchantmentArrogance);
        if(Cuirass != null)Cst_Arro = EnchantmentManager.hasEnchantment(Cuirass, Enchantments.enchantmentArrogance);
        if(Leggings != null)Lgs_Arro = EnchantmentManager.hasEnchantment(Leggings, Enchantments.enchantmentArrogance);
        if(Boots != null)Bts_Arro = EnchantmentManager.hasEnchantment(Boots, Enchantments.enchantmentArrogance);
        if(Holding != null)Hnd_Arro = EnchantmentManager.hasEnchantment(Holding, Enchantments.enchantmentArrogance);
        boolean Arro = Hel_Arro || Cst_Arro || Lgs_Arro || Bts_Arro || Hnd_Arro;
        return this.experience < 2300 && Arro;
    }

    public boolean InFreeze(){
        BiomeBase biome = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
        ItemStack wearingItemStack = this.getCuirass();
        if (EnchantmentManager.hasEnchantment(wearingItemStack, Enchantments.enchantmentCallofNether)) {
            return false;
        }
        if (biome.temperature <= (this.worldObj.getWorldSeason() == 3 ? 1.0F : 0.16F) && (this.isOutdoors() || this.worldObj.provider.dimensionId == -2)){
            if(this.getHelmet() != null && this.getHelmet().itemID == Items.WolfHelmet.itemID &&
                    this.getCuirass() != null && this.getCuirass().itemID == Items.WolfChestplate.itemID &&
                    this.getLeggings() != null && this.getLeggings().itemID == Items.WolfLeggings.itemID &&
                    this.getBoots() != null && this.getBoots().itemID == Items.WolfBoots.itemID){
                return false;
            } else{
                return true;
            }
        }
        return false;
    }
    public boolean InHeat(){
        BiomeBase biome = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
        return biome.temperature >= 1.50F && StuckTagConfig.TagConfig.TagHeatStorm.ConfigValue;
    }
    private void activeNegativeUndying() {
        this.clearActivePotions();
        this.setHealth(this.getMaxHealth(),true,this.getHealFX());
        this.entityFX(EnumEntityFX.smoke_and_steam);
        this.makeSound("imported.random.totem_use", 3.0F, 1.0F+this.rand.nextFloat()*0.1F);
        this.addPotionEffect(new MobEffect(MobEffectList.blindness.id, 40, 4));
        this.vision_dimming+=0.75F;
        this.triggerAchievement(AchievementExtend.cheatdeath);
    }
    protected void checkForAfterDamage(Damage damage, EntityDamageResult result) {
        if (result.entityWasDestroyed()) {
            ItemStack var5 = this.getHeldItemStack();
            if (var5 != null && var5.getItem() instanceof ItemTotem) {
                result.setEntity_was_destroyed(false);
                ((ItemTotem) var5.getItem()).performNegativeEffect(this.getAsPlayer());
            }
            if (hunt_counter > 0){
                result.setEntity_was_destroyed(false);
                this.setHealth(1);
            }
        }
    }
    @Redirect(method = "attackEntityFrom",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/EntityLiving;attackEntityFrom(Lnet/minecraft/Damage;)Lnet/minecraft/EntityDamageResult;"))
    private EntityDamageResult redirectEntityAttack(EntityLiving caller,Damage damage){
        EntityDamageResult entityDamageResult = super.attackEntityFrom(damage);

        if (entityDamageResult != null && (double)this.getHealthFraction() <= 0.1D && !entityDamageResult.entityWasDestroyed()) {
            ItemStack var5 = this.getHeldItemStack();
            if (var5 != null && var5.getItem() instanceof ItemTotem) {
                entityDamageResult.setEntity_was_destroyed(false);
                this.activeNegativeUndying();
                this.setHeldItemStack(null);
            }
        }
        return entityDamageResult;
    }
    private int HeatResistance;
    private int FreezingCooldown;
    private int diarrheaCounter;
    private int FreezingWarning;
    private int reduce_weight;
    public float BodyTemperature = 37.2F;
    private double dry_resist;
    public boolean Hasdrunked = false;
    private int drunk_duration = 0;
    private int water_duration = 0;
    public int getReduce_weight(){
        int Weight = 0;
        if(this.getHelmet()!= null && this.getHelmet().itemID == Items.WolfHelmet.itemID){
            Weight += 2;
        }
        if(this.getCuirass()!= null && this.getCuirass().itemID == Items.WolfChestplate.itemID){
            Weight += 2;
        }
        if(this.getLeggings()!= null && this.getLeggings().itemID == Items.WolfLeggings.itemID){
            Weight += 2;
        }
        if(this.getBoots()!= null && this.getBoots().itemID == Items.WolfBoots.itemID ){
            Weight += 2;
        }
        if(this.getHelmet()!= null && this.getHelmet().itemID == Item.helmetLeather.itemID){
            Weight += 1;
        }
        if(this.getCuirass()!= null && this.getCuirass().itemID == Item.plateLeather.itemID){
            Weight += 1;
        }
        if(this.getLeggings()!= null && this.getLeggings().itemID == Item.legsLeather.itemID){
            Weight += 1;
        }
        if(this.getBoots()!= null && this.getBoots().itemID == Item.bootsLeather.itemID ){
            Weight += 1;
        }
        if(this.getHelmet()!= null){
            Weight += 2;
        }
        if(this.getCuirass()!= null) {
            Weight += 2;
        }
        if(this.getLeggings()!= null){
            Weight += 2;
        }
        if(this.getBoots()!= null){
            Weight += 2;
        }
        return Weight;
    }
    @Inject(method = "onLivingUpdate",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/EntityLiving;onLivingUpdate()V",
                    shift = At.Shift.AFTER))
    private void injectTick(CallbackInfo c){
        if (!this.worldObj.isRemote) {
            //拉屎！？
            if(this.isPotionActive(PotionExtend.dehydration) && this.diarrheaCounter <= 1200){
                ++this.diarrheaCounter;
            }else {
                if(this.diarrheaCounter > 0){
                    --this.diarrheaCounter;
                }
            }
            if(this.diarrheaCounter >= 1200){
                this.dropItem(Item.manure);
                this.triggerAchievement(AchievementExtend.pull);
                this.diarrheaCounter = 0;
            }
            //拓展诅咒
            if(this.hasCurse(CurseExtend.fear_of_light)) {
                float light_modifier = (18 - this.worldObj.getBlockLightValue(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + this.yOffset), MathHelper.floor_double(this.posZ))) / 15.0F;
                if(light_modifier < 0.5F && this.hasCurse(CurseExtend.fear_of_light,true)){
                }
            }
            if(this.hasCurse(CurseExtend.fear_of_darkness)) {
                float light_modifier = (this.worldObj.getBlockLightValue(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + this.yOffset), MathHelper.floor_double(this.posZ)) + 3) / 15.0F;
                if(light_modifier < 0.5F && this.hasCurse(CurseExtend.fear_of_darkness,true)){
                }
            }
            //巡猎图腾效果
            if(this.hunt_counter > (this.hunt_cap ? -1 : 0)){
                this.hunt_counter--;
            }
            if(this.hunt_counter % 80 == 79){
                EntityUndeadGuard Belongings = new EntityUndeadGuard(this.worldObj);
                Belongings.setPosition(this.posX, this.posY, this.posZ);
                Belongings.refreshDespawnCounter(-9600);
                this.worldObj.spawnEntityInWorld(Belongings);
                Belongings.onSpawnWithEgg(null);
                Belongings.entityFX(EnumEntityFX.summoned);
            }
            if(this.hunt_counter < 0){
                this.attackEntityFrom(new Damage(DamageSourceExtend.sacrificed, 10000.0F));
                this.hunt_counter = 0;
                this.hunt_cap = false;
            }
            //创造模式下减轻视觉黑暗
            if(Minecraft.inDevMode() && this.vision_dimming > 0.1F && this.isPlayerInCreative()){
                this.vision_dimming = 0.05F;
            }
            //站定喝水
            BiomeBase biome = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
            if(this.getBlockAtFeet()!= null && this.getBlockAtFeet().blockMaterial == Material.water && this.isSneaking()){
                ++this.water_duration;
            }else{
                this.water_duration = 0;
            }
            if (this.water_duration > 160) {
                this.water_duration = 0;
                if (biome == BiomeBase.swampRiver || biome == BiomeBase.swampland) {
                    this.getFoodStats().addWater(1);
                    this.addPotionEffect(new MobEffect(MobEffectList.poison.id, 450, 0));
                } else if(biome == BiomeBase.river || biome == BiomeBase.desertRiver){
                    this.getFoodStats().addWater(2);
                } else{
                    this.getFoodStats().addWater(1);
                    this.addPotionEffect(new MobEffect(PotionExtend.dehydration.id, 160, 0));
                }
            }
            //水分自然扣减
            dry_resist += (StuckTagConfig.TagConfig.TagHeatStroke.ConfigValue ? 2.0D : 1.0D) + (double) biome.getFloatTemperature();
            if(this.isPotionActive(PotionExtend.dehydration)){
                dry_resist += Math.min(80.0D, (this.getActivePotionEffect(PotionExtend.dehydration).getAmplifier() + 1) * 20D);
            }
            if(this.isPotionActive(PotionExtend.thirsty)){
                dry_resist += Math.min(80.0D, (this.getActivePotionEffect(PotionExtend.thirsty).getAmplifier() + 1) * 10D);
            }
            if(dry_resist > 12800.0D) {
                this.getFoodStats().addWater(-1);
                dry_resist = 0;
            }
            //喝酒
            if(this.Hasdrunked) {
                this.drunk_duration = 6000;
                this.Hasdrunked = false;
            }
            //寒冷惩罚
            int freezeunit = max(FreezingCooldown - (1500 * getReduce_weight()), 0);
            BodyTemperature = 37.2F - (0.000125F * freezeunit);
            int freezelevel = max(freezeunit/12000, 0);
            if(freezeunit > 12000 && this.InFreeze()){
                if(freezelevel >= 1){
                    if (freezelevel >= 4) {
                        ++FreezingWarning;
                        this.triggerAchievement(AchievementExtend.hypothermia);
                    }
                    if (FreezingWarning > 500) {
                        this.attackEntityFrom(new Damage(DamageSourceExtend.freeze, 4.0F));
                        FreezingWarning = 0;
                    }
                    this.addPotionEffect(new MobEffect(PotionExtend.freeze.id, freezeunit, this.isInRain() ? freezelevel : freezelevel - 1));
                }
            }
            //炎热惩罚
            if(this.HeatResistance > 3200 - (getReduce_weight() * 50)){
                this.addPotionEffect(new MobEffect(MobEffectList.confusion.id, 1600, 1));
                this.HeatResistance = 0;
            }
            if(this.InHeat()){
                this.HeatResistance++;
            }
            //叠加寒冷惩罚
            if(this.InFreeze() || this.drunk_duration > 0){
                FreezingCooldown += StuckTagConfig.TagConfig.TagLegendFreeze.ConfigValue ? 3 : 1;
                FreezingCooldown += this.drunk_duration > 0 ? StuckTagConfig.TagConfig.TagLegendFreeze.ConfigValue ? 3 : 1 : 0;
            }
            else{
                if(FreezingCooldown > 0){
                    --FreezingCooldown;
                }
            }
            //调用喝酒翻倍计算时间
            if(this.drunk_duration > 0){
                drunk_duration--;
            }
            if(this.getHealth() < 5.0F && ExperimentalConfig.TagConfig.Realistic.ConfigValue){
                this.vision_dimming = Math.max(this.vision_dimming,(1.0F - this.getHealthFraction()));
            }
        }
        //成就奖励
        if(Feast_trigger_sorbet &&Feast_trigger_cereal &&Feast_trigger_chestnut_soup &&Feast_trigger_chicken_soup &&Feast_trigger_beef_stew &&Feast_trigger_cream_mushroom_soup &&Feast_trigger_cream_vegetable_soup &&Feast_trigger_ice_cream &&Feast_trigger_lemonade &&Feast_trigger_mashed_potatoes &&Feast_trigger_porkchop_stew &&Feast_trigger_salad &&Feast_trigger_pumpkin_soup &&Feast_trigger_porridge &&Feast_trigger_mushroom_soup &&Feast_trigger_vegetable_soup &&Feast_trigger_salmon_soup &&Feast_trigger_beetroot_soup &&!rewarded_disc_damnation){
            this.triggerAchievement(AchievementExtend.feast);
            this.addExperience(2500);
            this.rewarded_disc_damnation = true;
            EntityItem RewardingRecord = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(Items.recordDamnation.itemID,1));
            worldObj.spawnEntityInWorld(RewardingRecord);
            RewardingRecord.entityFX(EnumEntityFX.summoned);
        }
        if(this.isPotionActive(MobEffectList.moveSpeed) && this.isPotionActive(MobEffectList.regeneration) && this.isPotionActive(MobEffectList.fireResistance) && this.isPotionActive(MobEffectList.nightVision) && this.isPotionActive(MobEffectList.damageBoost) && this.isPotionActive(MobEffectList.resistance) && this.isPotionActive(MobEffectList.invisibility) && !rewarded_disc_connected){
            this.triggerAchievement(AchievementExtend.invincible);
            this.addExperience(2500);
            this.rewarded_disc_connected = true;
            EntityItem RewardingRecord = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(Items.recordConnected.itemID,1));
            worldObj.spawnEntityInWorld(RewardingRecord);
            RewardingRecord.entityFX(EnumEntityFX.summoned);
        }
        //傲慢惩罚
        if(this.UnderArrogance()){
            this.addPotionEffect(new MobEffect(MobEffectList.wither.id, 100, 1));
        }
        //幻听
//        if(StuckTagConfig.TagConfig.TagAcousma.ConfigValue){
//            if(this.worldObj.isOverworld()){
//                if(this.ticksExisted % 600 == this.rand.nextInt(8)){
//                    boolean temp = false;
//                    if(this.rand.nextInt(4) == 0){
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 2;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 2;
//                        this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//                        temp = true;
//                    }
//                    if(this.rand.nextInt(8) == 0 && this.posY < 40){
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        for(int i = 0;i < (this.rand.nextInt(3) + 2);){
//                            if(this.ticksExisted % 20 == 4) {
//                                i++;
//                                x_offset += Math.sin(this.rotationYaw) * this.rand.nextDouble() * 4 - 2;
//                                y_offset += this.rand.nextDouble() * 1.5 - 0.75;
//                                z_offset += - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 4 - 2;
//                                this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"mob.endermen.portal", 1.0F, 1.0F);
//                            }
//                        }
//                    }
//                    if(this.rand.nextInt(64) == 0 && !temp){
//                        this.playSound("mob.endermen.stare", 1.0F, 1.0F);
//                    }
//                }
//            } else if(this.worldObj.isUnderworld()){
//                if(this.ticksExisted % 400 == this.rand.nextInt(8)){
//                    boolean temp = false;
//                    if(this.rand.nextInt(4) == 0 && this.posY > 120){
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 2;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 2;
//                        this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
//                        temp = true;
//                    }
//                    if(this.rand.nextInt(8) == 0 && this.posY < 40){
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"liquid.lavapop", 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F);
//                    }
//                    if(this.rand.nextInt(64) == 0 && this.posY > 120 && !temp){
//                         this.playSound("mob.endermen.stare", 1.0F, 1.0F);
//                    }
//                }
//            } else{
//                if(this.ticksExisted % 400 == this.rand.nextInt(4)){
//                    boolean temp = false;
//                    if(this.rand.nextInt(8) == 0){
//                        temp = true;
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"imported.mob.invisiblestalker.say", 1.25F, 1.0F);
//                    }
//                    if(this.rand.nextInt(8) == 0 && !temp){
//                        double x_offset = - Math.sin(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        double y_offset = - this.rand.nextDouble() * 1.5 + 3;
//                        double z_offset = - Math.cos(this.rotationYaw) * this.rand.nextDouble() * 8;
//                        this.worldObj.playSoundEffect(this.posX + x_offset,this.posY + y_offset,this.posZ + z_offset,"mob.zombiepig.zpigangry", 1.25F, 1.0F);
//                    }
//                }
//            }
//        }

        //实验性动态光源
//        if(this.getHeldItemStack()!=null&&this.getHeldItem().itemID==Block.torchWood.blockID){
//            if(this.getBlockAtFeet() == null && this.onGround){
//                this.worldObj.setBlock(this.getBlockPosX(), this.getFootBlockPosY(), this.getBlockPosZ(), Blocks.invisibleLight.blockID);
//            }
//        }
        //冲锋
//        if(this.isForwarding()){
//            List <Entity>targets = this.getNearbyEntities(4.0F, 4.0F);
//        }
    }

    public int getFreezingCooldown() {
        return FreezingCooldown;
    }
    public void setFreezingCooldown(int iptfreezingCooldown) {
        this.FreezingCooldown = iptfreezingCooldown;
    }
    public void addFreezingCooldown(int dummy){
        if(this.FreezingCooldown + dummy < 0)
            this.FreezingCooldown = 0;
        else{
            this.FreezingCooldown += dummy;
        }
    }
    public float getCurrentBiomeTemperature(){
        BiomeBase biome = this.worldObj.getBiomeGenForCoords(this.getBlockPosX(), this.getBlockPosZ());
        return biome.getFloatTemperature();
    }

    @Overwrite
    public void addExperience(int amount, boolean suppress_healing, boolean suppress_sound) {
        suppress_healing = true;
        if (amount < 0) {
            if (!suppress_sound) {
                this.worldObj.playSoundAtEntity(this, "imported.random.level_drain");
            }
        } else if (amount > 0) {
            this.addScore(amount);
            if (!suppress_sound) {
                this.worldObj.playSoundAtEntity(this, "random.orb", 0.1F, 0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
            }
        }

        float health_limit_before = this.getHealthLimit();
        int level_before = this.getExperienceLevel();
        this.experience += amount;
        if (this.experience < getExperienceRequired(-40)) {
            this.experience = getExperienceRequired(-40);
        }

        int level_after = this.getExperienceLevel();
        int level_change = level_after - level_before;
        if (level_change < 0) {
            this.setHealth(this.getHealth());
            this.foodStats.setSatiation(this.foodStats.getSatiation(), true);
            this.foodStats.setNutrition(this.foodStats.getNutrition(), true);
            this.addWater(0);
        } else if (level_change > 0) {
            if (this.getHealthLimit() > health_limit_before && (float)this.field_82249_h < (float)this.ticksExisted - 100.0F) {
                float volume = level_after > 30 ? 1.0F : (float)level_after / 30.0F;
                if (!suppress_sound) {
                    this.worldObj.playSoundAtEntity(this, "random.levelup", volume * 0.75F, 1.0F);
                }

                this.field_82249_h = this.ticksExisted;
            }

            if (!suppress_healing) {
                this.setHealth(this.getHealth() + this.getHealthLimit() - health_limit_before);
            }
        }

        if (level_change != 0 && !this.worldObj.isRemote) {
            MinecraftServer.a(MinecraftServer.F()).sendPlayerInfoToAllPlayers(true);
        }

//        if (this.getAsPlayer() instanceof ServerPlayer && DedicatedServer.tournament_type == EnumTournamentType.score) {
//            DedicatedServer.getOrCreateTournamentStanding(this.getAsPlayer()).experience = this.experience;
//            DedicatedServer.updateTournamentScoreOnClient(this.getAsPlayer(), true);
//        }

    }
    public FoodMetaData getFoodStats(){
    return foodStats;
}
    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    public void displayGUIEnchantReserver(int x, int y, int z, EnchantReserverSlots slots) {
    }
    @Overwrite
    public EnumQuality getMaxCraftingQuality(float unadjusted_crafting_difficulty_to_produce, Item item, int[] applicable_skillsets) {
        if (!this.worldObj.areSkillsEnabled()) {
            applicable_skillsets = null;
        }

        if (this.experience <= 0) {
            return this.getMinCraftingQuality(item, applicable_skillsets);
        } else if (applicable_skillsets != null && !this.hasAnyOfTheseSkillsets(applicable_skillsets)) {
            return this.getMinCraftingQuality(item, applicable_skillsets);
        } else {
            if (item.getLowestCraftingDifficultyToProduce() == Float.MAX_VALUE) {
                Minecraft.setErrorMessage("getMaxCraftingQuality: item has no recipes! " + item.getItemDisplayName((ItemStack)null));
            }

            for(int i = item.getMaxQuality().ordinal(); i > EnumQuality.average.ordinal(); --i) {
                if (this.getCraftingExperienceCost(CraftingResult.getQualityAdjustedDifficulty(unadjusted_crafting_difficulty_to_produce, EnumQuality.values()[i])) <= this.experience) {
                    return EnumQuality.values()[i];
                }
            }

            return this.getMinCraftingQuality(item, applicable_skillsets);
        }
    }

    @Overwrite
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        this.diarrheaCounter = par1NBTTagCompound.getInteger("diarrheaCounter");
        this.hunt_cap = par1NBTTagCompound.getBoolean("UsedTotemOfHunt");
        this.hunt_counter = par1NBTTagCompound.getInteger("TotemDyingCounter");
        this.isNewPlayer = par1NBTTagCompound.getBoolean("isNewPlayer");
        this.FreezingCooldown = par1NBTTagCompound.getInteger("FreezingCooldown");
        this.FreezingWarning = par1NBTTagCompound.getInteger("FreezingWarning");
        this.drunk_duration = par1NBTTagCompound.getInteger("DrunkDuration");
        this.HeatResistance = par1NBTTagCompound.getInteger("HeatResistance");
        this.experience = par1NBTTagCompound.getInteger("experience");
        if (par1NBTTagCompound.hasKey("XpTotal")) {
            this.experience = par1NBTTagCompound.getInteger("XpTotal");
        }

        super.readEntityFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Inventory");
        this.inventory.readFromNBT(var2);
        this.inventory.currentItem = par1NBTTagCompound.getInteger("SelectedItemSlot");
        this.setScore(par1NBTTagCompound.getInteger("Score"));
        if (this.inBed()) {
            this.bed_location = new ChunkCoordinates(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.wakeUpPlayer(true, (Entity)null);
        }

        if (par1NBTTagCompound.hasKey("SpawnX") && par1NBTTagCompound.hasKey("SpawnY") && par1NBTTagCompound.hasKey("SpawnZ")) {
            this.spawnChunk = new ChunkCoordinates(par1NBTTagCompound.getInteger("SpawnX"), par1NBTTagCompound.getInteger("SpawnY"), par1NBTTagCompound.getInteger("SpawnZ"));
            this.spawnForced = par1NBTTagCompound.getBoolean("SpawnForced");
        }

        this.foodStats.readNBT(par1NBTTagCompound);
        this.capabilities.readCapabilitiesFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("EnderItems")) {
            NBTTagList var3 = par1NBTTagCompound.getTagList("EnderItems");
            this.theInventoryEnderChest.loadInventoryFromNBT(var3);
        }

        this.vision_dimming = par1NBTTagCompound.getFloat("vision_dimming");
    }
    @Overwrite
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("diarrheaCounter",this.diarrheaCounter);
        par1NBTTagCompound.setBoolean("UsedTotemOfHunt", this.hunt_cap);
        par1NBTTagCompound.setInteger("TotemDyingCounter", this.hunt_counter);
        par1NBTTagCompound.setBoolean("isNewPlayer", this.isNewPlayer);
        par1NBTTagCompound.setInteger("FreezingCooldown",this.FreezingCooldown);
        par1NBTTagCompound.setInteger("FreezingWarning",this.FreezingWarning);
        par1NBTTagCompound.setInteger("DrunkDuration",this.drunk_duration);
        par1NBTTagCompound.setInteger("HeatResistance",this.HeatResistance);
        par1NBTTagCompound.setInteger("experience", this.experience);
        super.writeEntityToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        par1NBTTagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
        par1NBTTagCompound.setInteger("Score", this.getScore());
        if (this.spawnChunk != null) {
            par1NBTTagCompound.setInteger("SpawnX", this.spawnChunk.posX);
            par1NBTTagCompound.setInteger("SpawnY", this.spawnChunk.posY);
            par1NBTTagCompound.setInteger("SpawnZ", this.spawnChunk.posZ);
            par1NBTTagCompound.setBoolean("SpawnForced", this.spawnForced);
        }

        this.foodStats.writeNBT(par1NBTTagCompound);
        this.capabilities.writeCapabilitiesToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
        par1NBTTagCompound.setFloat("vision_dimming", this.vision_dimming);
    }

    @Overwrite
    private void checkForArmorAchievements() {
        boolean wearing_leather = false;
        boolean wearing_full_suit_plate = true;
        boolean wearing_full_suit_adamantium_plate = true;
        boolean wearing_full_suit_wolf_fur = true;

        for(int i = 0; i < 4; ++i) {
            if (this.inventory.armorInventory[i] != null && this.inventory.armorInventory[i].getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor)this.inventory.armorInventory[i].getItem();
                Material material = armor.getArmorMaterial();
                if (material == Material.leather) {
                    wearing_leather = true;
                }

                if (material != Material.copper && material != Material.silver && material != Material.gold && material != Material.iron && material != Material.mithril && material != Material.adamantium && material != Material.ancient_metal && material != Materials.tungsten && material != Materials.nickel && material != Materials.ancient_metal_sacred && material != Materials.uru) {
                    wearing_full_suit_plate = false;
                }

                if (material != Material.adamantium) {
                    wearing_full_suit_adamantium_plate = false;
                }

                if (material != Materials.wolf_fur) {
                    wearing_full_suit_wolf_fur = false;
                }

            } else {
                wearing_full_suit_plate = false;
                wearing_full_suit_adamantium_plate = false;
                wearing_full_suit_wolf_fur = false;
            }
        }

        if (wearing_leather) {
            this.triggerAchievement(AchievementList.wearLeather);
        }

        if (wearing_full_suit_plate) {
            this.triggerAchievement(AchievementList.wearAllPlateArmor);
        }

        if (wearing_full_suit_adamantium_plate) {
            this.triggerAchievement(AchievementList.wearAllAdamantiumPlateArmor);
        }

        if (wearing_full_suit_wolf_fur) {
            this.triggerAchievement(AchievementExtend.BravetheCold);
        }

    }
    public float getNickelArmorCoverage()
    {
        float coverage = 0.0F;
        ItemStack[] worn_items = this.getWornItems();

        for (int i = 0; i < worn_items.length; ++i)
        {
            ItemStack item_stack = worn_items[i];

            if (item_stack != null)
            {
                if (item_stack.isArmor())
                {
                    ItemArmor barding = item_stack.getItem().getAsArmor();

                    if (barding.getArmorMaterial() == Materials.nickel)
                    {
                        coverage += barding.getCoverage() * barding.getDamageFactor(item_stack, this);
                    }
                }
                else if (item_stack.getItem() instanceof ItemHorseArmor)
                {
                    ItemHorseArmor var6 = (ItemHorseArmor)item_stack.getItem();

                    if (var6.getArmorMaterial() == Materials.nickel)
                    {
                        coverage += var6.getCoverage();
                    }
                }
            }
        }

        return coverage;
    }
    @Overwrite
    public EntityDamageResult attackEntityFrom(Damage damage) {
        float nickel_coverage = MathHelper.clamp_float(this.getNickelArmorCoverage(),0.0F,1.0F);
        if(damage.getResponsibleEntityC() instanceof EntityGelatinousCube){
            System.out.println("nickel_coverage = " + nickel_coverage);
            if(nickel_coverage >= 0.999F){
                return null;
            }
            damage.scaleAmount(1.0F - nickel_coverage);
        }
        if (this.ticksExisted < 1000 && Damage.wasCausedByPlayer(damage) && this.isWithinTournamentSafeZone()) {
            return null;
        } else if (this.capabilities.disableDamage && !damage.canHarmInCreative()) {
            return null;
        } else {
            if (this.inBed()) {
                this.wakeUpPlayer(true, damage.getResponsibleEntityC());
            }
            if (damage.isExplosion()) {
                if(damage.getResponsibleEntityC() == this){
                    return null;
                }
                damage.scaleAmount(1.5F);
            }
            if(ExperimentalConfig.TagConfig.FinalChallenge.ConfigValue){
                damage.scaleAmount(1.0F + (float) Constant.CalculateCurrentDiff() / 50);
            }
            EntityDamageResult result = super.attackEntityFrom(damage);
            return result;
        }
    }

    @Overwrite
    public static int getHealthLimit(int level) {
        int HealthLMTwithTag = 0;
        int HealthLMTwithoutTag = (Math.max(Math.min(6 + level / 5 * 2, 20), 6));
        if(level<=35) {
            HealthLMTwithTag = HealthLMTwithoutTag;
        }else{
            HealthLMTwithTag = (Math.max(Math.min(14 + level / 10 * 2, 40), 20));
        }
        return StuckTagConfig.TagConfig.TagDistortion.ConfigValue ? HealthLMTwithTag : HealthLMTwithoutTag;
    }
    @Overwrite
    public float getCurrentPlayerStrVsBlock(int x, int y, int z, boolean apply_held_item) {
        Block block = Block.blocksList[this.worldObj.getBlockId(x, y, z)];
        if (block == null) {
            return 0.0F;
        } else {
            float block_hardness = this.worldObj.getBlockHardness(x, y, z);
            if (block_hardness == 0.0F) {
                return 1.0F;
            } else {
                float min_str_vs_block = -3.4028235E38F;
                Item held_item = this.getHeldItem();
                float str_vs_block;
                if (block.isPortable(this.worldObj, this, x, y, z)) {
                    str_vs_block = min_str_vs_block = 4.0F * block_hardness;
                } else {
                    int metadata;
                    if (apply_held_item && held_item != null) {
                        metadata = this.worldObj.getBlockMetadata(x, y, z);
                        str_vs_block = held_item.getStrVsBlock(block, metadata);
//                        if(str_vs_block == 0.0F)
//                            System.out.println("Warning: strength_vs_block is 0.");
                        if (str_vs_block < 1.0F) {
                            return this.getCurrentPlayerStrVsBlock(x, y, z, false);
                        }

                        int var4 = EnchantmentManager.getEfficiencyModifier(this);
                        if (var4 > 0) {
                            float var6 = (float)(var4 * var4 + 1);
                            str_vs_block += var6;
                        }
                    } else {
                        metadata = this.worldObj.getBlockMetadata(x, y, z);
                        if (block.blockMaterial.requiresTool(block, metadata)) {
                            str_vs_block = 0.0F;
                        } else {
                            str_vs_block = 1.0F;
                        }
                    }
                }

                if (block == Block.web) {
                    boolean decrease_strength = true;
                    if (apply_held_item && held_item != null && held_item.isTool() && held_item.getAsTool().isEffectiveAgainstBlock(block, 0)) {
                        decrease_strength = false;
                    }

                    if (decrease_strength) {
                        str_vs_block *= 0.2F;
                    }
                }

                if (this.isPotionActive(MobEffectList.digSpeed)) {
                    str_vs_block *= 1.0F + (float)(this.getActivePotionEffect(MobEffectList.digSpeed).getAmplifier() + 1) * 0.2F;
                }

                if (this.isPotionActive(MobEffectList.digSlowdown)) {
                    str_vs_block *= 1.0F - (float)(this.getActivePotionEffect(MobEffectList.digSlowdown).getAmplifier() + 1) * 0.2F;
                }

                if (this.isPotionActive(PotionExtend.freeze)) {
                    str_vs_block *= 1.0F - (float)(this.getActivePotionEffect(PotionExtend.freeze).getAmplifier() + 1) * 0.5F;
                }

                if (this.isInsideOfMaterial(Material.water) && !EnchantmentManager.getAquaAffinityModifier(this)) {
                    str_vs_block /= 5.0F;
                }

                if (!this.onGround) {
                    str_vs_block /= 5.0F;
                }

                if (!this.hasFoodEnergy()) {
                    str_vs_block /= 5.0F;
                }

                str_vs_block *= 1.0F + this.getLevelModifier(EnumLevelBonus.HARVESTING);
                if(ExperimentalConfig.TagConfig.FinalChallenge.ConfigValue){
                    str_vs_block *= 1.0F - ((float) Constant.CalculateCurrentDiff() / 100);
                }
                if(ExperimentalConfig.TagConfig.Realistic.ConfigValue){
                    str_vs_block *= Math.min((float) Math.pow(this.getHealth() , 2) / 25.0F, 1.0F);
                }
//                if(str_vs_block == 0.0F)
//                    System.out.println("Warning: strength_vs_block is 0.");
                return Math.max(str_vs_block, min_str_vs_block);
            }
        }
    }
    public void attackMonsters(List <Entity>targets, float damage) {
        for(int i = 0; i< targets.size(); i++) {
            EntityLiving entityMonster = targets.get(i) instanceof EntityLiving ? (EntityLiving) targets.get(i) : null;
            if(entityMonster != null && entityMonster.getDistanceSqToEntity(this) <= this.getReach(EnumEntityReachContext.FOR_MELEE_ATTACK,entityMonster) && entityMonster.canEntityBeSeenFrom(this.posX,this.getEyePosY(),this.posZ,5.0)) {
                entityMonster.attackEntityFrom(new Damage(DamageSource.causePlayerDamage(this.getAsPlayer()), damage));
            }
        }
    }
//    public float getReach(EnumEntityReachContext context, Entity entity) {
//        if (this.hasExtendedReach()) {
//            return 5.0F;
//        } else {
//            float elevation_difference = (float)(this.posY - (double)this.yOffset - (entity.posY - (double)entity.yOffset));
//            float height_advantage;
//            if (elevation_difference < -0.5F) {
//                height_advantage = (elevation_difference + 0.5F) * 0.5F;
//                if (height_advantage < -1.0F) {
//                    height_advantage = -1.0F;
//                }
//            } else if (elevation_difference > 0.5F) {
//                height_advantage = (elevation_difference - 0.5F) * 0.5F;
//                if (height_advantage > 1.0F) {
//                    height_advantage = 1.0F;
//                }
//            } else {
//                height_advantage = 0.0F;
//            }
//
//            ItemStack item_stack = this.getHeldItemStack();
//            if (context == EnumEntityReachContext.FOR_MELEE_ATTACK) {
//                return entity.adjustPlayerReachForAttacking(this, 1.5F + height_advantage + (item_stack == null ? 0.0F : item_stack.getItem().getReachBonus()));
//            } else if (context == EnumEntityReachContext.FOR_INTERACTION) {
//                return entity.adjustPlayerReachForInteraction(this, 2.5F + height_advantage + (item_stack == null ? 0.0F : item_stack.getItem().getReachBonus(entity)));
//            } else {
//                Minecraft.setErrorMessage("getReach: invalid context");
//                return 0.0F;
//            }
//        }
//    }
//    public boolean isForwarding() {
//        return this.isUsingItem() && Item.itemsList[this.itemInUse.itemID].getItemInUseAction(this.itemInUse, this.getAsPlayer()) == EnumItemInUseActionExtend.FORWARDING;
//    }
//    public double getSpeed(){
//        return this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
//    }
    @Shadow
    protected ItemStack itemInUse;
    @Shadow
    public boolean isUsingItem() {
        return false;
    }
    @Shadow
    public float getLevelModifier(EnumLevelBonus kind) {
        return 0;
    }

    @Shadow
    public int getNutrition() {return 1;}
    @Shadow
    public int getSatiation() {return 1;}
    @Shadow
    public int getScore() {return 0;}
    @Shadow
    protected FoodMetaData foodStats;
    @Shadow
    public ItemStack[] getWornItems() {
        return new ItemStack[0];
    }
    @Shadow
    public boolean isWearing(ItemStack itemStack) {
        return false;
    }
    @Shadow
    public boolean setWornItem(int i, ItemStack itemStack) {
        return false;
    }
    @Shadow
    public void setHeldItemStack(ItemStack itemStack) {}
    @Shadow
    public ItemStack getHeldItemStack() {
        return null;
    }
    @Shadow
    public ItemStack getEquipmentInSlot(int i) {
        return null;
    }
    @Shadow
    public void setCurrentItemOrArmor(int i, ItemStack itemStack) {}
    @Shadow
    public ItemStack[] getInventory() {
        return new ItemStack[0];
    }
    @Shadow
    public PlayerAbilities capabilities;
    @Shadow
    public void wakeUpPlayer(boolean get_out_of_bed, Entity entity_to_look_at) {}
    @Shadow
    public int experience;
    @Shadow
    public EnumQuality getMinCraftingQuality(Item item, int[] applicable_skillsets) {
        return null;
    }
    @Shadow
    public boolean hasAnyOfTheseSkillsets(int[] skillsets) {
        return false;
    }
    @Shadow
    public int getCraftingExperienceCost(float quality_adjusted_crafting_difficulty) {
        return 1;
    }
    @Shadow
    public void setSizeNormal() {}
    @Shadow
    public void setSizeProne() {}
    @Shadow
    public EntityItem dropPlayerItemWithRandomChoice(ItemStack par1ItemStack, boolean par2) {return null;}
    @Shadow
    @Final
    protected String username;
    @Shadow
    public PlayerInventory inventory;
    @Shadow
    public void addStat(Statistic par1StatBase, int par2) {}
    @Shadow
    private InventoryEnderChest theInventoryEnderChest;
    @Shadow
    public void setScore(int par1) {}
    @Shadow
    public boolean isImmuneByGrace() {
        return false;
    }
    @Shadow
    public boolean willDeliverCriticalStrike() {return false;}
    @Shadow
    public float calcRawMeleeDamageVs(Entity target, boolean critical, boolean suspended_in_liquid) {
        return 0;
    }
    @Shadow
    public void onCriticalHit(Entity par1Entity) {}
    @Shadow
    public void onEnchantmentCritical(Entity par1Entity) {}
    @Shadow
    public void addHungerServerSide(float hunger) {}
    @Shadow
    public void triggerAchievement(Statistic par1StatBase) {}
    @Shadow
    public void sendPacket(Packet packet) {}

    @Shadow
    public abstract void addExperience(int amount);
    @Shadow
    public ChunkCoordinates bed_location;
    @Shadow
    private ChunkCoordinates spawnChunk;
    @Shadow
    private boolean spawnForced;
    @Shadow
    public boolean is_cursed;
    @Shadow
    public float vision_dimming;
    @Shadow protected float speedOnGround;

    @Shadow protected abstract void entityInit();

    @Shadow public abstract float getReach(EnumEntityReachContext context, Entity entity);

    @Shadow public abstract double getEyePosY();

    @Shadow protected abstract void fall(float par1);

    @Shadow public abstract void playSound(String par1Str, float par2, float par3);
    @Shadow public void addScore(int par1){}
    @Shadow
    public final int getExperienceLevel() {
        return 0;
    }
    @Shadow
    public float getHealthLimit() {return 0;}
    @Shadow
    protected static final int getExperienceRequired(int level) {return 0;}
    @Shadow
    public void learnCurseEffect() {}
    @Shadow
    public boolean hasCurse(Curse curse) {
        return this.hasCurse(curse, false);
    }
    @Shadow
    public boolean hasExtendedReach() {
        return this.capabilities.isCreativeMode;
    }

    //try to trigger Achievement - Feast
    public boolean Feast_trigger_salad = false;
    public boolean Feast_trigger_porridge = false;
    public boolean Feast_trigger_beef_stew = false;
    public boolean Feast_trigger_cereal = false;
    public boolean Feast_trigger_chicken_soup = false;
    public boolean Feast_trigger_mushroom_soup = false;
    public boolean Feast_trigger_cream_mushroom_soup = false;
    public boolean Feast_trigger_vegetable_soup = false;
    public boolean Feast_trigger_cream_vegetable_soup = false;
    public boolean Feast_trigger_ice_cream = false;
    public boolean Feast_trigger_chestnut_soup = false;
    public boolean Feast_trigger_lemonade = false;
    public boolean Feast_trigger_mashed_potatoes = false;
    public boolean Feast_trigger_porkchop_stew = false;
    public boolean Feast_trigger_pumpkin_soup = false;
    public boolean Feast_trigger_sorbet = false;
    public boolean Feast_trigger_salmon_soup = false;
    public boolean Feast_trigger_beetroot_soup = false;
    private boolean rewarded_disc_damnation = false;
    private boolean rewarded_disc_connected = false;

}
