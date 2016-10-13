package hybridPetriNet.arcs;

import enums.ArcType;
import hybridPetriNet.places.Place;
import hybridPetriNet.transitions.Transition;

/**
 * A test arc uses it's weight as a test threshold; to avoid the declaration of
 * new attributes. The firing of a transition does nothing, as if the weight
 * was zero.
 */
public class TestArc extends Arc {

	public TestArc(Place place, Transition transition, String weight) {
		super(place, transition, weight);
		this.type = ArcType.TEST;
	}
	
	public TestArc(Place place, Transition transition) {
		this(place, transition, "-1.0");
	}

	public TestArc(Transition transition, Place place) {
		this(place, transition, "1.0");
	}
	
	/** 
	 * The test arc disabling function: tests if the markings in the
	 * place are SMALLER than the weight.
	 */
	public boolean thresholdDisablingFunction() {

		boolean disableTransition = false; 
		
		if  (this.place.getMarkings() < this.weight) {
			disableTransition = true;
		}
		
		return disableTransition; 
	}
	
	@Override
	public boolean finalDisablingFunction() {		
		boolean disableTransition = false;
		
		// in a test arc, the default disabling function is ignored, because
		// this arc does not change the markings of the associated place.
		
		if (this.thresholdDisablingFunction()){
			disableTransition = true;
		}
		
		// Override put here other disabling functions testing
		
		return disableTransition;
	}
	
	/**
	 * The order to fire from a test arc does nothing, as if the weight was zero.
	 */
	@Override
	public void fireTransition() {}

}
