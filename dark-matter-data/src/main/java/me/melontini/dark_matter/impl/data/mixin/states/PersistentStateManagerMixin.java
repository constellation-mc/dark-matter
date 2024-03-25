package me.melontini.dark_matter.impl.data.mixin.states;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DataFixer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentStateManager.class)
public class PersistentStateManagerMixin {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/datafixer/DataFixTypes;update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/NbtCompound;II)Lnet/minecraft/nbt/NbtCompound;"), method = "readNbt")
    private NbtCompound dark_matter$skipFixing(DataFixTypes instance, DataFixer dataFixer, NbtCompound nbt, int oldVersion, int newVersion, Operation<NbtCompound> original) {
        if (instance == null) return nbt;
        return original.call(instance, dataFixer, nbt, oldVersion, newVersion);
    }
}
