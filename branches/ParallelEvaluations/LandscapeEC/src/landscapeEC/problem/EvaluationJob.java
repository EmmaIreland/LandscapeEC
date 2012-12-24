package landscapeEC.problem;

import landscapeEC.problem.sat.SatEvaluator;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class EvaluationJob implements Kernel {
	
	private Problem problem;
	private Individual individual;
	private Evaluator evaluator;
	
	public EvaluationJob(Problem problem, Individual individual) {
		this.evaluator = GlobalProblem.getEvaluatorType();
		this.problem = problem;
		this.individual = individual;
	}

	@Override
	public void gpuMethod() {
		double fitness = evaluator.evaluate(problem, individual);
		individual.setGlobalFitness(fitness);
	}
}