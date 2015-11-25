package voters;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class VotersModel implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {

		Parameters p = RunEnvironment.getInstance().getParameters();
		int numAgents = (Integer)p.getValue("initialNumAgents");
		int height = (Integer)p.getValue("worldHeight");
		int width = (Integer)p.getValue("worldWidth");
		int percentVotersA = (Integer)p.getValue("percentVotersA");
		int percentVotersB = (Integer)p.getValue("percentVotersB");
		int agentId = 0;
		
		// Create a new 2D torroidal, single occupancy grid on which the agents will live.
		final Grid<Object> grid = GridFactoryFinder
				.createGridFactory(null)
				.createGrid(
						"Grid", 
						context, 
						new GridBuilderParameters<Object>(
								new WrapAroundBorders(), 
								new SimpleGridAdder<Object>(), 
								true, 
								width, 
								height)
							);
		
		if ((percentVotersA > 0 && percentVotersB > 0) && (percentVotersA < 100 && percentVotersB < 100)) {
			int agentChoice = -1;
			int[] agentGroups = new int[3];
			agentGroups[0] = (numAgents * percentVotersA) / 100;
			agentGroups[1] = numAgents - ((numAgents * percentVotersA) / 100) - ((numAgents * percentVotersB) / 100);
			agentGroups[2] = (numAgents * percentVotersB) / 100;

			for (int k = 0; k < agentGroups.length; k++) {
				int l = 0;
				while (l < agentGroups[k]) {
					int x = RandomHelper.nextIntFromTo(0, width);
					int y = RandomHelper.nextIntFromTo(0, height);
					while (grid.getObjectAt(x, y) == null) {
						Agent agent = new Agent("Agent-" + agentId, agentChoice);
						context.add(agent);
						grid.moveTo(agent, x, y);
						l++;
						agentId++;
					}
				}
				agentChoice++;
			}
		}
		else {
			for (int i = 0; i < width; ++i) {
	            for (int j = 0; j < height; ++j) {
	            	final int agentChoice = RandomHelper.nextIntFromTo(-1, 1);
	            	Agent agent = new Agent("Agent-" + agentId, agentChoice);
	            	context.add(agent);
	            	agentId++;
                    grid.moveTo(agent, i, j);
	            }
			}
		}
		return context;
	}

}
