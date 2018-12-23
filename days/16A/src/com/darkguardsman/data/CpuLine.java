package com.darkguardsman.data;

import java.util.Arrays;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class CpuLine
{
    public final int[] data = new int[4];

    public int getOptCode()
    {
        return data[0];
    }

    public int getInputA()
    {
        return data[1];
    }

    public int getInputB()
    {
        return data[2];
    }

    public int getOutputReg()
    {
        return data[3];
    }

    @Override
    public String toString()
    {
        return "CpuLine(" + Arrays.toString(data) + ")";
    }
}
