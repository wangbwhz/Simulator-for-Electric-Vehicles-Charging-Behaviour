package mainAPI;


class Timer implements Runnable {
	

	private Thread TimerThread;
	private int iInterval;
	private static double clock;
	private static boolean status = true;
	private static double realTime;// in mins
	private static double simulationTime ;// in miliseconds
	public static void setStatus(boolean status) {
		Timer.status = status;
	}

	public static void setTimeMapping(double rt, double st) {
		realTime = rt;
		simulationTime = st;
	}

	public static int getClock() {
		return (int) Math.round(clock);
	}

	public static double getPreciseClock() {
		return clock;
	}

	public static void setClock(double clock) {
		Timer.clock = clock;
	}

	public Timer(int i) // Interval constructor
	{
		iInterval = i;

	}

	public void start() {
		System.out.println("Start Again");
		if (TimerThread == null) {
			TimerThread = new Thread(this);
			TimerThread.start();
		} else {
			System.out.println("Status is true");

			status = true;
		}
	}



	public static void pause() {
		status = false;
	}

	public void run() {
		Boolean productionEnd=false;;
		int iSleepTime = iInterval / 10;
		if (iSleepTime == 0)
			iSleepTime = 1;
		while (true) {
			if (status) {
				try {
					Thread.sleep(iSleepTime);
					// Wake up every 1/10 interval and see
				} // if it's time yet.
				catch (InterruptedException e1) {
				}


				clock += iSleepTime * realTime / simulationTime;// in actual
																// mins
				
				//let all the cars move, charge or leave
				Simulator.onTimer();

				

			} else {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
} // End of Timer class