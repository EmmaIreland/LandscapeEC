package landscapeEC.observers.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;



import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.sat.EmptyWorldException;
import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.SatEvaluator;
import landscapeEC.sat.SatInstance;


public class MapVisualizer extends JFrame implements Observer {    
    private final double xScale;
    private final double yScale;
    private final double intensityScale;

    private final int worldWidth, worldHeight;
    private final double width, height;
    
    private BufferedImage canvas;

    public MapVisualizer() {
    	Integer[] worldDimensions = IntArrayParameter.WORLD_DIMENSIONS.getValue();
    	
        if(worldDimensions.length != 2) throw new IllegalArgumentException("World must be 2 dimensional for map visualization.");
        
        worldWidth = worldDimensions[0];
        worldHeight = worldDimensions[1];
    	
        xScale = IntParameter.VISUALIZER_X_SCALE.getValue();
        yScale = IntParameter.VISUALIZER_X_SCALE.getValue();
        intensityScale = IntParameter.VISUALIZER_INTENSITY_SCALE.getValue();
        
        width = worldWidth*xScale;
        height = worldHeight*yScale;
        
        setupFrame();
    }
    
    private void setupFrame() {
        setSize((int)width+40, (int)height+60);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    public void generationData(int generationNumber, World world, SatInstance satInstance, int successes) {        
        Graphics g = canvas.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        IndividualComparator comparator = new IndividualComparator(satInstance);
        Individual globalBest = findBestIndividual(world, comparator);
        double globalBestFitness = SatEvaluator.evaluate(satInstance, globalBest);
        
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
                    
                    Color foreground = new Color((int) (scaledFitness*255), (int) ((1-difficultyScale)*255), 0);
                    String clauseString = SatEvaluator.getSolvedClausesBitstring(satInstance, bestIndividual);
                    Integer clausesNumber = clauseString.hashCode();
                    foreground = Color.getHSBColor((Math.abs(clausesNumber)%255)/255.0f, (float)Math.pow(onesPercent(clauseString), 30), (float)Math.pow(onesPercent(clauseString), 30));
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                    
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background);
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK);

                /*if(SatEvaluator.evaluate(satInstance, bestIndividual) == globalBestFitness) {
                    GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.WHITE);
                }*/
            }
        }
        
        repaint();
    }
    
    private float onesPercent(String str) {
        float count = 0;
        for(int i=0; i<str.length(); i++) {
            if(str.charAt(i) == '1') count++;
        }
        count /= (float)str.length();
        return count;
    }

    private Individual findBestIndividual(World world, IndividualComparator comparator) {
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for (Vector p : world) {
            if (world.getLocation(p).getNumIndividuals() > 0) {
                bestFromCells.add(Collections.max(world.getIndividualsAt(p), comparator));
            }
        }
        if (bestFromCells.isEmpty()) {
            throw new EmptyWorldException();
        }
        return Collections.max(bestFromCells, comparator);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
