package landscapeEC.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import landscapeEC.locality.Location;
import landscapeEC.locality.MigrationInWorldOfSizeOneException;
import landscapeEC.locality.Vector;
import landscapeEC.locality.World;
import landscapeEC.observers.Observer;
import landscapeEC.parameters.BooleanParameter;
import landscapeEC.parameters.DoubleArrayParameter;
import landscapeEC.parameters.DoubleParameter;
import landscapeEC.parameters.GlobalParameters;
import landscapeEC.parameters.IntArrayParameter;
import landscapeEC.parameters.IntParameter;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.sat.Individual;
import landscapeEC.problem.sat.DiversityCalculator;
import landscapeEC.problem.sat.EmptyWorldException;
import landscapeEC.problem.sat.GlobalSatInstance;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;
import landscapeEC.problem.sat.SeedType;
import landscapeEC.problem.sat.SnapShot;
import landscapeEC.problem.sat.operators.CrossoverOperator;
import landscapeEC.problem.sat.operators.MutationOperator;
import landscapeEC.problem.sat.operators.SelectionOperator;
import landscapeEC.util.ParameterClassLoader;
import landscapeEC.util.SharedPRNG;

public class GARun {

   private MutationOperator mutationOperator;
   private SelectionOperator selectionOperator;
   private CrossoverOperator crossoverOperator;

   private PopulationManager popManager;

   private World world;

   private List<Observer> observers = new ArrayList<Observer>();
   private int successes;
   private String propertiesFilename;
   private FileWriter writer;
   private double bestOverallFitness;

   private double[] intervalFitnesses;
   private double[] intervalDiversities;

   public GARun(String propertiesFilename) {
      this.propertiesFilename = propertiesFilename;
   }

   public void run() throws Exception {
      mutationOperator = ParameterClassLoader.loadClass(StringParameter.MUTATION_OPERATOR);
      selectionOperator = ParameterClassLoader.loadClass(StringParameter.SELECTION_OPERATOR);
      crossoverOperator = ParameterClassLoader.loadClass(StringParameter.CROSSOVER_OPERATOR);

      intervalFitnesses = new double[getReportingIntervals().length];
      intervalDiversities = new double[getReportingIntervals().length];

      SatParser satParser = new SatParser();
      SatInstance satInstance = satParser.parseInstance(new FileReader(new File(StringParameter.PROBLEM_FILE.getValue())));
      GlobalSatInstance.setInstance(satInstance);

      popManager = new PopulationManager();

      int numRuns = IntParameter.NUM_RUNS.getValue();

      setupObservers();

      generateRFile(successes);

      successes = 0;
      for(int i = 0; i < numRuns; i++) {
         System.out.println("\nRUN " + (i + 1) + "\n");

         Arrays.fill(intervalFitnesses, Double.NaN);

         try {
            if(runGenerations(i)) {
               addRunToRFile(true, SatEvaluator.getNumEvaluations(), 1.0);
               successes++;
            } else {
               addRunToRFile(false, IntParameter.NUM_EVALS_TO_DO.getValue(), bestOverallFitness);
            }
         } catch(EmptyWorldException e) {
            System.err.println("All individuals died!");
         }

         SatEvaluator.resetEvaluationsCounter();
         SharedPRNG.updateGenerator(); //Generate a new seed for each run
      }

      System.out.println(successes + "/" + numRuns + " runs successful");

      closeRFile();
   }

   private void addRunToRFile(boolean success, int completedEvaluations, double bestFitness)
         throws IOException {
      Set<String> propertyNames = GlobalParameters.getParameterNames();
      for(String name : propertyNames) {
         if(GlobalParameters.isSet(name)) {
            writer.write(GlobalParameters.getStringValue(name).replaceAll(" ", "") + " ");
         }
      }

      for(int i = 0; i < intervalFitnesses.length; i++) {
         double fitness = intervalFitnesses[i];
         if(Double.isNaN(fitness)) {
            fitness = 1.0;
         }
         writer.write(fitness + " ");
      }

      for(int i = 0; i < intervalDiversities.length; i++) {
         double diversity = intervalDiversities[i];
         writer.write(diversity + " ");
      }

      writer.write(success + " " + completedEvaluations + " " + bestFitness + "\n");
   }

   private void generateRFile(int successes) throws IOException {
      writer = new FileWriter(new File(propertiesFilename + ".R"));

      Set<String> propertyNames = GlobalParameters.getParameterNames();
      for(String name : propertyNames) {
         if(GlobalParameters.isSet(name)) {
            writer.write(name + " ");
         }
      }
      for(int i = 0; i < getReportingIntervals().length; i++) {
         writer.write("INTERVAL_" + getReportingIntervals()[i] + "_FITNESS ");
      }
      for(int i = 0; i < getReportingIntervals().length; i++) {
         writer.write("INTERVAL_" + getReportingIntervals()[i] + "_DIVERSITY ");
      }
      writer.write("SUCCESS COMPLETED_EVALS BEST_FITNESS\n");
   }

   private double[] getReportingIntervals() {
      return DoubleArrayParameter.REPORTING_INTERVALS.getValue();
   }

   private void closeRFile() throws IOException {
      writer.flush();
      writer.close();
   }

   @SuppressWarnings("unchecked")
   private void setupObservers() throws Exception {
      if(!GlobalParameters.isSet(StringParameter.OBSERVERS.toString()))
         return;

      String observerNames[] = StringParameter.OBSERVERS.getValue().split(",");

      for(String observerName : observerNames) {
         Class<Observer> obs = (Class<Observer>) Class.forName(observerName);
         Constructor<Observer> cons = obs.getConstructor();
         Observer instance = (Observer) cons.newInstance();
         observers.add(instance);
      }
   }

   private boolean runGenerations(int currentRun) throws Exception {
      List<Individual> population = popManager.generatePopulation();

      world = new World(new Vector(IntArrayParameter.WORLD_DIMENSIONS.getValue()),
            BooleanParameter.TOROIDAL.getValue());

      world.clear();

      SeedType seedType = SeedType.valueOf(StringParameter.STARTING_POPULATION.getValue());

      switch(seedType) {
      case ORIGIN:
         world.getOrigin().setIndividuals(population);
         break;
      case EVERYWHERE:
         fillLocations(world);
         break;
      case CORNERS:
         Vector topLeft = Vector.origin(world.getDimensions().size());
         Vector bottomRight = world.getDimensions().minusToAll(1);
         Vector topRight = Vector.getCorner(bottomRight, topLeft);
         Vector bottomLeft = Vector.getCorner(topLeft, bottomRight);
         List<Vector> vectors = Arrays.asList(topLeft, bottomLeft, topRight, bottomRight);
         fillLocations(vectors);
         break;
      }

      int i = 0;
      bestOverallFitness = 0.0;
      Individual bestIndividual = null;
      while(SatEvaluator.getNumEvaluations() < IntParameter.NUM_EVALS_TO_DO.getValue()) {
         processAllLocations();

         for(Observer o : observers) {
            o.generationData(i, world, successes);
         }

         bestIndividual = world.findBestIndividual();
         // System.out.println("Generation " + (i + 1));
         // System.out.println("   Best individual: " + bestIndividual);
         bestOverallFitness = bestIndividual.getGlobalFitness();        
         // System.out.println("   Best fitness: " + bestFitness);

         double[] reportingIntervals = getReportingIntervals();
         for(int j = 0; j < reportingIntervals.length; j++) {
            if(SatEvaluator.getNumEvaluations() > reportingIntervals[j]
                  * IntParameter.NUM_EVALS_TO_DO.getValue()
                  && Double.isNaN(intervalFitnesses[j])) {
               intervalFitnesses[j] = bestOverallFitness;
               intervalDiversities[j] = DiversityCalculator.calculateClauseListDiversity();
               SnapShot.saveSnapShot(propertiesFilename + ".run" + currentRun + ".part" + j, world);
            }
         }

         if(BooleanParameter.QUIT_ON_SUCCESS.getValue() && bestOverallFitness == 1.0) {
            System.out.println("Best Fitness: " + bestOverallFitness);
            SatEvaluator.printUnsolvedClauses(bestIndividual);
            //This will be removed during refactoring
            System.out.println("SUCCESS");
            return true;
         }

         i++;
      }

      if (bestOverallFitness == 1.0) {
          System.out.println("Best Fitness: " + bestOverallFitness);
          SatEvaluator.printUnsolvedClauses(bestIndividual);
          //This will be removed during refactoring
          System.out.println("SUCCESS");
          return true;            
      }
      
      System.out.println("Best Fitness: " + bestOverallFitness);
      SatEvaluator.printUnsolvedClauses(bestIndividual);
      //This will be removed during refactoring
      System.out.println("FAILURE");
      return false;
   }

   private void fillLocations(Iterable<Vector> vectors) {
      for(Vector v : vectors) {
         Location l = world.getLocation(v);
         l.setIndividuals(popManager.generatePopulation());
      }
   }

   private void processAllLocations() {
      updateDiversityCounts();
      performMigration();
      addFromPendingIndividuals();

      performDraconianReaper();
      setFromPendingIndividuals();

      performElitism();
      performReproduction();
      setFromPendingIndividuals();
   }

   private void updateDiversityCounts() {
       DiversityCalculator.reset();
       for (Vector position : world) {
           List<Individual> locationIndividuals = world.getIndividualsAt(position);
           for (Individual individual : locationIndividuals) {
               DiversityCalculator.addIndividual(individual);
           }
       }
   }

private void performDraconianReaper() {

      for(Vector position : world) {
         List<Individual> locationIndividuals = world.getIndividualsAt(position);

         for(Individual individual : locationIndividuals) {
            SatInstance locationInstance = world.getLocation(position).getComparator()
                  .getInstance();
            double fitness = SatEvaluator.evaluate(locationInstance, individual);
            int numClauses = locationInstance.getNumClauses();
            int correctClauses = (int) Math.round(fitness * numClauses);
            if(numClauses == correctClauses) {
               world.getLocation(position).addToPendingIndividuals(individual);
            }
         }
      }
   }

   private void performMigration() {

      double migrationProbability = DoubleParameter.MIGRATION_PROBABILITY.getValue();
      int migrationDistance = IntParameter.MIGRATION_DISTANCE.getValue();

      if(migrationProbability <= 0 || migrationDistance <= 0)
         return;

      for(Vector position : world) {
         List<Individual> locationIndividuals = world.getIndividualsAt(position);
         List<Individual> individualsToRemove = new ArrayList<Individual>();

         for(Individual i : locationIndividuals) {
            if(SharedPRNG.instance().nextDouble() < migrationProbability) {
               individualsToRemove.add(i);
               List<Vector> neighborhood = world.getNeighborhood(position, migrationDistance);
               neighborhood.remove(position);
               Vector newPosition;
               try {
                  newPosition = neighborhood
                        .get(SharedPRNG.instance().nextInt(neighborhood.size()));
               } catch(IndexOutOfBoundsException e) {
                  throw new MigrationInWorldOfSizeOneException(e);
               }
               world.getLocation(newPosition).addToPendingIndividuals(i);
            }
         }
         world.getLocation(position).removeAll(individualsToRemove);
      }
   }

   private void performElitism() {
      for(Vector position : world) {
         List<Individual> locationIndividuals = world.getIndividualsAt(position);

         if(!locationIndividuals.isEmpty()) {
            List<Individual> elite = popManager.getElite(locationIndividuals,
                  DoubleParameter.ELITE_PROPORTION.getValue());

            world.getLocation(position).addToPendingIndividuals(elite);
         }
      }
   }

   private void performReproduction() {
      for(Vector position : world) {
         List<Individual> locationIndividuals = world.getIndividualsAt(position);

         if(locationIndividuals.size() >= IntParameter.TOURNAMENT_SIZE.getValue()) {
            List<Individual> crossoverPop = popManager.crossover(locationIndividuals,
                  selectionOperator, crossoverOperator);

            List<Individual> mutatedPopulation = popManager.mutatePopulation(crossoverPop,
                  mutationOperator);

            world.getLocation(position).addToPendingIndividuals(mutatedPopulation);
         } else if(BooleanParameter.PROMOTE_SMALL_POPULATIONS.getValue()) {
            List<Individual> copiedPopulation = new ArrayList<Individual>();
            for(Individual individual : locationIndividuals) {
               copiedPopulation.add(individual);
            }

            List<Individual> mutatedPopulation = popManager.mutatePopulation(copiedPopulation,
                  mutationOperator);

            world.getLocation(position).addToPendingIndividuals(mutatedPopulation);
         }
      }
   }

   private void setFromPendingIndividuals() {
      for(Vector position : world) {
         world.getLocation(position).setFromPendingIndividuals();
         // assert world.getLocation(position).getNumIndividuals() <=
         // IntParameter.CARRYING_CAPACITY.getValue();
      }
   }

   private void addFromPendingIndividuals() {
      for(Vector position : world) {
         world.getLocation(position).addFromPendingIndividuals();
      }
   }
}
