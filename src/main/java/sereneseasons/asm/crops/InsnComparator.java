package sereneseasons.asm.crops;

import java.io.Serializable;
import java.util.Comparator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InsnComparator implements Comparator<AbstractInsnNode>, Serializable
{
    private static final long serialVersionUID = 408241651446425342L;
    public static final int INT_WILDCARD = -1;
    public static final String WILDCARD = "*";

    @Override
    public int compare(AbstractInsnNode a, AbstractInsnNode b)
    {
        return areInsnsEqual(a, b) ? 0 : 1;
    }

    /**
     * Respects {@link #INT_WILDCARD} and {@link #WILDCARD} instruction properties.
     * Always returns true if {@code a} and {@code b} are label, line number, or
     * frame instructions.
     * 
     * @return Whether or not the given instructions are equivalent.
     */
    public boolean areInsnsEqual(AbstractInsnNode a, AbstractInsnNode b)
    {
        if (a == b)
            return true;

        if (a == null || b == null)
            return false;

        if (a.equals(b))
            return true;

        if (a.getOpcode() != b.getOpcode())
            return false;

        switch (a.getType())
        {
        case AbstractInsnNode.VAR_INSN:
            return areVarInsnsEqual((VarInsnNode) a, (VarInsnNode) b);
        case AbstractInsnNode.TYPE_INSN:
            return areTypeInsnsEqual((TypeInsnNode) a, (TypeInsnNode) b);
        case AbstractInsnNode.FIELD_INSN:
            return areFieldInsnsEqual((FieldInsnNode) a, (FieldInsnNode) b);
        case AbstractInsnNode.METHOD_INSN:
            return areMethodInsnsEqual((MethodInsnNode) a, (MethodInsnNode) b);
        case AbstractInsnNode.LDC_INSN:
            return areLdcInsnsEqual((LdcInsnNode) a, (LdcInsnNode) b);
        case AbstractInsnNode.IINC_INSN:
            return areIincInsnsEqual((IincInsnNode) a, (IincInsnNode) b);
        case AbstractInsnNode.INT_INSN:
            return areIntInsnsEqual((IntInsnNode) a, (IntInsnNode) b);
        default:
            return true;
        }
    }

    private boolean areVarInsnsEqual(VarInsnNode a, VarInsnNode b)
    {
        return intValuesMatch(a.var, b.var);
    }

    private boolean areTypeInsnsEqual(TypeInsnNode a, TypeInsnNode b)
    {
        return valuesMatch(a.desc, b.desc);
    }

    private boolean areFieldInsnsEqual(FieldInsnNode a, FieldInsnNode b)
    {
        return valuesMatch(a.owner, b.owner) && valuesMatch(a.name, b.name) && valuesMatch(a.desc, b.desc);
    }

    private boolean areMethodInsnsEqual(MethodInsnNode a, MethodInsnNode b)
    {
        return valuesMatch(a.owner, b.owner) && valuesMatch(a.name, b.name) && valuesMatch(a.desc, b.desc);
    }

    private boolean areIntInsnsEqual(IntInsnNode a, IntInsnNode b)
    {
        return intValuesMatch(a.operand, b.operand);
    }

    private boolean areIincInsnsEqual(IincInsnNode a, IincInsnNode b)
    {
        return intValuesMatch(a.var, b.var) && intValuesMatch(a.incr, b.incr);
    }

    private boolean areLdcInsnsEqual(LdcInsnNode a, LdcInsnNode b)
    {
        return valuesMatch(a.cst, b.cst);
    }

    private boolean intValuesMatch(int a, int b)
    {
        return a == b || a == INT_WILDCARD || b == INT_WILDCARD;
    }

    private boolean valuesMatch(Object a, Object b)
    {
        return a.equals(b) || a.equals(WILDCARD) || b.equals(WILDCARD);
    }
}