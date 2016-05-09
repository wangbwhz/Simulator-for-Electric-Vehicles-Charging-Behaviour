package main;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;

import mainAPI.ReadExcelFile;
public class Simulator {
	public static void main(String[] args) {
		double latitude=1.3355762066278836;
		double longitude=103.85393142700195;
		int zoomLevel=6;
		mainAPI.Simulator s= new mainAPI.Simulator(latitude,longitude ,zoomLevel);
		s.setSize(1024 + 200, 768);
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.setVisible(true);
		s.setTimeMapping(1,900);//1min=20miliseconds
		//https://mapzen.com/data/metro-extracts can get OSM data here, noted: first time using the OSM file it needs more time for extracting the map data
		
		s.setMapOSM("import\\mapData"+"\\"+"singapore.osm");
		s.integrateTraffic("import\\trafficData.json");
		
		s.setStations("import\\s.xlsx");
		String zoneArrayCenter[]={"Raffles Place","Chinatown","Queenstown","Keppel","Dover","City Hall","Bugis","Farrer Park","Orchard","Tanglin","Bukit Timah","Toa Payoh","Macpherson","Kembangan","Katong","Bayshore","Changi","Pasir Ris","Punggol","Bishan","Clementi","Boon Lay","Bukit Batok","Lim Chu Kang","Admiralty","Tagore","Sembawang","Seletar"
};
		ShortestDistanceStrategy sDs=new ShortestDistanceStrategy();
		s.addRandomCarGenerater(sDs,5,4,1280, zoneArrayCenter);
		s.addRandomCarGenerater(sDs,5,4,1120, zoneArrayCenter);
		s.draw();
	}
}

//String zoneArrayCenter[]={"Tanglin","Orchard","Novena","CityHall"};
//String zoneArray[]={"ChoaChuKang","BukitBatok","Seragoon","AngMokio","Clementi","ToaPayoh","ChinaTown"};
	
//s.addFixedCarGenerater(5,10,50, 1.36094, 103.82432, destinationStationArray, 2);//mins
	
//s.addCar("Car_3",1.336434280186183, 103.809814453125,"1",2,5);
//s.addCar(1.31042, 103.75406,destinationStation,5);
//s.addCar(1.31042, 103.75406,destinationStation,2);
//
//for(int i=0;i<50;i++)
//{
//	int no=randInt(0,2);
//	int des=randInt(0,2);
//	s.generateCar("haha", "haha", 5);
//
//}