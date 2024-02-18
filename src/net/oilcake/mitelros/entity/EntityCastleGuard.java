package net.oilcake.mitelros.entity;

import net.minecraft.*;

public class EntityCastleGuard extends EntityEarthElemental {
    public EntityCastleGuard(World world) {
        super(world);
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.setEntityAttribute(GenericAttributes.followRange, 20.0D);
        this.setEntityAttribute(GenericAttributes.movementSpeed, 0.20000000298023224D);
        this.setEntityAttribute(GenericAttributes.attackDamage, 12.0D);
        this.setEntityAttribute(GenericAttributes.maxHealth, 30.0D);
    }

    protected void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
        this.dropItem(Block.stoneBrick.blockID, 1);
    }
}
