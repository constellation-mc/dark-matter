package me.melontini.dark_matter.api.mixin.annotations;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.tree.ClassNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MixinPredicate {

    Mod[] mods() default {};

    /**
     * A predicate instance is lazily constructed on every encounter.
     */
    @ApiStatus.Experimental
    Class<? extends IMixinPredicate>[] predicates() default {};

    interface IMixinPredicate {
        boolean shouldApplyMixin(String targetClassName, String mixinClassName, ClassNode mixinNode);
    }
}
