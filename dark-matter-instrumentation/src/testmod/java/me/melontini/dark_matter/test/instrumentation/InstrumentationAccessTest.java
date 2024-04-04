package me.melontini.dark_matter.test.instrumentation;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.instrumentation.InstrumentationAccess;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentationAccessTest implements PreLaunchEntrypoint {

    @SneakyThrows
    @Override
    public void onPreLaunch() {
        MakeSure.isTrue(Test.get() == 4);

        InstrumentationAccess.getOrEmpty().orElseThrow();
        InstrumentationAccess.retransform(node -> {
            for (MethodNode method : node.methods) {
                if ("get".equals(method.name)) {
                    for (AbstractInsnNode instruction : method.instructions) {
                        if (instruction.getOpcode() == Opcodes.ICONST_4) {
                            method.instructions.set(instruction, new InsnNode(Opcodes.ICONST_1));
                            return node;
                        }
                    }
                }
            }
            throw new IllegalStateException();
        }, Test.class);

        MakeSure.isTrue(Test.get() == 1);
    }

    public static class Test {
        public static int get() {
            return 4;
        }
    }
}
