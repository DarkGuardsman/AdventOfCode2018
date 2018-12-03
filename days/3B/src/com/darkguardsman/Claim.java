package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/3/18.
 */
public class Claim {
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;

    public boolean overlaps = false;

    public Claim(String input) {
        //Get split points
        int atIndex = input.indexOf("@");
        int dotIndex = input.indexOf(":");

        //Split out id and parse
        String id = input.substring(1, atIndex).trim();
        this.id = Integer.parseInt(id);

        //Split out xy and parse
        String xy = input.substring(atIndex + 1, dotIndex).trim();
        String[] split = xy.split(",");
        x = Integer.parseInt(split[0]);
        y = Integer.parseInt(split[1]);

        //Split out wh and parse
        String wh = input.substring(dotIndex + 1).trim();
        split = wh.split("x");
        width = Integer.parseInt(split[0]);
        height = Integer.parseInt(split[1]);
    }

    @Override
    public String toString() {
        return "Claim[#" + id + " @ " + x + "," + y + ": " + width + "x" + height + "]";
    }
}
