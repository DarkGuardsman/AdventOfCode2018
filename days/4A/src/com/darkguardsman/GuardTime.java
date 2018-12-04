package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/18.
 */
public class GuardTime {
    public final int id;

    public final List<TimeEntry> timeEntryList = new ArrayList();
    public final List<TimeDuration> sleepTimes = new ArrayList();

    public int timeAsleep = 0;
    public int mostOftenMinAsleep = 0;

    public GuardTime(int id) {
        this.id = id;
    }

    public void addTimeEntry(TimeEntry timeEntry) {
        timeEntryList.add(timeEntry);
        timeEntry.guardID = id;
    }

    public void calculate() {

        //Earliest time value of sleep start
        int firstStartHour = 0;
        int firstStartMin = 0;

        //Latest time value of wake (or sleep end)
        int lastEndHour = 0;
        int lastEndMin = 0;

        //Debug guad Id
        System.out.println("\nGuard #" + id);

        //Debug print time entries
        System.out.println("\tTime:");
        for (TimeEntry entry : timeEntryList) {
            System.out.println("\t\t" + entry);
        }

        //Map sleep and wake cycle to durations, add up total sleep hours
        System.out.println("\tSleep Durations:");
        TimeEntry sleepStart = null;
        for (TimeEntry timeEntry : timeEntryList) {
            if (timeEntry.type == TimeType.SLEEP) {

                //Error if we already started a sleep cycle but tried to start another
                if (sleepStart != null) {
                    System.out.println("Error sleep was already started, likely means poorly sorted data");
                    System.exit(-1);
                }

                //Track start time
                sleepStart = timeEntry;

                //Store earliest start time
                if(timeEntry.hour < firstStartHour)
                {
                    firstStartHour = timeEntry.hour;
                    firstStartMin = timeEntry.min;
                }
                else if(timeEntry.hour == firstStartHour && timeEntry.min < firstStartMin)
                {
                    firstStartMin = timeEntry.min;
                }

                System.out.println("\t\tSleep start " + sleepStart.hour + ":" + sleepStart.min);
            } else if (timeEntry.type == TimeType.WAKE) {

                //Make sure we have started a sleep cycle
                if (sleepStart != null) {
                    System.out.println("\t\t-Sleep end " + timeEntry.hour + ":" + timeEntry.min);

                    //Build duration object
                    TimeDuration duration = new TimeDuration(sleepStart.hour, sleepStart.min, timeEntry.hour, timeEntry.min - 1);

                    //Track duration
                    sleepTimes.add(duration);

                    //Get duration in minutes
                    int time = duration.getDifferenceMins();

                    //If -1 it means its invalid
                    if (time == -1) {
                        System.out.println("Error end time is less then start time, likely means poorly sorted data");
                        System.exit(-1);
                    } else {
                        sleepStart = null;
                    }

                    //Store earliest start time
                    if(timeEntry.hour > lastEndHour)
                    {
                        lastEndHour = timeEntry.hour;
                        lastEndMin = timeEntry.min;
                    }
                    else if(timeEntry.hour == lastEndHour && timeEntry.min > lastEndMin)
                    {
                        lastEndMin = timeEntry.min;
                    }

                    timeAsleep += time;

                    System.out.println("\t\t-TimeAsleep: +" + time + " >> " + timeAsleep);
                }
                //Error if no sleep cycle was started
                else {
                    System.out.println("Error sleep never started, likely means poorly sorted data");
                    System.exit(-1);
                }

            }
            //Error if we have an active sleep cycle but moved on without closing
            else if (sleepStart != null) {
                System.out.println("Error sleep started but didn't end, likely means poorly sorted data");
                System.exit(-1);
            }
        }

        System.out.println("\tFinding most often min asleep:");
        int highestMinCount = 0;
        int highestMin = -1;
        for(int min = firstStartMin; min < lastEndMin; min++)
        {
            System.out.println("\t\tmin: " + min);
            int count = 0;
            for(TimeDuration duration : sleepTimes)
            {
                if(duration.isInside(firstStartHour, min))
                {
                    System.out.println("\t\t\t" + duration);
                    count++;
                }
            }
            if(count > highestMinCount)
            {
                highestMinCount = count;
                highestMin = min;
            }
        }

        mostOftenMinAsleep = highestMin;
        System.out.println("\tMost Overlap Min: " + mostOftenMinAsleep);

        //Output total sleep
        System.out.println("\tTotal: " + timeAsleep);
    }

    @Override
    public String toString() {
        return "Guard[" + id + "]";
    }
}
