package sereneseasons.asm.crops;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class ASMHelper
{
    private static Boolean isCauldron = null;
    public static InsnComparator insnComparator = new InsnComparator();
    private static final Multimap<String, String> interfaceLookupCache = HashMultimap.create();

    /**
     * @return Whether or not Cauldron is loaded in the current environment.<br>
     *         <br>
     *         See: http://cauldron.minecraftforge.net/
     */
    public static boolean isCauldron()
    {
        if (ASMHelper.isCauldron == null)
        {
            try
            {
                byte[] bytes = ((LaunchClassLoader) ASMHelper.class.getClassLoader()).getClassBytes("net.minecraftforge.cauldron.api.Cauldron");
                ASMHelper.isCauldron = bytes != null;
            }
            catch (IOException e)
            {
                ASMHelper.isCauldron = false;
            }
        }

        return ASMHelper.isCauldron;
    }

    /**
     * Converts a class name to an internal class name.
     * 
     * @return internal/class/name
     */
    public static String toInternalClassName(String className)
    {
        return className.replace('.', '/');
    }

    /**
     * @return true if the String is a valid descriptor;
     */
    public static boolean isDescriptor(String descriptor)
    {
        return descriptor.length() == 1 || (descriptor.startsWith("L") && descriptor.endsWith(";"));
    }

    /**
     * Converts a class name to a descriptor.
     * 
     * @return Linternal/class/name;
     */
    public static String toDescriptor(String className)
    {
        return isDescriptor(className) ? className : "L" + toInternalClassName(className) + ";";
    }

    /**
     * Turns the given return and parameter values into a method descriptor Converts
     * the types into descriptors as needed
     * 
     * @return (LparamType;)LreturnType;
     */
    public static String toMethodDescriptor(String returnType, String... paramTypes)
    {
        StringBuilder paramDescriptors = new StringBuilder();
        for (String paramType : paramTypes)
            paramDescriptors.append(toDescriptor(paramType));

        return "(" + paramDescriptors.toString() + ")" + toDescriptor(returnType);
    }

    /**
     * Convert a byte array into a ClassNode.
     */
    public static ClassNode readClassFromBytes(byte[] bytes)
    {
        return readClassFromBytes(bytes, 0);
    }

    /**
     * Overload of {@link #readClassFromBytes(byte[])} with a flags parameter.
     */
    public static ClassNode readClassFromBytes(byte[] bytes, int flags)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    /**
     * Convert a ClassNode into a byte array. Attempts to resolve issues with
     * resolving super classes in an obfuscated environment. See
     * {@link ObfRemappingClassWriter}.
     */
    public static byte[] writeClassToBytes(ClassNode classNode)
    {
        return writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS);
    }

    /**
     * Overload of {@link #writeClassToBytes(ClassNode)} with a flags parameter.
     */
    public static byte[] writeClassToBytes(ClassNode classNode, int flags)
    {
        if (ObfHelper.isObfuscated() && !ObfHelper.runsAfterDeobfRemapper())
        {
            ClassWriter writer = new ObfRemappingClassWriter(flags);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        else
            return writeClassToBytesNoDeobf(classNode, flags);
    }

    /**
     * Convert a ClassNode into a byte array. Will have issues with resolving super
     * classes in an obfuscated environment.
     */
    public static byte[] writeClassToBytesNoDeobf(ClassNode classNode)
    {
        return writeClassToBytesNoDeobf(classNode, ClassWriter.COMPUTE_MAXS);
    }

    /**
     * Overload of {@link #writeClassToBytesNoDeobf(ClassNode)} with a flags
     * parameter.
     */
    public static byte[] writeClassToBytesNoDeobf(ClassNode classNode, int flags)
    {
        ClassWriter writer = new ClassWriter(flags);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    /**
     * @return An InputStream instance for the specified class name loaded by the
     *         specified ClassLoader.
     */
    public static InputStream getClassAsStreamFromClassLoader(String className, ClassLoader classLoader)
    {
        return classLoader.getResourceAsStream(className.replace('.', '/') + ".class");
    }

    /**
     * @return A ClassReader instance for the specified class name.
     */
    public static ClassReader getClassReaderForClassName(String className) throws IOException
    {
        return new ClassReader(getClassAsStreamFromClassLoader(className, ASMHelper.class.getClassLoader()));
    }

    /**
     * @return Whether or not the class read by the ClassReader has a valid super
     *         class.
     */
    public static boolean classHasSuper(ClassReader classReader)
    {
        return classReader.getSuperName() != null && !classReader.getSuperName().equals("java/lang/Object");
    }

    private static Collection<String> findAllInterfaces(ClassReader classReader)
    {
        // TODO: Find interfaces inside interfaces
        Set<String> interfaces = Sets.newHashSet(classReader.getInterfaces());

        try
        {
            if (classHasSuper(classReader))
            {
                String className2 = ObfHelper.getInternalClassName(classReader.getSuperName());
                if (interfaceLookupCache.containsKey(className2))
                    interfaces.addAll(interfaceLookupCache.get(className2));
                else
                    interfaces.addAll(findAllInterfaces(getClassReaderForClassName(className2)));
            }
        }
        catch (IOException e)
        {
            // This will trigger when the super class is abstract; just ignore the error
        }

        interfaceLookupCache.putAll(classReader.getClassName(), interfaces);
        return interfaces;
    }

    /**
     * @return Whether or not the class read by the ClassReader implements the
     *         specified interface.
     */
    public static boolean doesClassImplement(ClassReader classReader, String targetInterfaceInternalClassName)
    {
        if (!interfaceLookupCache.containsKey(classReader.getClassName()))
            findAllInterfaces(classReader);

        return interfaceLookupCache.get(classReader.getClassName()).contains(targetInterfaceInternalClassName);
    }

    /**
     * @return Whether or not the class read by the ClassReader extends the
     *         specified class.
     */
    public static boolean doesClassExtend(ClassReader classReader, String targetSuperInternalClassName)
    {
        if (!classHasSuper(classReader))
            return false;

        String immediateSuperName = ObfHelper.getInternalClassName(classReader.getSuperName());
        if (immediateSuperName.equals(targetSuperInternalClassName))
            return true;

        try
        {
            return doesClassExtend(getClassReaderForClassName(immediateSuperName), targetSuperInternalClassName);
        }
        catch (IOException e)
        {
            // This will trigger when the super class is abstract; just ignore the error
        }
        return false;
    }

    /**
     * @return Whether or not the instruction is a label or a line number.
     */
    public static boolean isLabelOrLineNumber(AbstractInsnNode insn)
    {
        return insn.getType() == AbstractInsnNode.LABEL || insn.getType() == AbstractInsnNode.LINE;
    }

    /**
     * @return The first instruction for which {@link AbstractInsnNode#getType()} ==
     *         {@code type} (could be {@code firstInsnToCheck}). If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type)
    {
        return getOrFindInstructionOfType(firstInsnToCheck, type, false);
    }

    /**
     * @return The first instruction for which {@link AbstractInsnNode#getType()} ==
     *         {@code type} (could be {@code firstInsnToCheck}). If
     *         {@code reverseDirection} is {@code true}, instructions will be
     *         traversed backwards (using getPrevious()). If a matching instruction
     *         cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type, boolean reverseDirection)
    {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
        {
            if (instruction.getType() == type)
                return instruction;
        }
        return null;
    }

    /**
     * @return The first instruction for which {@link AbstractInsnNode#getOpcode()}
     *         == {@code opcode} (could be {@code firstInsnToCheck}). If a matching
     *         instruction cannot be found, returns {@code null}
     */
    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode)
    {
        return getOrFindInstructionWithOpcode(firstInsnToCheck, opcode, false);
    }

    /**
     * @return The first instruction for which {@link AbstractInsnNode#getOpcode()}
     *         == {@code opcode} (could be {@code firstInsnToCheck}). If
     *         {@code reverseDirection} is {@code true}, instructions will be
     *         traversed backwards (using getPrevious()). If a matching instruction
     *         cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode, boolean reverseDirection)
    {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
        {
            if (instruction.getOpcode() == opcode)
                return instruction;
        }
        return null;
    }

    /**
     * @return The first instruction for which {@link #isLabelOrLineNumber} ==
     *         {@code true} (could be {@code firstInsnToCheck}). If
     *         {@code reverseDirection} is {@code true}, instructions will be
     *         traversed backwards (using getPrevious()). If a matching instruction
     *         cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindLabelOrLineNumber(AbstractInsnNode firstInsnToCheck)
    {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    /**
     * @return The first instruction for which {@link #isLabelOrLineNumber} ==
     *         {@code true} (could be {@code firstInsnToCheck}). If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindLabelOrLineNumber(AbstractInsnNode firstInsnToCheck, boolean reverseDirection)
    {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
        {
            if (isLabelOrLineNumber(instruction))
                return instruction;
        }
        return null;
    }

    /**
     * @return The first instruction for which {@link #isLabelOrLineNumber} ==
     *         {@code false} (could be {@code firstInsnToCheck}). If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck)
    {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    /**
     * @return The first instruction for which {@link #isLabelOrLineNumber} ==
     *         {@code false} (could be {@code firstInsnToCheck}). If
     *         {@code reverseDirection} is {@code true}, instructions will be
     *         traversed backwards (using getPrevious()). If a matching instruction
     *         cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection)
    {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
        {
            if (!isLabelOrLineNumber(instruction))
                return instruction;
        }
        return null;
    }

    /**
     * @return The first instruction of the {@code method} for which
     *         {@link #isLabelOrLineNumber} == {@code false}. If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode findFirstInstruction(MethodNode method)
    {
        return getOrFindInstruction(method.instructions.getFirst());
    }

    /**
     * @return The first instruction of the {@code method} for which
     *         {@link AbstractInsnNode#getOpcode()} == {@code opcode}. If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode findFirstInstructionWithOpcode(MethodNode method, int opcode)
    {
        return getOrFindInstructionWithOpcode(method.instructions.getFirst(), opcode);
    }

    /**
     * @return The last instruction of the {@code method} for which
     *         {@link AbstractInsnNode#getOpcode()} == {@code opcode}. If a matching
     *         instruction cannot be found, returns {@code null}.
     */
    public static AbstractInsnNode findLastInstructionWithOpcode(MethodNode method, int opcode)
    {
        return getOrFindInstructionWithOpcode(method.instructions.getLast(), opcode, true);
    }

    /**
     * @return The next instruction after {@code instruction} for which
     *         {@link #isLabelOrLineNumber} == {@code false} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findNextInstruction(AbstractInsnNode instruction)
    {
        return getOrFindInstruction(instruction.getNext());
    }

    /**
     * @return The next instruction after {@code instruction} for which
     *         {@link AbstractInsnNode#getOpcode()} == {@code opcode} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findNextInstructionWithOpcode(AbstractInsnNode instruction, int opcode)
    {
        return getOrFindInstructionWithOpcode(instruction.getNext(), opcode);
    }

    /**
     * @return The next instruction after {@code instruction} for which
     *         {@link #isLabelOrLineNumber} == {@code true} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findNextLabelOrLineNumber(AbstractInsnNode instruction)
    {
        return getOrFindLabelOrLineNumber(instruction.getNext());
    }

    /**
     * @return The previous instruction before {@code instruction} for which
     *         {@link #isLabelOrLineNumber} == {@code false} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findPreviousInstruction(AbstractInsnNode instruction)
    {
        return getOrFindInstruction(instruction.getPrevious(), true);
    }

    /**
     * @return The previous instruction before {@code instruction} for which
     *         {@link AbstractInsnNode#getOpcode()} == {@code opcode} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findPreviousInstructionWithOpcode(AbstractInsnNode instruction, int opcode)
    {
        return getOrFindInstructionWithOpcode(instruction.getPrevious(), opcode, true);
    }

    /**
     * @return The previous instruction before {@code instruction} for which
     *         {@link #isLabelOrLineNumber} == {@code true} (excluding
     *         {@code instruction}). If a matching instruction cannot be found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findPreviousLabelOrLineNumber(AbstractInsnNode instruction)
    {
        return getOrFindLabelOrLineNumber(instruction.getPrevious(), true);
    }

    /**
     * @return The method of the class that has both a matching {@code methodName}
     *         and {@code methodDesc}. If no matching method is found, returns
     *         {@code null}.
     */
    public static MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
    {
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(methodName) && (methodDesc == null || method.desc.equals(methodDesc)))
            {
                return method;
            }
        }
        return null;
    }

    /**
     * @return The method of the class that has a matching {@code srgMethodName} or
     *         {@code mcpMethodName} and a matching {@code methodDesc}. If no
     *         matching method is found, returns {@code null}.
     */
    public static MethodNode findMethodNodeOfClass(ClassNode classNode, String srgMethodName, String mcpMethodName, String methodDesc)
    {
        for (MethodNode method : classNode.methods)
        {
            if ((method.name.equals(srgMethodName) || method.name.equals(mcpMethodName)) && (methodDesc == null || method.desc.equals(methodDesc)))
            {
                return method;
            }
        }
        return null;
    }

    /**
     * Adding instructions to abstract methods will cause a
     * {@link java.lang.ClassFormatError}
     * 
     * @return Whether or not the {@code MethodNode} is abstract
     */
    public static boolean isMethodAbstract(MethodNode method)
    {
        return (method.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /**
     * Useful for defining the end label for ASM-inserted local variables.
     * 
     * @return The last label of the method (usually after the RETURN instruction).
     *         If no matching {@link LabelNode} is found, returns {@code null}.
     */
    public static LabelNode findEndLabel(MethodNode method)
    {
        for (AbstractInsnNode instruction = method.instructions.getLast(); instruction != null; instruction = instruction.getPrevious())
        {
            if (instruction instanceof LabelNode)
                return (LabelNode) instruction;
        }
        return null;
    }

    /**
     * Remove instructions from {@code insnList} starting with
     * {@code startInclusive} up until reaching {@code endNotInclusive}
     * ({@code endNotInclusive} will not be removed).
     * 
     * @return The number of instructions removed
     */
    public static int removeFromInsnListUntil(InsnList insnList, AbstractInsnNode startInclusive, AbstractInsnNode endNotInclusive)
    {
        AbstractInsnNode insnToRemove = startInclusive;
        int numDeleted = 0;
        while (insnToRemove != null && insnToRemove != endNotInclusive)
        {
            numDeleted++;
            insnToRemove = insnToRemove.getNext();
            insnList.remove(insnToRemove.getPrevious());
        }
        return numDeleted;
    }

    /**
     * Note: This is an alternative to
     * {@link #removeFromInsnListUntil(InsnList, AbstractInsnNode, AbstractInsnNode)
     * removeFromInsnListUntil} and will achieve a similar result. <br>
     * <br>
     * 
     * Skip instructions from {@code insnList} starting with {@code startInclusive}
     * up until reaching {@code endNotInclusive} ({@code endNotInclusive} will not
     * be skipped).
     *
     * This is achieved by inserting a GOTO instruction before
     * {@code startInclusive} which is branched to a LabelNode that is inserted
     * before {@code endNotInclusive}.
     */
    public static void skipInstructions(InsnList insnList, AbstractInsnNode startInclusive, AbstractInsnNode endNotInclusive)
    {
        LabelNode skipLabel = new LabelNode();
        JumpInsnNode gotoInsn = new JumpInsnNode(Opcodes.GOTO, skipLabel);
        insnList.insertBefore(startInclusive, gotoInsn);
        insnList.insertBefore(endNotInclusive, skipLabel);
    }

    /**
     * Note: Does not move the instruction, but rather gets the instruction a
     * certain distance away from {@code start}.<br>
     * <br>
     * 
     * <b>Example:</b>
     * 
     * <pre>
     * {
     *     &#64;code
     * // fifthInsn is pointing to the 5th instruction of insnList
     *     AbstractInsnNode thirdInsn = ASMHelper.move(fifthInsn, -2);
     *     AbstractInsnNode eightInsn = ASMHelper.move(fifthInsn, 3);
     * }
     * </pre>
     * 
     * @param start    The instruction to move from
     * @param distance The distance to move (can be positive or negative)
     * @return The instruction {@code distance} away from the {@code start}
     *         instruction
     */
    public static AbstractInsnNode move(final AbstractInsnNode start, int distance)
    {
        AbstractInsnNode movedTo = start;
        for (int i = 0; i < Math.abs(distance) && movedTo != null; i++)
        {
            movedTo = distance > 0 ? movedTo.getNext() : movedTo.getPrevious();
        }
        return movedTo;
    }

    /**
     * Convenience method for accessing {@link InsnComparator#areInsnsEqual}
     */
    public static boolean instructionsMatch(AbstractInsnNode first, AbstractInsnNode second)
    {
        return insnComparator.areInsnsEqual(first, second);
    }

    /**
     * @return Whether or not the pattern in {@code checkFor} matches starting at
     *         {@code checkAgainst}
     */
    public static boolean patternMatches(InsnList checkFor, AbstractInsnNode checkAgainst)
    {
        return checkForPatternAt(checkFor, checkAgainst).getFirst() != null;
    }

    /**
     * Checks whether or not the pattern in {@code checkFor} matches, starting at
     * {@code checkAgainst}.
     * 
     * @return All of the instructions that were matched by the {@code checkFor}
     *         pattern. If the pattern was not found, returns an empty
     *         {@link InsnList}.<br>
     *         <br>
     *         Note: If the pattern was matched, the size of the returned
     *         {@link InsnList} will be >= {@code checkFor}.size().
     */
    public static InsnList checkForPatternAt(InsnList checkFor, AbstractInsnNode checkAgainst)
    {
        InsnList foundInsnList = new InsnList();
        boolean firstNeedleFound = false;
        for (AbstractInsnNode lookFor = checkFor.getFirst(); lookFor != null;)
        {
            if (checkAgainst == null)
                return new InsnList();

            if (isLabelOrLineNumber(lookFor))
            {
                lookFor = lookFor.getNext();
                continue;
            }

            if (isLabelOrLineNumber(checkAgainst))
            {
                if (firstNeedleFound)
                    foundInsnList.add(checkAgainst);
                checkAgainst = checkAgainst.getNext();
                continue;
            }

            if (!instructionsMatch(lookFor, checkAgainst))
                return new InsnList();

            foundInsnList.add(checkAgainst);
            lookFor = lookFor.getNext();
            checkAgainst = checkAgainst.getNext();
            firstNeedleFound = true;
        }
        return foundInsnList;
    }

    /**
     * Searches for the pattern in {@code needle}, starting at
     * {@code haystackStart}.
     * 
     * @return All of the instructions that were matched by the pattern. If the
     *         pattern was not found, returns an empty {@link InsnList}.<br>
     *         <br>
     *         Note: If the pattern was matched, the size of the returned
     *         {@link InsnList} will be >= {@code checkFor}.size().
     */
    public static InsnList findAndGetFoundInsnList(AbstractInsnNode haystackStart, InsnList needle)
    {
        int needleStartOpcode = needle.getFirst().getOpcode();
        AbstractInsnNode checkAgainstStart = getOrFindInstructionWithOpcode(haystackStart, needleStartOpcode);
        while (checkAgainstStart != null)
        {
            InsnList found = checkForPatternAt(needle, checkAgainstStart);

            if (found.getFirst() != null)
                return found;

            checkAgainstStart = findNextInstructionWithOpcode(checkAgainstStart, needleStartOpcode);
        }
        return new InsnList();
    }

    /**
     * Searches for the pattern in {@code needle} within {@code haystack}.
     * 
     * @return The first instruction of the matched pattern. If the pattern was not
     *         found, returns an empty {@link InsnList}.
     */
    public static AbstractInsnNode find(InsnList haystack, InsnList needle)
    {
        return find(haystack.getFirst(), needle);
    }

    /**
     * Searches for the pattern in {@code needle}, starting at
     * {@code haystackStart}.
     * 
     * @return The first instruction of the matched pattern. If the pattern was not
     *         found, returns {@code null}.
     */
    public static AbstractInsnNode find(AbstractInsnNode haystackStart, InsnList needle)
    {
        if (needle.getFirst() == null)
            return null;

        InsnList found = findAndGetFoundInsnList(haystackStart, needle);
        return found.getFirst();
    }

    /**
     * Searches for an instruction matching {@code needle} within {@code haystack}.
     * 
     * @return The matching instruction. If a matching instruction was not found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode find(InsnList haystack, AbstractInsnNode needle)
    {
        return find(haystack.getFirst(), needle);
    }

    /**
     * Searches for an instruction matching {@code needle}, starting at
     * {@code haystackStart}.
     * 
     * @return The matching instruction. If a matching instruction was not found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode find(AbstractInsnNode haystackStart, AbstractInsnNode needle)
    {
        InsnList insnList = new InsnList();
        insnList.add(needle);
        return find(haystackStart, insnList);
    }

    /**
     * Searches for the pattern in {@code needle} within {@code haystack} and
     * replaces it with {@code replacement}.
     * 
     * @return The instruction after the replacement. If the pattern was not found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findAndReplace(InsnList haystack, InsnList needle, InsnList replacement)
    {
        return findAndReplace(haystack, needle, replacement, haystack.getFirst());
    }

    /**
     * Searches for the pattern in {@code needle} within {@code haystack} (starting
     * at {@code haystackStart}) and replaces it with {@code replacement}.
     * 
     * @return The instruction after the replacement. If the pattern was not found,
     *         returns {@code null}.
     */
    public static AbstractInsnNode findAndReplace(InsnList haystack, InsnList needle, InsnList replacement, AbstractInsnNode haystackStart)
    {
        InsnList found = findAndGetFoundInsnList(haystackStart, needle);
        if (found.getFirst() != null)
        {
            haystack.insertBefore(found.getFirst(), replacement);
            AbstractInsnNode afterNeedle = found.getLast().getNext();
            removeFromInsnListUntil(haystack, found.getFirst(), afterNeedle);
            return afterNeedle;
        }
        return null;
    }

    /**
     * Searches for all instances of the pattern in {@code needle} within
     * {@code haystack} and replaces them with {@code replacement}.
     * 
     * @return The number of replacements made.
     */
    public static int findAndReplaceAll(InsnList haystack, InsnList needle, InsnList replacement)
    {
        return findAndReplaceAll(haystack, needle, replacement, haystack.getFirst());
    }

    /**
     * Searches for all instances of the pattern in {@code needle} within
     * {@code haystack} (starting at {@code haystackStart}) and replaces them with
     * {@code replacement}.
     * 
     * @return The number of replacements made.
     */
    public static int findAndReplaceAll(InsnList haystack, InsnList needle, InsnList replacement, AbstractInsnNode haystackStart)
    {
        int numReplaced = 0;
        // insert/insertBefore clears the replacement list, so we need to use a copy
        // each time
        while ((haystackStart = findAndReplace(haystack, needle, cloneInsnList(replacement), haystackStart)) != null)
        {
            numReplaced++;
        }
        return numReplaced;
    }

    /**
     * Clones an instruction list, remapping labels in the process.
     * 
     * @return The cloned {@code InsnList}
     */
    public static InsnList cloneInsnList(InsnList source)
    {
        InsnList clone = new InsnList();

        // used to map the old labels to their cloned counterpart
        Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();

        // build the label map
        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext())
        {
            if (instruction instanceof LabelNode)
            {
                labelMap.put(((LabelNode) instruction), new LabelNode());
            }
        }

        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext())
        {
            clone.add(instruction.clone(labelMap));
        }

        return clone;
    }

    /**
     * @return The local variable of the method that has both a matching
     *         {@code varName} and {@code varDesc}. If no matching local variable is
     *         found, returns {@code null}.
     */
    public static LocalVariableNode findLocalVariableOfMethod(MethodNode method, String varName, String varDesc)
    {
        for (LocalVariableNode localVar : method.localVariables)
        {
            if (localVar.name.equals(varName) && localVar.desc.equals(varDesc))
            {
                return localVar;
            }
        }
        return null;
    }

    /**
     * Copy a method and rename it; everything else will be exactly the same
     * 
     * @return The renamed method copy
     */
    public static MethodNode copyAndRenameMethod(ClassNode classNode, MethodNode method, String newMethodName)
    {
        MethodVisitor methodCopyVisitor = classNode.visitMethod(method.access, newMethodName, method.desc, method.signature, method.exceptions.toArray(new String[method.exceptions.size()]));
        method.accept(new RemappingMethodAdapter(method.access, method.desc, methodCopyVisitor, new Remapper()
        {
        }));
        return methodCopyVisitor instanceof MethodNode ? (MethodNode) methodCopyVisitor : null;
    }

    private static Printer printer = new Textifier();
    private static TraceMethodVisitor methodprinter = new TraceMethodVisitor(printer);

    /**
     * @return {@code insnList} as a string.<br>
     *         <br>
     *         <b>Example output:</b><br>
     * 
     *         <pre>
     *    ALOAD 0
     *    GETFIELD net/minecraft/util/FoodStats.foodLevel : I
     *    ISTORE 3
     *    ALOAD 0
     *         </pre>
     */
    public static String getInsnListAsString(InsnList insnList)
    {
        insnList.accept(methodprinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

    /**
     * @return {@code method} as a string.
     */
    public static String getMethodAsString(MethodNode method)
    {
        method.accept(methodprinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }
}