package me.melontini.dark_matter.impl.enums.transformers;

import me.melontini.dark_matter.api.base.util.mixin.AsmUtil;
import me.melontini.dark_matter.api.base.util.mixin.IAsmTransformer;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Bytecode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class StaticEnumTransformer implements IAsmTransformer {

    private static final Type ITF = Type.getObjectType("me/melontini/dark_matter/api/enums/interfaces/ExtendableEnum");
    private static final Type OBJECT = Type.getType(Object.class);
    private static final String METHOD_NAME = "dark_matter$extendEnum";
    private static final String INIT_METHOD = "dark_matter$init";

    private static boolean loadsArray(InsnList insnList) {
        for (AbstractInsnNode ins : insnList) {
            if (ins instanceof VarInsnNode varinsn) {
                if (varinsn.getOpcode() == Opcodes.ALOAD && varinsn.var == 1) return true;
            }
        }
        return false;
    }

    @Override
    public void afterApply(ClassNode targetClass, IMixinInfo mixinInfo) {
        if ((targetClass.access & Opcodes.ACC_ENUM) == 0) return;

        for (MethodNode method : targetClass.methods) {
            if (METHOD_NAME.equals(method.name)) return;
        }

        MethodNode init = targetClass.methods.stream().filter(methodNode -> methodNode.name.equals(INIT_METHOD)).findFirst().orElse(null);
        boolean passArgs = init != null && loadsArray(init.instructions);

        int mod = Modifier.PRIVATE | Modifier.STATIC | Opcodes.ACC_SYNTHETIC;
        String desc = "[L" + targetClass.name + ";";
        FieldNode tempValues = null;
        for (FieldNode field : targetClass.fields) {
            if (desc.equals(field.desc) && (field.access & mod) == mod) {
                tempValues = field;
                break;
            }
        }
        if (tempValues == null) return;
        FieldNode values = tempValues;
        values.access = values.access & ~Opcodes.ACC_FINAL;

        List<MethodNode> ctxs = new ArrayList<>();
        for (MethodNode method : targetClass.methods) {
            if (method.name.equals("<init>")) ctxs.add(method);
        }
        if (ctxs.isEmpty()) return;

        if (targetClass.interfaces == null) targetClass.interfaces = new ArrayList<>();
        if (!targetClass.interfaces.contains(ITF.getInternalName()))
            targetClass.interfaces.add(ITF.getInternalName());

        Type target = Type.getObjectType(targetClass.name);
        for (MethodNode ctx : ctxs) {
            Type[] oldArgs = Type.getArgumentTypes(ctx.desc);
            Type[] args = ArrayUtils.remove(oldArgs, 1);

            int a = args.length;
            AsmUtil.insAdapter(targetClass, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, METHOD_NAME, Type.getMethodDescriptor(target, args), ia -> {
                ia.getstatic(target.getInternalName(), values.name, values.desc);
                ia.store(a, OBJECT);
                ia.load(a, OBJECT);
                ia.arraylength();
                ia.store(a + 1, Type.INT_TYPE);
                ia.iconst(0);
                ia.store(a + 2, Type.INT_TYPE);

                Label l1 = new Label();
                Label l2 = new Label();
                ia.mark(l1);
                ia.load(a + 2, Type.INT_TYPE);
                ia.load(a + 1, Type.INT_TYPE);
                ia.ificmpge(l2);
                ia.load(a, OBJECT);
                ia.load(a + 2, Type.INT_TYPE);
                ia.aload(Type.getType(Object.class));
                ia.store(a + 3, OBJECT);

                Label l3 = new Label();
                ia.mark(l3);
                ia.load(a + 3, OBJECT);
                ia.invokevirtual(target.getInternalName(), "name", "()Ljava/lang/String;", false);
                ia.load(0, OBJECT);
                ia.invokevirtual("java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false);

                Label l4 = new Label();
                ia.ifeq(l4);
                ia.load(a + 3, OBJECT);
                ia.areturn(OBJECT);

                ia.mark(l4);
                ia.iinc(a + 2, 1);
                ia.goTo(l1);

                ia.mark(l2);
                ia.getstatic(target.getInternalName(), values.name, values.desc);
                ia.getstatic(target.getInternalName(), values.name, values.desc);
                ia.arraylength();
                ia.iconst(1);
                ia.sub(Type.INT_TYPE);
                ia.aload(OBJECT);
                ia.store(a, OBJECT);

                ia.anew(target);
                ia.dup();
                ia.load(0, OBJECT);
                ia.load(a, OBJECT);
                ia.invokevirtual(target.getInternalName(), "ordinal", "()I", false);
                ia.iconst(1);
                ia.add(Type.INT_TYPE);
                for (int i = 1; i < args.length; i++) {
                    ia.load(i, args[i]);
                }
                ia.invokespecial(target.getInternalName(), ctx.name, ctx.desc, false);
                ia.store(a + 1, OBJECT);

                ia.getstatic(target.getInternalName(), values.name, values.desc);
                ia.load(a + 1, OBJECT);
                ia.invokestatic("org/apache/commons/lang3/ArrayUtils", "add", "([Ljava/lang/Object;Ljava/lang/Object;)[Ljava/lang/Object;", false);
                ia.checkcast(Type.getObjectType(desc));
                ia.putstatic(target.getInternalName(), values.name, values.desc);

                ia.aconst(target);
                ia.invokestatic("me/melontini/dark_matter/api/enums/EnumUtils", "clearEnumCache", "(Ljava/lang/Class;)V", false);

                if (init != null) {
                    ia.load(a + 1, OBJECT);
                    if (passArgs) {
                        ia.iconst(a);
                        ia.newarray(OBJECT);
                        for (int i = 0; i < a; i++) {
                            ia.dup();
                            ia.iconst(i);
                            ia.load(i, args[i]);
                            String boxed = Bytecode.getBoxingType(args[i]);
                            if (boxed != null) {
                                ia.invokestatic(boxed, "valueOf", "(" + args[i].getDescriptor() + ")" + Type.getObjectType(boxed).getDescriptor(), false);
                            }
                            ia.astore(OBJECT);
                        }
                    } else {
                        ia.aconst(null);
                    }
                    ia.checkcast(Type.getObjectType("[Ljava/lang/Object;"));
                    ia.invokevirtual(target.getInternalName(), INIT_METHOD, "([Ljava/lang/Object;)V", false);
                }

                ia.aconst("Extended enum {}");
                ia.iconst(1);
                ia.newarray(OBJECT);
                ia.dup();
                ia.iconst(0);
                ia.load(a + 1, OBJECT);
                ia.astore(OBJECT);
                ia.invokestatic("me/melontini/dark_matter/impl/base/DarkMatterLog", "debug", "(Ljava/lang/String;[Ljava/lang/Object;)V", false);

                ia.load(a + 1, OBJECT);
                ia.areturn(OBJECT);
            });
        }
    }
}
