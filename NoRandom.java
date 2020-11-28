import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class NoRandom {
    static File log = new File("/home/commandmaster/norandlog.txt");
    static FileWriter logger;
    private static Class<?> mathHelper;
    private static Field RANDOM;
    public static void premain(String argument, Instrumentation instrumentation) throws IOException, UnmodifiableClassException, InterruptedException {
        log.createNewFile();
        logger = new FileWriter(log);
        logger.write("Started No random!!!\n");
        Class<?> cls = Random.class;
        ClassFileTransformer transformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            try {
                return cls.equals(classBeingRedefined) ? transform(className, classfileBuffer) : classfileBuffer;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        instrumentation.addTransformer(transformer, true);
        instrumentation.retransformClasses(cls);
        instrumentation.removeTransformer(transformer);
        new Thread(() -> {
            try {
                try {
                    Thread.sleep(10000);
                    mathHelper = null;
                    Class<?>[] loaded = instrumentation.getAllLoadedClasses();
                    for (Class<?> classs : loaded) {
//                        logger.write(classs.getCanonicalName() + "\n");
                        if ("net.minecraft.util.math.MathHelper".equals(classs.getCanonicalName())) {
                            mathHelper = classs;
                        }
                    } // (Ljava/util/Random;)Ljava/util/UUID;
                    logger.write(mathHelper + "\n");
                    for (Field f : mathHelper.getDeclaredFields()) {
                        if (f.getType() == Random.class) {
                            RANDOM = f;
                        }
//                        logger.write(f + " " + f.getName() + " " + Type.getInternalName(f.getType()) + "\n");
                    }
                    ClassFileTransformer transformer2 = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
                        try {
                            return mathHelper.equals(classBeingRedefined) ? transform2(className, classfileBuffer) : classfileBuffer;
                        } catch (IOException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    };
                    logger.write(RANDOM + "\n");
                    logger.flush();
                    instrumentation.addTransformer(transformer2, true);
                    instrumentation.retransformClasses(mathHelper);
                    instrumentation.removeTransformer(transformer2);
//                    logger.write(Arrays.toString(mathHelper.getMethods()) + "\n");
                } catch (Exception e) {
                    e.printStackTrace(new PrintWriter(logger));
                }
                logger.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static byte[] transform(String name, byte[] data) throws IOException {
        ClassReader reader = new ClassReader(data);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        transform(node.name, node);
        ClassWriter writer = new ClassWriter(2);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static void transform(String s, ClassNode b) throws IOException {
        for (MethodNode node : b.methods) {
            logger.write(node.name + '\n');
            if (node.name.equals("<init>") && node.desc.equals("()V")) {
                node.instructions.clear();
                node.instructions.clear();
                node.visitVarInsn(ALOAD, 0);
                node.visitLdcInsn(0L);
                node.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Random.class), "<init>", "(J)V", false);
                node.visitInsn(RETURN);
            }
        }
    }

    private static byte[] transform2(String name, byte[] data) throws IOException, NoSuchMethodException {
        ClassReader reader = new ClassReader(data);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        transform2(node.name, node);
        ClassWriter writer = new ClassWriter(2);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static void transform2(String s, ClassNode b) throws IOException, NoSuchMethodException {
        for (MethodNode node : b.methods) {
            logger.write(node.desc + '\n');
            if (node.desc.equals("(Ljava/util/Random;)Ljava/util/UUID;")) {
                logger.write("Found!\n");
                node.instructions.clear();
                node.visitFieldInsn(GETSTATIC, Type.getInternalName(mathHelper),
                        RANDOM.getName(), Type.getDescriptor(RANDOM.getType()));
                node.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Random.class),
                        "nextLong", "()J", false);
                node.visitLdcInsn(-61441L);
                node.visitInsn(LAND);
                node.visitLdcInsn(16384L);
                node.visitInsn(LOR);
                node.visitVarInsn(LSTORE, 1);
                node.visitFieldInsn(GETSTATIC, Type.getInternalName(mathHelper),
                        RANDOM.getName(), Type.getDescriptor(RANDOM.getType()));
                node.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Random.class),
                        "nextLong", "()J", false);
                node.visitLdcInsn(4611686018427387903L);
                node.visitInsn(LAND);
                node.visitLdcInsn(-9223372036854775808L);
                node.visitInsn(LOR);
                node.visitVarInsn(LSTORE, 3);
                node.visitTypeInsn(NEW, Type.getInternalName(UUID.class));
                node.visitInsn(DUP);
                node.visitVarInsn(LLOAD, 1);
                node.visitVarInsn(LLOAD, 3);
                node.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(UUID.class),
                        "<init>", Type.getConstructorDescriptor(UUID.class.getConstructor(long.class, long.class)), false);
                node.visitInsn(ARETURN);
            }
//            if (node.name.equals("<init>") && node.desc.equals("()V")) {
//                node.instructions.clear();
//                node.instructions.clear();
//                node.visitVarInsn(ALOAD, 0);
//                node.visitLdcInsn(0L);
//                node.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Random.class), "<init>", "(J)V", false);
//                node.visitInsn(RETURN);
//            }
        }
        logger.flush();
    }
}

//interface AsmClassTransformer {
//    void transform(String var1, ClassNode var2);
//
//    default AsmClassTransformer andThen(AsmClassTransformer fixer) {
//        return (s, c) -> {
//            this.transform(s, c);
//            fixer.transform(s, c);
//        };
//    }
//
//    default RawClassTransformer asRaw() {
//        return (name, data) -> {
//            ClassReader reader = new ClassReader(data);
//            ClassNode node = new ClassNode();
//            reader.accept(node, 0);
//            this.transform(node.name, node);
//            ClassWriter writer = new ClassWriter(2);
//            node.accept(writer);
//            return writer.toByteArray();
//        };
//    }
