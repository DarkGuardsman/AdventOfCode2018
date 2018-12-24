package com.darkguardsman;


import com.darkguardsman.data.CPU;
import com.darkguardsman.data.CpuLine;
import com.darkguardsman.data.CpuSample;
import com.darkguardsman.data.OptCode;
import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
        List<CpuSample> allSamples = getSamples(lines);
        allSamples.forEach(s -> System.out.println(s));

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
        for (CpuSample sample : allSamples)
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

        System.out.println("\nPossible Codes: ");

        allSamples.sort(Comparator.comparingInt(s -> s.possibleCodes.size()));
        allSamples.forEach(s -> System.out.println(s.toString() + "   [" + s.possibleCodes.stream().map(o -> o.name).collect(Collectors.joining(",")) + "]"));

        System.out.println("\nEliminating Single entries: ");
        List<CpuSample> singleEntries = allSamples.stream().filter(s -> s.possibleCodes.size() == 1).collect(Collectors.toList());
        int run = 0;
        do
        {
            System.out.println("\tPass: " + (run++));
            //Get all codes with only 1 ID left
            int prev = allSamples.size();
            allSamples.removeAll(singleEntries);
            System.out.println("\t\tFound " + (prev - allSamples.size()) + " entries containing only 1 code");

            //Loop through until we run out of entries
            while (singleEntries.size() > 0)
            {
                //Get code and assign ID
                final CpuSample sample =  singleEntries.get(0);
                final OptCode code =sample.possibleCodes.get(0);
                code.id = sample.getOptCode();
                System.out.println("\t\tMatch: " + code);

                //Remove all found in single entries that match code
                prev = singleEntries.size();
                singleEntries.removeIf(s -> s.possibleCodes.contains(code));
                System.out.println("\t\tFound " + (prev - singleEntries.size()) + " single entries using code");

                //Remove code from other entries
                int removed = (int)allSamples.stream().filter(s -> s.possibleCodes.remove(code)).count();
                System.out.println("\t\tFound " + removed + " multi entries using code");
            }

            //Get entries for next run
            singleEntries = allSamples.stream().filter(s -> s.possibleCodes.size() == 1).collect(Collectors.toList());
        }
        while (singleEntries.size() > 0);

        //Map codes to ID array
        System.out.println("\nCodes: ");
        ArrayList<OptCode> codes = new ArrayList();
        codes.addAll(cpu.optCodeMap.values());
        codes.sort(Comparator.comparingInt(c -> c.id));
        codes.forEach(c -> System.out.println("\t" + c));
        cpu.optCodes = codes.toArray(new OptCode[codes.size()]);


        System.out.println("\nRunning program: ");
        cpu.setRegisters(new int[] {0, 0, 0, 0});
        for(CpuLine line : cpuLines)
        {
            System.out.println("\tBefore: " + Arrays.toString(cpu.registers));
            System.out.println("\t" + line);

            cpu.run(line);

            System.out.println("\tAfter: " + Arrays.toString(cpu.registers) + "\n");
        }


        System.out.println("\nResult: ");
        System.out.println("\t" + Arrays.toString(cpu.registers));
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
