package observers.vis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import observers.Observer;
import parameters.IntParameter;

import locality.Position;
import locality.World;

import sat.EmptyWorldException;
import sat.Individual;
import sat.IndividualComparator;
import sat.SatEvaluator;
import sat.SatInstance;

public class DataDisplay extends JFrame implements Observer {
    private int width = 300;
    private int height = 135;

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
    public void generationData(int generationNumber, World world, SatInstance satInstance) {
        IndividualComparator comparator = new IndividualComparator(satInstance);

        width = getWidth();
        height = getHeight();
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        Font font = new Font("Trebuchet MS", Font.BOLD, 12);
        
        Individual bestIndividual = findBestIndividual(world, comparator);
        
        GraphicsUtil.drawString(g, "RUN " + (SatEvaluator.getNumResets()+1), 5, 20, font, Color.RED);
        GraphicsUtil.drawString(g, "Generation " + generationNumber, 5, 35, font, Color.WHITE);
        GraphicsUtil.drawString(g, "NumEvalutaions " + SatEvaluator.getNumEvaluations() + "/" + IntParameter.NUM_EVALS_TO_DO.getValue(), 5, 50, font, Color.WHITE);
        double bestFitness = SatEvaluator.evaluate(satInstance, bestIndividual);
        GraphicsUtil.drawString(g, "Best fitness: " + bestFitness, 5, 65, font, Color.WHITE);
        int numIndividuals = getIndividualCount(world);
        GraphicsUtil.drawString(g, "Number of individuals: " + numIndividuals, 5, 80, font, Color.WHITE);
        int inhabitedCells = getInhabitedCellCount(world);
        GraphicsUtil.drawString(g, "Number of inhabited cells: " + inhabitedCells, 5, 95, font, Color.WHITE);

        repaint();
    }
    
    private int getIndividualCount(World world) {
        int count = 0;
        for(Position p:world) {
            count += world.getLocation(p).getNumIndividuals();
        }
        return count;
    }
    
    private int getInhabitedCellCount(World world) {
        int count = 0;
        for(Position p:world) {
            if(world.getLocation(p).getNumIndividuals() > 0) count++;
        }
        return count;
    }

    private Individual findBestIndividual(World world, IndividualComparator comparator) {
        List<Individual> bestFromCells = new ArrayList<Individual>();
        for (Position p : world) {
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
        g.drawImage(canvas, 5, 25, null);
    }
}
