package landscapeEC.sat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import landscapeEC.locality.World;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.Parameters;

public class SnapShot implements Serializable{
    private static final long serialVersionUID = -2078745941945517413L;
    private Parameters params;
    private World world;
    private SatInstance satInstance;

    public SnapShot(World world) {
        params = GlobalParameters.getParameters();
        this.world = world;
        this.satInstance = GlobalSatInstance.getInstance();
    }
    
    public static void saveSnapShot(String fileName, World world) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(fileName + ".sav");
        ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);
        outputStream.writeObject(new SnapShot(world));
    }
    
    public static SnapShot loadSnapShot(String file) throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(file);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);
        SnapShot result = (SnapShot) objectStream.readObject();
        return result;
    }
    
    public Parameters getParams() {
        return params;
    }

    public World getWorld() {
        return world;
    }

    public SatInstance getSatInstance() {
        return satInstance;
    }
}
