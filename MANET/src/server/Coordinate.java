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
		double radius = 6371000; // metres
		double lat1 = Math.toRadians(lat);
		double lat2 = Math.toRadians(c1.getLat());
		double lat = Math.toRadians(c1.getLat()- this.lat);
		double longt = Math.toRadians(c1.getLng() - this.lng);

		double a = Math.sin(lat/2) * Math.sin(lat/2) +
		        Math.cos(lat1) * Math.cos(lat2) *
		        Math.sin(longt/2) * Math.sin(longt/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		
		return radius * c < 200;
	}
	
	

}
