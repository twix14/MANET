package server;

public enum EventType {
	HOUSE_FIRE("House on fire"),
	STREET_FIRE("Fire in the streets"),
	INJURY("Injured people"),
	BROKEN_CABLE_LINE("Coms line needs fixing"),
	CARD_STRANDED("Cars surrounded by fire"),
	DAMSEL_IN_DISTRESS("There's some damsel in distress");

	private final String text;
	
	EventType(final String s) { text = s; }

	@Override
	public String toString() {
		return text;
	}}