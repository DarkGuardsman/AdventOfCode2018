package com.darkguardsman.helpers.path;

import com.darkguardsman.helpers.Direction2D;
import com.darkguardsman.helpers.Dot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/18/18.
 */
public class BreadthFirstPath {

    private final Queue<Dot> toSearch = new LinkedList();
    private final Set<Dot> pathed = new HashSet();

    public StopPathFunction stopPathFunction;
    public ShouldPathFunction shouldPathFunction;
    public OnPathFunction onPathFunction;

    private Dot start;

    /**
     * Called to start the pathfinder fresh. Will
     * clear out any old data but leave alone any
     * functions or settings of the pathfinder. Useful
     * if you want to recycle the pathfinder object
     * for the next path.
     *
     * @param start - start point
     * @return true if stopped by a stop function
     */
    public boolean startPath(Dot start) {

        //Clear old data
        reset();

        //Set start
        this.start = start;

        //Set first point to search
        toSearch.add(start);

        //Run path
        return path();
    }

    /**
     * Called to resume the pathfinder from
     * where it stopped last. Called {@link #startPath(Dot)}
     * for a fresh run.
     *
     * @return true if stopped by a stop function
     */
    public boolean path() {
        while (toSearch.peek() != null) {
            final Dot current = toSearch.poll();
            pathed.add(current);

            //Check if we should stop
            if (stopPathFunction != null && stopPathFunction.shouldStop(start, current, pathed.contains(current))) {
                return true;
            }

            //Loop next points
            for (Direction2D direction2D : Direction2D.MAIN) {
                final Dot next = current.add(direction2D);

                //Check if we should path the next point
                if (shouldPath(start, current, next)) {

                    //Trigger path event
                    if (onPathFunction != null) {
                        onPathFunction.apply(current, next);
                    }

                    toSearch.add(next);
                } else {
                    pathed.add(next);
                }
            }
        }
        return false;
    }

    protected boolean shouldPath(Dot start, Dot current, Dot next) {
        if (shouldPathFunction != null) {
            return shouldPathFunction.shouldPath(start, current, next, pathed.contains(next));
        }
        return !pathed.contains(next);
    }

    public void reset() {
        start = null;
        toSearch.clear();
        pathed.clear();
    }
}
