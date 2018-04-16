/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.asm.transformer;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import sereneseasons.asm.ASMHelper;
import sereneseasons.asm.ObfHelper;

import java.util.List;

public class EntityRendererTransformer implements IClassTransformer
{
    private static final String[] RENDER_RAIN_SNOW_NAMES = new String[] { "renderRainSnow", "func_78474_d", "c" };
    private static final String[] ADD_RAIN_PARTICLES_NAMES = new String[] { "addRainParticles", "func_78484_h", "q" };

    private static final String[] GET_FLOAT_TEMPERATURE_NAMES = new String[] { "getTemperature", "func_180626_a", "a" };
    private static final String[] CAN_RAIN_NAMES = new String[] { "canRain", "func_76738_d", "d" };
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer"))
        {
            return transformEntityRenderer(basicClass, !transformedName.equals(name));
        }
        
        return basicClass;
    }
    
    private byte[] transformEntityRenderer(byte[] bytes, boolean obfuscatedClass)
    {
        //Decode the class from bytes
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        List<String> successfulTransformations = Lists.newArrayList();
        
        //Iterate over the methods in the class
        for (MethodNode methodNode : classNode.methods)
        {
            if (ASMHelper.methodEquals(methodNode, RENDER_RAIN_SNOW_NAMES, "(F)V"))
            {
                int successCount = 0;

                MethodInsnNode targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, Opcodes.INVOKEVIRTUAL, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), CAN_RAIN_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z"));

                // Replace biome.canRain() || biome.getEnableSnow() check with our own
                if (targetMethodInsnNode != null)
                {
                    int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);
                    int worldAloadIndex = targetInsnIndex - 7;
                    int worldVarNum = -1;

                    // Take the world var num from a nearby instruction incase some other mod breaks it.
                    // Looking at you Optifine!
                    if (methodNode.instructions.get(worldAloadIndex) instanceof VarInsnNode)
                    {
                        worldVarNum = ((VarInsnNode)methodNode.instructions.get(worldAloadIndex)).var;
                    }
                    else
                    {
                        throw new RuntimeException("Failed to locate world var number whilst replacing biome.canRain() || biome.getEnableSnow()");
                    }

                    targetMethodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                    targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
                    targetMethodInsnNode.name = "shouldRenderRainSnow";
                    targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/world/biome/Biome");

                    ASMHelper.clearNextInstructions(methodNode, methodNode.instructions.get(targetInsnIndex + 1), 3);

                    // Add world argument
                    methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 1), new VarInsnNode(Opcodes.ALOAD, worldVarNum));

                    targetInsnIndex -= 1;

                    int offset = 0;
                    AbstractInsnNode currentInsn = methodNode.instructions.get(targetInsnIndex - offset);;

                    // Remove any extraneous instructions on this line. This should hopefully help prevent other mods
                    // from breaking our stuff
                    do
                    {
                        if (!(currentInsn instanceof VarInsnNode))
                        {
                            methodNode.instructions.remove(currentInsn);
                        }
                        else
                        {
                            VarInsnNode varInsnNode = (VarInsnNode)currentInsn;

                            if (varInsnNode.getOpcode() == Opcodes.ALOAD && varInsnNode.var != 5 && varInsnNode.var != 29)
                            {
                                methodNode.instructions.remove(currentInsn);
                            }
                            else
                            {
                                offset++;
                            }
                        }

                        currentInsn = methodNode.instructions.get(targetInsnIndex - offset);
                    }
                    while(!(currentInsn instanceof LabelNode));

                    successCount++;
                }

                targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, Opcodes.INVOKEVIRTUAL, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), GET_FLOAT_TEMPERATURE_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/util/math/BlockPos"));

                if (targetMethodInsnNode != null)
                {
                    //Redirect the call to our own version of getFloatTemperature
                    targetMethodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                    targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
                    targetMethodInsnNode.name = "getFloatTemperature";
                    targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/world/biome/Biome", "net/minecraft/util/math/BlockPos");
                    successCount++;
                }

                if (successCount == 2)
                    successfulTransformations.add(methodNode.name + " " + methodNode.desc);
            }
            else if (ASMHelper.methodEquals(methodNode, ADD_RAIN_PARTICLES_NAMES, "()V"))
            {
                int successCount = 0;

                MethodInsnNode targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, Opcodes.INVOKEVIRTUAL, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), CAN_RAIN_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "Z"));

                if (targetMethodInsnNode != null)
                {
                    int targetInsnIndex = methodNode.instructions.indexOf(targetMethodInsnNode);

                    //Redirect the call to canRain to our own check
                    targetMethodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                    targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
                    targetMethodInsnNode.name = "shouldAddRainParticles";
                    targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "Z", "net/minecraft/world/World", "net/minecraft/world/biome/Biome");

                    // Add world argument
                    methodNode.instructions.insertBefore(methodNode.instructions.get(targetInsnIndex - 1), new VarInsnNode(Opcodes.ALOAD, 3));

                    successCount++;
                }

                targetMethodInsnNode = ASMHelper.getUniqueMethodInsnNode(methodNode, Opcodes.INVOKEVIRTUAL, ObfHelper.unmapType(obfuscatedClass, "net/minecraft/world/biome/Biome"), GET_FLOAT_TEMPERATURE_NAMES, ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/util/math/BlockPos"));

                if (targetMethodInsnNode != null)
                {
                    //Redirect the call to our own version of getFloatTemperature
                    targetMethodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                    targetMethodInsnNode.owner = "sereneseasons/season/SeasonASMHelper";
                    targetMethodInsnNode.name = "getFloatTemperature";
                    targetMethodInsnNode.desc = ObfHelper.createMethodDescriptor(obfuscatedClass, "F", "net/minecraft/world/biome/Biome", "net/minecraft/util/math/BlockPos");
                    successCount++;
                }

                if (successCount == 2)
                    successfulTransformations.add(methodNode.name + " " + methodNode.desc);
            }
        }
        
        if (successfulTransformations.size() != 2) throw new RuntimeException("An error occurred transforming EntityRenderer. Applied transformations: " + successfulTransformations.toString());
        
        //Encode the altered class back into bytes
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        bytes = writer.toByteArray();
        
        return bytes;
    }
}
