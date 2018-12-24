package com.darkguardsman.data;

import java.util.HashMap;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class CPU
{
    public final int[] registers;

    public final HashMap<String, OptCode> optCodeMap = new HashMap();
    public OptCode[] optCodes;

    public CPU(int registerCount)
    {
        registers = new int[registerCount];
    }

    public void setRegisters(int[] data)
    {
        for (int i = 0; i < registers.length; i++)
        {
            registers[i] = data[i];
        }
    }

    public void addOptCode(OptCode optCode)
    {
        optCodeMap.put(optCode.name, optCode);
    }

    public void run(CpuLine line)
    {
        optCodes[line.getOptCode()].run(this, line.getInputA(), line.getInputB(), line.getOutputReg());
    }
}
