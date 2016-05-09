package mainAPI;
import java.awt.Point;

import org.jxmapviewer.viewer.GeoPosition;
import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;

public class Car {
	static final long serialVersionUID = 1L; // assign a long value
	private PointList carRoute;
	private double latitude;
	private double longtitude;
	private double x;
	private double y;
	private String ID;
	private int status;

	private static int carCount = 0;
	private static int DEFAULT = 0;
	private static int CHARGINGALR = 1;
	private String destination;	

	private long startTime;
	private Point endP;
	private Point prevP;

	private int chargingBeginTIme;//same as start time but is rounded
	private int chargingTime;
	private int departureTime;
	private double movingVelocity;
	
	private double[] disRoute;
	private double movingTime;
	private int generationTime = 0;
	
	private int arrivalTime;

	public int getGenerationTime() {
		return generationTime;
	}
	public double getLatitude() {
		return latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String getDestination() {
		return destination;
	}

	
	public String getID() {
		return ID;
	}

	
	public int getArrivalTime() {
		return arrivalTime;
	}

	public int getChargingBeginTIme() {
		return chargingBeginTIme;
	}

	public int getChargingTime() {
		return chargingTime;
	}

	public int getDepartureTime() {
		return departureTime;
	}

	public GeoPosition getGeoLoc() {
		return new GeoPosition(latitude, longtitude);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	

	public long getcStartTime() {
		return startTime;
	}

	public void setcStartTime(int cStartTime) {
		this.startTime = cStartTime;
	}






	/**
	 * Convert the route to the point on the map
	 * @param pl
	 */
	public void routeConverstion(PointList pl) {
		disRoute = new double[pl.size()];
		disRoute[0] = 0;
		Point prev = prevP;
		Double endlat;
		Double endlon;
		Double prevDis = 0.0;
		for (int i = 1; i < pl.size(); i++) {
			endlat = carRoute.getLatitude(i);
			endlon = carRoute.getLongitude(i);
			Point next = Simulator.geoToCor(endlat, endlon);

			disRoute[i] = (getDistance(prev, next)) + prevDis;
			prevDis = disRoute[i];
			prev = next;

		}
		movingVelocity = disRoute[pl.size() - 1] / movingTime;
	}

	
	public Car(double latitude, double longtitude, String destination, int generationTime,int chargingTime) {
		this.ID = String.valueOf(carCount);

		Point p = Simulator.geoToCor(latitude, longtitude);
		this.generationTime=generationTime;

		this.latitude = latitude;
		this.longtitude = longtitude;
		this.chargingTime = chargingTime;
		x = (int) p.getX();
		y = (int) p.getY();
		this.destination = destination;
		status = DEFAULT;
		carCount++;
		Simulator.getStations().get(destination).incChargingCarNo();


	}
	

	public Car(String ID, double latitude, double longtitude, String destination, int generationTime,int chargingTime) {
		Point p = Simulator.geoToCor(latitude, longtitude);
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.chargingTime = chargingTime;
		this.generationTime=generationTime;
		this.ID = ID;
		x = (int) p.getX();
		y = (int) p.getY();
		this.destination = destination;
		status = DEFAULT;
		carCount++;
		System.out.println("Car ID is "+ID);
		//inc the number of cars will be charged in the station
		Simulator.getStations().get(destination).incChargingCarNo();

	}
	/**
	 * Using Graphhoper API to get route
	 * @return
	 */
	public GHResponse getCarRoute() {
		StationPoint desStation = Simulator.getStations().get(destination); 															
		GeoPosition startGeoPostion = new GeoPosition(latitude, longtitude);
		GeoPosition endGeoPosition = desStation.getPosition();
		GHResponse res = Simulator.getRoute(startGeoPostion, endGeoPosition);
		return res;

	}
	/*
	 * Save the route result to the car
	 */
	public void saveRoute(GHResponse res)
	{
		carRoute = res.getPoints();
		long millis = res.getTime();
		movingTime = millis / 1000 / 60;
		latitude = carRoute.getLatitude(0);
		longtitude = carRoute.getLongitude(0);
		prevP = Simulator.geoToCor(latitude, longtitude);
		x = (int) prevP.getX();
		y = (int) prevP.getY();
		arrivalTime = (int) (generationTime + millis / 1000 / 60);
		routeConverstion(carRoute);
	}
	
	public boolean checkReachStation() {

			if (Math.abs(Timer.getPreciseClock() - arrivalTime)<=0.1)
			{
				return true;
			}
			else
			{
				return false;
			}

	}

	/**
	 * Move along the route
	 */
	public void update() {
		double dist = 0.0;
		if (carRoute.size() >1) {
			if (status == DEFAULT) {
				double currentClock = Timer.getPreciseClock();
				double time = currentClock - generationTime;
				dist = time * movingVelocity;
				int index = -1;
				for (int i = 0; i < disRoute.length; i++) {
					if (dist <= disRoute[i]) {
						index = i;
						break;

					}

				}
				if (disRoute.length == 0) {
					System.out.println("The route length is 0");

				}
				if (index == -1) {
//					System.out.println(
//							"Warning! The car reaches station immediately with the setting of time mapping,so that it can not be seen ");
				}
				if (index != 0 && index != -1) {
					Double endlat = carRoute.getLatitude(index);
					Double endlon = carRoute.getLongitude(index);

					Double prelat = carRoute.getLatitude(index - 1);
					Double prelon = carRoute.getLongitude(index - 1);

					endP = Simulator.geoToCor(endlat, endlon);
					prevP = Simulator.geoToCor(prelat, prelon);

					int deltaX = (int) (endP.getX() - prevP.getX());
					int deltaY = (int) (endP.getY() - prevP.getY());
					double direction = Math.atan2(deltaY, deltaX);

					x = (prevP.getX() + ((dist - disRoute[index - 1]) * Math.cos(direction)));
					y = (prevP.getY() + ((dist - disRoute[index - 1]) * Math.sin(direction)));

					longtitude = Simulator.corToGeo((int) x, (int) y).getLongitude();
					latitude = Simulator.corToGeo((int) x, (int) y).getLatitude();
				}

			}
		}
	}

	private double getDistance(Point a, Point b) {
		double x1 = a.getX();
		double x2 = b.getX();
		double y1 = a.getY();
		double y2 = b.getY();
		double dis = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		return dis;

	}



	public void startCharging()
	{
		chargingBeginTIme = Timer.getClock();
		startTime = System.currentTimeMillis();
	}
	public Boolean checkCompeleteCharging()
	{
		if (Timer.getClock() - chargingBeginTIme >= chargingTime)
		{
			departureTime = Timer.getClock();
			status = CHARGINGALR;
			return true;

		}
		else{
			
			return false;
			}
	}
}
