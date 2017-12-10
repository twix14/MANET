package server;

public class Coordinate {
	
	private int lat;
	private int lng;
	
	public Coordinate(String lat, String lng) {
		this.setLat(Integer.parseInt(lat));
		this.setLng(Integer.parseInt(lng));
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLng() {
		return lng;
	}

	public void setLng(int lng) {
		this.lng = lng;
	}
	
	public boolean checkDistance(Coordinate c1) {
		return Math.sqrt(Math.pow(c1.getLat() - lat, 2) 
				+ Math.pow(c1.getLng() - lng, 2)) > 200;
	}
	
	

}
