//package net.oilcake.mitelros.util.events.command;
//
//import net.minecraft.*;
//
//public class CommandSeason extends CommandAbstract {
//    @Override
//    public String getCommandName() {
//        return "season";
//    }
//
//    @Override
//    public String getCommandUsage(ICommandListener iCommandListener) {
//        return "commands.season.usage";
//    }
//
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 0;
//    }
//
//    @Override
//    public void processCommand(ICommandListener iCommandListener, String[] strings, World par1World) {
//        int day = par1World.getDayOfWorld();
//        int springList = new int {1,5,9,13,17};
//        if(springList = day / 32){
//            iCommandListener.sendChatToPlayer(ChatMessage.createFromText("当前季节为春天").setColor(EnumChatFormat.WHITE));
//        }
//
//
//
//    }
//}
