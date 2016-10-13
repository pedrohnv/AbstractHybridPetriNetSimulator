package hybridPetriNet.arcs;

import enums.ArcType;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.Transition;

public class InhibitorArc extends TestArc {

	public InhibitorArc(Place place, Transition transition, String weight) {
		super(place, transition, weight);
		this.type = ArcType.INHIBITOR;
	}
	
	public InhibitorArc(Place place, Transition transition) {
		this(place, transition, "-1.0");
	}

	public InhibitorArc(Transition transition, Place place) {
		this(place, transition, "1.0");
	}

	/** 
	 * The test arc disabling function: tests if the markings in the
	 * place are EQUAL or GREATER than the weight.
	 */
	public boolean thresholdDisablingFunction() {

		boolean disableTransition = false; 
		
		if  (this.place.getMarkings() >= this.weight) {
			disableTransition = true;
		}
		
		return disableTransition; 
	}

}
