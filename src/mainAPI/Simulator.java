package mainAPI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;

/**
 * This class implements the simulator including the map, setting panel and
 * information pop up panel
 * 
 * @author Wang Bowen
 * @version 1.0
 */
public class Simulator extends JFrame {


	
	private static final long serialVersionUID = 1L;
	
	private static ArrayList<PopUpPanel> popUpStationPanel;
	private static JLayeredPane layer;
	private static Timer AnimationTimer; // Timer for control of the animation
											// rate

	private static Map<String, StationPoint> stations = new HashMap<String, StationPoint>();
	// private static List<StationPoint> stations;
	private static JXMapViewer mapViewer;
	private static JPanel mainlayer;
	public static int stationIndexCount = 0;
	private static boolean zoneDivision = false;
	// painter related
	private static WaypointPainter<StationPoint> stationpointPainter;
	private static List<Painter<JXMapViewer>> painters;
	private static RoutePainter routePainter;
	private static ZonePainter zonePainter;
	private static int carSize = 0;
	//color used to represent the congestion level
	public static final Color colorLevel6 = new Color(205,66,56);
	public static final Color colorLevel5 = new Color(214,93,14);
	public static final Color colorLevel4 = new Color(139,139,18);
	public static final Color colorLevel3 = new Color(153,154,205);
	public static final Color colorLevel2 = new Color(95,163,221);
	public static final Color colorLevel1 = new Color(92,213,116);
	/**
	 * Get the current number of cars generated
	 * @return number of cars generated
	 */
	public static int getCarSize() {
		return carSize;
	}
	/**
	 * Set the current number cars generated
	 * @param noOfCars number of cars
	 */
	public static void setCarSize(int noOfCars) {
		carSize = noOfCars;
	}
	
	private static GraphHopper hopper;
	private static Report report;
	private static ArrayList<Car> CarArray = (ArrayList<Car>) new ArrayList<Car>();
	
	/**
	 * Update the information on the right of simulator(Map center latitude, Map center Longitude)
	 */
	public static void updateGeoCenter()
	{
		SettingPanel.setLat(String.valueOf(Simulator.getCenter().getLatitude()));
		SettingPanel.setLong(String.valueOf(Simulator.getCenter().getLongitude()));
	}
	/**
	 * Update information on the right of simulator(Map zoom level)
	 */
	public static void updateZoomlbl()
	{
		SettingPanel.setZoom(String.valueOf(Simulator.getZoomLevel()));
	}
	/**
	 * Update the information on the right of simulator(Number of cars generated)
	 * @param noOfCarsGenerated number of cars generated
	 */
	public static void updateCarNolbl(int noOfCarsGenerated)
	{
		Simulator.setCarSize(Simulator.getCarSize() + noOfCarsGenerated);
		SettingPanel.setCarNo(String.valueOf(Simulator.getCarSize()));
	}
	/**
	 * Get current zoom level of the map
	 * @return zoom level
	 */
	public static int getZoomLevel() {
		return mapViewer.getZoom();
	}
	/**
	 * Get center point of the map 
	 * @return center point
	 */
	public static GeoPosition getCenter() {
		return mapViewer.getCenterPosition();
	}
	private static String osm_file_name;
	private static String traffic_file_name;

	/**
	 * Set OSM data for offline Routing service
	 * 
	 * @param file_name
	 *            The path for each file name
	 */
	public void setMapOSM(String file_name) {
		osm_file_name = System.getProperty("user.dir") + "\\" + file_name;

	}
/**
 * Set Stations through excel file
 * @param file_name file name of excel file
 */
	public void setStations(String file_name)
	{
		ReadExcelFile.readXLSFile("import\\s.xlsx",this);

	}
/**
 * Set the path for the imported json File
 * @param file_name the file Name of Json file, the default to directory is the source code folder
 */
	public void integrateTraffic(String file_name) {
		traffic_file_name = System.getProperty("user.dir") + "\\" + file_name;

	}
	private void initStation()
	{
		
	}
/**
 * initialize the Graphhoper
 */
	private void initHopper() {
		// Point point = entry.getPoints().get(0);

		// System.out.println(file_name); // create one GraphHopper instance
		hopper = new GraphHopper().forServer();
		hopper.setOSMFile(osm_file_name);
		// where to store graphhopper files?
		hopper.setGraphHopperLocation(System.getProperty("user.dir") + "\\" + "mapData" + "\\");
		hopper.setEncodingManager(new EncodingManager("car"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();
	}
/**
 * Get the route between two geographic locations it will return null, if the route can not be found.According to the GHResponse, the distance,time,instructions,points can be achieved
 * @param g1 one geographic location
 * @param g2 another geographic location
 * @return GHResponse
 */
	public static GHResponse getRoute(GeoPosition g1, GeoPosition g2) {

		GHRequest req = new GHRequest(g1.getLatitude(), g1.getLongitude(), g2.getLatitude(), g2.getLongitude())
				.setWeighting("fastest").setVehicle("car").setLocale(Locale.ENGLISH);
		GHResponse rsp = hopper.route(req);
		if (rsp.hasErrors()) {
			// handle them!
			System.out.println("Error" + rsp.getErrors());
			return null;
		} else {
			return rsp;
		}		
	}


/**
 * Get all the stations in the simulator
 * @return stations array(using the station ID to map each station)
 */
	public static Map<String, StationPoint> getStations() {
		return stations;
	}
/**
 * Get JXMapViewer object
 * @return JXMapViewer
 */
	public static JXMapViewer getMapViewer() {
		return mapViewer;
	}
/**
 * Get station index count,it is total number of stations -1,used to identify the pop up panel for each station
 * @return station index count
 */
	public static int getStationIndexCount() {
		return stationIndexCount;
	}

/**
 * Get all the station information panel
 * @return station information panel array
 */
	public static ArrayList<PopUpPanel> getPopUpStationPanel() {
		return popUpStationPanel;
	}
/**
 * Draw the road with certain color
 * @param posList all the points along the road
 * @param color color used to draw
 */
	public static void updateRoutePainter(PointList posList,Color color)
	{
		routePainter.addRoute(posList, color);
	}



	protected static Report getReport() {
		return report;
	}

	/**
	 * Map the real time to the simulator time ,TimeMapping(1,900)means 1 minute
	 * in real =20 milliseconds for simulator
	 * 
	 * @param realTime
	 *            real time in minutes
	 * @param simulatorTime
	 *            simulation time in milliseconds
	 */
	public void setTimeMapping(double realTime, double simulatorTime) {
		Timer.setTimeMapping(realTime, simulatorTime);
	}

	/**
	 * Initialize the simulator
	 * 
	 * @param latitude
	 *            the latitude of the center of the map
	 * @param longtitude
	 *            the longitude of the center of map
	 * @param zoomLevel
	 *            the zoom level of map (0-19 the smaller, the nearer, proper
	 *            value is 6)
	 */
	public Simulator(double latitude, double longtitude, int zoomLevel) {

		// popupstaionPanel
		popUpStationPanel = new ArrayList<PopUpPanel>();

		// Setup JXMapViewer
		mapViewer = new JXMapViewer();

		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		mapViewer.setTileFactory(tileFactory);

		// Use 8 threads in parallel to load the tiles
		tileFactory.setThreadPoolSize(8);

		GeoPosition ntuHall = new GeoPosition(latitude, longtitude);

		mapViewer.setZoom(zoomLevel);
		mapViewer.setAddressLocation(ntuHall);
		mapViewer.addMouseWheelListener(new ZoomWheelListener(mapViewer));
		// Add interactions
		MapPanMouseInputListener mia = new MapPanMouseInputListener(mapViewer);
		mapViewer.addMouseListener(mia);
		mapViewer.addMouseMotionListener(mia);

	

		this.setTitle("Charging Staion Simulator"); // Display the viewer in a
													// JFrame

		// frame.getContentPane().add(mapViewer);
		layer = new JLayeredPane();
		layer.setLayout(new OverlayLayout(layer));
		mainlayer = new JPanel();
		mainlayer.setLayout(new BorderLayout());
		mainlayer.add(mapViewer);
		mainlayer.add("East", new SettingPanel());
		layer.add(mainlayer, new Integer(1));
		painters = new ArrayList<Painter<JXMapViewer>>();

		this.add(layer);

		stationpointPainter = new StaionCarOverlayPainter();
		int iTimerInterval = 40; // 33 ms is 30 frames per sec (TV rate)
		AnimationTimer = new Timer(iTimerInterval); // 40 ms is 25
		report = new Report();
		SettingPanel.disableStartSimulatorbtn();
		SettingPanel.disablePausebtn();

	}

	/**
	 * Running repeatedly
	 */
	protected static void onTimer() // Called by the timer
	{
		Boolean production=true;

		if(!Simulator.generateCar())//produce successfully
		{
			production=false;
		}
		//check if all the station ends
		Boolean isEnd = true;
		Iterator<Entry<String, StationPoint>> it = stations.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, StationPoint> pair = it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			StationPoint s = (StationPoint) pair.getValue();
			if (!s.isEnd())//car will be charged
			{
				isEnd = false;
				s.updateBQ();
				s.checkReachStation();
				s.checkChargingComplete();
				s.processCharging();
				
				//all the cars have been generated, check if the station is end or not
				if (!production) {
					s.runningEnd();
				}
				s.updateWQLTOLE();
				s.updateChargingTime();

			}

		}

		SettingPanel.setRunningTime(String.valueOf(Timer.getClock()));
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
		//all the station ends of running
		if (isEnd) {
			// save all the station data to the report
			Iterator<Entry<String, StationPoint>> it1 = stations.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<String, StationPoint> pair = it1.next();
				// System.out.println(pair.getKey() + " = " + pair.getValue());
				StationPoint s = (StationPoint) pair.getValue();
				Report.saveStationData(s);

			}
			SettingPanel.enableReportbtn();
			SettingPanel.disablePausebtn();
			Timer.setStatus(false);
		}
	}

	/**
	 * Convert corresponding position on the map to geographic location
	 * 
	 * @param x
	 *            x coordinate on the map
	 * @param y
	 *            y coordinate on the map
	 * @return GeoPostion
	 */
	public static GeoPosition corToGeo(int x, int y) {
		Rectangle rectangle = mapViewer.getViewportBounds();
		int pixelX = (int) (x + rectangle.getX());
		int pixelY = (int) (y + rectangle.getY());
		GeoPosition geo = mapViewer.getTileFactory().pixelToGeo(new Point(pixelX, pixelY), mapViewer.getZoom());
		return geo;
	}

	/**
	 * Convert geographic location to the corresponding position on the map
	 * 
	 * @param latitude
	 *            latitude of a position
	 * @param longitude
	 *            longitude of a position
	 * @return point on the map
	 */
	public static Point geoToCor(double latitude, double longitude) {
		GeoPosition geo = new GeoPosition(latitude, longitude);
		Point2D point = mapViewer.getTileFactory().geoToPixel(geo, mapViewer.getZoom());
		Rectangle rectangle = mapViewer.getViewportBounds();
		int x = (int) (point.getX() - rectangle.getX());
		int y = (int) (point.getY() - rectangle.getY());

		return new Point(x, y);
	}

	/**
	 * Draw all the components on the map and refresh
	 */
	public void draw() {
		initHopper();
		// add car painter
		// station drawing
		Set<StationPoint> stationPoints = new HashSet<StationPoint>(stations.values());
		stationpointPainter.setWaypoints(stationPoints);
		for (StationPoint w : stationPoints) {
			mapViewer.add(w.getButton());
		}
		routePainter = new RoutePainter();

		TrafficUpdater tU = new TrafficUpdater(hopper, traffic_file_name);
		tU.updateTrafficInformation();

		// mapViewer.add(sp.getButton());
		painters.add(routePainter);
		painters.add(stationpointPainter);
		generateZone();
		// put the car into the car array of each destination
		initializeCar();
		CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
		mapViewer.setOverlayPainter(painter);
		mainlayer.validate();
		SettingPanel.enableStartSimulatorbtn();
		

	}



	

	/**
	 * Add a car on the map
	 * 
	 * @param ID
	 *            the ID of the car,if it is not set, if will defined by a index
	 *            number
	 * @param latitude
	 *            the latitude of the car
	 * @param longitude
	 *            the longitude of the car
	 * @param destination
	 *            the ID of the station selected to charge
	 * @param generationTime
	 *            the time that car generated
	 * @param chargingTIme
	 *            the time for car to charge in station defined in destination
	 */
	public void addCar(String ID, double latitude, double longitude, String destination, int generationTime,
			int chargingTIme) // Called

	{

		CarArray.add(new Car(ID, latitude, longitude, destination, generationTime, chargingTIme));

	}

	/**
	 * Divide the map into several zones
	 */
	protected static void generateZone() {

		// remove the zonePainter if exists
		if (zonePainter != null) {
			painters.remove(zonePainter);
		}
		if (zoneDivision) {
			zonePainter = new ZonePainter();
			Point[] points = new Point[stations.size()];
			Iterator<Entry<String, StationPoint>> it = stations.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, StationPoint> pair = it.next();
				StationPoint s = (StationPoint) pair.getValue();
				points[s.getStationIndex() ] = s.getPoint();
			}

			Zone.setThiessenPolygon(points);
			painters.add(zonePainter);
			zoneDivision = false;
		}
	}

	
	/**
	 * add station Description : Add a station on the map
	 * 
	 * @param latitude
	 *            the latitude of the station
	 * @param longitude
	 *            the longitude of the station
	 * @param stationID
	 *            the ID of the station(Unique)
	 * @param capacity
	 *            the number of cars can be charged at same time
	 */
	public void addStation(double latitude, double longitude, String stationID, int capacity) {
		GeoPosition geo = new GeoPosition(latitude, longitude);
		StationPoint sp = new StationPoint(stationID, capacity, geo);
		stations.put(stationID, sp);
		zoneDivision = true;
		stationIndexCount++;
	}

	
	/**
	 * 
	 * @param x
	 *            The x coordinate of the station added on the map
	 * @param y
	 *            The y coordinate of the station added on the map
	 * @param stationID
	 *            The ID of station
	 * @param capacity
	 *            The number of cars can be charged at same for the station
	 */
	public static void addStation(int x, int y, String stationID, int capacity) {

		// x=x+StationPoint.width/2;
		// y=y+StationPoint.height/2;

		GeoPosition geo = corToGeo(x, y);
		StationPoint sp = new StationPoint(stationID, capacity, geo);
		stations.put(stationID, sp);

		stationIndexCount++;

	}

	/**
	 * Randomly generate a car on the map within a specified zone at time 0
	 * 
	 * @param stationZoneNo
	 *            Randomly generate a car on the map in a zone
	 * @param destination
	 *            the ID of the station selected to charge
	 * @param chargingTime
	 *            the time of the car to charge
	 */
	public void generateCar(String[] stationZoneNo, String destination, int chargingTime) {
		Boolean error = true;

		while (error) {
			
			
			Point source = CarFactory.randomPointWithinZone(stationZoneNo);
			Point des = stations.get(destination).getPoint();
			int y2 = (int) des.getY();
			int x2 = (int) des.getX();
			int x1 = (int) source.getX();
			int y1 = (int) source.getY();

			GeoPosition geo1 = corToGeo(x1, y1);
			GeoPosition geo2 = corToGeo(x2, y2);

			GHResponse res = getRoute(geo1, geo2);
			if (res == null) {
				error = true;
			} else {
				error = false;
				Car c = new Car(geo1.getLatitude(), geo1.getLongitude(), destination, 0, chargingTime);
				c.saveRoute(res);
			}

		}
	}
	
	/**
	 * Generate a car within a zone in specified time 
	 * @param zone
	 * @param destination
	 * @param generationTime
	 * @param chargingTime
	 * @return
	 */
	protected static Car generateRandomCar(String zone, String[] destination, int generationTime,
			int chargingTime) {
		Boolean error = true;
		String desti=CarFactory.randomStation(destination);
		while (error) {
			//CarFactory.randomPointWithinZone(stationZoneNoArray)stationZoneNoArray
			
			Point source = stations.get(destination).getPoint();
			Point des = stations.get(destination).getPoint();
			int y2 = (int) des.getY();
			int x2 = (int) des.getX();
			int x1 = (int) source.getX();
			int y1 = (int) source.getY();

			GeoPosition geo1 = corToGeo(x1, y1);
			GeoPosition geo2 = corToGeo(x2, y2);

			GHResponse res = getRoute(geo1, geo2);
			if (res == null) {
				error = true;
			} else {
				error = false;
				Car c = new Car(geo1.getLatitude(), geo1.getLongitude(), desti, generationTime, chargingTime);
				c.saveRoute(res);
				// CarArray.add(c);
				return c;
				// addCar(geo1.getLatitude(), geo1.getLongitude(), destination,
				// generationTime,chargingTime,res);
			}

		}
		return null;
	}
	
	
	
	/**
	 * Pause the simulator
	 */
	public static void pauseSimulator() {
		Timer.pause();
	}

	/*
	 * To initialize the cars with route
	 */
	private static void initializeCar() {
		for (int i = 0; i < CarArray.size(); i++) {
			Car c = CarArray.get(i);
			c.saveRoute(c.getCarRoute());
			Simulator.getStations().get(c.getDestination()).addMovingQueue(c);
		}
		int carSize = CarArray.size();
		setCarSize(carSize);
		CarArray = null;
		SettingPanel.setCarNo(String.valueOf(carSize));
	}

	/**
	 * generate cars with the car factory
	 * @return end of generation
	 */
	public static Boolean generateCar() {
		return CarFactory.produce();
	}

	/**
	 * 
	 * @param min
	 *            Min integer number
	 * @param max
	 *            Max integer number
	 * @return random number
	 */
	public static int randInt(int min, int max) {
		Random rn = new Random();
		int range = max - min + 1;
		int randomNum = rn.nextInt(range) + min;
		return randomNum;
	}

	/**
	 * 
	 * @param producerSpeed
	 *            The time interval for car generation
	 * @param noOfCarsGenerated
	 *            Number of cars generated each time
	 * @param expectedNo
	 *            Total number of cars generated
	 * @param latitude
	 *            The latitude of the car
	 * @param longitude
	 *            The longitude of the car
	 */
	public void addFixedCarGenerater(int producerSpeed, int noOfCarsGenerated, int expectedNo, double latitude,
			double longitude) {
		CarFactory.addFixedCarProductionLine(producerSpeed, noOfCarsGenerated, expectedNo, latitude, longitude
				 );
	}



	/**
	 * 
	 * @param s
	 * 			  The Strategy used to select the charging station
	 * @param producerSpeed
	 *            The time interval for car generation
	 * @param noOfCarsGenerated
	 *            The time interval for car generation
	 * @param expectedNo
	 *            The total number of cars generated
	 * @param zone
	 *            Zone ID (station ID) Array selected to choose a random stations  
	 */
	public void addRandomCarGenerater(Strategy s,int producerSpeed, int noOfCarsGenerated, int expectedNo, String[] zone
			) {
		
		CarFactory.addRandomCarProductionLine(s,producerSpeed, noOfCarsGenerated, expectedNo, zone);
	}

	/**
	 * Start to run the simulator
	 */
	public static void startSimulator() {


		zoneDivision = true;
		painters.remove(zonePainter);
		
		generateZone();
		// Start the simulator
		AnimationTimer.start();

	}

	/**
	 * 
	 * @param i
	 * @param pos
	 */
	protected static void addPopUpPanel(int i, GeoPosition pos) {

		PopUpPanel popUpStationPane = new PopUpPanel(i, pos);
		popUpStationPane.setOpaque(false);

		layer.add(popUpStationPane, JLayeredPane.POPUP_LAYER);
		popUpStationPanel.add(popUpStationPane);
		popUpStationPane.setVisible(false);

	}
	/**
	 * get Graphhoper obeject
	 * @return GraphHopper
	 */
	public static GraphHopper getHopper() {
		return hopper;
	}

}
