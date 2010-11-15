package landscapeEC.locality.geography;

import landscapeEC.locality.World;
import landscapeEC.sat.SatInstance;

public interface Geography {

    void generateGeography(SatInstance satInstance, World world);

}