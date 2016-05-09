package mainAPI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingPanel extends JPanel{

	/**
	 * a setting panel on the right of the simulator
	 */
	private static final long serialVersionUID = 1L;

	private static JTextField chargingTimetxt = new JTextField("1000");
	private static JTextField speedtxt = new JTextField("");
	private static JTextField staionCapacity = new JTextField("10");
	private static JTextField longitudetxt = new JTextField("103.8");
	private static JButton reportbtn;
	private static JButton startSimulatorbtn;
	private static JButton pauseSimulatorbtn;
	private static JLabel runningTimeValuelbl;
	private static JLabel carNoValuelbl;
	private static JLabel zoomlbl;
	private static JLabel latlbl;
	private static JLabel longlbl;

	private final Font fontValue = new Font("Arial", Font.PLAIN, 14);
	private final Font fontTitle = new Font("Arial", Font.BOLD, 14);
	public static JTextField getLongitudetxt() {
		return longitudetxt;
	}
	public static void setRunningTime(String s) {
		runningTimeValuelbl.setText(s);
	}
	public static void setZoom(String s) {
		zoomlbl.setText(s);
	}
	public static void setLat(String s) {
		latlbl.setText(s);
	}
	public static void setLong(String s) {
		longlbl.setText(s);
	}
	public static void setCarNo(String s) {
		carNoValuelbl.setText(s);
	}
	public static JTextField getLatitudetxt() {
		return latitudetxt;
	}

	private static JTextField latitudetxt = new JTextField("1.3667");


		public SettingPanel() {
			

			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setPreferredSize(new Dimension(220, 768));

			setLayout(new GridLayout(4, 1, 0, 0));
			JPanel defaultColorRect = new JPanel();
			JPanel exploredColorRect = new JPanel();
			JPanel obstaclesColorRect = new JPanel();
			JPanel obstaclesDetectedColorRect = new JPanel();
			JPanel obstaclesDetectedColorRectE = new JPanel();
			JPanel obstaclesDetectedColorRectF= new JPanel();

			JLabel defaultLabel = new JLabel("A");
			defaultLabel.setFont(fontValue);
			JLabel exploredLabel = new JLabel("B");
			exploredLabel.setFont(fontValue);
			JLabel obstaclesLabel = new JLabel("C");
			obstaclesLabel.setFont(fontValue);
			JLabel obstaclesDetectedLabel = new JLabel("D");
			obstaclesDetectedLabel.setFont(fontValue);
			JLabel obstaclesDetectedLabelE = new JLabel("E");
			obstaclesDetectedLabelE.setFont(fontValue);
			JLabel obstaclesDetectedLabelF = new JLabel("F");
			obstaclesDetectedLabelF.setFont(fontValue);
			
			defaultColorRect.setBackground(Simulator.colorLevel1);
			exploredColorRect.setBackground(Simulator.colorLevel2);
			obstaclesColorRect.setBackground(Simulator.colorLevel3);
			obstaclesDetectedColorRect.setBackground(Simulator.colorLevel4);
			obstaclesDetectedColorRectE.setBackground(Simulator.colorLevel5);
			obstaclesDetectedColorRectF.setBackground(Simulator.colorLevel6);

			JPanel legendPanel = new JPanel();
			legendPanel.setLayout(new GridLayout(8, 2, 1, 1));
			JLabel legendTitle = new JLabel("Traffic");
			legendTitle.setFont(fontTitle);
			legendPanel.add(legendTitle);
			legendPanel.add(new JLabel(" "));
			legendPanel.add(defaultLabel);
			legendPanel.add(defaultColorRect);
			
			legendPanel.add(exploredLabel);
			legendPanel.add(exploredColorRect);
			
			legendPanel.add(obstaclesLabel);
			legendPanel.add(obstaclesColorRect);
			
			legendPanel.add(obstaclesDetectedLabel);
			legendPanel.add(obstaclesDetectedColorRect);
			
			legendPanel.add(obstaclesDetectedLabelE);
			legendPanel.add(obstaclesDetectedColorRectE);
			
			legendPanel.add(obstaclesDetectedLabelF);
			legendPanel.add(obstaclesDetectedColorRectF);

			add(legendPanel);

			//map part
			JPanel mapSettingPanel = new JPanel();
			mapSettingPanel.setLayout(new GridLayout(4, 2, 5, 5));
			JLabel mapSettingTitle = new JLabel("Map Setting");
			mapSettingTitle.setFont(fontTitle);
			
			JLabel longitudelbl = new JLabel("Longitude:");
			longitudelbl.setFont(fontValue);
			
			JLabel latitudelbl = new JLabel("Latitude:");
			latitudelbl.setFont(fontValue);
			longitudetxt.setBackground(Color.white);
			longitudetxt.setText(String.valueOf(103.8));
			
			latitudetxt.setBackground(Color.white);
			latitudetxt.setText(String.valueOf(1.3667));

			JButton mapButton = new JButton("Move");


			mapSettingPanel.add(mapSettingTitle);
			mapSettingPanel.add(new JLabel(""));

			mapSettingPanel.add(longitudelbl);
			mapSettingPanel.add(longitudetxt);
			mapSettingPanel.add(latitudelbl);
			mapSettingPanel.add(latitudetxt);
			mapSettingPanel.add(new JLabel(""));

			mapSettingPanel.add(mapButton);

			
			
			// set value part
			JPanel setExploreValuePanel = new JPanel();
			setExploreValuePanel.setLayout(new GridLayout(7, 1, 1, 1));

			JLabel setExploreValueTitle = new JLabel("Value Setting");

			setExploreValueTitle.setFont(fontTitle);
			
			

			

			
			JLabel chargingTimelbl = new JLabel("Charging Time[In Second]");
			chargingTimelbl.setFont(fontValue);

			JLabel staionCapacitylbl = new JLabel("Staion Capacity");
			staionCapacitylbl.setFont(fontValue);

			JLabel speedlbl = new JLabel("Number Of Cars Generated Within The Zone");
			speedlbl.setFont(fontValue);
			
			setExploreValuePanel.add(setExploreValueTitle);
			setExploreValuePanel.add(chargingTimelbl);
			chargingTimetxt.setBackground(Color.white);
			setExploreValuePanel.add(chargingTimetxt);
			setExploreValuePanel.add(staionCapacitylbl);
			staionCapacity.setBackground(Color.white);
			setExploreValuePanel.add(staionCapacity);

			setExploreValuePanel.add(speedlbl);
			speedtxt.setBackground(Color.white);
			setExploreValuePanel.add(speedtxt);
//			add(setExploreValuePanel);
			// Button Panel
			JPanel buttonPanel = new JPanel();
			JLabel controlTitle = new JLabel("Station Action:");
			controlTitle.setFont(fontTitle);
			buttonPanel.setLayout(new GridLayout(5, 1, 5, 5));
			buttonPanel.add(controlTitle);

			 pauseSimulatorbtn = new JButton("Pause Simulator");

			startSimulatorbtn = new JButton("Start Simulatuor");
			reportbtn = new JButton("Generate Report");

			buttonPanel.add(pauseSimulatorbtn);
			buttonPanel.add(startSimulatorbtn);
			buttonPanel.add(reportbtn);


			pauseSimulatorbtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Simulator.pauseSimulator();
					startSimulatorbtn.setEnabled(true);
					pauseSimulatorbtn.setEnabled(false);

				}
			});
			startSimulatorbtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Simulator.startSimulator();
					startSimulatorbtn.setEnabled(false);
					pauseSimulatorbtn.setEnabled(true);

					
				}
			});
			reportbtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					new Report().setVisible(true);
					if(	Simulator.getReport().isVisible()){
						Simulator.getReport().setVisible(false);
					}
					else
					{
						Simulator.getReport().setVisible(true);

					}

					
				}
			});
//			reportbtn.setEnabled(false);
			add(buttonPanel);
			JPanel ctrlPane = new JPanel();
			JLabel title = new JLabel("Simulator Information");
			JLabel carNolbl = new JLabel("Number of cars generated:");
			JLabel runningTimelbl = new JLabel("Running TIme(mins)");
			JLabel zoomLevellbl = new JLabel("Map Zoom Level");
			JLabel centerLatTitle = new JLabel("Map Center Latitude");
			JLabel centerLongTitle = new JLabel("Map Center Longtitude");

			carNoValuelbl = new JLabel("");
			runningTimeValuelbl = new JLabel("");
			zoomlbl = new JLabel(String.valueOf(Simulator.getZoomLevel()));
			latlbl = new JLabel(String.valueOf(Simulator.getCenter().getLatitude()));
			longlbl = new JLabel(String.valueOf(Simulator.getCenter().getLongitude()));

			title.setFont(fontTitle);
			carNolbl.setFont(fontValue);
			runningTimelbl.setFont(fontValue);
			centerLatTitle.setFont(fontValue);
			centerLongTitle.setFont(fontValue);
			zoomLevellbl.setFont(fontValue);
			carNoValuelbl.setFont(fontValue);
			runningTimeValuelbl.setFont(fontValue);

			latlbl.setFont(fontValue);
			longlbl.setFont(fontValue);
			zoomlbl.setFont(fontValue);

			ctrlPane.add(title);
			ctrlPane.add(carNolbl);
			ctrlPane.add(carNoValuelbl);
			ctrlPane.add(runningTimelbl);
			ctrlPane.add(runningTimeValuelbl);
			ctrlPane.add(zoomLevellbl);
			ctrlPane.add(zoomlbl);
			
			ctrlPane.add(centerLatTitle);
			ctrlPane.add(latlbl);
			ctrlPane.add(centerLongTitle);
			ctrlPane.add(longlbl);
			ctrlPane.setLayout(new GridLayout(11, 1, 5, 0));

			add(ctrlPane);

		}
		public static void disablePausebtn()
		{
			pauseSimulatorbtn.setEnabled(false);

		}
		public static void enableStartSimulatorbtn()
		{
			startSimulatorbtn.setEnabled(true);

		}
		public static void disableStartSimulatorbtn()
		{
			startSimulatorbtn.setEnabled(false);

		}
		public static void enableReportbtn(){
			reportbtn.setEnabled(true);
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	
}
