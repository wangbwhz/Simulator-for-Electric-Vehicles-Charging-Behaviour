package mainAPI;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * update the traffic information with the json file
 * @author wangb
 *
 */
public class TrafficUpdater {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final GraphHopper hopper;
	private String filepath;
	
	/**
	 * update the graphhoper API road speed according to the file imported to the simulator
	 * @param hopper
	 * @param filePath
	 */
	public TrafficUpdater(GraphHopper hopper,String filePath) {
		this.hopper = hopper;
		this.filepath=filePath;
	}
	public void updateTrafficInformation()
	{
		if(filepath!=null||filepath!="")
		{
			updateRoad(fetch(filepath));

		}

	}
	private void updateRoad(ArrayList<RoadEntry> data) {
		Graph graph = hopper.getGraphHopperStorage();


		FlagEncoder carEncoder = hopper.getEncodingManager().getEncoder("car");
		LocationIndex locationIndex = hopper.getLocationIndex();
		int errors = 0;
		int updates = 0;
		TIntHashSet edgeIds = new TIntHashSet(data.size());

		
		for (RoadEntry entry : data) {
			
			for(int j=0;j<entry.getPoints().size();j++)
			{

				GHPoint point=new GHPoint(entry.getPoints().getLatitude(j), entry.getPoints().getLongitude(j));
				QueryResult qr = locationIndex.findClosest(entry.getPoints().getLatitude(j), entry.getPoints().getLongitude(j), EdgeFilter.ALL_EDGES);
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
				
				
				double value = entry.getValue();
				if ("replace".equalsIgnoreCase(entry.getMode())) {

					if ("speed".equalsIgnoreCase(entry.getValueType())) {
						//get edge from the map
						EdgeIteratorState edge = graph.getEdgeIteratorState(edgeId, Integer.MIN_VALUE);
						//add the congestion level to the road 
						double oldSpeed = carEncoder.getSpeed(edge.getFlags());
						Double time=(60*60*edge.getDistance())/(1000*oldSpeed);
						Double time2=(60*60*edge.getDistance())/(1000*value);
						//can be used toe represent the congestion level if needed
						Double duration=time2-time;
						
						Color color=null;
						double percent=(oldSpeed-value)*100/oldSpeed;
//						System.out.println("Percent is "+percent);
						//the method to represent the congestion level
						if(percent<=10)
						{
							color=Simulator.colorLevel6;
						}
						else if(percent<=15)
						{
							color=Simulator.colorLevel5;

						}
						else if(percent<=25)
						{
							color=Simulator.colorLevel4;

						}
						else if(percent<=35)
						{
							color=Simulator.colorLevel3;

						}
						else if(percent<=50)
						{
							color=Simulator.colorLevel2;

						}
						else
						{
							color=Simulator.colorLevel1;

						}
						//draw the updated road with congestion level
						Simulator.updateRoutePainter(edge.fetchWayGeometry(edgeId), color);

						if (oldSpeed != value) {
							updates++;
							// TODO use different speed for the different directions
							// (see e.g. Bike2WeightFlagEncoder)
							logger.info("Speed change at " + entry.getId() + " (" + point + "). Old: " + oldSpeed + ", new:"
									+ value);
							System.out.println("Speed change at " + entry.getId() + " (" + point + "). Old: " + oldSpeed + ", new:"
									+ value);
							edge.setFlags(carEncoder.setSpeed(edge.getFlags(), value));
						}
					} else {
						throw new IllegalStateException("currently no other value type than 'speed' is supported");
					}
				} else {
					throw new IllegalStateException("currently no other mode than 'replace' is supported");
				}
			}

			}
			logger.info("Updated " + updates + " street elements of " + data.size() + ". Unchanged:"
				+ (data.size() - updates) + ", errors:" + errors);
	}

	
	/**
	 * fetch the json
	 * @param fileName path for the json file
	 * @return road data
	 */

	public ArrayList<RoadEntry> fetch(String fileName) {
		ArrayList<RoadEntry> data = new ArrayList<RoadEntry>();
		String text = "";
		try {
			text = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
//			System.out.println(text);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Reading Real Time Traffic Data Error");

			e.printStackTrace();
		}
		JSONArray arr = new JSONArray(text);
//		System.out.println(arr);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(i);
			String idStr = obj.getString("id");
			Double speed=Double.valueOf(obj.getInt("value"));
			JSONArray paths = obj.getJSONObject("geometry").getJSONArray("paths");
			for (int pathPointIndex = 0; pathPointIndex < paths.length(); pathPointIndex++) {
				PointList points = new PointList();
				JSONArray pathPoints = paths.getJSONArray(pathPointIndex);
				for (int pointIndex = 0; pointIndex < pathPoints.length(); pointIndex++) {
					JSONArray point = pathPoints.getJSONArray(pointIndex);
					points.add(new GHPoint(point.getDouble(0), point.getDouble(1)));
				}

				if (!points.isEmpty()) {
					data.add(new RoadEntry(idStr + "_" + pathPointIndex, points, speed, "speed", "replace"));
				}
			}
			
		}
		return data;

	}


}
