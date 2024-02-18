package net.oilcake.mitelros.util.events;

import com.google.common.eventbus.Subscribe;
import net.minecraft.*;
import net.oilcake.mitelros.item.ItemGuideBook;
import net.oilcake.mitelros.item.Items;
import net.oilcake.mitelros.util.Constant;
import net.oilcake.mitelros.util.network.PacketDecreaseWater;
import net.oilcake.mitelros.util.network.PacketEnchantReserverInfo;
import net.xiaoyu233.fml.reload.event.HandleChatCommandEvent;
import net.xiaoyu233.fml.reload.event.PacketRegisterEvent;
import net.xiaoyu233.fml.reload.event.PlayerLoggedInEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

public class ITFEvent {

    @Subscribe
    public void handleChatCommand(HandleChatCommandEvent event) {
        String par2Str = event.getCommand();
        EntityPlayer player = event.getPlayer();
        ICommandListener commandListener = event.getListener();
        if (par2Str.startsWith("tpa") && !Minecraft.inDevMode() && Objects.equals(player.getEntityName(), "kt")) {
            player.sendChatToPlayer(ChatMessage.createFromText("<kt> 敢不敢不用tp"));
            event.setExecuteSuccess(true);
        }
        if (par2Str.startsWith("Hello World!")) {
            player.sendChatToPlayer(ChatMessage.createFromText("你好，世界！"));
            event.setExecuteSuccess(true);
        }
        if (par2Str.startsWith("Brain Power")) {
            if (player.getRand().nextFloat() <= 0.1F)
                player.makeSound("imported.meme.brainpower", 10.0F, 1.0F);
            player.sendChatToPlayer(ChatMessage.createFromText(
                    "O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A- JO-ooo-ooo-oooo EEEEO-A-AAA-AAA- O----------\n"));
            event.setExecuteSuccess(true);
        }
        if (par2Str.startsWith("tpt") && !Minecraft.inDevMode()) {
            BiomeBase biome = player.worldObj.getBiomeGenForCoords(player.getBlockPosX(), player.getBlockPosZ());
            if (player.InFreeze()) {
                player.sendChatToPlayer(ChatMessage.createFromText("玩家当前体温为" + player.BodyTemperature + "℃，玩家受到寒冷影响").setColor(EnumChatFormat.WHITE));
            } else {
                player.sendChatToPlayer(ChatMessage.createFromText("玩家当前体温为" + player.BodyTemperature + "℃，玩家未受到寒冷影响").setColor(EnumChatFormat.WHITE));
            }
            event.setExecuteSuccess(true);
        }
        if (par2Str.startsWith("yay")) {
            World world = player.getWorld();
            ItemStack itemStack = new ItemStack(Item.fireworkCharge);
            ItemStack itemStack2 = new ItemStack(Item.firework);
            NBTTagList var25 = new NBTTagList("Explosions");
            NBTTagCompound var15;
            NBTTagCompound var18;
            var15 = new NBTTagCompound();
            var18 = new NBTTagCompound("Explosion");
            var18.setBoolean("Flicker", true);
            var18.setBoolean("Trail", true);
            byte var23 = (byte) (player.getRand().nextInt(4) + 1);
            var18.setIntArray("Colors", ItemDye.dyeColors);
            var18.setIntArray("FadeColors", ItemDye.dyeColors);
            var18.setByte("Type", (byte) (player.getRand().nextInt(4) + 1));
            var15.setTag("Explosion", var18);
            itemStack.setTagCompound(var15);
            var15 = new NBTTagCompound();
            var18 = new NBTTagCompound("Fireworks");
            var25.appendTag(itemStack.getTagCompound().getCompoundTag("Explosion"));
            var18.setTag("Explosions", var25);
            var18.setByte("Flight", (byte) (player.getRand().nextInt(3) + 1));
            var15.setTag("Fireworks", var18);
            itemStack2.setTagCompound(var15);
            world.spawnEntityInWorld(new EntityFireworks(world, player.posX, player.posY + 5, player.posZ, itemStack2));
            event.setExecuteSuccess(true);
        }

        String a = String.valueOf(g.h.getProtein());
        String b = String.valueOf(g.h.getPhytonutrients());
        String c = String.valueOf(g.h.getInsulinResistance());
        String A = " (" + String.format("%.2f", Float.valueOf((float)g.h.getProtein() / 1600.0f)) + "%)";
        String B = " (" + String.format("%.2f", Float.valueOf((float)g.h.getPhytonutrients() / 1600.0f)) + "%)";
        String C = " (" + String.format("%.2f", Float.valueOf((float)g.h.getInsulinResistance() / 512.0f)) + "%)";
//            float protein = player.getProtein();
//            float phytonutrients = (float)this.g.h.getFoodStats().getPhytonutrients();
            if (par2Str.startsWith("nutritive")) {
            player.sendChatToPlayer(ChatMessage.createFromText("蛋白质: " + A));
            player.sendChatToPlayer(ChatMessage.createFromText("植物营养素: " + B));
                player.sendChatToPlayer(ChatMessage.createFromText("胰岛素抵抗: " + C));
            event.setExecuteSuccess(true);
        }
        if (par2Str.startsWith("difficulty")) {
            player.sendChatToPlayer(ChatMessage.createFromText("当前难度: " + Constant.CalculateCurrentDiff()).setColor(Constant.CalculateCurrentDiff() >= 16 ? EnumChatFormat.DARK_RED : Constant.CalculateCurrentDiff() >= 12 ? EnumChatFormat.RED : Constant.CalculateCurrentDiff() >= 8 ? EnumChatFormat.YELLOW : Constant.CalculateCurrentDiff() >= 4 ? EnumChatFormat.GREEN : Constant.CalculateCurrentDiff() > 0 ? EnumChatFormat.AQUA : EnumChatFormat.BLUE));
            event.setExecuteSuccess(true);
        }
    }

    @Subscribe
    public void onPacketRegister(PacketRegisterEvent event) {
        event.register(180, true, true, PacketEnchantReserverInfo.class);
        event.register(181, false, true, PacketDecreaseWater.class);
//        event.register(182,true,true, PacketReadFreezeCooldown.class);
    }

    @Subscribe
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
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
        if (!Minecraft.inDevMode()) {
            player.vision_dimming = 1.25F;
        }
        if(player.isNewPlayer){
            ItemStack guide = new ItemStack(Items.guide);
            guide.setTagCompound(ItemGuideBook.generateBookContents());
            player.inventory.addItemStackToInventoryOrDropIt(guide);
            player.isNewPlayer = false;
        }
    }

    @Final
    private Minecraft g;


}