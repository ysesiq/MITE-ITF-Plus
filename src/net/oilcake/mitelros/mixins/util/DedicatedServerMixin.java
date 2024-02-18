package net.oilcake.mitelros.mixins.util;

import net.minecraft.*;
import net.oilcake.mitelros.util.Constant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "playerLoggedIn",at = @At("RETURN"))
    public void playerLoggedIn(ServerPlayer player, CallbackInfo callbackInfo) {
        player.setHealth(player.getHealth());
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[Server]")
                .appendComponent(ChatMessage.createFromTranslationKey("MITE-ITF-Plus挂载成功,当前版本:").setColor(EnumChatFormat.BLUE))
                .appendComponent(ChatMessage.createFromText(Constant.VERSION).setColor(EnumChatFormat.YELLOW)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("MITE-ITF作者:Lee074,Huix,Kalsey").setColor(EnumChatFormat.BLUE)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("MITE-ITF-Plus作者:Xy_Lose").setColor(EnumChatFormat.BLUE)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("感谢所有在MITE-ITF以及MITE-ITF-Plus更新历程中贡献思路/测试bug的玩家").setColor(EnumChatFormat.BLUE)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("若有bug请在群聊和私聊内反馈...").setColor(EnumChatFormat.AQUA)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("MITE-ITF-Plus发布群：661223990").setColor(EnumChatFormat.AQUA)));
        player.sendChatToPlayer(ChatMessage.createFromTranslationKey("[MITE-ITF-Plus]")
                .appendComponent(ChatMessage.createFromTranslationKey("当前难度：" + Constant.CalculateCurrentDiff()).setColor(Constant.CalculateCurrentDiff() >= 16 ? EnumChatFormat.DARK_RED : Constant.CalculateCurrentDiff() >= 12 ? EnumChatFormat.RED : Constant.CalculateCurrentDiff() >= 8 ? EnumChatFormat.YELLOW : Constant.CalculateCurrentDiff() >= 4 ? EnumChatFormat.GREEN : Constant.CalculateCurrentDiff() > 0 ? EnumChatFormat.AQUA : EnumChatFormat.BLUE)));
        if(!Minecraft.inDevMode()){
            player.vision_dimming = 1.25F;
        }
//        if(player.isNewPlayer){
//            ItemStack guide = new ItemStack(Items.guide);
//            guide.setTagCompound(ItemGuideBook.generateBookContents());
//            player.vision_dimming = 3.75F;
//            player.addPotionEffect(new MobEffect(new MobEffect(MobEffectList.blindness.id,180,0)));
//            player.inventory.addItemStackToInventoryOrDropIt(guide);
//            player.isNewPlayer = false;
//        }
    }
    @Shadow
    public void updatePlayersFile() {
    }
}
