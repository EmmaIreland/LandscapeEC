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
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.sat.DiversityCalculator;

public class DataDisplay extends JFrame implements Observer {
    private static final long serialVersionUID = 4151957839382676250L;
    private int width = 300;
    private int height = 190;

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
    public void generationData(int generationNumber, World world, int successes) {

        width = getWidth();
        height = getHeight();
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = canvas.getGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        Font font = new Font("Trebuchet MS", Font.BOLD, 12);
        
        Individual bestIndividual = world.findBestIndividual();
        
        Evaluator evaluator = GlobalProblem.getEvaluator();
        
        GraphicsUtil.drawString(g, "RUN " + (evaluator.getNumResets()+1), 5, 20, font, Color.RED);
        GraphicsUtil.drawString(g, successes + "/" + evaluator.getNumResets() + " runs successful", 5, 35, font, Color.WHITE);
        GraphicsUtil.drawString(g, "Generation " + generationNumber, 5, 50, font, Color.WHITE);
        GraphicsUtil.drawString(g, "NumEvaluations " + evaluator.getNumEvaluations() + "/" + IntParameter.NUM_EVALS_TO_DO.getValue(), 5, 65, font, Color.WHITE);
        double bestFitness = bestIndividual.getGlobalFitness();
        GraphicsUtil.drawString(g, "Best fitness: " + bestFitness, 5, 80, font, Color.WHITE);
        int numIndividuals = getIndividualCount(world);
        GraphicsUtil.drawString(g, "Number of individuals: " + numIndividuals, 5, 95, font, Color.WHITE);
        int inhabitedCells = getInhabitedCellCount(world);
        GraphicsUtil.drawString(g, "Number of inhabited cells: " + inhabitedCells, 5, 110, font, Color.WHITE);
        double diversity = DiversityCalculator.calculateBitStringDiversity();
        GraphicsUtil.drawString(g,  String.format("Current bitstring diversity: %.2f", diversity), 5, 125, font, Color.WHITE);
        double semanticDiversity = DiversityCalculator.calculateResultStringDiversity();
        GraphicsUtil.drawString(g,  String.format("Current solved clauses diversity: %.2f", semanticDiversity), 5, 140, font, Color.WHITE);
        
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
