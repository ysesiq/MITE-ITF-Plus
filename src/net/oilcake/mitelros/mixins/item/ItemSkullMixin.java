//package net.oilcake.mitelros.mixins.item;
//
//import java.util.List;
//
//import net.minecraft.*;
//import org.spongepowered.asm.mixin.Mixin;
//
//@Mixin(ItemSkull.class)
//public class ItemSkullMixin extends Item {
//    private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper", "witherLord"};
//    public static final String[] field_94587_a = new String[]{"skeleton", "wither", "zombie", "steve", "creeper","witherLord"};
//    private IIcon[] containerItem;
//
//    public ItemSkullMixin(int par1) {
//        super(par1, Material.bone, "skull");
//        this.setCreativeTab(CreativeModeTab.tabDecorations);
//    }
//
//    @Override
//    public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down) {
//        RaycastCollision rc = player.getSelectedObject(partial_tick, true);
//        if (rc == null || !rc.isBlock()) {
//            return false;
//        }
//        if (rc.face_hit.isBottom()) {
//            return false;
//        }
//        return player.tryPlaceHeldItemAsBlock(rc, Block.skull);
//    }
//
//    @Override
//    public void a(int par1, CreativeModeTab par2CreativeTabs, List par3List) {
//        for (int var4 = 0; var4 < skullTypes.length; ++var4) {
//            par3List.add(new ItemStack(par1, 1, var4));
//        }
//    }
//
//    @Override
//    public IIcon getIconFromSubtype(int par1) {
//        if (par1 < 0 || par1 >= skullTypes.length) {
//            par1 = 0;
//        }
//        return this.containerItem[par1];
//    }
//
//    @Override
//    public int getMetadata(int par1) {
//        return par1;
//    }
//
//    @Override
//    public String getUnlocalizedName(ItemStack par1ItemStack) {
//        int var2 = par1ItemStack.getItemSubtype();
//        if (var2 < 0 || var2 >= skullTypes.length) {
//            var2 = 0;
//        }
//        return super.getUnlocalizedName() + "." + skullTypes[var2];
//    }
//
//    @Override
//    public String getItemDisplayName(ItemStack par1ItemStack) {
//        if (par1ItemStack == null) {
//            return "Skull";
//        }
//        return par1ItemStack.getItemSubtype() == 3 && par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("SkullOwner") ? LocaleI18n.translateToLocalFormatted("item.skull.player.name", par1ItemStack.getTagCompound().getString("SkullOwner")) : super.getItemDisplayName(par1ItemStack);
//    }
//
//    @Override
//    public void a(mt par1IconRegister) {
//        this.containerItem = new IIcon[field_94587_a.length];
//        for (int var2 = 0; var2 < field_94587_a.length; ++var2) {
//            this.containerItem[var2] = par1IconRegister.a(this.A() + "_" + field_94587_a[var2]);
//        }
//    }
//}