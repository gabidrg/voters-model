package voters;

import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.AbstractGridQuery;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Agent {
	
	private String id;
	private int choice;
	private AbstractGridQuery<Object> neighborhood;
	private Long choosingA;
	private Long choosingB;
	private Long choosingAB;
	
	public Agent(String id, int choice) {
		this.id = id;
		this.choice = choice;
	}
	
	public String getId() {
		return id;
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}
	
	public Long getChoosingA() {
		return choosingA;
	}

	public Long getChoosingB() {
		return choosingB;
	}

	public Long getChoosingAB() {
		return choosingAB;
	}
	
	@ScheduledMethod(start = 0, interval = 1, priority = 1)
	public void step() {
		@SuppressWarnings("unchecked")
		Context<Object> context = (Context<Object>)ContextUtils.getContext(this);
		@SuppressWarnings("unchecked")
		Grid<Object> grid = (Grid<Object>)context.getProjection("Grid");	
		
		// Set query type, default is Moore neighborhood
		if (this.getQueryType() == 2) {
			this.neighborhood = getVonNeumannQuery(grid);
		}
		else {
			this.neighborhood = getMooreQuery(grid);
		}
		
		// compute majority in the neighborhood and make a choice
		int neighborhoodChoice = this.aggregateNeighborhoodMajorityChoice(this.neighborhood);
		int finalChoice = Integer.signum(this.getChoice() + neighborhoodChoice);
		this.setChoice(finalChoice);

	}
	
	private int getQueryType() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		return (int) p.getValue("queryType");
	}

	private MooreQuery<Object> getMooreQuery(Grid<Object> grid) {
		MooreQuery<Object> query = new MooreQuery<Object>(grid, this);
		return query;
	}
	
	private VNQuery<Object> getVonNeumannQuery(Grid<Object> grid) {
		VNQuery<Object> query = new VNQuery<Object>(grid, this);
		return query;
	}
	
	private int aggregateNeighborhoodMajorityChoice(AbstractGridQuery<Object> neighborhood) {
		int choices = 0;
		for (Object result : neighborhood.query()){
			Agent neighbor = isAgent(result);
			if (neighbor != null) {
				choices = choices + neighbor.getChoice();
			}
		}
		return Integer.signum(choices);
	}
	
	private Agent isAgent(Object object) {
        if (object instanceof Agent) {
        	return (Agent) object;
        }
        return null;
	}
	
	private void countGroups() {
		@SuppressWarnings("unchecked")
		Iterable<Agent> agents = RunState.getInstance().getMasterContext().getObjects(Agent.class);
		this.choosingA = StreamSupport.stream(agents.spliterator(), false).filter(this.choiceA).count();
		this.choosingB = StreamSupport.stream(agents.spliterator(), false).filter(this.choiceB).count();
		this.choosingAB = StreamSupport.stream(agents.spliterator(), false).filter(this.choiceAB).count();
	}
	
	public Predicate<Agent> choiceA = new Predicate<Agent>() {
	    @Override
	    public boolean test(Agent t) {
	        return t.getChoice() == -1;
	    }
	};
	
	public Predicate<Agent> choiceB = new Predicate<Agent>() {
	    @Override
	    public boolean test(Agent t) {
	        return t.getChoice() == 1;
	    }
	};
	
	public Predicate<Agent> choiceAB = new Predicate<Agent>() {
	    @Override
	    public boolean test(Agent t) {
	        return t.getChoice() == 0;
	    }
	};
}
