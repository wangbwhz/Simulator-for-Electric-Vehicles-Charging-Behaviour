package main;

import java.awt.Point;

import com.graphhopper.GHResponse;

import mainAPI.Simulator;
import mainAPI.Strategy;

public class ShortestDistanceStrategy extends Strategy {
	
	@Override
	public int setChargingTime(Point generationPoint) {
		// TODO Auto-generated method stub
		return 30;
	}

	public String chooseDestination(Point generationPoint){
		String zoneID=super.getZoneID(generationPoint);
		int carNo=super.getCarNo(zoneID);//real time car number within the zone
//		System.out.println("Zone ID "+ super.getZoneID(generationPoint)+" Car No "+carNo);
		GHResponse shortestDistanceRes=null;
		String selectedStation=null;
		for (String myVal : Simulator.getStations().keySet()) {
			GHResponse res=super.getInfomration(generationPoint, myVal);
			if(res==null)
			{
				
			}
			else if(shortestDistanceRes==null)
			{
				double averageCapacity=super.getRouteCapacity(res);
				shortestDistanceRes=res;
				selectedStation=myVal;
				continue;
			}
			else
			{
				double averageCapacity=super.getRouteCapacity(res);
//				System.out.println(averageCapacity);

				if(res.getDistance()<shortestDistanceRes.getDistance())
				{
					shortestDistanceRes=res;
					selectedStation=myVal;
				}
			}
		}
		return selectedStation;
		
	}

}
