package me.melontini.dark_matter.test.data.nbt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.data.nbt.NbtUtil;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class NbtUtilTest {

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
  public void testWriteInvToNbt(TestContext context) {
    var inv = new SimpleInventory(Items.ALLIUM.getDefaultStack());
    NbtCompound nbt = new NbtCompound();
    NbtUtil.writeInventoryToNbt(nbt, inv);
    System.out.println(nbt);

    MakeSure.isTrue(
        Objects.equals(nbt.toString(), "{Items:[{Count:1b,Slot:0b,id:\"minecraft:allium\"}]}"));
    context.complete();
  }

  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
  public void testReadInvFromNbt(TestContext context) throws CommandSyntaxException {
    var inv = new SimpleInventory(1);
    NbtCompound nbt = StringNbtReader.parse("{Items:[{Count:1b,Slot:0b,id:\"minecraft:allium\"}]}");
    NbtUtil.readInventoryFromNbt(nbt, inv);
    System.out.println(inv.toNbtList());

    MakeSure.isTrue(ItemStack.areEqual(inv.getStack(0), Items.ALLIUM.getDefaultStack()));
    context.complete();
  }
}
