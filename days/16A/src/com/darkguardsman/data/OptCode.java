package com.darkguardsman.data;

import com.darkguardsman.OptCodeFunction;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
public class OptCode
{
    public final String name;
    public final OptCodeFunction function;

    public OptCode(String name, OptCodeFunction function)
    {
        this.name = name;
        this.function = function;
    }

    public void run(CPU cpu, int inputA, int inputB, int outputReg)
    {
        function.handle(cpu, inputA, inputB, outputReg);
    }
}
