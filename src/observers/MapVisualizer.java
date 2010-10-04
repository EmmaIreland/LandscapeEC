package observers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;

import parameters.IntParameter;

import locality.Location;
import locality.Position;
import locality.World;

import sat.GARun;
import sat.Individual;
import sat.IndividualComparator;
import sat.SatEvaluator;
import sat.SatInstance;

public class MapVisualizer extends JFrame implements Observer {    
    private final int xScale;
    private final int yScale;
    private final double intensityScale;

    private final int worldWidth, worldHeight;
    private final int width, height;
    
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
        setSize(width+40, height+60);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
    
    @Override
    public void generationData(World world, SatEvaluator satEvaluator, SatInstance satInstance) {
        IndividualComparator comparator = new IndividualComparator(satInstance, satEvaluator);
        
        for(int y=0; y<worldHeight; y++) {
            for(int x=0; x<worldWidth; x++) {
                Location loc = world.getLocation(new Position(new Integer[] {x, y}));
                if(loc.getNumIndividuals() > 0) {
                    Individual bestIndividual = Collections.max(loc.getIndividuals(), comparator);
                    double bestFitness = satEvaluator.evaluate(satInstance, bestIndividual);
                    double scaledFitness = Math.pow(bestFitness, intensityScale);
                    Color color = new Color(0, (int) (scaledFitness*255), 0);
                    drawRect(canvas.getGraphics(), x*xScale, y*yScale, xScale, yScale, color);
                } else {
                    drawRect(canvas.getGraphics(), x*xScale, y*yScale, xScale, yScale, Color.BLACK);
                }
            }
        }
        
        repaint();
    }
    
    private void drawString(Graphics g, String str, double x, double y, Font font, Color c) {
        System.out.println(str);
        g.setColor(c);
        g.setFont(font);
        g.drawString(str, (int)x, (int)y);
    }

    private void drawRect(Graphics g, double x, double y, double width, double height, Color c) {
        g.setColor(Color.BLACK);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        g.setColor(c);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        g.setColor(Color.GRAY);
        g.drawRect((int)x, (int)y, (int)width, (int)height);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
