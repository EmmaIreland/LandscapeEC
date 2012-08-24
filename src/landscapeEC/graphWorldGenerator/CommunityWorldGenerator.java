
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.io.Writer;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Random;

	public class OwlOntologyGenerator {

	    //This generates Ring, fully connected (complete), tree, and grid shaped ontologies
	    //See README in /amboro/tests/regression-testing/ for details on this program

	    static String FileName = null;
	    //These ints are used for multiple purposes depending on the graph type so they must be named generically.
	    static String type;
	    static Boolean rand;
	    static String in1;
	    static String in2;
	    static String in3;
	    static String in4;
	    static String in5;
	    static int linkNum = 0;
	    static Integer numOfNodes = 0;

	    //Here are the global constants that determine the variable amounts.
	    final static Integer LINK_THROUGH_MIN = 50;
	    final static Integer LINK_THROUGH_MAX = 200;
	    final static Integer PE_THROUGH_MIN = 1000;
	    final static Integer PE_THROUGH_MAX = 3000;
	    final static Integer CPU_CAPACITY_MIN = 50000;
	    final static Integer CPU_CAPACITY_MAX = 100000;
	    final static Integer MEM_CAPACITY_MIN = 4;
	    final static Integer MEM_CAPACITY_MAX = 16;
	    final static Double FAIL_MIN = .1;
	    final static Double FAIL_MAX = .9;
	    //end variable amounts.

	    final static File DIR = new File(new File(new File(new File(".."),"tests"),"regression-test"),"ontologies");
	    //final static File DIR = new File(new File(".."),"..");
	    static Writer output = null; 
	    static Random gen = new Random();

	    public static void main(String[] args) {


	        if (args.length >= 1) {
	            type = args[0];
	        }

	        if (args.length >= 2) {
	            rand = Boolean.parseBoolean(args[1]);
	        }

	        if (args.length >= 3) {
	            in1 = args[2];
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <tree depth>, <number of branches> for selected graph shape: " + type);
	            }
	        }

	        if (args.length >= 4) {
	            in2 = args[3];
	        }

	        if (args.length >= 5) {
	            in3 = args[4];
	        }

	        if (args.length >= 6) {
	            in4 = args[5];
	        }
	        
	        if (args.length >= 7) {
	            in5 = args[6];
	        }

	        if (!(type.equals("complete") || type.equals("ring") || type.equals("grid") || type.equals("tree") || type.equals("star") || type.equals("scaleFree") || type.equals("community"))) {
	            throw new IllegalArgumentException("Supported graph shapes: complete, ring, grid, tree, star, scaleFree, community, you gave:" + type);
	        }

	        if (args[0].equals("ring")) {
	            if (args.length != 3) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <number of elements> for selected graph shape: " + type);
	            }
	            makeRing();
	        }

	        if (args[0].equals("complete")) {
	            if (args.length != 3) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <number of elements> for selected graph shape: " + type);
	            }
	            makeComplete();
	        }

	        if (args[0].equals("star")) {
	            if (args.length != 3) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <number of elements> for selected graph shape: " + type);
	            }
	            makeStar();
	        }

	        if (args[0].equals("tree")) {
	            if (args.length != 4) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <tree depth>, <number of branches> for selected graph shape: " + type);
	            }
	            makeTree();
	        }

	        if (args[0].equals("grid")) {
	            if (args.length != 4) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <length of a side>, <dimensionality> for selected graph shape: " + type);
	            }
	            makeGrid();
	        }

	        if (args[0].equals("scaleFree")) {
	            if (args.length != 4 && args.length!= 5) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <number of elements>, <connectivity value>, <seed (optional)> for selected graph shape: " + type);
	            }
	            makeScaleFree();
	        }

	        if(args[0].equals("community")) {
	            if(args.length != 6 && args.length != 7) {
	                throw new IllegalArgumentException("Arguments: <ontology shape>, <boolean>, <number of elements>, <number of communities>, <community prob>, " +
	                        "<intercommunity prob>, <seed (optional)> for selected graph shape: " + type);
	            }
	            makeCommunity();
	        }
	    }

	    
	    private static void makeCommunity() {
	        //The number of nodes will be split as evenly as possible between the number of communities. Each node has a chance equal
	        //to the 5th input to be connected with any single node in it's community. Additionally, it has a chance equal to the 6th
	        //input to have a connection with a random node outside of the community.
	        numOfNodes = Integer.parseInt(in1);
	        //Note: "comm" below refers to community, not communications
	        Integer commNum = Integer.parseInt(in2);
	        double commProb = Double.parseDouble(in3);
	        double interProb = Double.parseDouble(in4);

	        if (commProb < 0 || commProb > 1) {
	            throw new IllegalArgumentException("The third input must be between or equal to 0 to 1 for community graphs.");
	        }
	        if (interProb < 0 || interProb > 1) {
	            throw new IllegalArgumentException("The fourth input must be between or equal to 0 to 1 for community graphs.");
	        }

	        //community-#N-#Com-#ComProb-#InterProb.owl
	        FileName = type + "-" + getRand() + "-" + numOfNodes + "N-" + commNum + "CommNum-" + commProb + "CommProb-" + interProb + "InterProb.owl";

	        if (in5 != null) {
	            Long seed = new Long(in5);
	            gen.setSeed(seed);
	        }
	        
	        makeFile();
	        makeHeader();

	        //put all the nodes in their communities
	        //HashMap<Integer, Integer> commMap = new HashMap<Integer, Integer>();
	        ArrayList<Node> nodeList = new ArrayList<Node>();
	        int commSelector = numOfNodes/commNum;

	        //System.out.println("---------------------------------------------------------------");
	        //initialize all the nodes as well as sort them into their communities
	        for (int i = 0; i < numOfNodes; i++) {
	            nodeList.add(new Node(i, i/commSelector));
	            //System.out.println("Node " + nodeList.get(i).getName() + " is in community " + nodeList.get(i).getCommunity());
	            
	        }
	        //System.out.println("---------------------------------------------------------------");
	        //add community links
	        for (int i = 0; i < numOfNodes; i++) {
	            Node curr = nodeList.get(i);
	            for (int k = (i+1); k < numOfNodes; k++) {
	                Node toNode = nodeList.get(k);
	                if (curr.getCommunity().equals(toNode.getCommunity()) && !toNode.contains(curr.getName()) ) {
	                    if (gen.nextDouble() < commProb) {
	                        makeLink(i, k);
	                        makeLink(k, i);
	                        //System.out.println("Community link from " + i + " to " + k);
	                    }
	                    curr.add(toNode.getName());
	                    toNode.add(curr.getName());
	                }
	            }
	        }
	        //System.out.println("---------------------------------------------------------------");
	        //add inter community links
	        for (int i = 0; i < numOfNodes; i++) {
	            Node curr = nodeList.get(i);
	            if (gen.nextDouble() < interProb) {
	                int rand = gen.nextInt(numOfNodes);
	                while (curr.getName().equals(rand) || curr.contains(rand) || curr.getCommunity().equals(nodeList.get(rand).getCommunity())) {
	                    rand = gen.nextInt(numOfNodes);
	                }
	                //System.out.println("inter community link from " + i + " to " + rand);
	                makeLink(i, rand);
	                makeLink(rand, i);
	                curr.add(rand);
	                nodeList.get(rand).add(i);
	            }
	        }
	        
	        
	        for(int l = 0; l < numOfNodes; l++){
	            makePe(l);
	        }

	        makeFooter();

	    }

	    private static void makeScaleFree() {
	        //the way the scale free graph will be generated is an initial line of nodes will be made. Then, more nodes will be added.
	        //Each new node will have D (conn) number of connections which will go to a stochastically chosen node. A node with more
	        //connections will have a higher chance of getting more connections. Whenever a node gets a connection, the node is put
	        //into a hashtable with mapNum as it's key. Then, the node is connected to another node by selecting a random number on
	        //the hashtable and selecting the node the key indicated.
	        numOfNodes = Integer.parseInt(in1);
	        Integer conn = Integer.parseInt(in2);
	        
	        if (numOfNodes <= conn) {
	            throw new IllegalArgumentException("The number of nodes must be higher than the number of connections each added node has.");
	        }
	        if (conn <= 1) {
	            throw new IllegalArgumentException("The number of connections a new node makes must be higher than one.");
	        }
	        
	        
	        
	        if (in3 != null) {
	            Long seed = new Long(in3);
	            gen.setSeed(seed);
	        }
	        
	        FileName = type + "-" + getRand() + "-" + numOfNodes.toString() + "N-" + conn.toString() + "D.owl";

	        HashMap<Integer, Integer> probMap = new HashMap<Integer, Integer>();
	        HashMap<Integer, Node> nodeMap =  new HashMap<Integer, Node>();
	        int mapNum = 0;

	        makeFile();
	        makeHeader();

	        for (int i = 0; i < (conn-1)+((numOfNodes-conn)*conn); i++) {
	            nodeMap.put(i, new Node(i));
	        }

	        for (int i = 0; i < conn; i ++) {
	            makeLink(i, i+1);
	            nodeMap.get(i).add(i+1);
	            probMap.put(mapNum, i);
	            mapNum++;
	            makeLink(i+1, i);
	            nodeMap.get(i+1).add(i);
	            probMap.put(mapNum, (i+1));
	            mapNum++;
	        }

	        for (int i = conn+1; i < numOfNodes; i++) {
	            for (int k = 0; k < conn; k++){
	                boolean bool = true;
	                Integer linkTo = null;
	                while(bool) {
	                    linkTo = gen.nextInt(probMap.size());
	                    if (!(probMap.get(linkTo) == i || nodeMap.get(i).contains(probMap.get(linkTo)))) {
	                        bool = false;
	                    }
	                }

	                Integer toNode = probMap.get(linkTo);

	                makeLink(i, toNode);
	                nodeMap.get(i).add(toNode);
	                probMap.put(mapNum, toNode);
	                mapNum++;
	                makeLink(toNode, i);
	                nodeMap.get(toNode).add(i);
	                probMap.put(mapNum, i);
	                mapNum++;
	            }
	        }


	        for(int l = 0; l < numOfNodes; l++){
	            makePe(l);
	        }

	        makeFooter();

	    }

	    private static void makeStar() {
	        numOfNodes = Integer.parseInt(in1);  
	        FileName = type + "-" + getRand() + "-" + numOfNodes.toString() + "N.owl";

	        makeFile();
	        makeHeader();

	        for (int i = 1; i < numOfNodes; i ++) {
	            makeLink(0, i);
	            makeLink(i, 0);
	        }

	        for(int l = 0; l < numOfNodes; l++){
	            makePe(l);
	        }

	        makeFooter();

	    }

	    private static void makeGrid() {

	        int L = Integer.parseInt(in1);
	        int D = Integer.parseInt(in2);
	        numOfNodes = (int) Math.pow(L, D);
	        FileName = type + "-" + getRand() + "-" + numOfNodes.toString() + "N-" + L + "L-" + D + "D.owl";

	        makeFile();
	        makeHeader();

	        for (int i = 1; i <= D; i++) {
	            for (int k = 0; k < numOfNodes; k++) {


	                if(round((k/Math.pow(L, i))) == round((((k+Math.pow(L, i-1)))/Math.pow(L, i)))) {
	                    makeLink(k, (int) (k+Math.pow(L, i-1)));
	                }

	                if(round((k/Math.pow(L, i))) == round((((int) (k-Math.pow(L, i-1)))/Math.pow(L, i)))) {
	                    makeLink(k, (int) (k-Math.pow(L, i-1)));
	                }
	            }
	        }

	        for(int l = 0; l < numOfNodes; l++){
	            makePe(l);
	        }

	        makeFooter();
	    }

	    private static int round(double num) {
	        int value;
	        if (num >= 0) {
	            value = (int) num;
	        }else{
	            value = ((int) num) - 1;
	        }
	        return value;
	    }



	    //FirstInt is the depth of the tree and SecondInt is the number of branches from each node
	    private static void makeTree() {

	        int D = Integer.parseInt(in1);
	        int B = Integer.parseInt(in2);
	        int numOfNodes = 0;

	        for(Integer k = 0; k < D; k++) {
	            numOfNodes = (int) (numOfNodes + Math.pow(B, k));
	        }

	        FileName = type + "-" + getRand() + "-" + numOfNodes + "N-" + D + "D-" + B + "B.owl";

	        makeFile();
	        makeHeader();

	        //this number will be the number of nodes that aren't leaves.
	        int linkedNodes = 0;
	        for(int i = 0; i < D-1; i++) {
	            linkedNodes = (int) (linkedNodes + Math.pow(B, i));
	        }
	        //this number will keep track of which link number is next
	        //this num will keep track of which node we are on
	        int nodeNum = 1;
	        for(int j = 0; j < linkedNodes; j++) {
	            for (int m = 0; m < B; m++) {
	                makeLink(j, nodeNum);
	                makeLink(nodeNum, j);
	                nodeNum++;
	            }
	        }


	        //find number of pe's we will need

	        for(int l = 0; l < numOfNodes; l++){
	            makePe(l);
	        }

	        makeFooter();

	    }


	    //FirstInt is the number of nodes in the graph, SecondInt is unused
	    private static void makeComplete() {

	        numOfNodes = Integer.parseInt(in1);  
	        FileName = type + "-" + getRand() + "-" + numOfNodes.toString() + "N.owl";

	        makeFile();
	        makeHeader();

	        for(int i = 0; i < numOfNodes; i++) {
	            for(int k = 0; k < numOfNodes; k++) {
	                if (k != i){
	                    makeLink(i, k);
	                }
	            }
	        }

	        for(int i = 0; i < numOfNodes; i++){
	            makePe(i);
	        }

	        makeFooter();

	    }


	    //FirstInt is the number of nodes in the graph, SecondInt is unused.
	    private static void makeRing() {

	        numOfNodes = Integer.parseInt(in1);  
	        FileName = type + "-" + getRand() + "-" + numOfNodes.toString() + "N.owl";

	        makeFile();
	        makeHeader();

	        for(int i = 0; i < numOfNodes-1; i++){
	            makeLink(i, i+1);
	            makeLink(i+1, i);
	        }
	        makeLink(numOfNodes-1, 0);
	        makeLink(0, numOfNodes-1);

	        for(int i = 0; i < numOfNodes; i++){
	            makePe(i);
	        }

	        makeFooter();

	    }


	    private static String getRand() {
	        String result = "NoRand";

	        if(rand) {
	            result = "FullRand";
	        }

	        return result;
	    }


	    private static void makeFile() {
	        File file = new File(DIR, FileName);
	        try {
	            output = new BufferedWriter(new FileWriter(file));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void makePe(Integer num) {
	        String pe = "\n\n\n\n   <!-- http://www.adventiumlabs.com/ontologies/" + FileName + "#pe" + num + " -->\n\n" +

	        "   <owl:NamedIndividual rdf:about=\"http://www.adventiumlabs.com/ontologies/" + FileName + "#pe" + num + "\">\n" +
	        "       <rdf:type rdf:resource=\"&AmboroNetworkSchema;ProcessingElement\"/>\n" +
	        "       <AmboroNetworkSchema:hasThroughputCapacity rdf:datatype=\"&xsd;double\">" + getPEThroughPut() + "</AmboroNetworkSchema:hasThroughputCapacity>\n" +
	        "       <AmboroNetworkSchema:hasCpuCapacity rdf:datatype=\"&xsd;double\">" + getCPUCapacity() + "</AmboroNetworkSchema:hasCpuCapacity>\n" +
	        "       <AmboroNetworkSchema:hasMemoryCapacity rdf:datatype=\"&xsd;double\">" + getMemoryCapacity() + "</AmboroNetworkSchema:hasMemoryCapacity>\n" +
	        "       <AmboroNetworkSchema:hasProbabilityOfFailure rdf:datatype=\"&xsd;float\">" + getFailure() + "</AmboroNetworkSchema:hasProbabilityOfFailure>\n" +
	        "   </owl:NamedIndividual>";

	        try {
	            output.write(pe);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }

	    private static String getFailure() {
	        Double num;
	        if (rand) {
	            if (FAIL_MAX.equals(FAIL_MIN)){
	                num = FAIL_MAX;
	            }else{
	                num = gen.nextDouble();
	                while(notInRange(num)) {
	                    num = gen.nextDouble();
	                }

	            }
	        } else {
	            num = FAIL_MIN;
	        }
	        return num.toString();
	    }


	    private static boolean notInRange(Double test) {
	        boolean bool = false;

	        if(test.compareTo(FAIL_MIN) < 0 && test.compareTo(FAIL_MAX) > 0){
	            bool = true;
	        }

	        return bool;
	    }

	    private static String getMemoryCapacity() {
	        Integer num;
	        if (rand) {
	            if (MEM_CAPACITY_MAX.equals(MEM_CAPACITY_MIN)){
	                num = MEM_CAPACITY_MAX;
	            }else{
	                int range = MEM_CAPACITY_MAX - MEM_CAPACITY_MIN;
	                num = MEM_CAPACITY_MIN + gen.nextInt(range);
	            }
	        } else {
	            num = MEM_CAPACITY_MAX;
	        }
	        return num.toString();
	    }

	    private static String getCPUCapacity() {
	        Integer num;
	        if (rand) {
	            if (CPU_CAPACITY_MIN.equals(CPU_CAPACITY_MAX)){
	                num = MEM_CAPACITY_MAX;
	            }else{
	                int range = CPU_CAPACITY_MAX - CPU_CAPACITY_MIN;
	                num = CPU_CAPACITY_MIN + gen.nextInt(range);
	            }
	        } else {
	            num = CPU_CAPACITY_MAX;
	        }
	        return num.toString();
	    }

	    private static String getPEThroughPut() {
	        Integer num;
	        if (rand) {
	            if (PE_THROUGH_MAX.equals(PE_THROUGH_MIN)) {
	                num = PE_THROUGH_MAX;
	            }else{
	                int range = PE_THROUGH_MAX - PE_THROUGH_MIN;
	                num = PE_THROUGH_MIN + gen.nextInt(range);
	            }
	        } else {
	            num = PE_THROUGH_MAX;
	        }
	        return num.toString();
	    }

	    private static void makeLink(Integer source, Integer dest) {
	        String link = "\n\n\n\n   <!-- http://www.adventiumlabs.com/ontologies/" + FileName + "#link" + linkNum + " -->\n\n" +

	        "   <owl:NamedIndividual rdf:about=\"http://www.adventiumlabs.com/ontologies/" + FileName + "#link" + linkNum + "\">\n" +
	        "       <rdf:type rdf:resource=\"&AmboroNetworkSchema;Link\"/>\n" +
	        "       <AmboroNetworkSchema:hasThroughputCapacity rdf:datatype=\"&xsd;double\">" + getLinkThroughPut() + "</AmboroNetworkSchema:hasThroughputCapacity>\n" +
	        "       <AmboroNetworkSchema:hasSourceProcessingElement rdf:resource=\"http://www.adventiumlabs.com/ontologies/" + FileName + "#pe" + source + "\"/>\n" +
	        "       <AmboroNetworkSchema:hasDestinationProcessingElement rdf:resource=\"http://www.adventiumlabs.com/ontologies/" + FileName + "#pe" + dest + "\"/>\n" +
	        "   </owl:NamedIndividual>";

	        try {
	            output.write(link);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        linkNum++;
	    }

	    private static String getLinkThroughPut() {
	        Integer num;
	        if (rand) {
	            if (LINK_THROUGH_MAX.equals(LINK_THROUGH_MIN)) {
	                num = LINK_THROUGH_MAX;
	            }else{
	                int range = LINK_THROUGH_MAX - LINK_THROUGH_MIN;
	                num = LINK_THROUGH_MIN + gen.nextInt(range);
	            }
	        } else {
	            num = LINK_THROUGH_MAX;
	        }
	        return num.toString();
	    }

	    private static void makeHeader() {
	        String header = "<?xml version=\"1.0\"?>\n\n\n" +


	        "<!DOCTYPE rdf:RDF [\n" +
	        "   <!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n" +
	        "   <!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n" +
	        "   <!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n" +
	        "   <!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n" +
	        "   <!ENTITY AmboroNetworkSchema \"http://www.adventiumlabs.com/ontologies/AmboroNetworkSchema.owl#\" >\n" +
	        "   <!ENTITY AmboroDemandsSchema \"http://www.adventiumlabs.com/ontologies/AmboroDemandsSchema.owl#\" >\n" +
	        "]>\n\n\n" +


	        "<rdf:RDF xmlns=\"http://www.adventiumlabs.com/ontologies/" + FileName + "#\"\n" +
	        "   xml:base=\"http://www.adventiumlabs.com/ontologies/" + FileName + "\"\n" +
	        "   xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
	        "   xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
	        "   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
	        "   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
	        "   xmlns:AmboroNetworkSchema=\"http://www.adventiumlabs.com/ontologies/AmboroNetworkSchema.owl#\"\n" +
	        "   xmlns:AmboroDemandsSchema=\"http://www.adventiumlabs.com/ontologies/AmboroDemandsSchema.owl#\">\n" +
	        "   <owl:Ontology rdf:about=\"http://www.adventiumlabs.com/ontologies/" + FileName + "\">\n" +
	        "       <owl:imports rdf:resource=\"http://www.adventiumlabs.com/ontologies/AmboroDemandsSchema.owl\"/>\n" +
	        "       <owl:imports rdf:resource=\"http://www.adventiumlabs.com/ontologies/AmboroNetworkSchema.owl\"/>\n" +
	        "   </owl:Ontology>\n\n\n\n" +



	        "   <!--\n" + 
	        "   ///////////////////////////////////////////////////////////////////////////////////////\n" +
	        "   //\n"+
	        "   // Datatypes\n"+
	        "   //\n"+
	        "   ///////////////////////////////////////////////////////////////////////////////////////\n" +
	        "   -->\n\n\n\n\n" +





	        "   <!--\n" + 
	        "   ///////////////////////////////////////////////////////////////////////////////////////\n" +
	        "   //\n"+
	        "   // Individuals\n"+
	        "   //\n"+
	        "   ///////////////////////////////////////////////////////////////////////////////////////\n" +
	        "   -->\n\n\n\n\n";

	        try {
	            output.write(header);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private static void makeFooter() {

	        try {
	            output.write("\n</rdf:RDF>\n\n\n\n" +
	                    "<!-- Generated by the OWL API (version 3.2.3.1824) http://owlapi.sourceforge.net -->\n\n");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        try {
	            output.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            output.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }



	    }
	
