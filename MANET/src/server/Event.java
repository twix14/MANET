package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;

public class Event implements Serializable{
	private static final long serialVersionUID = -962652137307409545L;

	private boolean join;
	private Peer peer;
	
	private EventType type;
	private int counter;
	private String message;
	private InetAddress connectTo;
	private int portConnectTo;

	public Event(String message) {
		this.message = message;
	}

	public Event() {

	}

	public static Event deserializeBA(byte[] bytes) throws IOException, ClassNotFoundException {
		try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
			try(ObjectInputStream o = new ObjectInputStream(b)){
				return (Event) o.readObject();
			}

		}
	}

	public static byte[] serializeBA(Event obj) throws IOException{
		try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
			try(ObjectOutputStream o = new ObjectOutputStream(b)){
				o.writeObject(obj);
			}
			return b.toByteArray();
		}
	}

	public boolean isJoin() {
		return join;
	}

	public void setJoin(boolean join) {
		this.join = join;
	}

	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InetAddress getConnectTo() {
		return connectTo;
	}

	public void setConnectTo(InetAddress connectTo) {
		this.connectTo = connectTo;
	}

	public int getPortConnectTo() {
		return portConnectTo;
	}

	public void setPortConnectTo(int portConnectTo) {
		this.portConnectTo = portConnectTo;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "[ TYPE: " + type + (isJoin()?"isJoin" + peer:"Isn'tJoin") + "\nMESSAGE: [ " + message + " ] \nIP: " + connectTo + "PORT: " + portConnectTo;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}