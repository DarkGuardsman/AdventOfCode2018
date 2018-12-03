package com.darkguardsman;

import java.util.List;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 12/3/18.
 */
@FunctionalInterface
public interface ClaimCellFilter {

    /**
     * Should this claim we included in the filtered
     * results
     *
     * @param claim     - claim
     * @param claimList - all claims in cell, including claim param
     * @param x         - cell x
     * @param y         - cell y
     * @return true if it should be included
     */
    boolean filter(Claim claim, List<Claim> claimList, int x, int y);
}
