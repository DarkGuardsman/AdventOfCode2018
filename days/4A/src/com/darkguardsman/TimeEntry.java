package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/4/18.
 */
public class TimeEntry implements Comparable<TimeEntry> {

    public final int year;
    public final int month;
    public final int day;

    public final int hour;
    public final int min;

    public int guardID = -1;

    public final String note;

    public final TimeType type;

    public TimeEntry(final String input) {
        String data = input.trim();

        //Get year
        int index = data.indexOf("-");
        year = Integer.parseInt(data.substring(1, index));
        data = data.substring(index + 1);

        //Get month
        index = data.indexOf("-");
        month = Integer.parseInt(data.substring(0, index));
        data = data.substring(index + 1);

        //get day
        index = data.indexOf(' ');
        day = Integer.parseInt(data.substring(0, index));
        data = data.substring(index).trim();

        //get hour & min
        index = data.indexOf("]");
        String time = data.substring(0, index).trim();
        String[] split = time.split(":");
        hour = Integer.parseInt(split[0]);
        min = Integer.parseInt(split[1]);
        data = data.substring(index + 1);

        //Place remaining in note
        note = data.trim();

        //ID note type
        if (note.contains("begins")) {
            type = TimeType.START;

            //Get guard ID
            if (note.contains("#")) {
                String g = note.substring(note.indexOf('#') + 1);
                g = g.substring(0, g.indexOf(' '));
                guardID = Integer.parseInt(g);
            }
        } else if (note.contains("wakes")) {
            type = TimeType.WAKE;
        } else if (note.contains("asleep")) {
            type = TimeType.SLEEP;
        }
        else
        {
            type = null;
        }
    }

    @Override
    public String toString() {
        return "TimeEntry([" + year + "-" + month + "-" + day + " " + hour + ":" + min + "] " + note + ")";
    }

    @Override
    public int compareTo(TimeEntry entry) {
        if (year == entry.year) {
            if (month == entry.month) {
                if (day == entry.day) {
                    if (hour == entry.hour) {
                        if (min == entry.min) {
                            return Integer.compare(type.ordinal(), entry.type.ordinal());
                        }
                        return Integer.compare(min, entry.min);
                    }
                    return Integer.compare(hour, entry.hour);
                }
                return Integer.compare(day, entry.day);
            }
            return Integer.compare(month, entry.month);
        }
        return Integer.compare(year, entry.year);
    }
}
