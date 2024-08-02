/*
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
*/

package me.melontini.dark_matter.impl.instrumentation;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.api.base.reflect.Reflect;
import me.melontini.dark_matter.api.base.reflect.UnsafeUtils;
import me.melontini.dark_matter.api.base.util.*;
import me.melontini.dark_matter.api.instrumentation.InstrumentationAccess;
import me.melontini.dark_matter.api.instrumentation.TransformationException;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * <a href="https://github.com/Devan-Kerman/GrossFabricHacks/blob/master/src/main/java/net/devtech/grossfabrichacks/instrumentation/InstrumentationApi.java">Devan-Kerman/GrossFabricHacks</a>
 *
 * @author <a href="https://github.com/Devan-Kerman/GrossFabricHacks/blob/master/src/main/java/net/devtech/grossfabrichacks/instrumentation/InstrumentationApi.java">Devan-Kerman/GrossFabricHacks</a>
 */
@ApiStatus.Internal
@UtilityClass
public class InstrumentationInternals {

  public static final Path EXPORT_DIR =
      FabricLoader.getInstance().getGameDir().resolve(".dark-matter/class");

  private static Instrumentation instrumentation;
  private static boolean triedAttaching = false;

  public static void retransform(
      InstrumentationAccess.AsmTransformer transformer, boolean export, String... cls)
      throws TransformationException {
    Class<?>[] classes = Arrays.stream(cls)
        .map(s -> {
          try {
            return Class.forName(s);
          } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't access %s class!".formatted(s), e);
          }
        })
        .toArray(Class[]::new);
    retransform(transformer, export, classes);
  }

  public static void retransform(
      InstrumentationAccess.AsmTransformer transformer, boolean export, Class<?>... cls)
      throws TransformationException {
    HashSet<Class<?>> classes = Arrays.stream(cls).collect(Collectors.toCollection(HashSet::new));
    AtomicReference<Throwable> throwable = new AtomicReference<>();
    InstrumentationAccess.AbstractFileTransformer fileTransformer =
        (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
          if (classes.contains(classBeingRedefined)) {
            try {
              ClassReader reader = new ClassReader(classfileBuffer);
              ClassNode node = new ClassNode();
              reader.accept(node, ClassReader.EXPAND_FRAMES);
              node = transformer.transform(node);
              ClassWriter writer =
                  new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
              node.accept(writer);
              byte[] clsFile = writer.toByteArray();
              if (export) {
                try {
                  Path path = EXPORT_DIR.resolve(className.replace('.', '/') + ".class");
                  Files.createDirectories(path.getParent());
                  Files.write(path, clsFile);
                } catch (IOException e) {
                  DarkMatterLog.error(String.format("Couldn't export %s", className), e);
                }
              }
              return clsFile;
            } catch (Throwable t) {
              throwable.set(t);
              return classfileBuffer;
            }
          }

          return classfileBuffer;
        };

    try {
      instrumentation.addTransformer(fileTransformer, true);
      instrumentation.retransformClasses(cls);
      instrumentation.removeTransformer(fileTransformer);
      if (throwable.get() != null) throw throwable.get();
    } catch (Throwable t) {
      throw new TransformationException(
          "Failed to retransform classes %s".formatted(Arrays.toString(cls)), t);
    }
  }

  public static void tryCleanup() {
    try {
      if (Files.exists(EXPORT_DIR)) {
        Files.walkFileTree(
            EXPORT_DIR,
            EnumSet.noneOf(FileVisitOption.class),
            Integer.MAX_VALUE,
            new SimpleFileVisitor<>() {
              @Override
              public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                  throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
              }

              @Override
              public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                  throws IOException {
                if (!dir.equals(EXPORT_DIR)) {
                  Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
              }
            });
      }
    } catch (Exception e) {
      DarkMatterLog.error(String.format("Couldn't clean %s", EXPORT_DIR), e);
    }
  }

  public static @Nullable Instrumentation getInstrumentation() {
    if (instrumentation != null) return instrumentation;
    if (triedAttaching) return null;

    synchronized (InstrumentationAccess.class) {
      if (instrumentation == null) {
        var result = Exceptions.supplyAsResult(InstrumentationInternals::unsafeAttach)
            .mapErr(t -> new IllegalStateException(
                "Failed to attach using InstrumentationImpl#loadAgent", t));

        if (result.value().isPresent()) {
          DarkMatterLog.info("Successfully attached instrumentation agent.");
          return instrumentation = result.value().orElseThrow();
        }
        DarkMatterLog.error(
            "An error occurred during an attempt to attach an instrumentation agent.",
            result.error().orElse(null));
        triedAttaching = true;
      }
      return null;
    }
  }

  private static Instrumentation unsafeAttach() throws Throwable {
    File self = AgentProvider.createJarFile();

    Class<?> ap;
    try {
      ap = ClassLoader.getSystemClassLoader().loadClass(AgentProvider.class.getName());
    } catch (Throwable t) {
      try (var is = AgentProvider.class
          .getClassLoader()
          .getResourceAsStream(AgentProvider.class.getName().replace('.', '/') + ".class")) {
        ap = UnsafeUtils.defineClass(
            ClassLoader.getSystemClassLoader(),
            AgentProvider.class.getName(),
            Objects.requireNonNull(is).readAllBytes(),
            AgentProvider.class.getProtectionDomain());
      } catch (Throwable t1) {
        throw new RuntimeException("Failed to define " + AgentProvider.class.getName(), t1);
      }
    }

    AtomicReference<Throwable> t = new AtomicReference<>();
    var opt = ModuleLayer.boot().findModule("java.instrument").map(module -> {
      try {
        Class<?> cls = Class.forName("sun.instrument.InstrumentationImpl");
        MethodHandles.Lookup lookup = UnsafeUtils.lookupIn(cls);
        lookup
            .findStatic(cls, "loadAgent", MethodType.methodType(void.class, String.class))
            .invokeWithArguments(self.toString());
      } catch (Throwable e) {
        Throwable throwable = e;
        if (throwable instanceof InvocationTargetException) throwable = throwable.getCause();
        t.set(throwable);
      }
      return self;
    });
    if (opt.isEmpty())
      throw new IllegalStateException("'java.instrument' module is not available!");
    if (t.get() != null) throw t.get();

    return (Instrumentation)
        Reflect.setAccessible(Reflect.findField(ap, "instrumentation").orElseThrow())
            .get(null);
  }
}
