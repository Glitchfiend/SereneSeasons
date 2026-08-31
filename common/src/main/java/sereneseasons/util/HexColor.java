package sereneseasons.util;

import java.util.HexFormat;

public final class HexColor extends Number {
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();
    public final int color;

    public HexColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "0x" + HEX_FORMAT.toHexDigits(color).substring(2);
    }

    @Override
    public int intValue() {
        return color;
    }

    @Override
    public long longValue() {
        return color;
    }

    @Override
    public float floatValue() {
        return color;
    }

    @Override
    public double doubleValue() {
        return color;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(color);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexColor other)) return false;
        return color == other.color;
    }
}
