package mainAPI;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A waypoint that is represented by a button on the map.
 *
 * @author Wangb
 */
public class StationPoint extends DefaultWaypoint {

	private String ID;
	private int chargingTime;
	private int capacity;
	private int totalCarNo=0; //total number of cars coming to charge
	private int totalWaitingTime=0;//total waiting time for all the cars coming to charge
	private GeoPosition geoPostion;
	protected ArrayList<Car> movingQueue = null;

	public ArrayList<Car> getMovingQueue() {
		return movingQueue;
	}

	protected ArrayList<Car> waitingQueue = null;
	protected ArrayList<Car> chargingQueue = null;

	public class QueueTime {
		private int time;
		private int queueLength;

		public int getTime() {
			return time;
		}

		public int getQueueLength() {
			return queueLength;
		}

		public QueueTime(int time, int queueLength) {
			this.time = time;
			this.queueLength = queueLength;
		}

	}
	public int getTotalCarNo() {
		return totalCarNo;
	}

	protected ArrayList<QueueTime> qt = new ArrayList<QueueTime>();;

	private final JButton button;
	public static final int width = 50;
	public static final int height = 20;
	private final int stationIndex;
	// private int timeofLastEvent;
	private int waitingQueueLen = 0;
	private int queueLengthArea = 0;
	private int chargingCarNo = 0;
	private int timeofLastEvent = -1;

	public Boolean isEnd() {
		if (chargingCarNo == -1) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<QueueTime> getQt() {
		return qt;
	}

	public void addMovingQueue(Car c) {
		movingQueue.add(c);
	}

	public void incChargingCarNo() {
		chargingCarNo++;
		totalCarNo++;
	}

	public int getQueueLengthArea() {
		return queueLengthArea;
	}

	public int getBusyTimeArea() {
		return busyTimeArea;
	}

	private int busyTimeArea = 0;

	public int getStationIndex() {
		return stationIndex;
	}

	public String getID() {
		return ID;
	}

	public int getChargingTime() {
		return chargingTime;
	}

	// private static ArrayList<Car>[] stationQueue1;

	public Point getPoint() {
		return Simulator.geoToCor(geoPostion.getLatitude(), geoPostion.getLongitude());
	}

	public int getWaitingSize() {
		return waitingQueue.size();
	}

	public int getChargingSize() {
		return chargingQueue.size();
	}

	public void checkReachStation() {

		for (Iterator<Car> iterator = movingQueue.iterator(); iterator.hasNext();) {
			Car c = iterator.next();
			if (c != null) {

				if (c.checkReachStation()) {
					waitingCharge(c);
					iterator.remove();
					// System.out.println("Moving Queue Size is " +
					// movingQueue.size());

				} else {
					// System.out.println("**Check Station It does not reach
					// station");

					c.update();
				}
			}
		}

	}

	// car reach the station
	public void waitingCharge(Car c) {

		waitingQueue.add(c);

	}

	/**
	 * update timeofLastEvent,queueLengthArea,busyTimeArea
	 */
	public void updateBQ() {
		// Some cars are waiting charge
		if (Timer.getClock() != timeofLastEvent) {
			if (chargingQueue.size() > 0) {
				// System.out.println("Waiting Queue Len is not 0
				// ChargeQueue:");
				// System.out.println(chargingQueue.size()==capacity);
				busyTimeArea += Timer.getClock() - timeofLastEvent;
			}
			queueLengthArea += waitingQueueLen * (Timer.getClock() - timeofLastEvent);

		}

	}

	// update waitingQueueLength and Time Of Last Event
	public void updateWQLTOLE() {
		// check if this time, the waiting queue length data is saved or not
		if (Timer.getClock() != timeofLastEvent) {
			qt.add(new QueueTime(Timer.getClock(), waitingQueue.size()));
		}

		if (Timer.getClock() != timeofLastEvent) {
			timeofLastEvent = Timer.getClock();
			waitingQueueLen = waitingQueue.size();

		}

	}

	// take from waiting list and put into charging list
	public void processCharging() {
		if (waitingQueue.size() != 0 && getChargingSize() < capacity) {
			Car c1 = waitingQueue.get(0);
			if (c1 != null) {
				c1.startCharging();
				totalWaitingTime=totalWaitingTime+c1.getChargingBeginTIme()-c1.getArrivalTime();
				chargingQueue.add(c1);
				waitingQueue.remove(0);
			}

		}
		// al
		if (Timer.getClock() == 0 && waitingQueue.size() != 0 && getChargingSize() == capacity) {
			waitingQueueLen = getChargingSize();
			timeofLastEvent = Timer.getClock();

		}

		// update the queue length
		// Simulator.updateQueueInfo(waitingQueue.size(), chargingQueue.size());

	}

	public int getTotalWaitingTime() {
		return totalWaitingTime;
	}

	// chek if charged car can be removed from charging list or not
	public void checkChargingComplete() {
		// if (chargingQueue.size() == capacity) {
		// System.out.println("Size is "+chargingQueue.size());
		for (Iterator<Car> iterator = chargingQueue.iterator(); iterator.hasNext();) {
			Car c = iterator.next();
			if (c != null) {
				if (c.checkCompeleteCharging()) {
					compeleteCharging(c);
					iterator.remove();
				}
			}
		}

	}

	// remove the charging car from charging list
	public void compeleteCharging(Car c) {
		chargingCarNo--;
		Simulator.getReport().saveCarData(c);
	}

	public void runningEnd() {
		if (chargingCarNo == 0) {
			System.out.println("*StationID is " + ID);
			System.out.println("*End of Running");
			System.out.println("*Busy Time Area is " + busyTimeArea);
			System.out.println("*QueueLengthArea is " + queueLengthArea);
			System.out.println("*Charging Queue Size is" + chargingQueue.size());
			System.out.println("*Moving Queue Size is" + movingQueue.size());
			System.out.println("*Waiting Queue Size is" + waitingQueue.size());
			// Report.saveStationData(this);
			chargingCarNo = -1;
			for (int i = 0; i < qt.size(); i++) {
				int time = qt.get(i).getTime();
				int queueLen = qt.get(i).getQueueLength();
				System.out.println("Time is " + time + " QueueLen is " + queueLen);

			}
		}
	}

	public StationPoint(String ID, int capacity, GeoPosition coord) {
		super(coord);
		this.geoPostion = coord;
		this.stationIndex = Simulator.getStationIndexCount();
		this.ID = ID;
		this.capacity = capacity;
		if (ID.length() >= 4) {
			button = new JButton(ID.substring(0, 4));

		} else {
			button = new JButton(ID);

		}
		button.setBackground(Color.GREEN);
		button.setSize(width, height);
		Dimension d = button.getPreferredSize();
		d.height = height;
		button.setPreferredSize(d);
		// button.setPreferredSize(new Dimension(width, height));
		button.addMouseListener(new SwingWaypointMouseListener());
		button.setVisible(true);
		Simulator.addPopUpPanel(stationIndex, coord);
		if (waitingQueue == null) {
			waitingQueue = new ArrayList<Car>();
		}
		if (chargingQueue == null) {
			chargingQueue = new ArrayList<Car>();
		}
		if (movingQueue == null) {
			movingQueue = new ArrayList<Car>();
		}
		// timeofLastEvent = 0;
		queueLengthArea = 0;
		busyTimeArea = 0;
		// System.out.println("Update");
	}

	public JButton getButton() {
		return button;

	}

	public void updateChargingTime() {

		String chargingtimeLst = "";

		for (int i = 0; i < chargingQueue.size(); i++) {
			Car c = chargingQueue.get(i);
			chargingtimeLst = chargingtimeLst + ((System.currentTimeMillis() - c.getcStartTime()) / 1000) + " ";
		}
		Simulator.getPopUpStationPanel().get(stationIndex ).getChargingLstlbl().setText(chargingtimeLst);
		Simulator.getPopUpStationPanel().get(stationIndex ).getcWaitingNoLabel()
				.setText(String.valueOf(waitingQueue.size()));
		Simulator.getPopUpStationPanel().get(stationIndex ).getcChargingNoLabel()
				.setText(String.valueOf(chargingQueue.size()));

		// Simulator.getPopUpStationPanel().get(stationNo-1).setcChargingTimeNoLabel(5);

	}

	private class SwingWaypointMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			Simulator.getPopUpStationPanel().get(stationIndex).rePosition();
			;
			if (Simulator.getPopUpStationPanel().get(stationIndex ).isVisible()) {

				Simulator.getPopUpStationPanel().get(stationIndex).setVisible(false);
			} else {
				Simulator.getPopUpStationPanel().get(stationIndex).setVisible(true);

			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
}
