package landscapeEC.observers.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import landscapeEC.core.GARun;
import landscapeEC.core.SnapShot;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Location;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.Evaluator;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.Problem;
import landscapeEC.problem.ecc.EccEvaluator;
import landscapeEC.problem.ecc.EccProblem;
import landscapeEC.problem.sat.operators.LocalizedMutation;
import landscapeEC.util.VizHelper;

public class MapVisualizer extends JFrame implements Observer {
    private final int xScale;
    private final int yScale;
    private final double intensityScale;

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
        intensityScale = DoubleParameter.VISUALIZER_INTENSITY_SCALE.getValue();
        
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
    public void generationData(GARun run) {
        GridWorld world = (GridWorld) run.getWorld();
        Vector oneByOne = new Vector(new Integer[] {1, 1});
        if(world.getDimensions().equals(oneByOne)) {
            canvas = drawNonCellular(world);
        } else {
            canvas = drawCellular(world);
        }

        repaint();
    }

    public BufferedImage drawCellular(GridWorld world) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
                
        for(int y=0; y<worldHeight; y++) {
            for(int x=0; x<worldWidth; x++) {
                Location loc = world.getLocation(new Vector(new Integer[] {x, y}));
                
                double difficultyScale = loc.getProblem().getDifficulty();
                int intensity = (int) ((1-difficultyScale)*255);
                Color background = new Color(intensity, intensity, intensity);
                GraphicsUtil.fillRect(g, x*xScale, y*yScale, xScale, yScale, background);
                
                if(loc.getNumIndividuals() > 0) {
                    double popScale = Math.min(1.0, loc.getNumIndividuals()/(double)IntParameter.CARRYING_CAPACITY.getValue());
                    
                    Color foreground = getForegroundColor(loc, difficultyScale);
                    
                    GraphicsUtil.fillRect(g, x*xScale+(0.5-popScale*0.5)*xScale, y*yScale+(0.5-popScale*0.5)*yScale, xScale*popScale, yScale*popScale, foreground);
                }
                GraphicsUtil.drawRect(g, x*xScale+1, y*yScale+1, xScale-2, yScale-2, background); //outline for cells to show a bit of underlying geography
                GraphicsUtil.drawRect(g, x*xScale, y*yScale, xScale, yScale, Color.BLACK); //grid outline for cells
            }
        }
        return image;
    }
    
    private BufferedImage drawNonCellular(World world) {
//        System.out.println("About to draw non-cellular world");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
//        DefaultPieDataset pieDataSet = new DefaultPieDataset();
//        System.out.println("Length of clause list = " + DiversityCalculator.clauseLists().size());
//        for (String clauseList : DiversityCalculator.clauseLists()) {
//            double percentage = DiversityCalculator.clauseListPercentage(clauseList);
//            pieDataSet.setValue(clauseList, percentage);
////            System.out.println("Added " + clauseList + " with percentage " + percentage);
//        }
//        PiePlot piePlot = new PiePlot(pieDataSet);
//        Rectangle2D area = new Rectangle(width, height);
//        piePlot.draw(g, area, null, new PlotState(), null);
        
        return image;
    }

    private Color getForegroundColor(Location loc, double difficultyScale) {
        Individual bestIndividual = (Individual) Collections.max(loc.getIndividuals(), IndividualComparator.getComparator());
        double bestFitness = bestIndividual.getGlobalFitness();
        double scaledFitness = Math.pow(bestFitness, intensityScale);
        Color foreground = Color.black;
        
        Evaluator evaluator = GlobalProblem.getEvaluator();
        Problem problem = GlobalProblem.getProblem();
        
        switch (visType) {
            case FITNESS_ONLY:
                foreground = new Color((int) (scaledFitness*255), (int) ((1-difficultyScale)*255), 0);
            break;
            case COLORED_CLAUSES:
                String clauseString = evaluator.getResultString(bestIndividual);
                Integer clausesNumber = clauseString.hashCode();
                foreground = Color.getHSBColor((Math.abs(clausesNumber)%255)/(float)255.0, (float) scaledFitness, (float)  scaledFitness);
            break;
            case MISSING_GENES:
            	List<Individual> individuals = loc.getIndividuals();
            	int[] bits = new int[individuals.get(0).getBits().length];
            	for(int i = 0; i<individuals.size(); i++){
            		int[] individual = individuals.get(i).getBits();
            		for(int j=0; j<bits.length; j++){
            			if(individual[j]==1){
            				bits[j]=1;
            			}
            		}
            	}
            	foreground = Color.getHSBColor((Math.abs(bits.hashCode())%255)/(float)255.0, (float) scaledFitness, (float)  scaledFitness);
            break;
            case BRIGHT_MUTATION:
            	String cs = evaluator.getResultString(bestIndividual);
                Integer cn = cs.hashCode();
                foreground = new Color((int) (scaledFitness*255), 0, (int) ((LocalizedMutation.getAmplifier().getAmp((Vector) loc.getPosition())-1)*48)%255);
                break;
            case ECC_VISUALIZER:
                int numOfBitsPerWord = ((EccProblem) problem).getNumOfBitsPerWord();
                int numOfCodeWords = ((EccProblem) problem).getNumOfCodeWords();
                int[] individualString = bestIndividual.getBits();
                ArrayList<int[]> listOfCodeWords = new ArrayList<int[]>();
                int hamming=0;
                
                for(int i=0; i<numOfCodeWords; i++){
                    listOfCodeWords.add(Arrays.copyOfRange(individualString, numOfBitsPerWord*i, numOfBitsPerWord*(i+1)));
                }
                for(int i=0; i<numOfCodeWords; i++){
                    for(int j=0; j<numOfCodeWords; j++){
                        if(i != j){
                            hamming = ((EccEvaluator)evaluator).hammingDistance(listOfCodeWords.get(i), listOfCodeWords.get(j));
                        }
                    }
                }
                foreground = Color.getHSBColor((hamming*20)/(float)255.0, (float) scaledFitness, (float)  scaledFitness); // Color(0, hamming*20, 0);
            break;
            case AVERAGE_INDIVIDUAL:
                
            	int[] displayString = loc.getAverageIndividual();
            	
            	VizHelper vizHelper = new VizHelper();
                
            	int red = vizHelper.bitsToInt(displayString, 0, displayString.length / 3);
                int green = vizHelper.bitsToInt(displayString, displayString.length / 3, 2 * displayString.length / 3);
                int blue = vizHelper.bitsToInt(displayString, 2 * displayString.length / 3, displayString.length);
                foreground = new Color(red%256, green%256, blue%256);
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

        GlobalParameters.setParameters(snapShot.getParams());
        MapVisualizer vis = new MapVisualizer();
        
        //This doesn't work with graph world, but we never use it
        //saveImageToFile(vis.drawCellular(snapShot.getWorld()), outputFile);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(canvas, 20, 40, null);
    }
}
