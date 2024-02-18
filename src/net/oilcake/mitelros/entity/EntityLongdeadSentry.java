package net.oilcake.mitelros.entity;

import net.minecraft.*;

public class EntityLongdeadSentry extends EntityLongdead {
    public EntityLongdeadSentry(World world) {
        super(world);
        this.setSkeletonType(2);
        this.tasks.addTask(1, new PathfinderGoalMeleeAttack(this, 1.0, true));
        this.tasks.addTask(2, new PathfinderGoalMoveTowardsTarget(this, 0.9, 32.0f));
        this.tasks.addTask(3, new PathfinderGoalMoveThroughVillage(this, 0.6, true));
        this.tasks.addTask(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(6, new PathfinderGoalRandomStroll(this, 0.6));
        this.tasks.addTask(7, new PathfinderGoalLookAtPlayer(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(8, new PathfinderGoalRandomLookaround(this));
        this.targetTasks.addTask(2, new PathfinderGoalHurtByTarget(this, false));
        this.targetTasks.addTask(3, new PathfinderGoalNearestAttackableTarget(this, EntityInsentient.class, 0, false, true, IMonster.mobSelector));
    }
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.setEntityAttribute(GenericAttributes.maxHealth, 30.0);
        this.setEntityAttribute(GenericAttributes.followRange, 32.0);
    }
    @Override
    protected void addRandomEquipment() {
        this.setCurrentItemOrArmor(0, (new ItemStack(Item.warHammerAncientMetal, 1)));
        this.setCurrentItemOrArmor(1, (new ItemStack(Item.helmetGold, 1)));
        this.setCurrentItemOrArmor(2, (new ItemStack(Item.plateAncientMetal, 1)));
        this.setCurrentItemOrArmor(3, (new ItemStack(Item.legsAncientMetal, 1)));
        this.setCurrentItemOrArmor(4, (new ItemStack(Item.bootsAncientMetal, 1)));
        this.addPotionEffect(new MobEffect(MobEffectList.wither.id,2147483647, 0));
    }
    @Override
    public int getExperienceValue() {
        return super.getExperienceValue() * 0;
    }
}
