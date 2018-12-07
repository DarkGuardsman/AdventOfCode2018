package com.darkguardsman.tests;

import com.darkguardsman.GridDataMap;
import com.darkguardsman.GridDataPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/7/18.
 */
public class TestGrid {

    @Test
    public void testInit() {
        GridDataMap map = new GridDataMap(0, 0, 100, 100);
        assertEquals(0, map.x);
        assertEquals(0, map.y);
        assertEquals(100, map.sizeX);
        assertEquals(100, map.sizeY);
    }

    @Test
    public void testInit2() {
        GridDataMap map = GridDataMap.newMinMaxMap(-100, -100, 100, 100, 0);
        assertEquals(-100, map.x);
        assertEquals(-100, map.y);
        assertEquals(200, map.sizeX);
        assertEquals(200, map.sizeY);
    }

    @Test
    public void testInit3() {
        GridDataMap map = GridDataMap.newMinMaxMap(-100, -100, 100, 100, 100);
        assertEquals(-200, map.x);
        assertEquals(-200, map.y);
        assertEquals(400, map.sizeX);
        assertEquals(400, map.sizeY);
    }

    //Test for problems with getting index were it could change the result of getIndex
    @Test
    public void testIndexSingleRow() {
        //Values should never wrap back on themselves and always be +1
        GridDataMap map = GridDataMap.newMinMaxMap(-100, -100, 100, 100, 100);
        int last = 0;

        //Loop single x row
        for (int x = 0; x < map.sizeX; x++) {
            //get index
            final int index = map.getIndexInternal(x, 0);

            //Do not run test for first pass
            if (x != 0) {
                //Index should be 1 larger than last
                assertTrue(last == (index - 1));
            }

            //Index should equal x for this pass
            assertTrue(index == x);

            //Track last index
            last = index;
        }
    }

    @Test
    public void testIndexSingelRow2() {
        //Values should never wrap back on themselves and always be +1
        GridDataMap map = GridDataMap.newMinMaxMap(-100, -100, 100, 100, 100);
        int last = 0;

        //Loop single x row using start x
        for (int x = map.x; x < (map.x + map.sizeX); x++) {
            //get index normal
            final int index = map.getIndex(x, map.y);

            //Do not run test for first pass
            if (x != map.x) {
                //Index should be 1 larger than last
                assertEquals(last + 1, index, "Index for " + x);
            }

            //Index should equal x for this pass
            assertEquals((x - map.x), index, "Index for " + x);

            //Track last index
            last = index;
        }
    }

    @Test
    public void testIndexFullGrid() {
        //Values should never wrap back on themselves and always be +1
        GridDataMap map = GridDataMap.newMinMaxMap(-99, -99, 99, 99, 100);
        int last = 0;

        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            int yIndex = (y - map.y);
            int yIndexOffset = yIndex * map.sizeY;

            //Loop single x row using start x
            for (int x = map.x; x < (map.x + map.sizeX); x++) {
                //get index normal
                final int index = map.getIndex(x, y);

                //Do not run test for first pass
                if (x != map.x) {
                    //Index should be 1 larger than last
                    assertEquals(last + 1, index, "Index for " + x + "," + y);
                }

                //Index should equal x for this pass
                assertEquals((x - map.x), index - yIndexOffset, "Index for " + x + "," + y);
                assertEquals(yIndexOffset, index - (x - map.x), "Index for " + x + "," + y);

                //Track last index
                last = index;
            }
        }
    }

    @Test
    public void testInsert() {
        GridDataMap map = GridDataMap.newMinMaxMap(-99, -99, 99, 99, 100);

        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            for (int x = map.x; x < (map.x + map.sizeX); x++) {
                map.insert(x, y, new GridDataPoint(x, y));
            }
        }

        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            for (int x = map.x; x < (map.x + map.sizeX); x++) {
                GridDataPoint point = map.getData(x, y);
                assertNotNull(point);
                assertEquals(x, point.owner);
                assertEquals(y, point.distance);
            }
        }
    }

    @Test
    public void testInsideMap() {
        GridDataMap map = GridDataMap.newMinMaxMap(-99, -99, 99, 99, 100);
        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            for (int x = map.x; x < (map.x + map.sizeX); x++) {
                assertTrue(map.insideMap(x, y));
            }
        }
    }

    @Test
    public void testForEachPoint() {
        GridDataMap map = GridDataMap.newMinMaxMap(-99, -99, 99, 99, 100);

        map.forEachPosition((x, y) -> {
            map.insert(x, y, new GridDataPoint(x, y));
            return true;
        });

        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            for (int x = map.x; x < (map.x + map.sizeX); x++) {
                GridDataPoint point = map.getData(x, y);
                assertNotNull(point);
                assertEquals(x, point.owner);
                assertEquals(y, point.distance);
            }
        }
    }

    @Test
    public void testForEdge() {
        GridDataMap map = GridDataMap.newMinMaxMap(-99, -99, 99, 99, 100);

        for (int x = map.x; x < (map.x + map.sizeX); x++) {
            assertTrue(map.isOnEdge(x, map.y), "" + x);
            assertTrue(map.isOnEdge(x, map.y + map.sizeY - 1), "" + x);
        }

        for (int y = map.y; y < (map.y + map.sizeY); y++) {
            assertTrue(map.insideMap(map.x, y), "" + y);
            assertTrue(map.isOnEdge(map.x, y), "" + y);

            assertTrue(map.insideMap(map.x + map.sizeX - 1, y), "" + y);
            assertTrue(map.isOnEdge(map.x + map.sizeX - 1, y), "" + y);
        }
    }
}
