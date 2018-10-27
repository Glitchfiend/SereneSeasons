package sereneseasons.asm.transformer;

import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.Lists;

import net.minecraft.launchwrapper.IClassTransformer;
import sereneseasons.asm.ASMHelper;
import sereneseasons.asm.ObfHelper;
import sereneseasons.util.ISeasonsColorResolver;

public class BiomeColorHelperTransformer implements IClassTransformer {

//	private static final String[] COLOR_RESOLVER_NAMES = new String[] { "net.minecraft.world.biome.BiomeColorHelper.ColorResolver", "anh" };
	
	private static final String[] GET_COLOR_AT_POS_NAMES = new String[] { "getColorAtPos", "func_180285_a", "a" };
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.world.biome.BiomeColorHelper"))
        {
            return transformBiomeColorHelper(basicClass, !transformedName.equals(name));
        }
        
        return basicClass;
	}
	
    private byte[] transformBiomeColorHelper(byte[] bytes, boolean obfuscatedClass)
    {
        //Decode the class from bytes
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        List<String> successfulTransformations = Lists.newArrayList();

        String descGetColorAtPos = ObfHelper.createMethodDescriptor(obfuscatedClass, "I", "net/minecraft/world/IBlockAccess", "net/minecraft/util/math/BlockPos", "net/minecraft/world/biome/BiomeColorHelper$ColorResolver");
        
        for (MethodNode methodNode : classNode.methods)
        {
            if (ASMHelper.methodEquals(methodNode, GET_COLOR_AT_POS_NAMES, descGetColorAtPos))
            {
            	LabelNode labelIfNot = new LabelNode();
            	
            	// Adding this code to the beginning:
            	//
            	// if( colorResolver instanceof ISeasonsColorResolver )
        		//     return SeasonASMHelper.getColorAtPosExtended(blockAccess, pos, (ISeasonsColorResolver)colorResolver);
            	
            	InsnList insnList = new InsnList();
            	insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
            	insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, Type.getInternalName(ISeasonsColorResolver.class))); // "sereneseasons.util.ISeasonsColorResolver"));
            	insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelIfNot));
            	insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));	// blockAccess
            	insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));	// pos
            	insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));	// colorResolver
            	insnList.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(ISeasonsColorResolver.class)));
            	insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "sereneseasons/season/SeasonASMHelper", "getColorAtPosExtended",
            			ObfHelper.createMethodDescriptor(obfuscatedClass, "I", "net/minecraft/world/IBlockAccess", "net/minecraft/util/math/BlockPos", "sereneseasons/util/ISeasonsColorResolver"), false));
            	insnList.add(new InsnNode(Opcodes.IRETURN));
            	insnList.add(labelIfNot);
            	insnList.add(new InsnNode(Opcodes.NOP));
            	
            	methodNode.instructions.insertBefore(methodNode.instructions.get(0), insnList);
            	
            	successfulTransformations.add(methodNode.name + " " + methodNode.desc);
            }
        }
        
        if (successfulTransformations.size() != 1) throw new RuntimeException("An error occurred transforming BiomeColorHelper. Applied transformations: " + successfulTransformations.toString());
        
        //Encode the altered class back into bytes
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        bytes = writer.toByteArray();
        
        return bytes;
    }
}
