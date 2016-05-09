package mainAPI;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map.Entry;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import gnu.trove.set.hash.TIntHashSet;

import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.GHResponse;

/**
 * the strategy used to 
 * @author wangb
 *
 */
public abstract class Strategy {
	
/**
 * Set charging time for the car
 * @param generationPoint the point car generated
 * @return charging time
 */
	public abstract int setChargingTime(Point generationPoint);
	/**
	 * Choose the destination station to charge the car
	 * @param generationPoint the point car generated
	 * @return the ID of the destination(a Charging station ID)
	 */
	public abstract String chooseDestination(Point generationPoint);
	/**
	 * 
	 * @param generationPoint a point
	 * @return GeoPosition
	 */
	public GeoPosition getPointGelocation(Point generationPoint)
	{
		return Simulator.corToGeo((int)generationPoint.getX(), (int)generationPoint.getY());
	}
	/**
	 * Get the zone id for the point on the map
	 * @param generationPoint point on the map(x and y coordinate, left bottom is 0,0)
	 * @return Zone ID(a station ID string)
	 */
	public String getZoneID(Point generationPoint)
	{
		int zoneIndex=-1;
		for(int i=0;i<Zone.getPointCollection().size();i++)
		{
			if(Zone.getPointCollection().get(i).contains(generationPoint))
			{
				zoneIndex=i;
//				System.out.println("Generation Point "+generationPoint);
//				System.out.println("Zone Index "+zoneIndex);
				break;

			}
			

		}
		if(zoneIndex==-1)
		{
			System.out.println("Strategy Class: Point can not be founded on the map "+generationPoint);
			return null;
		}
		Iterator<Entry<String, StationPoint>> it1 = Simulator.getStations().entrySet().iterator();
		while (it1.hasNext()) {
			Entry<String, StationPoint> pair = it1.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			StationPoint s = (StationPoint) pair.getValue();
			if(zoneIndex==s.getStationIndex())
			{
				return pair.getKey();
			}
		}
		System.out.println("Strategy Class: Point can not be founded on the map");

		return null;
		
	}
	/**
	 * get number of cars within the zone(at the time function calls)
	 * @param zoneID a stationID 
	 * @return number of cars
	 */
	public int getCarNo(String zoneID)
	{
		if(Simulator.getStations().get(zoneID).movingQueue!=null)
		{
			return Simulator.getStations().get(zoneID).movingQueue.size();

		}
		return 0;
	}
	/**
	 * get route information such as distance, time from generation point to the charging station
	 * @param generationPoint point on the map that car generate
	 * @param chargingStation ID of charging station
	 * @return GHResponse Graphhoper API
	 */
	public GHResponse getInfomration(Point generationPoint,String chargingStation)
	{
		Point des = Simulator.getStations().get(chargingStation).getPoint();

		int y2 = (int) des.getY();
		int x2 = (int) des.getX();
		int x1 = (int) generationPoint.getX();
		int y1 = (int) generationPoint.getY();

		GeoPosition geo1 = Simulator.corToGeo(x1, y1);
		GeoPosition geo2 = Simulator.corToGeo(x2, y2);

		GHResponse res =Simulator.getRoute(geo1, geo2);
		if (res == null) {
			System.out.println("Strategy Class: Return null res!Destination Staion is "+chargingStation+" Genaeration Point is "+geo1);
			return null;
		} else {
			return res;
			
		}
	}
	/**
	 * Get Capacity of the route(veh/h)
	 * @param res response
	 * @return average capacity
	 */
	public double getRouteCapacity(GHResponse res)
	{
		Graph graph = Simulator.getHopper().getGraphHopperStorage();
		
		FlagEncoder carEncoder =  Simulator.getHopper().getEncodingManager().getEncoder("car");
		LocationIndex locationIndex =  Simulator.getHopper().getLocationIndex();
		int errors = 0;
		int updates = 0;
		TIntHashSet edgeIds = new TIntHashSet(res.getPoints().size());
		double totalCapacity = 0;
			for(int j=0;j<res.getPoints().size();j++)
			{

				GHPoint point=new GHPoint(res.getPoints().getLatitude(j), res.getPoints().getLongitude(j));
				QueryResult qr = locationIndex.findClosest(res.getPoints().getLatitude(j), res.getPoints().getLongitude(j), EdgeFilter.ALL_EDGES);
				int edgeId = qr.getClosestEdge().getEdge();


				if (!qr.isValid()) {
					errors++;
					continue;
				}
				if (edgeIds.contains(edgeId)) {
//					errors++;
					continue;
				}
				edgeIds.add(edgeId);
				EdgeIteratorState edge = graph.getEdgeIteratorState(edgeId, Integer.MIN_VALUE);
				double speed = carEncoder.getSpeed(edge.getFlags());
				totalCapacity+=speedToCapacity(speed);
				
			}
			double averageCapacity=totalCapacity/edgeIds.size();
			return averageCapacity ;

	}
	/**
	 * Change speed
	 * @param speed speed for that car
	 * @return capacity
	 */
	private  double speedToCapacity(double speed)
	{
		double capacity;
		if(speed<=15)
		{
			capacity=300;
			
		}
		else if(speed<=30)
		{
			capacity=600;
		}
		else if(speed<=50)
		{
			capacity=1000;
		}
		else if(speed<=60)
		{
			capacity=1500;
		}
		else
			
		{
			capacity=2000;

		}
		return capacity;
		
	}
	
}
