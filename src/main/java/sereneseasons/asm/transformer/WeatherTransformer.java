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

public class WeatherTransformer implements IClassTransformer
{

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        ClassReader classReader = new ClassReader(bytes);

        boolean transform = isBiome(classReader);
        if (!transform)
        {
            return bytes;
        }

        ClassNode classNode = new ClassNode();

        classReader.accept(classNode, 0);

        boolean changed = false;
        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals("getEnableSnow") || methodNode.name.equals("func_76746_c") || (methodNode.name.equals("d") && methodNode.desc.equals("()Z")))
            {
                methodNode.name = "getEnableSnowOld";
                changed = true;
                for (int i = 0; i < methodNode.instructions.size(); i++)
                {
                    AbstractInsnNode instruction = methodNode.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.INVOKESPECIAL)
                    {
                        MethodInsnNode superCall = (MethodInsnNode) instruction;
                        superCall.name = "getEnableSnowOld";
                    }
                }
            }

            if (methodNode.name.equals("getFloatTemperature") || methodNode.name.equals("func_150564_a") || (methodNode.name.equals("a") && methodNode.desc.equals("(III)F")))
            {
                methodNode.name = "getFloatTemperatureOld";
                changed = true;
                for (int i = 0; i < methodNode.instructions.size(); i++)
                {
                    AbstractInsnNode instruction = methodNode.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.INVOKESPECIAL)
                    {
                        MethodInsnNode superCall = (MethodInsnNode) instruction;
                        superCall.name = "getFloatTemperatureOld";
                    }
                }
            }

            if (methodNode.name.equals("canSpawnLightningBolt") || methodNode.name.equals("func_76738_d") || (methodNode.name.equals("e") && methodNode.desc.equals("()Z")))
            {
                methodNode.name = "canSpawnLightningBoltOld";
                changed = true;
                for (int i = 0; i < methodNode.instructions.size(); i++)
                {
                    AbstractInsnNode instruction = methodNode.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.INVOKESPECIAL)
                    {
                        MethodInsnNode superCall = (MethodInsnNode) instruction;
                        superCall.name = "canSpawnLightningBoltOld";
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

    public static boolean isBiome(ClassReader classReader)
    {
        String superClassName = classReader.getSuperName();

        while (!superClassName.equals("java/lang/Object"))
        {
            if (superClassName.equals("net/minecraft/world/biome/BiomeGenBase") || superClassName.equals("ahu"))
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
