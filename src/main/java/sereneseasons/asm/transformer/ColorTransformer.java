package sereneseasons.asm.transformer;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ColorTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        ClassReader classReader = new ClassReader(bytes);

        boolean transform = isBlock(classReader);
        if (!transform)
        {
            return bytes;
        }

        ClassNode classNode = new ClassNode();

        classReader.accept(classNode, 0);

        boolean changed = false;
        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals("colorMultiplier") || methodNode.name.equals("func_149720_d") || (methodNode.name.equals("d") && methodNode.desc.equals("(Lahl;III)I")))
            {
                methodNode.name = "colorMultiplierOld";
                changed = true;
                for (int i = 0; i < methodNode.instructions.size(); i++)
                {
                    AbstractInsnNode instruction = methodNode.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.INVOKESPECIAL)
                    {
                        MethodInsnNode superCall = (MethodInsnNode) instruction;
                        if (superCall.name.equals("colorMultiplier") || superCall.name.equals("func_149720_d") || (superCall.name.equals("d") && superCall.desc.equals("(Lahl;III)I")))
                        {
                            superCall.name = "colorMultiplierOld";
                        }
                    }
                }
            }
        }

        if (changed)
        {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(writer);
            bytes = writer.toByteArray();
        }

        return bytes;
    }

    public static boolean isBlock(ClassReader classReader)
    {

        String superClassName = classReader.getSuperName();

        while (!superClassName.equals("java/lang/Object"))
        {
            if (superClassName.equals("net/minecraft/block/Block") || superClassName.equals("aji"))
            {
                return true;
            }
            try
            {
                classReader = new ClassReader(superClassName);
                superClassName = classReader.getSuperName();
            }
            catch (IOException e)
            {
                return false;
            }
        }

        return false;
    }
}
