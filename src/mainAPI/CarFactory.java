package mainAPI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

/**
 * A integration of car generators
 * @author wangb
 *
 */
public class CarFactory {
	private static ArrayList<CarProductionLine> pSArray=null;
	public static ArrayList<CarProductionLine> getpSArray() {
		if(pSArray==null)
		{
			pSArray=new ArrayList<CarProductionLine>();

		}
		
		return pSArray;
	}
	public static void addFixedCarProductionLine(int producerSpeed, int noOfCarsGenerated, int expectedNo, double latitude,
			double longitude)
	{
		CarProductionLine cPL = new CarProductionLine(producerSpeed, noOfCarsGenerated, expectedNo, latitude, longitude);
		CarFactory.getpSArray().add(cPL);
	}
	public static void addRandomCarProductionLine(Strategy s,int producerSpeed, int noOfCarsGenerated, int expectedNo, String[] zone)
	{
		CarProductionLine cPL = new CarProductionLine(s,producerSpeed, noOfCarsGenerated, expectedNo, zone);
		CarFactory.getpSArray().add(cPL);
	}
			
	public static Boolean produce()
	{
		Boolean produce = false;
		for (int i = 0; i < pSArray.size(); i++) {
			CarProductionLine cPL = pSArray.get(i);
			if (!cPL.isEnd()) {
				CarFactory.getpSArray().get(i).run();
				produce = true;

			}

		}
		return produce;

	}
	/**
	 * randomly select the station from the station Array
	 * @param ranArray
	 * @return
	 */
	public static String randomStation(String[] stationArray) {
		String destination = null;

		if (stationArray.length == 1) {
			destination = stationArray[0];
		} else if (stationArray == null || stationArray.length == 0) {
			int randomIndex = Simulator.randInt(0, Simulator.getStations().size() - 1);
			int x = 0;

			for (String myVal : Simulator.getStations().keySet()) {
				if (x == randomIndex) {
					destination = myVal;
				}
				x++;
			}
		} else {
			int randomIndex = Simulator.randInt(0, stationArray.length - 1);
			destination = stationArray[randomIndex];

		}
		return destination;
	}
	/**
	 * get random points within the zone, can be further improved
	 * @param stationZoneIDArray
	 * @return
	 */
	public static Point randomPointWithinZone(String[] stationZoneIDArray) {
		ArrayList<Point> allPoints=new ArrayList<Point>();
		int randomSize=0;
		//put all the possible points together
		for(int i=0;i<stationZoneIDArray.length;i++)
		{
			int stationNo = Simulator.getStations().get(stationZoneIDArray[i]).getStationIndex();
			//get all pixel point within the zone
			ArrayList<Point> source = Zone.getPointCollection().get(stationNo);
			System.out.println("number of points within the zone is "+source.size());
			
			allPoints.addAll(source);
			randomSize+= Zone.getPointCollection().get(stationNo).size();
		}
		 
		Random randomno = new Random();
		int ranInt = randomno.nextInt(randomSize);

		return allPoints.get(ranInt);
	}
	

	
}
