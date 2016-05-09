package mainAPI;

import java.awt.Point;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.GHResponse;


public class CarProductionLine {
	int lastGenerationTime;
	int producerSpeed;
	double latitude;
	double longitude;
	//Zones selected to randomly generate the car
	String[] zoneArray;

	//two types of generators
	int type;
	int noOfCarsGenerated;
	int expectedNo;
	int currentNo = 0;
	final int FIXED = 0;
	final int RANDOM = 1;
	final int RANDOMWITHSTRATEGY=2;
	Strategy strategy;
	int noWithinCircle;// when choose one pixel within a zone

	public CarProductionLine(int producerSpeed, int noOfCarsGenerated, int expectedNo, double latitude,
			double longitude) {
		this.noOfCarsGenerated = noOfCarsGenerated;
		this.expectedNo = expectedNo;
		this.lastGenerationTime = 0;
		this.latitude = latitude;
		this.longitude = longitude;

		this.producerSpeed = producerSpeed;
		this.type = FIXED;
	}

	public CarProductionLine(Strategy s,int producerSpeed, int noOfCarsGenerated, int expectedNo, String[] zoneArray)
	{
		this.noOfCarsGenerated = noOfCarsGenerated;
		this.lastGenerationTime = 0;
		this.producerSpeed = producerSpeed;
		this.zoneArray = zoneArray;

		this.type = RANDOM;
		this.expectedNo = expectedNo;
		this.strategy=s;
	}

	/**
	 * Put a car on the map
	 */
	public void setCar() {



		if(type ==RANDOM)
		{
			for (int i = 0; i < noOfCarsGenerated; i++) {
				Car c =generateRandomCar(zoneArray, Timer.getClock());
				Simulator.getStations().get(c.getDestination()).addMovingQueue(c);
				
			}
		}
		else if (type == FIXED) {
			Point source=Simulator.geoToCor(latitude, longitude);
			String destination=strategy.chooseDestination(source);

			for (int i = 0; i < noOfCarsGenerated; i++) {
				Car c = new Car(latitude, longitude, destination, Timer.getClock(), strategy.setChargingTime(source));
				c.saveRoute(c.getCarRoute());
				Simulator.getStations().get(c.getDestination()).addMovingQueue(c);
			}
		}

		currentNo += noOfCarsGenerated;

		Simulator.updateCarNolbl(noOfCarsGenerated);


	}
	/**
	 * Generate a car randomly within a zone
	 * 
	 * @param stationZoneNo
	 * @param destination
	 * @param generationTime
	 * @param chargingTime
	 * @return
	 */
	private  Car generateRandomCar(String[] stationZoneNoArray, int generationTime) {
		Boolean error = true;
		int errorCount=0;
		while (error) {
			Point source = CarFactory.randomPointWithinZone(stationZoneNoArray);
			String destination;			
			destination=strategy.chooseDestination(source);
					//sDs.chooseDestination(source);
			if(destination!=null)
			{
				error=false;
				Point des = Simulator.getStations().get(destination).getPoint();
				int chargingTime=strategy.setChargingTime(source);
				int y2 = (int) des.getY();
				int x2 = (int) des.getX();
				int x1 = (int) source.getX();
				int y1 = (int) source.getY();

				GeoPosition geo1 = Simulator.corToGeo(x1, y1);
				GeoPosition geo2 = Simulator.corToGeo(x2, y2);

				GHResponse res =Simulator.getRoute(geo1, geo2);
				Car c = new Car(geo1.getLatitude(), geo1.getLongitude(), destination, generationTime, chargingTime);
				c.saveRoute(res);
				return c;
			}
			errorCount++;
			//can not successfully get the station destination
			if(errorCount==10)
			{
				System.out.println("System Error From Car Production Line: Can not get the destination with current strategt");
			}
					


		}
		return null;
	}
	
	/**
	 * Check if the generation ends
	 * @return
	 */
	public Boolean isEnd() {
		// keep running
		if (currentNo == -1) {
			return false;
		}
		
		// car production for this production line is done
		else if (currentNo >= expectedNo) {
//			System.out.println("isEnd of car Production line is ");
//			System.out.print(currentNo >= expectedNo);

			return true;
		} else {
//			System.out.println("isEnd of car Production line is ");
//			System.out.print(currentNo >= expectedNo);
			return false;
		}

	}
	
	/**
	 * Generate a car 
	 */
	public void run() {
		if (Timer.getClock() - lastGenerationTime >= producerSpeed) {
			setCar();
			lastGenerationTime = Timer.getClock();
		}
	}
}
