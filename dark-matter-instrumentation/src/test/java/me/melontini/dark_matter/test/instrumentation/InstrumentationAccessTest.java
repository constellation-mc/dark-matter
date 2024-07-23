package me.melontini.dark_matter.test.instrumentation;

import lombok.SneakyThrows;
import me.melontini.dark_matter.api.instrumentation.InstrumentationAccess;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentationAccessTest {

  @Test
  @SneakyThrows
  public void testInstrumentation() {
    Assertions.assertThat(TestClass.get()).isEqualTo(4);
    Assertions.assertThat(InstrumentationAccess.get()).isNotNull();

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

    Assertions.assertThat(TestClass.get()).isEqualTo(1);
  }

  public static class TestClass {
    public static int get() {
      return 4;
    }
  }
}
