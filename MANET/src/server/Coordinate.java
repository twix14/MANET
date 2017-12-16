package server;

import java.io.Serializable;

public class Coordinate implements Serializable {
	private static final long serialVersionUID = -7888511295412357123L;
	
	private double lat;
	private double lng;
	
	public Coordinate(String lat, String lng) {
		this.setLat(Double.parseDouble(lat));
		this.setLng(Double.parseDouble(lng));
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double  lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public boolean checkDistance(Coordinate c1) {
		return Math.sqrt(Math.pow(c1.getLat() - lat, 2) 
				+ Math.pow(c1.getLng() - lng, 2)) < 200;
	}
	
	

}
