package net.oilcake.mitelros.mixins.util;

import net.minecraft.Entity;
import net.minecraft.EntityTypes;
import net.minecraft.World;
import net.oilcake.mitelros.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTypes.class)
public class EntityTypesMixin {
    @Shadow
    public static void addMapping(Class par0Class, String par1Str, int par2) {
    }
    @Shadow
    private static void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4) {
    }

    @Shadow
    public static Entity createEntityByID(int par0, World par1World) {
        return null;
    }
    @Inject(method = "<clinit>",at = @At("RETURN"))
    private static void injectClinit(CallbackInfo callbackInfo) {
        addMapping(EntityWitherBoneLord.class, "EntityWitherBoneLord", 541,1314564, 13003008);
        addMapping(EntityClusterSpider.class, "EntityClusterSpider", 542,15474675 ,5051227);
        addMapping(EntityWitherBodyguard.class,"EntityWitherBodyguard",543,1314564, 7039851);
        addMapping(EntitySpiderKing.class,"EntitySpiderKing",544,3419431, 15790120);
        addMapping(EntityStray.class,"EntityStray",545,10862020, 871004);
        addMapping(EntityHusk.class,"EntityHusk",546,9798412, 3940871);
        addMapping(EntityPigmanLord.class,"EntityPigManlord",547,15373203, 5066061);
        addMapping(EntityLich.class,"EntityLich",548,13422277, 14008320);
        addMapping(EntityLichShadow.class,"EntityLichShadow",549,13422277, 7699821);
        addMapping(EntityStalkerCreeper.class,"EntityStalkerCreeper",550,10921638, 0);;
        addMapping(EntityWandFireball.class,"EntityWandFireball",551);
        addMapping(EntityWandIceBall.class,"EntityWandIceball",552);
        addMapping(EntityWandShockWave.class,"EntityWandShockWave",553);
        addMapping(EntityZombieLord.class,"EntityZombieLord?",554, 44975, 7969893);
        addMapping(EntityRetinueZombie.class,"EntityZombieRetinue",555, 44975, 7969893);
        addMapping(EntityBoneBodyguard.class,"EntityBoneBodyguard",556, 12698049, 4802889);
        addMapping(EntityGhost.class,"EntityGhost",557,9539985, 6629376);
        addMapping(EntityEvil.class,"EntityEvil",558,9539985, 14008320);
        addMapping(EntityUndeadGuard.class, "EntityUndeadGuard",559,12698049, 4802889);
        addMapping(EntityPigmanGuard.class,"EntityPigManGuard",560,15373203, 5066061);
        addMapping(EntityCastleGuard.class, "EntityCastleGuard",561,0x565656,0x999999);
        addMapping(EntitySpirit.class,"EntitySpirit",562,0xFFFFFFF,0xFFAD0000);
        addMapping(EntityLongdeadSentry.class,"EntityLongdeadSentry", 563,13422277, 7699821);
        addMapping(EntityUnknown.class, "null",1895);
//        addMapping(EntityMoobloom.class,"EntityMooBloom",561);
    }
}
