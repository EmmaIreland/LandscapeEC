package locality;

import org.jbehave.scenario.Scenario;

public class ShellScenario extends Scenario {

	public ShellScenario() {
		super(new NeighborhoodSteps());
	}
}