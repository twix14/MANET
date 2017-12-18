package server;

public enum EventType {
	HOUSE_FIRE("House on Fire"),
	STREET_FIRE("Fire in the streets"),
	INJURY("Injured person"),
	BROKEN_CABLE_LINE("Coms line needs fixing"),
	CAR_STRANDED("Car surrounded by fire"),
	DAMSEL_IN_DISTRESS("There's a damnsel in distress");

	private final String text;
	
	EventType(final String s) { text = s; }

	@Override
	public String toString() {
		return text;
	}}