package jp.hashiwa.bci;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static jdk.internal.org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * Created by hashiwa on 16/09/24.
 */
public class PrintStackTraceAdapter extends ClassVisitor {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private final int api;
  private final List<Pattern> targetPatterns;
  private final List<Pattern> ignorePatterns;
  private boolean isInterface;
  private String className;

  public PrintStackTraceAdapter(int api, ClassVisitor cv, String targetMethod) {
    this(api, cv, Arrays.asList(targetMethod));
  }
  public PrintStackTraceAdapter(int api, ClassVisitor cv, List<String> targetMethods) {
    super(api, cv);
    this.api = api;
    this.targetPatterns = targetMethods.stream()
            .map(Pattern::compile)
            .collect(Collectors.toList());
    this.ignorePatterns =
            Stream.of("^java/.*$", "^sun/.*$")
            .map(Pattern::compile)
            .collect(Collectors.toList());
  }

  @Override
  public void visit(int version, int access, String name, String signature,
                    String superName, String[] interfaces)
  {
    super.visit(version, access, name, signature, superName, interfaces);
    isInterface = (access & ACC_INTERFACE) != 0;
    className = name;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
                                   String signature, String[] exceptions)
  {
    String fullMethodName = this.className + ":" + name;
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    if (!isInterface && mv != null && this.isTargetMethod(fullMethodName)) {
      mv = new Visitor(this.api, mv);
    }
    return mv;
  }

  private boolean isTargetMethod(String methodName) {
    for (Pattern pattern: this.ignorePatterns) {
      if (pattern.matcher(methodName).matches()) {
        return false;
      }
    }
    for (Pattern pattern: this.targetPatterns) {
      if (pattern.matcher(methodName).matches()) {
        return true;
      }
    }
    return false;
  }

  private static final class Visitor extends MethodVisitor {
    public Visitor(int api, MethodVisitor mv) {
      super(api, mv);
    }

    @Override
    public void visitCode() {
      super.visitCode();
      mv.visitMethodInsn(INVOKESTATIC, "jp/hashiwa/bci/PrintStackTraceAdapter", "printStackTrace", "()V", false);
    }
  }

  public static void printStackTrace() {
    try {
      StringBuilder buf = new StringBuilder();
      Thread current = Thread.currentThread();
      buf.append(current.getName() + "[id=" + current.getId() + ", piority=" + current.getPriority() + "]");
      buf.append(LINE_SEPARATOR);
      StackTraceElement[] trace = current.getStackTrace();
      if (trace != null) {
        for (int i=2 ; i<trace.length ; i++) {
          buf.append("\tat " + trace[i]);
          buf.append(LINE_SEPARATOR);
        }
      }
      System.out.print(buf.toString());
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }
  }
}