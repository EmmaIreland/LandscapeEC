package landscapeEC.problem;

import java.util.ArrayList;
import java.util.List;

import landscapeEC.locality.Location;
import landscapeEC.problem.sat.Clause;
import landscapeEC.problem.sat.SatEvaluator;
import landscapeEC.problem.sat.SatInstance;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;

public class ParallelEvaluator {
    private int numEvaluations = 0;
    private int numResets = 0;
    
    private Rootbeer rootbeer;
    private List<Kernel> currentJobs;

    public ParallelEvaluator() {
    	this.rootbeer = new Rootbeer();
    	this.currentJobs = new ArrayList<Kernel>();
    }
    
    public void addEvalJobs(List<Individual> individuals) {
    	numEvaluations += individuals.size(); //Update eval counter
    	for (Individual individual : individuals) {
    		this.addEvalJob(individual);
    	}
    }
    
    public void addEvalJob(Individual individual) {
    	EvaluationJob evalJob = new EvaluationJob(GlobalProblem.getProblem(), individual);
    	currentJobs.add(evalJob);
    }
    
	public void addLocalFitnessJob(Individual individual, Location location) {
		LocalFitnessJob evalJob = new LocalFitnessJob(individual, location);
		currentJobs.add(evalJob);
	}
	
	 public void addLocalFitnessJobs(List<Individual> individuals, Location location) {
	    	for (Individual individual : individuals) {
	    		this.addLocalFitnessJob(individual, location);
	    	}
	    }
	
	public void runPendingEvaluations() {
		rootbeer.runAll(currentJobs);
		currentJobs.clear();
	}
	
	public String getResultString(Individual individual) {
		return getResultString(GlobalProblem.getProblem(), individual);
	}
	
	public String getResultString(Problem problem, Individual individual) {
		return GlobalProblem.getEvaluatorType().getResultString(problem, individual);
	}

	public int getNumEvaluations() {
		return numEvaluations;
	}

	public void resetEvaluationsCounter() {
		numEvaluations = 0;
		numResets++;
	}
	
	public int getNumResets() {
        return numResets;
    }
}
