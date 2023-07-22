/*
 This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 If a copy of the MPL was not distributed with this file,
 You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.melontini.dark_matter.danger.instrumentation;

import me.melontini.dark_matter.DarkMatterLog;
import me.melontini.dark_matter.reflect.ReflectionUtil;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author //<a href="https://github.com/Devan-Kerman/GrossFabricHacks/blob/master/src/main/java/net/devtech/grossfabrichacks/instrumentation/InstrumentationApi.java">Devan-Kerman/GrossFabricHacks</a>
 */
@SuppressWarnings("unused")
public class InstrumentationAccess {
    private InstrumentationAccess() {
        throw new UnsupportedOperationException();
    }
    public static final String EXPORT_DIR = ".dark-matter/class";
    public static final String GAME_DIR = FabricLoader.getInstance().getGameDir().toString();
    public static final String AGENT_DIR = ".dark-matter/agent";
    private static Instrumentation instrumentation;
    private static boolean canInstrument = false;

    public static void retransform(AsmTransformer transformer, String... cls) {
        retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, String... cls) {
        try {
            Class<?>[] classes = Arrays.stream(cls).map(s -> {
                try {
                    return Class.forName(s);
                } catch (ClassNotFoundException e) {
                    try {
                        return ReflectionUtil.accessRestrictedClass(s);
                    } catch (Exception e1) {
                        throw new RuntimeException(String.format("Couldn't access %s class!", s), e1);
                    }
                }
            }).toArray(Class[]::new);
            retransform(transformer, export, classes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void retransform(AsmTransformer transformer, Class<?>... cls) {
        retransform(transformer, false, cls);
    }

    public static void retransform(AsmTransformer transformer, boolean export, Class<?>... cls) {
        try {
            HashSet<Class<?>> classes = Arrays.stream(cls).collect(Collectors.toCollection(HashSet::new));
            AbstractFileTransformer fileTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                if (classes.contains(classBeingRedefined)) {
                    ClassReader reader = new ClassReader(classfileBuffer);
                    ClassNode node = new ClassNode();
                    reader.accept(node, ClassReader.EXPAND_FRAMES);
                    node = transformer.transform(node);
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                    node.accept(writer);
                    byte[] clsFile = writer.toByteArray();
                    if (export) {
                        try {
                            Path path = Path.of(GAME_DIR, EXPORT_DIR, className.replace(".", "/") + ".class");
                            Files.createDirectories(path.getParent());
                            Files.write(path, clsFile);
                        } catch (IOException e) {
                            DarkMatterLog.error(String.format("Couldn't export %s", className), e);
                        }
                    }
                    return clsFile;
                }

                return classfileBuffer;
            };

            instrumentation.addTransformer(fileTransformer, true);
            instrumentation.retransformClasses(cls);
            instrumentation.removeTransformer(fileTransformer);
        } catch (Exception e) {
            throw new RuntimeException("Exception while retransforming classes. Have you subscribed to danger in fabric.mod.json?", e);
        }
    }

    public static boolean canInstrument() {
        return canInstrument;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    static {
        try {
            Path path = Path.of(GAME_DIR, EXPORT_DIR);
            if (Files.exists(path)) {
                Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (!dir.equals(path)) {
                            Files.delete(dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (Exception e) {
            DarkMatterLog.error(String.format("Couldn't clean %s", EXPORT_DIR), e);
        }

        bootstrap();
    }

    public static void bootstrap() {
        if (canInstrument()) return;

        try {
            final String name = ManagementFactory.getRuntimeMXBean().getName();
            final Path jarPath = Paths.get(GAME_DIR, AGENT_DIR, "dark_matter_instrumentation_agent.jar");
            final File jar = jarPath.toFile();

            DarkMatterLog.info("Attaching instrumentation agent to VM.");

            if (!Files.exists(jarPath)) {
                createAgentJar(jarPath, jar);
            } else {
                try (InputStream stream = InstrumentationAccess.class.getClassLoader().getResourceAsStream("jar/dark_matter_instrumentation_agent.jar")) {
                    if (stream != null) {
                        byte[] bytes = stream.readAllBytes();
                        if (!Arrays.equals(Files.readAllBytes(jarPath), bytes)) {
                            DarkMatterLog.info("Newer jar found, overwriting the old one...");
                            Files.delete(jarPath);
                            createAgentJar(jarPath, jar);
                        }
                    } else {
                        DarkMatterLog.error("Couldn't find included \"jar/dark_matter_instrumentation_agent.jar\"! Couldn't check jar version! Trying to attach anyway...");
                    }
                }
            }
            ByteBuddyAgent.attach(jar, name.substring(0, name.indexOf('@')));

            DarkMatterLog.info("Successfully attached instrumentation agent.");

            final Field field = Class.forName("me.melontini.dark_matter.danger.instrumentation.InstrumentationAgent", false, FabricLoader.class.getClassLoader()).getField("instrumentation");

            field.setAccessible(true);

            instrumentation = (Instrumentation) field.get(null);
            if (instrumentation != null) canInstrument = true;
        } catch (final Throwable throwable) {
            DarkMatterLog.error("An error occurred during an attempt to attach an instrumentation agent.", throwable);
        }
    }

    private static void createAgentJar(Path jarPath, File jar) throws IOException {
        Files.createDirectories(jarPath.getParent());
        try (InputStream stream = InstrumentationAccess.class.getClassLoader().getResourceAsStream("jar/dark_matter_instrumentation_agent.jar")) {
            if (stream != null) {
                try (FileOutputStream outputStream = new FileOutputStream(jar)) {
                    outputStream.write(stream.readAllBytes());
                }
            } else {
                throw new NullPointerException("Couldn't find included \"jar/dark_matter_instrumentation_agent.jar\"!");
            }
        }
    }

    @FunctionalInterface
    public interface AbstractFileTransformer extends ClassFileTransformer {
        @Override
        byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer);
    }

    @FunctionalInterface
    public interface AsmTransformer {
        ClassNode transform(ClassNode node);
    }
}
