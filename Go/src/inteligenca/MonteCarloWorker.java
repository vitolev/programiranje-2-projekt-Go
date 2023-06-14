package inteligenca;

public class MonteCarloWorker implements Runnable{
	
	public static int simulationResult;
	public static int numRuns;
	
	private MonteCarloTreeNode node;
	
	public MonteCarloWorker(MonteCarloTreeNode node) {
		this.node = node;
	}
	
	@Override
	public void run() {
		int simulationResult = MonteCarlo.simulirajIgro(node.getIgra());
		numRuns++;
		MonteCarloWorker.simulationResult += simulationResult;
	}
}
