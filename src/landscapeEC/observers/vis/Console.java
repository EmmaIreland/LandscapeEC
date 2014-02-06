package landscapeEC.observers.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import landscapeEC.core.GARun;
import landscapeEC.locality.GridWorld;
import landscapeEC.locality.Vector;
import landscapeEC.observers.Observer;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.sat.SatInstance;


public class Console extends JFrame implements ActionListener, Observer {
    private int width = 400;
    private int height = 300;
    
    JTextField commandInput;
    JTextArea displayOutput;
    JScrollPane scrollPane;

    boolean newLine = false;
    private GridWorld world;

    public Console() {
        init();
    }

    public void init() {
        setBackground(Color.white);
        setForeground(Color.black);

        setFocusable(false);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout b = new BorderLayout();
        setLayout(b);

        displayOutput = new JTextArea(17, 80);
        displayOutput.setEditable(false);
        displayOutput.setBackground(Color.white);
        displayOutput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createMatteBorder(5, 5, 5, 5,
                Color.white)));
        displayOutput.setLineWrap(true);
        displayOutput.setWrapStyleWord(true);
        displayOutput.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

        scrollPane = new JScrollPane(displayOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        commandInput = new JTextField(80);
        commandInput.addActionListener(this);
        commandInput.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

        add(commandInput);
        add("North", scrollPane);

        commandInput.requestFocus();
        
        repaint();
        
        this.setLocation(this.getLocation().x - width, this.getLocation().y);
        
        resize(width+1, height+1);
    }

    public void print(String str) {
        if (newLine) {
            displayOutput.setText("");
            newLine = false;
        }
        displayOutput.append(str);
    }

    public void println(String str) {
        if (newLine) {
            displayOutput.setText("");
            newLine = false;
        }
        displayOutput.append(str + "\n");
    }

    public void endline() {
        newLine = true;
        displayOutput.append("\n>");
    }

    public void executeCommand() {
        String command;
        command = commandInput.getText();
        displayOutput.append("> " + command + "\n");
        commandInput.setText("");
        
        parseCommand(command);
    }

    public void parseCommand(String command) {
        String[] token = command.split(" +");
        String keyword = token[0].toLowerCase();
        
        if(keyword.equals("getindividuals")) {
            
            Integer[] coord = parseCoordinates(token);
            if (coord[0] != -1) {
                println(world.getLocation(new Vector(coord)).getIndividuals().toString());
            }
            
        } else if(keyword.equals("getclauses")) {
            
            Integer[] coord = parseCoordinates(token);
            if (coord[0] != -1) {
                println(world.getLocation(new Vector(coord)).getProblem().toString());
            }
            
        } else if(keyword.equals("getnumclauses")) {
            
            Integer[] coord = parseCoordinates(token);
            if (coord[0] != -1) {
                println(((SatInstance) world.getLocation(new Vector(coord)).getProblem()).getNumClauses()+"");
            }
            
        } else if(keyword.equals("getbest")) {

            Integer[] coord = parseCoordinates(token);
            if (coord[0] != -1) {
                IndividualComparator comparator = IndividualComparator.getComparator();
                List<Individual> individuals = world.getLocation(new Vector(coord)).getIndividuals();
                Individual best = Collections.max(individuals, comparator);
                println("Bitstring: " + best.toString());
                println("Fitness: " + best.getGlobalFitness());
            }
            
        } else if(keyword.equals("getbestoverall")) {
            Individual best = world.findBestIndividual();
            println("Bitstring: " + best.toString());
            println("Fitness: " + best.getGlobalFitness());
            
        } else if(keyword.equals("help")) {
            
            println("List of commands:\n" +
                    "getindividuals <position> -- get all individuals at a location\n" +
                    "getbest <position> -- get the best individual at a location\n" +
                    "getbestoverall -- get the best individual in the world\n" +
                    "getclauses <position> -- get the required clauses at a location\n" +
                    "getnumclauses <position> -- get the number of required clauses at a location\n" +
            "help -- print this command list\n\n" +
            "NOTE: positions are space-separated. example: getbest 2 3\n");
            
        } else {
            
            println("Unkown command. Type \"help\" for a list of commands.");
        }
    }
    
    public Integer[] parseCoordinates(String[] token) {
        Integer[] coord;
        if(token.length-1 == world.getDimensions().size()) {
            coord = new Integer[token.length-1];
            for(int i=0; i<token.length-1; i++) {
                try {
                    coord[i] = Integer.parseInt(token[i+1]);
                } catch (NumberFormatException exception) {
                    println("Error: position arguments must contain only integers");
                    coord = new Integer[1];
                    coord[0] = -1;
                    break;
                }
            }
        } else {
            println("incorrect # of args (must be equal to number of dimensions)");
            coord = new Integer[1];
            coord[0] = -1;
        }
        return coord;
    }

    public void actionPerformed(ActionEvent e) {
        executeCommand();
    }

    @Override
    public void generationData(GARun run) {
        this.world = (GridWorld) run.getWorld();
    }
}