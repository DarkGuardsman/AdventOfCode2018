package com.darkguardsman;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/3/18.
 */
public class ClaimArea {
    public final int width;
    public final int height;

    private ArrayList<Claim>[] cells;

    public ClaimArea(int width, int height) {
        this.width = width;
        this.height = height;

        cells = new ArrayList[width * height];
    }

    public void map(Claim claim) {
        for (int x = claim.x; x < (claim.x + claim.width); x++) {
            for (int y = claim.y; y < (claim.y + claim.height); y++) {
                insert(x, y, claim);
            }
        }
    }

    public void forEachCell(Consumer<ArrayList<Claim>> function) {
        for (int i = 0; i < cells.length; i++) {
            function.accept(cells[i]);
        }
    }

    public int countCells(Function<ArrayList<Claim>, Boolean> function) {
        int count = 0;
        for (int i = 0; i < cells.length; i++) {
            if (function.apply(cells[i])) {
                count++;
            }
        }
        return count;
    }

    public List<Claim> filterClaims(ClaimCellFilter function) {
        List<Claim> claims = new ArrayList();
        for (int i = 0; i < cells.length; i++) {

            ArrayList<Claim> claimsInCell = cells[i];
            if (claimsInCell != null && !claimsInCell.isEmpty()) {

                for (Claim claim : claimsInCell) {
                    if (!claims.contains(claim) && function.filter(claim, claims, i % height, i / height)) {
                        claims.add(claim);
                    }
                }
            }
        }

        return claims;
    }

    private void insert(int x, int y, Claim claim) {
        if (isInside(x, y)) {
            final int index = getIndex(x, y);
            if (cells[index] == null) {
                cells[index] = new ArrayList();
            }
            cells[index].add(claim);
            if(cells[index].size() > 1)
            {
                for(Claim c : cells[index])
                {
                    c.overlaps = true;
                }
            }

        } else {
            System.out.println("Pos(" + x + "," + y + ") of " + claim + " is outside of the area");
        }
    }

    private int getIndex(int x, int y) {
        return y * height + x;
    }

    private boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
