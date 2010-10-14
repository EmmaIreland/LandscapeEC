package observers.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Collections;

import javax.swing.JFrame;

import observers.Observer;

import parameters.IntParameter;

import locality.Location;
import locality.Position;
import locality.World;

import sat.Individual;
import sat.SatEvaluator;
import sat.SatInstance;

public class MapVisualizer extends JFrame implements Observer {    
    private final double xScale;
    private final double yScale;
    private final double intensityScale;

    private final int worldWidth, worldHeight;
    private final double width, height;
    
    private BufferedImage canvas;

    public MapVisualizer(World world) {        
        Integer[] worldDimensions = world.getDimensions();
        
        if(worldDimensions.length != 2) throw new IllegalArgumentException("World must be 2 dimensional for map visualization.");
        
        xScale = IntParameter.VISUALIZER_X_SCALE.getValue();
        yScale = IntParameter.VISUALIZER_X_SCALE.getValue();
        intensityScale = IntParameter.VISUALIZER_INTENSITY_SCALE.getValue();
        
        worldWidth = worldDimensions[0];
        worldHeight = worldDimensions[1];
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
    public void generationData(int generationNumber, World world, SatInstance satInstance) {
        
        Graphics g = canvas.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        for(int y=0; y<worldHeight; y++) {
            for(int x=0; x<worldWidth; x++) {
                Location loc = world.getLocation(new Position(new Integer[] {x, y}));
                
                double difficultyScale = loc.getComparator().getInstance().getNumClauses()/(double)satInstance.getNumClauses();
                Color background = new Color(0, (int) (difficultyScale*255), 0);
                GraphicsUtil.fillRect(g, x*xScale, y*yScale, xScale, yScale, background);
                
                if(loc.getNumIndividuals() > 0) {
                    Individual bestIndividual = Collections.max(loc.getIndividuals(), loc.getComparator());
                    double bestFitness = SatEvaluator.evaluate(loc.getComparator().getInstance(), bestIndividual);
                    double scaledFitness = Math.pow(bestFitness, intensityScale);
                    double popScale = Math.min(1.0, loc.getNumIndividuals()/(double)IntParameter.CARRYING_CAPACITY.getValue());
                    
                    Color foreground = new Color((int) (scaledFitness*255), (int) (difficultyScale*255), 0);
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                    
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background);
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK);
            }
        }
        
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
