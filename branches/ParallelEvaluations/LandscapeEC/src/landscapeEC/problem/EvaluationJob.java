package landscapeEC.problem;

import landscapeEC.problem.sat.SatEvaluator;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class EvaluationJob implements Kernel {
	// TODO: Refactor this so that it can use other evaluator types besides SatEvaluator
	
	private Problem problem;
	private Individual individual;
	private SatEvaluator evaluator;
	
	public EvaluationJob(Problem problem, Individual individual) {
		this.evaluator = new SatEvaluator();
		this.problem = problem;
		this.individual = individual;
	}

	@Override
	public void gpuMethod() {
		double fitness = evaluator.evaluate(problem, individual);
		individual.setGlobalFitness(fitness);
	}
}