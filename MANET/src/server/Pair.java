package server;

public class Pair {
	
	private Peer peer;
	private boolean isAlive;
	
	public Pair(Peer t1, boolean t2) {
		this.setPeer(t1);
		this.setAlive(t2);
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
	        return false;
		
	    final Peer other = ((Pair) obj).getPeer();
	    
	    if (this.getPeer().getPort() != other.getPort())
	        return false;
	    
	    if (this.getPeer().getIp().getHostAddress() != other.getIp().getHostAddress()) 
	        return false;
	    
	    return true;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

}
