package server;

import java.util.List;

public class PubSub {

	private List<EventType> subscriptions;
	private List<Event> publishings;
	
	public List<EventType> getSubscriptions() {
		return subscriptions;
	}
	public void setSubscriptions(List<EventType> subscriptions) {
		this.subscriptions = subscriptions;
	}
	public List<Event> getPublishings() {
		return publishings;
	}
	public void setPublishings(List<Event> publishings) {
		this.publishings = publishings;
	}
	public static void printSub(EventType e) {
		System.out.println(e);
	}
	public static void printPub(Event e) {
		System.out.println(e);
	}	
}