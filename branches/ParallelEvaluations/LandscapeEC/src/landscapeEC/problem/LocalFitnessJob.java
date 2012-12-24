package landscapeEC.problem;

import landscapeEC.locality.Location;
import landscapeEC.parameters.StringParameter;
import landscapeEC.problem.sat.SatEvaluator;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class LocalFitnessJob implements Kernel {
	
	private Problem problem;
	private Individual individual;
	private Location location;
	private Evaluator evaluator;
	private boolean isFitnessEffect;
	
	public LocalFitnessJob(Individual individual, Location location) {
		this.evaluator = GlobalProblem.getEvaluatorType();
		this.location = location;
		this.problem = location.getProblem();
		this.individual = individual;
		this.isFitnessEffect = "FITNESS".equals(StringParameter.REAPER_EFFECT.getValue());
	}

	@Override
	public void gpuMethod() {
		boolean satisfiesRequirements = evaluator.solvesSubProblem(individual, problem);
		
		if (isFitnessEffect) {
			if (!satisfiesRequirements) {
				individual.updateLocalFitness(problem.getDifficulty());
			}
			location.addToPendingIndividuals(individual);
		} else {
			if (satisfiesRequirements) {
				location.addToPendingIndividuals(individual);
			}
		}
	}
}