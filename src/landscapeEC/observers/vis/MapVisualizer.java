package landscapeEC.observers.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Arc2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.JFrame;



import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.sat.EmptyWorldException;
import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.SatEvaluator;
import landscapeEC.sat.SatInstance;
import landscapeEC.sat.SnapShot;


public class MapVisualizer extends JFrame implements Observer {    
    private final int xScale;
    private final int yScale;
    private final int intensityScale;

    private final int worldWidth, worldHeight;
    private final int width, height;
    
    private BufferedImage canvas;

    private VisualizerType visType;
    
    public MapVisualizer() {
    	Integer[] worldDimensions = IntArrayParameter.WORLD_DIMENSIONS.getValue();
    	
        if(worldDimensions.length != 2) throw new IllegalArgumentException("World must be 2 dimensional for map visualization.");
        
        worldWidth = worldDimensions[0];
        worldHeight = worldDimensions[1];
    	
        xScale = IntParameter.VISUALIZER_X_SCALE.getValue();
        yScale = IntParameter.VISUALIZER_Y_SCALE.getValue();
        intensityScale = IntParameter.VISUALIZER_INTENSITY_SCALE.getValue();
        
        width = (worldWidth*xScale);
        height = (worldHeight*yScale);
        
        visType = VisualizerType.valueOf(StringParameter.VISUALIZER_TYPE.getValue());
        
        setupFrame();
    }
    
    private void setupFrame() {
        setSize(width+40, height+60);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    public void generationData(int generationNumber, World world, SatInstance satInstance, int successes) {
        canvas = drawMapImage(world, satInstance, xScale, yScale, intensityScale, visType);
        
        repaint();
    }

    public static BufferedImage drawMapImage(World world, SatInstance satInstance, int xScale, int yScale, int intensityScale, VisualizerType visType) {
        Vector worldDimensions = world.getDimensions();
        
        int worldWidth = worldDimensions.get(0);
        int worldHeight = worldDimensions.get(1);
        
        int width = worldWidth*xScale;
        int height = worldHeight*yScale;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
                
        for(int y=0; y<worldHeight; y++) {
            for(int x=0; x<worldWidth; x++) {
                Location loc = world.getLocation(new Vector(new Integer[] {x, y}));
                
                double difficultyScale = loc.getComparator().getInstance().getNumClauses()/(double)satInstance.getNumClauses();
                int intensity = (int) ((1-difficultyScale)*255);
                Color background = new Color(intensity, intensity, intensity);
                GraphicsUtil.fillRect(g, x*xScale, y*yScale, xScale, yScale, background);
                
                Individual bestIndividual = null;
                if(loc.getNumIndividuals() > 0) {
                    bestIndividual = Collections.max(loc.getIndividuals(), loc.getComparator());
                    double bestFitness = SatEvaluator.evaluate(loc.getComparator().getInstance(), bestIndividual);
                    double scaledFitness = Math.pow(bestFitness, intensityScale);
                    double popScale = Math.min(1.0, loc.getNumIndividuals()/(double)IntParameter.CARRYING_CAPACITY.getValue());
                    
                    Color foreground = Color.black;
                    
                    switch (visType) {
                        case FITNESS_ONLY:
                            foreground = new Color((int) (scaledFitness*255), (int) ((1-difficultyScale)*255), 0);
                        break;
                        case COLORED_CLAUSES:
                            String clauseString = SatEvaluator.getSolvedClausesBitstring(satInstance, bestIndividual);
                            Integer clausesNumber = clauseString.hashCode();
                            foreground = Color.getHSBColor((Math.abs(clausesNumber)%255)/255.0f, (float)Math.pow(onesPercent(clauseString), 30), (float)Math.pow(onesPercent(clauseString), 30));
                        break;
                    }
                    
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                    
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background);
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK);

                /*if(SatEvaluator.evaluate(satInstance, bestIndividual) == globalBestFitness) {
                    GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.WHITE);
                }*/
            }
        }
        return image;
    }
    
    private static float onesPercent(String str) {
        float count = 0;
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '1') count++;
        }
        count /= str.length();
        return count;
    }
    
    public static void saveImageToFile(BufferedImage image, String filename) throws IOException {
        File file = new File(filename + ".png");
        ImageIO.write(image, "png", file);
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length < 6) {
            System.out.println("Usage: outputImage snapshot.sav xScale yScale intensityScale visualizerType");
            return;
        }
        
        String outputFile = args[0];
        SnapShot snapShot = SnapShot.loadSnapShot(args[1]);
        int xScale = Integer.parseInt(args[2]);
        int yScale = Integer.parseInt(args[3]);
        int intensityScale = Integer.parseInt(args[4]);
        VisualizerType visType = VisualizerType.valueOf(args[5]);

        GlobalParameters.setParameters(snapShot.getParams());
        
        saveImageToFile(drawMapImage(snapShot.getWorld(), snapShot.getSatInstance(), xScale, yScale, intensityScale, visType), outputFile);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
