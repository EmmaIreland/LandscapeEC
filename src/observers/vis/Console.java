package observers.vis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import locality.Vector;
import locality.World;

import observers.Observer;
import sat.SatInstance;

public class Console extends JFrame implements ActionListener, Observer {
    private int width = 400;
    private int height = 300;
    
    JTextField commandInput;
    JTextArea displayOutput;
    JScrollPane scrollPane;

    boolean newLine = false;
    private int generationNumber;
    private World world;
    private SatInstance satInstance;

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
        if(token[0].matches("^\\w+At.*")) {
            if(token.length-1 == world.getDimensions().size()) {
                Integer[] coord = new Integer[token.length-1];
                for(int i=0; i<token.length-1; i++) {
                    coord[i] = Integer.parseInt(token[i+1]);
                }
                if(token[0].equals("getIndividualsAt")) {
                    println(world.getLocation(new Vector(coord)).getIndividuals().toString());
                } else if(token[0].equals("getClausesAt")) {
                    println(world.getLocation(new Vector(coord)).getComparator().getInstance().getClauseList().toString());
                } else if(token[0].equals("getNumClausesAt")) {
                    println(world.getLocation(new Vector(coord)).getComparator().getInstance().getClauseList().size()+"");
                }
            } else {
                println("incorrect # of args (must be equal to number of dimensions)");
            }
        } else {
            println("Unknown command");
        }
    }

    public void actionPerformed(ActionEvent e) {
        executeCommand();
    }

    @Override
    public void generationData(int generationNumber, World world, SatInstance satInstance, int successes) {
        this.generationNumber = generationNumber;
        this.world = world;
        this.satInstance = satInstance;
    }
}