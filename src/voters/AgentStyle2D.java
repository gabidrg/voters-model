package voters;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;

public class AgentStyle2D extends DefaultStyleOGL2D {
	
	@Override
	public Color getColor(Object o) {
		Agent agent = (Agent) o;
		if (agent.getChoice() == -1)	
			return Color.RED;
		else if ((agent.getChoice() == 1))
			return Color.BLUE;
		else if ((agent.getChoice() == 0))
			return Color.GREEN;
		return null;
	}
	
	@Override
	public float getScale(Object o) {
		return (float) 1;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
	    if (spatial == null) {
	    	spatial = shapeFactory.createCircle(6, 16);
	    }
	    return spatial;
	}

}
