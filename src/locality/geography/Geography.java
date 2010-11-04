package locality.geography;

import locality.World;
import sat.SatInstance;

public interface Geography {

    void generateGeography(SatInstance satInstance, World world);

}