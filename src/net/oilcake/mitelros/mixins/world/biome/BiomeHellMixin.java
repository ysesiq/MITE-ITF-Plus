package net.oilcake.mitelros.mixins.world.biome;

import net.minecraft.*;
import net.oilcake.mitelros.entity.*;
import net.oilcake.mitelros.util.StuckTagConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeHell.class)
public class BiomeHellMixin extends BiomeBase {
    protected BiomeHellMixin(int par1) {
        super(par1);
    }

    @Inject(method = "<init>",at = @At("RETURN"))
    public void injectCtor(CallbackInfo callbackInfo) {
        if (StuckTagConfig.TagConfig.TagDimensionInvade.ConfigValue) {
            this.spawnableMonsterList.add(new BiomeMeta(EntityWitch.class, 20, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntitySpiderKing.class, 10, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityLongdeadGuardian.class, 40, 2, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityLongdead.class, 80, 4, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityAncientBoneLord.class, 20, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityStalkerCreeper.class, 50, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityCaveSpider.class,40,4,4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityWitherBoneLord.class,10,1,2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityWitherBodyguard.class,15,2,2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityBlackWidowSpider.class,40,4,2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityWoodSpider.class,60,4,4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityStray.class,40,2,4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityHusk.class,40,2,4));
            this.spawnableMonsterList.clear();
            this.spawnableMonsterList.add(new BiomeMeta(EntityGhast.class, 50, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityPigZombie.class, 100, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityMagmaCube.class, 10, 4, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityEarthElemental.class, 40, 1, 1));
            this.spawnableMonsterList.add(new BiomeMeta(EntityInfernalCreeper.class, 20, 1, 3));
            this.spawnableMonsterList.add(new BiomeMeta(EntityDemonSpider.class, 20, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityHellhound.class, 20, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityEvil.class, 50, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityPigmanLord.class, 5, 1, 1));
            this.spawnableMonsterList.add(new BiomeMeta(EntitySpirit.class,10,1,2));
        } else {
            this.spawnableMonsterList.clear();
            this.spawnableMonsterList.add(new BiomeMeta(EntityGhast.class, 50, 1, 2));
            this.spawnableMonsterList.add(new BiomeMeta(EntityPigZombie.class, 100, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityMagmaCube.class, 10, 4, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityEarthElemental.class, 40, 1, 1));
            this.spawnableMonsterList.add(new BiomeMeta(EntityInfernalCreeper.class, 20, 1, 3));
            this.spawnableMonsterList.add(new BiomeMeta(EntityDemonSpider.class, 20, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityHellhound.class, 20, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityEvil.class, 50, 1, 4));
            this.spawnableMonsterList.add(new BiomeMeta(EntityPigmanLord.class, 5, 1, 1));
            this.spawnableMonsterList.add(new BiomeMeta(EntitySpirit.class,10,1,2));
        }
    }
}
