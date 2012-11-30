package landscapeEC.problem;

import java.util.ArrayList;
import java.util.List;

import landscapeEC.problem.sat.SatEvaluator;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;

public class ParallelEvaluator extends SatEvaluator {
    private int numEvaluations = 0;
    private int numResets = 0;
    
    private Rootbeer rootbeer;
    private List<Kernel> currentJobs;
    
    private SatEvaluator satEval;

    public ParallelEvaluator() {
    	this.rootbeer = new Rootbeer();
    	this.currentJobs = new ArrayList<Kernel>();
    }
    
    public void addEvalJobs(List<Individual> individuals) {
    	for (Individual individual : individuals) {
    		this.addEvalJob(individual);
    	}
    }
    
    public void addEvalJob(Individual individual) {
    	EvaluationJob evalJob = new EvaluationJob(GlobalProblem.getProblem(), individual);
    	currentJobs.add(evalJob);
    }
    
	public void addJob(Problem problem) {
	    //To be implemented for Drac. Reaper
	}
	
	public void runPendingEvaluations() {
		numEvaluations += currentJobs.size();
		rootbeer.runAll(currentJobs);
		currentJobs.clear();
	}
}
