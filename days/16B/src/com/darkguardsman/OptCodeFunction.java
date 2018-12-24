package com.darkguardsman;

import com.darkguardsman.data.CPU;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/2018.
 */
@FunctionalInterface
public interface OptCodeFunction
{
    void handle(CPU cpu, int inputA, int inputB, int outputRegister);
}
