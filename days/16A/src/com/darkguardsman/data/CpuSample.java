package com.darkguardsman.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class CpuSample extends CpuLine
{
    public final int[] before = new int[4];
    public final int[] after = new int[4];

    public final List<OptCode> possibleCodes = new ArrayList();

    public void init(CPU cpu)
    {
        cpu.setRegisters(before);
    }

    public boolean doesMatchAfter(CPU cpu)
    {
        for (int i = 0; i < cpu.registers.length; i++)
        {
            if (cpu.registers[i] != after[i])
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "CpuSample(" + Arrays.toString(before) + " >> " + Arrays.toString(data) + " >> " + Arrays.toString(after) + ")";
    }
}
