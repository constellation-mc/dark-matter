package me.melontini.dark_matter.api.minecraft.util;

import java.util.Optional;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.util.MathUtil;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unused")
public class ItemStackUtil {

  public static ItemStack getStackOrEmpty(ItemConvertible item) {
    return Optional.ofNullable(item)
        .map(ItemConvertible::asItem)
        .map(Item::getDefaultStack)
        .orElse(ItemStack.EMPTY);
  }

  public static void spawn(@NonNull BlockPos pos, @NonNull ItemStack stack, @NonNull World world) {
    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }

  public static void spawn(@NonNull Vec3d pos, @NonNull ItemStack stack, @NonNull World world) {
    ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }

  public static void spawnVelocity(
      @NonNull BlockPos pos,
      @NonNull ItemStack stack,
      @NonNull World world,
      double minX,
      double maxX,
      double minY,
      double maxY,
      double minZ,
      double maxZ) {
    ItemEntity itemEntity = new ItemEntity(
        world,
        pos.getX(),
        pos.getY(),
        pos.getZ(),
        stack,
        MathUtil.nextDouble(minX, maxX),
        MathUtil.nextDouble(minY, maxY),
        MathUtil.nextDouble(minZ, maxZ));
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }

  public static void spawnVelocity(
      @NonNull Vec3d pos,
      @NonNull ItemStack stack,
      @NonNull World world,
      double minX,
      double maxX,
      double minY,
      double maxY,
      double minZ,
      double maxZ) {
    ItemEntity itemEntity = new ItemEntity(
        world,
        pos.getX(),
        pos.getY(),
        pos.getZ(),
        stack,
        MathUtil.nextDouble(minX, maxX),
        MathUtil.nextDouble(minY, maxY),
        MathUtil.nextDouble(minZ, maxZ));
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }

  public static void spawnVelocity(
      @NotNull BlockPos pos, @NonNull ItemStack stack, @NonNull World world, @NotNull Vec3d vec3d) {
    ItemEntity itemEntity =
        new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack, vec3d.x, vec3d.y, vec3d.z);
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }

  public static void spawnVelocity(
      @NotNull Vec3d pos, @NonNull ItemStack stack, @NonNull World world, @NotNull Vec3d vec3d) {
    ItemEntity itemEntity =
        new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack, vec3d.x, vec3d.y, vec3d.z);
    itemEntity.setToDefaultPickupDelay();
    world.spawnEntity(itemEntity);
  }
}
