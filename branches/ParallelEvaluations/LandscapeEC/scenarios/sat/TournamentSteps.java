package sat;

import static junit.framework.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import landscapeEC.parameters.GlobalParameters;
import landscapeEC.problem.GlobalProblem;
import landscapeEC.problem.Individual;
import landscapeEC.problem.IndividualComparator;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;
import landscapeEC.problem.sat.SatParser;
import landscapeEC.problem.sat.operators.TournamentSelection;
import landscapeEC.util.FrequencyCounter;

import org.jbehave.scenario.annotations.AfterScenario;
import org.jbehave.scenario.annotations.Given;
import org.jbehave.scenario.annotations.Then;
import org.jbehave.scenario.annotations.When;
import org.jbehave.scenario.steps.Steps;

import util.ChiSquaredStatistics;
import util.MockParameters;

public class TournamentSteps extends Steps {
    
    private static final double EPSILON = 0.001;
    private SatInstance satInstance;
    private List<Individual> individuals = new ArrayList<Individual>();
    private FrequencyCounter<Individual> counter;

    @Given("a SAT evaluator")
    public void constructSATEvaluator() { //
    }
    
    @When("I have this clauseList $clauseList")
    public void getSatInstance(String clauseList) throws IOException {
        SatParser satParser= new SatParser();
        StringReader stringReader = new StringReader(clauseList);
        satInstance = satParser.parseProblem(stringReader);        
        GlobalProblem.setProblem(satInstance);
    }
    
    @When("I have a population of $population")
    public void getPopulation(String population) {
        String[] lines = population.split("\n");
        for(String line:lines) {
            Individual individual = new Individual(line);
            individuals.add(individual);
        }
    }
    
    @When("I run $numTournaments tournaments of size $tournamentSize")
    public void runTournaments(int numTournaments, String tournamentSize) {
        TournamentSelection tournament = new TournamentSelection();
        MockParameters mockParams = new MockParameters();
        mockParams.put("TOURNAMENT_SIZE", tournamentSize);
        GlobalParameters.setParameters(mockParams);
        
        IndividualComparator comparator = IndividualComparator.getComparator();
        
        counter = new FrequencyCounter<Individual>();
        for(int i = 0; i < numTournaments; i++) {
            List<Individual> winners = tournament.selectParents(individuals, comparator);
            for (Individual winner : winners) {
                counter.addItem(winner);
            }
        }
    }
    
    @Then("the expected distribution is $distribution")
    public void checkDistribution(String distribution) {
        Map<Individual, Double> expectedProportions = new HashMap<Individual, Double>();
        String[] lines = distribution.split("\n");
        for (int i=0; i<lines.length; ++i) {
            double proportion = Double.parseDouble(lines[i]);
            if (proportion > EPSILON) {
                expectedProportions.put(individuals.get(i), proportion);
            }
        }
        
        assertTrue(ChiSquaredStatistics.chiSquaredTest(counter, expectedProportions));
    }
    
    @AfterScenario
    public void clearGlobalSatInstance() {
        GlobalProblem.unsetProblem();
    }
}
