package landscapeEC.observers.vis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.IntParameter;
import landscapeEC.sat.EmptyWorldException;
import landscapeEC.sat.Individual;
import landscapeEC.sat.IndividualComparator;
import landscapeEC.sat.SatEvaluator;
import landscapeEC.sat.SatInstance;


public class DataDisplay extends JFrame implements Observer {
    private int width = 300;
    private int height = 155;

    private BufferedImage canvas;

    public DataDisplay() {
        setupFrame();
    }

    private void setupFrame() {
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(this.getLocation().x + width, this.getLocation().y);
    }

    @Override
    public void generationData(int generationNumber, World world, SatInstance satInstance, int successes) {
        IndividualComparator comparator = new IndividualComparator(satInstance);

        width = getWidth();
        height = getHeight();
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        Font font = new Font("Trebuchet MS", Font.BOLD, 12);
        
        Individual bestIndividual = world.findBestIndividual(comparator);
        
        GraphicsUtil.drawString(g, "RUN " + (SatEvaluator.getNumResets()+1), 5, 20, font, Color.RED);
        GraphicsUtil.drawString(g, successes + "/" + SatEvaluator.getNumResets() + " runs successful", 5, 35, font, Color.WHITE);
        GraphicsUtil.drawString(g, "Generation " + generationNumber, 5, 50, font, Color.WHITE);
        GraphicsUtil.drawString(g, "NumEvaluations " + SatEvaluator.getNumEvaluations() + "/" + IntParameter.NUM_EVALS_TO_DO.getValue(), 5, 65, font, Color.WHITE);
        double bestFitness = SatEvaluator.evaluate(satInstance, bestIndividual);
        GraphicsUtil.drawString(g, "Best fitness: " + bestFitness, 5, 80, font, Color.WHITE);
        int numIndividuals = getIndividualCount(world);
        GraphicsUtil.drawString(g, "Number of individuals: " + numIndividuals, 5, 95, font, Color.WHITE);
        int inhabitedCells = getInhabitedCellCount(world);
        GraphicsUtil.drawString(g, "Number of inhabited cells: " + inhabitedCells, 5, 110, font, Color.WHITE);

        repaint();
    }
    
    private int getIndividualCount(World world) {
        int count = 0;
        for(Vector p:world) {
            count += world.getLocation(p).getNumIndividuals();
        }
        return count;
    }
    
    private int getInhabitedCellCount(World world) {
        int count = 0;
        for(Vector p:world) {
            if(world.getLocation(p).getNumIndividuals() > 0) count++;
        }
        return count;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 5, 25, null);
    }
}
