package me.melontini.dark_matter.test.instrumentation;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.instrumentation.InstrumentationAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentationAccessTest {

  @Test
  @SneakyThrows
  public void testInstrumentation() {
    Assertions.assertEquals(TestClass.get(), 4);

    InstrumentationAccess.getOrEmpty().orElseThrow();
    InstrumentationAccess.retransform(
        node -> {
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
        },
        TestClass.class);

    Assertions.assertEquals(TestClass.get(), 1);
  }

  public static class TestClass {
    public static int get() {
      return 4;
    }
  }
}
