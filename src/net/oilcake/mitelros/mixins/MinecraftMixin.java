package net.oilcake.mitelros.mixins;

import net.minecraft.EnumChatFormat;
import net.minecraft.Minecraft;
import net.minecraft.aul;
import net.minecraft.client.main.Main;
import net.oilcake.mitelros.util.Constant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    public aul u;
    public float ReportedGamma;

    @Overwrite
    public static String getVersionDescriptor(boolean include_formatting) {
        String white = include_formatting ? EnumChatFormat.WHITE.toString() : "";
        String red = include_formatting ? EnumChatFormat.RED.toString() : "";
        return "1.6.4-MITE" + "-ITF-Plus" + (Main.is_MITE_DS ? "-DS" : "") + (Minecraft
                .inDevMode() ? red + " DEV§f" : "") + white + " 当前难度:" + Constant.CalculateCurrentDiff();
    }

    @Overwrite
    public static boolean inDevMode() {
        return false;
    }
//    @Inject(method = "S()V", at = @At(value = "RETURN"))
//    public void ResetGammaTail(CallbackInfo callbackInfo){
//        this.u.ak = this.ReportedGamma;
//    }
//    @Inject(method = "S()V", at = @At(value = "HEAD"))
//    public void ResetGammaHead(CallbackInfo callbackInfo){
//        this.ReportedGamma = this.u.ak;
//    }
}
