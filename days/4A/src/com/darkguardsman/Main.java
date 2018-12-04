package com.darkguardsman;

import com.darkguardsman.helpers.FileHelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/18.
 */
public class Main {

    public static void main(String... args) {
        //get args
        final File file = new File(args[0]);

        //Arg output
        System.out.println("File: " + file);

        //Read file
        System.out.println("\nReading File: ");
        List<String> lines = FileHelpers.getLines(file);
        if (lines == null) {
            System.exit(-1);
        }

        //Convert strings to objects
        System.out.println("\nConverting: ");
        List<TimeEntry> timeEntries = new ArrayList(lines.size());
        for (String line : lines) {
            TimeEntry entry = new TimeEntry(line);
            timeEntries.add(entry);
            System.out.println("\t" + entry);
        }

        //Sort objects by order
        System.out.println("\nSorting: ");
        Collections.sort(timeEntries);
        for(TimeEntry entry : timeEntries)
        {
            System.out.println("\t" + entry);
        }

        //Map data to guards
        GuardTime guardTime = null;
        HashMap<Integer, GuardTime> guardMap = new HashMap<>();
        for(TimeEntry timeEntry : timeEntries)
        {
            if(timeEntry.type == TimeType.START)
            {
                if(!guardMap.containsKey(timeEntry.guardID)) {
                    guardTime = new GuardTime(timeEntry.guardID);
                    guardMap.put(timeEntry.guardID, guardTime);
                }
                guardTime = guardMap.get(timeEntry.guardID);
            }

            if(guardTime != null)
            {
                guardTime.addTimeEntry(timeEntry);
            }
            else
            {
                System.out.println("Error: no guardTime set, likely means the list is not sorted correctly");
                System.exit(-1);
            }
        }

        //Calculate data
        int longestTime = 0;
        for(GuardTime guard : guardMap.values())
        {
            guard.calculate();
            if(guard.timeAsleep > longestTime)
            {
                guardTime = guard;
                longestTime = guard.timeAsleep;
            }
        }

        System.out.println("Guard asleep longest: " + guardTime + " with a time of " + guardTime.timeAsleep + " most often asleep at min " + guardTime.mostOftenMinAsleep);

        System.out.println("Answer: " + (guardTime.id * guardTime.mostOftenMinAsleep));

    }
}
