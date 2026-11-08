import java.nio.file.*;
import org.objectweb.asm.*;

/** Changes only the initial selection-builder factory; confirmation stays untouched. */
public final class PatchCurrencySelection {
    public static void main(String[] args) throws Exception {
        byte[] original = Files.readAllBytes(Path.of(args[0]));
        ClassReader reader = new ClassReader(original);
        ClassWriter writer = new ClassWriter(reader, 0);
        int[] replacements = {0};
        reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                    String signature, String[] exceptions) {
                MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (!name.equals("open")) return delegate;
                return new MethodVisitor(Opcodes.ASM9, delegate) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String method,
                            String desc, boolean isInterface) {
                        if (opcode == Opcodes.INVOKESTATIC
                                && owner.equals("ca/landonjw/gooeylibs2/api/template/types/ChestTemplate")
                                && method.equals("builder")
                                && desc.equals("(I)Lca/landonjw/gooeylibs2/api/template/types/ChestTemplate$Builder;")) {
                            owner = "org/pokesplash/gts/UI/ReusableSelectionTemplate";
                            replacements[0]++;
                        }
                        super.visitMethodInsn(opcode, owner, method, desc, isInterface);
                    }
                };
            }
        }, 0);
        if (replacements[0] != 1) throw new IllegalStateException("Expected one factory, found " + replacements[0]);
        Files.write(Path.of(args[1]), writer.toByteArray());
    }
}
