package jp.hashiwa.bci;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * Created by hashiwa on 16/09/24.
 */
public class Agent {
  public static void premain(String agentArgs, Instrumentation instrumentation) {
//    System.out.println(agentArgs);
    if (agentArgs != null) {
      List<String> targetMethods = Arrays.asList(agentArgs.split(","));
      instrumentation.addTransformer(new Transformer(targetMethods));
    }
  }

  private static class Transformer implements ClassFileTransformer {
    private static final List<Pattern> IGNORE_CLASS_PATTERNS =
            Stream.of(
                    "^java/.*$",
                    "^sun/.*$"
            )
            .map(Pattern::compile)
            .collect(Collectors.toList());
    private List<String> targetMethods;

    Transformer(List<String> targetMethods) {
      this.targetMethods = targetMethods;
    }

    @Override
    public byte[] transform(ClassLoader classLoader, String s,
                            Class<?> aClass, ProtectionDomain protectionDomain,
                            byte[] bytes) throws IllegalClassFormatException
    {
      if (canTransform(s)) {
        try {
          ClassWriter cw = new ClassWriter(0);
          ClassVisitor cv = new PrintStackTraceAdapter(ASM5, cw, this.targetMethods);
          ClassReader cr = new ClassReader(bytes);
          cr.accept(cv, ClassReader.EXPAND_FRAMES);
          return cw.toByteArray();
        } catch(Exception e) {
          e.printStackTrace();
          return bytes;
        }
      } else {
        return bytes;
      }
    }

    private boolean canTransform(String className) {
      for (Pattern pattern: IGNORE_CLASS_PATTERNS) {
        if (pattern.matcher(className).matches()) {
          return false;
        }
      }
      return true;
    }
  }
}
