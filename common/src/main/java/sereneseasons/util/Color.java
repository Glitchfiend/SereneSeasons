/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.util;

public class Color
{
    int r, g, b;

    public Color(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(int color)
    {
        this((color >> 16) & 255, (color >> 8) & 255, color & 255);
    }

    public Color(double r, double g, double b)
    {
        this((int)(r * 255.0), (int)(g * 255), (int)(b * 255));
    }

    public int getRed()
    {
        return this.r;
    }

    public int getGreen()
    {
        return this.g;
    }

    public int getBlue()
    {
        return this.b;
    }

    public int toInt()
    {
        return (this.getRed() & 255) << 16 | (this.getGreen() & 255) << 8 | this.getBlue() & 255;
    }

    public double[] toHSV()
    {
        return convertRGBtoHSV(this.getRed() / 255.0, this.getGreen() / 255.0, this.getBlue() / 255.0);
    }

    // Based on https://stackoverflow.com/questions/3018313/algorithm-to-convert-rgb-to-hsv-and-hsv-to-rgb-in-range-0-255-for-both
    public static double[] convertRGBtoHSV(double r, double g, double b)
    {
        double h, s, v;
        double min, max, delta;

        min = r < g ? r : g;
        min = min  < b ? min  : b;

        max = r > g ? r : g;
        max = max  > b ? max  : b;

        v = max;
        delta = max - min;
        if (delta < 0.00001)
        {
            s = 0;
            h = 0; // undefined, maybe nan?
            return new double[]{h, s, v};
        }
        if (max > 0.0)
        {
            // NOTE: if Max is == 0, this divide would cause a crash
            s = (delta / max);
        }
        else
        {
            // if max is 0, then r = g = b = 0
            // s = 0, h is undefined
            s = 0.0;
            h = -1.0; // its now undefined
            return new double[]{h, s, v};
        }
        if (r >= max)
            h = ( g - b ) / delta; // between yellow & magenta
        else
        {
            if (g >= max)
                h = 2.0 + (b - r) / delta;  // between cyan & yellow
            else
                h = 4.0 + (r - g) / delta;  // between magenta & cyan
        }

        h *= 60.0;                              // degrees

        if (h < 0.0)
            h += 360.0;

        return new double[]{h, s, v};
    }

    public static Color convertHSVtoRGB(double h, double s, double v)
    {
        double hh, p, q, t, ff;
        int i;
        double r, g, b;

        if (s <= 0.0)
        {
            r = v;
            g = v;
            b = v;
            return new Color(r, g, b);
        }
        hh = h;
        if (hh >= 360.0) hh = 0.0;
        hh /= 60.0;
        i = (int)hh;
        ff = hh - i;
        p = v * (1.0 - s);
        q = v * (1.0 - (s * ff));
        t = v * (1.0 - (s * (1.0 - ff)));

        switch (i)
        {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;

            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
            default:
                r = v;
                g = p;
                b = q;
                break;
        }
        return new Color(r, g, b);
    }
}
