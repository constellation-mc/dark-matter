package me.melontini.dark_matter.api.enums;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@UtilityClass
public class Parameters {

  public static final Empty EMPTY = new Empty();

  public record Empty() implements Base {
    @Override
    public Object[] get() {
      return new Object[0];
    }
  }

  public record RaidMember(EntityType<? extends RaiderEntity> type, int[] countInWave)
      implements Base {
    @Override
    public Object[] get() {
      return new Object[] {type(), countInWave()};
    }
  }

  public record Rarity(int index, String name, net.minecraft.util.Formatting formatting)
      implements Base {

    @Override
    public Object[] get() {
      return new Object[] {index(), name(), formatting()};
    }
  }

  public record BoatEntityType(Block baseBlock, String name) implements Base {
    @Override
    public Object[] get() {
      return new Object[] {baseBlock(), name()};
    }
  }

  @Environment(EnvType.CLIENT)
  public interface RecipeBookGroup extends Base {
    static RecipeBookGroup of(ItemStack... stacks) {
      return () -> stacks;
    }

    ItemStack[] entries();

    @Override
    default Object[] get() {
      return new Object[] {entries()};
    }
  }

  public record Formatting(
      String name, char code, boolean modifier, int colorIndex, @Nullable Integer colorValue)
      implements Base {

    public Formatting(String name, char code, int colorIndex, @Nullable Integer colorValue) {
      this(name, code, false, colorIndex, colorValue);
    }

    public Formatting(String name, char code, boolean modifier) {
      this(name, code, modifier, -1, null);
    }

    @Override
    public Object[] get() {
      return new Object[] {name(), code(), modifier(), colorIndex(), colorValue()};
    }
  }

  private interface Base extends Supplier<Object[]> {}
}
