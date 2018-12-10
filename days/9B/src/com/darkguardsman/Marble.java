package com.darkguardsman;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/10/18.
 */
public class Marble {
    public final long number;

    private Marble next;
    private Marble prev;

    public Marble(long number) {
        this.number = number;
        next = this;
        prev = this;
    }

    public Marble next()
    {
        return next;
    }

    public Marble prev()
    {
        return prev;
    }

    public void insertAfter(Marble marble)
    {
        final Marble temp = next;

        //This
        next = marble;
        marble.prev = this;

        //What use to be the next
        marble.next = temp;
        temp.prev = marble;
    }

    public void remove() {
        next.prev = prev;
        prev.next = next;
    }
}
