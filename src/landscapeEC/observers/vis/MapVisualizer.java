package landscapeEC.observers.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.sound.midi.SysexMessage;
import javax.swing.JFrame;

import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.sat.Individual;
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
        Vector oneByOne = new Vector(new Integer[] {1, 1});
        if(world.getDimensions().equals(oneByOne)) {
            canvas = drawNonCellular(world, satInstance);
        } else {
            canvas = drawCellular(world, satInstance);
        }

        repaint();
    }

    public BufferedImage drawCellular(World world, SatInstance satInstance) {
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
                
                if(loc.getNumIndividuals() > 0) {
                    double popScale = Math.min(1.0, loc.getNumIndividuals()/(double)IntParameter.CARRYING_CAPACITY.getValue());
                    
                    Color foreground = getForegroundColor(satInstance, loc, difficultyScale);
                    
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background); //outline for cells to show a bit of underlying geography
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK); //grid outline for cells
            }
        }
        return image;
    }
    
    private BufferedImage drawNonCellular(World world, SatInstance satInstance) {
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
                
                if(loc.getNumIndividuals() > 0) {
                    double popScale = Math.min(1.0, loc.getNumIndividuals()/(double)IntParameter.CARRYING_CAPACITY.getValue());
                    
                    Color foreground = getForegroundColor(satInstance, loc, difficultyScale);
                    
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background); //outline for cells to show a bit of underlying geography
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK); //grid outline for cells
            }
        }
        return image;
    }

    private Color getForegroundColor(SatInstance satInstance, Location loc, double difficultyScale) {
        Individual bestIndividual = Collections.max(loc.getIndividuals(), loc.getComparator()); //TODO is this thing doing what we want??
        double bestFitness = bestIndividual.getGlobalFitness(satInstance);
        double scaledFitness = Math.pow(bestFitness, intensityScale);
        Color foreground = Color.black;
        
        switch (visType) {
            case FITNESS_ONLY:
                foreground = new Color((int) (scaledFitness*255), (int) ((1-difficultyScale)*255), 0);
            break;
            case COLORED_CLAUSES:
                String clauseString = SatEvaluator.getSolvedClausesBitstring(satInstance, bestIndividual);
                Integer clausesNumber = clauseString.hashCode();
                if (Math.abs(bestFitness - onesPercent(clauseString)) > 1e-5) {
                    int len = clauseString.length();
                    System.out.println("Individual: " + bestIndividual);
                    System.out.println("Clauses: " + clauseString);
                    System.out.println("They were different! " + (bestFitness * len) + " vs. " + (onesPercent(clauseString)*len));
                    System.out.println(satInstance.getClauseList());
                    //System.exit(1);
                    throw new RuntimeException("Bad stuff happened!");
                }
                foreground = Color.getHSBColor((Math.abs(clausesNumber)%255)/(float)255.0, (float) Math.pow(bestFitness, 30), (float)  Math.pow(bestFitness, 30));
            break;
        }
        return foreground;
    }
    
    public static double onesPercent(String str) {
        double count = 0;
        for (int i=0; i < str.length(); ++i) {
            if (str.charAt(i) == '1') {
                ++count;
            }
        }
        double result = count / (double) str.length();
        return result;
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
        MapVisualizer vis = new MapVisualizer();
        saveImageToFile(vis.drawCellular(snapShot.getWorld(), snapShot.getSatInstance()), outputFile);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
