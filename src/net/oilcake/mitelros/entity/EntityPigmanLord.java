package net.oilcake.mitelros.entity;

import net.minecraft.*;
import net.oilcake.mitelros.item.Items;
import net.oilcake.mitelros.util.StuckTagConfig.TagConfig;

import java.util.ArrayList;
import java.util.List;

public class EntityPigmanLord extends EntityPigZombie {
    private int angerLevel;
    private int randomSoundDelay;
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.setEntityAttribute(GenericAttributes.maxHealth, TagConfig.TagDemonDescend.ConfigValue ? 45.0 : 30.0);
        this.setEntityAttribute(GenericAttributes.followRange, 128.0);
        this.setEntityAttribute(GenericAttributes.attackDamage, TagConfig.TagDemonDescend.ConfigValue ? 11.25 : 9.0);
    }

    public EntityPigmanLord(World par1World) {
        super(par1World);
    }
    protected EntityPlayer findPlayerToAttack(float max_distance) {
        Entity previous_target = this.getEntityToAttack();
        EntityPlayer target = super.findPlayerToAttack(max_distance);
        if (target != null && target != previous_target) {
            this.becomeAngryAt(target);
        }

        return target;
    }
    private void becomeAngryAt(Entity par1Entity) {
        this.entityToAttack = par1Entity;
        this.angerLevel = 4000;
        this.randomSoundDelay = this.rand.nextInt(40);
    }
    public void addRandomWeapon() {
        List items = new ArrayList();
        items.add(new RandomItemListEntry(Items.tungstenSword, 2));
        if (!Minecraft.isInTournamentMode()) {
            items.add(new RandomItemListEntry(Items.tungstenBattleAxe, 1));
            items.add(new RandomItemListEntry(Items.tungstenWarHammer, 1));
            if (TagConfig.TagDemonDescend.ConfigValue){
                items.add(new RandomItemListEntry(Items.morningStarTungsten, 1));
            }
        }
        RandomItemListEntry entry = (RandomItemListEntry)WeightedRandom.getRandomItem(this.rand, items);
        this.setHeldItemStack((new ItemStack(entry.item)).randomizeForMob(this, true));
    }

    public void addRandomEquipment() {
        this.addRandomWeapon();
        this.setBoots((new ItemStack(Items.tungstenBoots)).randomizeForMob(this, true));
        this.setLeggings((new ItemStack(Items.tungstenLeggings)).randomizeForMob(this, true));
        this.setCuirass((new ItemStack(Items.tungstenChestplate)).randomizeForMob(this, true));
        this.setHelmet((new ItemStack(Items.tungstenHelmet)).randomizeForMob(this, true));
    }
    public int getExperienceValue() {
        return super.getExperienceValue() * 2;
    }
    private int spawnCounter;
    private int spawnSums;
    private boolean gathering_troops = false;
    public void onUpdate() {
        super.onUpdate();
        if (!getWorld().isRemote) {
            if (this.getTarget() != null) {
                if (!this.isNoDespawnRequired() && this.getTarget() != null) {
                    this.gathering_troops = true;
                    this.func_110163_bv();
                }
            }
            if (spawnSums <= 8 && gathering_troops) {
                if (spawnCounter < 20) {
                    if (TagConfig.TagDemonDescend.ConfigValue) spawnCounter++;
                } else {
                    EntityPigmanGuard Belongings = new EntityPigmanGuard(worldObj);
                    Belongings.setPosition(posX, posY, posZ);
                    Belongings.refreshDespawnCounter(-9600);
                    worldObj.spawnEntityInWorld(Belongings);
                    Belongings.onSpawnWithEgg(null);
                    Belongings.entityFX(EnumEntityFX.summoned);
                    spawnCounter = 0;
                    spawnSums++;
                }
            }
        }
    }

}
