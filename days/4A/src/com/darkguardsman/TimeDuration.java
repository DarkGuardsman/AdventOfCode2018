package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/18.
 */
public class TimeDuration {
    public final int startHour;
    public final int startMin;

    public final int endHour;
    public final int endMin;

    private int timeDeltaMins = -2;

    public TimeDuration(int startHour, int startMin, int endHour, int endMin) {
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
    }

    /**
     * Gets the difference between start and end in minutes
     *
     * @return difference in mins
     */
    public int getDifferenceMins() {
        //Cache check, -2 means no cache
        if (timeDeltaMins == -2) {
            if (startHour == endHour) {
                //Different in mins inside the same hour
                //Ex: 4:10 -> 4:30 >> 30 - 10 = 20
                timeDeltaMins = endMin - startMin;
            } else if (startHour < endHour) {
                //Difference in mins between each hour
                //Ex: 3:20 -> 4:10 >> (60 - 20 = 40) + 10 = 50 mins
                timeDeltaMins = (60 - startMin) + endMin;
                // add additional hours over 1 difference
                // Ex: 1 hour dif >> (5 - 4 = 1) - 1 = 0 >> +0 mins
                // Ex: 1 hour dif >> (5 - 3 = 2) - 1 = 1 >> +60 mins
                timeDeltaMins += 60 * (endHour - startHour - 1);
            }
            //invalid, -1 means time is backwards TODO setup so it can give negative time
            else {
                timeDeltaMins = -1;
            }
        }
        return timeDeltaMins;
    }

    @Override
    public String toString() {
        return "TimeDuration[" + startHour + ":" + startMin + " to " + endHour + ":" + endMin + " for " + getDifferenceMins() + "]";
    }
}
