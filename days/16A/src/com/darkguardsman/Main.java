package com.darkguardsman;


import com.darkguardsman.data.CPU;
import com.darkguardsman.data.CpuLine;
import com.darkguardsman.data.CpuSample;
import com.darkguardsman.data.OptCode;
import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/23/18.
 */
public class Main
{
    public static void main(String... args)
    {
        final File file = new File(args[0]);
        System.out.println("File: " + file);

        System.out.println("\nReading lines: ");
        List<String> lines = readFile(file);

        System.out.println("\nConverting to objects: ");
        List<CpuSample> samples = getSamples(lines);
        samples.forEach(s -> System.out.println(s));

        System.out.println("\n");
        List<CpuLine> cpuLines = getCpuLine(lines);
        cpuLines.forEach(s -> System.out.println(s));

        System.out.println("\nSetting up CPU: ");
        final CPU cpu = new CPU(4);
        cpu.addOptCode(new OptCode("addr", (u, a, b, c) -> u.registers[c] = u.registers[a] + u.registers[b]));
        cpu.addOptCode(new OptCode("addi", (u, a, b, c) -> u.registers[c] = u.registers[a] + b));

        cpu.addOptCode(new OptCode("mulr", (u, a, b, c) -> u.registers[c] = u.registers[a] * u.registers[b]));
        cpu.addOptCode(new OptCode("muli", (u, a, b, c) -> u.registers[c] = u.registers[a] * b));

        //https://www.tutorialspoint.com/Java-Bitwise-Operators
        cpu.addOptCode(new OptCode("banr", (u, a, b, c) -> u.registers[c] = u.registers[a] & u.registers[b]));
        cpu.addOptCode(new OptCode("bani", (u, a, b, c) -> u.registers[c] = u.registers[a] & b));

        cpu.addOptCode(new OptCode("borr", (u, a, b, c) -> u.registers[c] = u.registers[a] | u.registers[b]));
        cpu.addOptCode(new OptCode("bori", (u, a, b, c) -> u.registers[c] = u.registers[a] | b));

        cpu.addOptCode(new OptCode("setr", (u, a, b, c) -> u.registers[c] = u.registers[a]));
        cpu.addOptCode(new OptCode("seti", (u, a, b, c) -> u.registers[c] = a));

        cpu.addOptCode(new OptCode("gtir", (u, a, b, c) -> u.registers[c] = a > u.registers[b] ? 1 : 0));
        cpu.addOptCode(new OptCode("gtri", (u, a, b, c) -> u.registers[c] = u.registers[a] > b ? 1 : 0));
        cpu.addOptCode(new OptCode("gtrr", (u, a, b, c) -> u.registers[c] = u.registers[a] > u.registers[b] ? 1 : 0));

        cpu.addOptCode(new OptCode("eqir", (u, a, b, c) -> u.registers[c] = a == u.registers[b] ? 1 : 0));
        cpu.addOptCode(new OptCode("eqri", (u, a, b, c) -> u.registers[c] = u.registers[a] == b ? 1 : 0));
        cpu.addOptCode(new OptCode("eqrr", (u, a, b, c) -> u.registers[c] = u.registers[a] == u.registers[b] ? 1 : 0));

        System.out.println("\tCodes: " + cpu.optCodeMap.size());

        System.out.println("\nRunning Samples: ");
        for (CpuSample sample : samples)
        {
            for (OptCode optCode : cpu.optCodeMap.values())
            {
                cpu.setRegisters(sample.before);
                optCode.run(cpu, sample.getInputA(), sample.getInputB(), sample.getOutputReg());
                if (sample.doesMatchAfter(cpu))
                {
                    sample.possibleCodes.add(optCode);
                }
            }
        }

        System.out.println("\nResults: ");
        System.out.println("\tCount: " + (samples.stream().filter(s -> s.possibleCodes.size() >= 3).count()));
    }

    static List<String> readFile(File file)
    {
        List<String> lines = FileHelpers.getLines(file);
        System.out.println("\tLines: " + lines.size());
        return lines;
    }

    static List<CpuSample> getSamples(List<String> lines)
    {
        final List<CpuSample> samples = new ArrayList();

        final Iterator<String> it = lines.iterator();
        CpuSample sample = null;
        while (it.hasNext())
        {
            String line = it.next();

            //Start of sample
            if (line.startsWith("Before:"))
            {
                if (sample == null)
                {
                    sample = new CpuSample();
                }

                //Get data
                line = line.substring(line.indexOf("[") + 1, line.indexOf("]")).trim();

                //Parse
                parseData(sample.before, line, ',');
                it.remove();
            }
            //End of sample
            else if (line.startsWith("After:"))
            {
                //Get data
                line = line.substring(line.indexOf("[") + 1, line.indexOf("]")).trim();

                //Parse
                parseData(sample.after, line, ',');

                //Add to list, clear field
                samples.add(sample);
                sample = null;
                it.remove();
            }
            //Middle of sample
            else if (sample != null)
            {
                //Parse
                parseData(sample.data, line.trim(), ' ');
                it.remove();
            }
            //Empty line check
            else if (line.isEmpty())
            {
                it.remove();
            }
            //End of CPU samples
            else
            {
                break;
            }
        }

        return samples;
    }

    static List<CpuLine> getCpuLine(List<String> lines)
    {
        List<CpuLine> cpuLines = new ArrayList();
        final Iterator<String> it = lines.iterator();
        while (it.hasNext())
        {
            String line = it.next();
            if (!line.isEmpty())
            {
                CpuLine cpuLine = new CpuLine();
                parseData(cpuLine.data, line.trim(), ' ');
                cpuLines.add(cpuLine);
            }
        }

        return cpuLines;
    }

    static void parseData(int[] assignTo, String line, char splitChar)
    {
        final String[] split = line.split("" + splitChar);

        //Error check
        if (split.length != 4)
        {
            throw new RuntimeException("Expected 4 numbers for CPU data. " + line);
        }

        for (int i = 0; i < split.length; i++)
        {
            assignTo[i] = Integer.parseInt(split[i].trim());
        }
    }
}
