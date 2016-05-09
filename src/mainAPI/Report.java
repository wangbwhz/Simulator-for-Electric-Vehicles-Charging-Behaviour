package mainAPI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * 
 * @author wangb
 *
 */
public class Report extends JFrame {

	/**
	 * Show the report data
	 */
	private static final long serialVersionUID = -4299612384355005212L;

	private static DefaultTableModel carModel;
	private static DefaultTableModel staionModel;
	private final  Font font = new Font("Arial", Font.PLAIN, 18);


	/*
	 * keep two decimal points for a double value
	 */
	private static String keepTwoDeci(double d) {
		double d2 = Math.round(d * 100);
		d2 = d2 / 100;
		if (d2 == (long) d2)
			return String.format("%d", (long) d2);
		else
			return String.format("%s", d2);
	}

	/*
	 * Save the car data to the report
	 */
	public void saveCarData(Car c) {
		String reachStationStr = String.valueOf(c.getDestination());
		String arrivalTimeStr = String.valueOf(c.getArrivalTime());
		String generationTimeStr = String.valueOf(c.getGenerationTime());

		String chargingBeginTimeStr = String.valueOf(c.getChargingBeginTIme());
		String chargingTime = String.valueOf(c.getChargingTime());
		String departueTime = String.valueOf(c.getDepartureTime());
		String oneRowData[] = { c.getID(), reachStationStr, generationTimeStr, arrivalTimeStr, chargingBeginTimeStr,
				chargingTime, departueTime };
		carModel.addRow(oneRowData);
	}

	/*
	 * save station data to the report
	 */
	public static void saveStationData(StationPoint s) {
		if (Timer.getClock() != 0) {
			Double qLA = s.getQueueLengthArea() * 1.0 / Timer.getClock();
			Double bTA = s.getBusyTimeArea() * 1.0 / Timer.getClock();

			String queueLengthAreaStr = keepTwoDeci(qLA);
			String busyTimeAreaStr = keepTwoDeci(bTA);
			String runningTimeStr = String.valueOf(Timer.getClock());
			String oneRowData[] = { s.getID(), queueLengthAreaStr, busyTimeAreaStr, runningTimeStr };
			staionModel.addRow(oneRowData);

		}

	}

	public Report() {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setSize(1024, 768);
		carModel = new DefaultTableModel();
		carModel.addColumn("Car ID");
		carModel.addColumn("Charging Station No");
		carModel.addColumn("Generation Time(mins)");
		carModel.addColumn("Arrival Time(mins)");
		carModel.addColumn("Time charging begins(mins)");
		carModel.addColumn("Charging Time(mins)");
		carModel.addColumn("Departue Time(mins)");

		staionModel = new DefaultTableModel();
		staionModel.addColumn("Station ID");
		staionModel.addColumn("Average Number Watiing For Charging");
		staionModel.addColumn("Charger Station Utilization");
		staionModel.addColumn("Charger Station Running Time(mins)");

		JTable stationTable = new JTable(staionModel);
		stationTable.setFont(font);
		stationTable.setRowHeight(30);
		stationTable.setPreferredScrollableViewportSize(new Dimension(500, 200));
		JScrollPane stationScrollPane = new JScrollPane(stationTable);

		JTable table = new JTable(carModel);
		table.setFont(font);
		table.setRowHeight(30);
		table.setPreferredScrollableViewportSize(new Dimension(500, 200));
		JScrollPane scrollPane = new JScrollPane(table);
		JPanel exportQueueLPanel = new JPanel();
		JButton exportQueueLBtn = new JButton("Export Queue Length With Respect Of Time");
		exportQueueLBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, StationPoint> sps = Simulator.getStations();
				exporteQueueTime(sps);
			}

		});
		exportQueueLPanel.add(exportQueueLBtn);

		JPanel exportStationPanel = new JPanel();
		JButton exportStationBtn = new JButton("Export Station Table As CSV");
		exportStationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportCSV(staionModel, "Station");
			}

		});
		exportStationPanel.add(exportStationBtn);

		JPanel exportCarPanel = new JPanel();

		JButton exportCarsBtn = new JButton("Export Car Table As CSV");
		exportCarsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportCSV(carModel, "Car");
			}

		});
		exportCarPanel.add(exportCarsBtn);

		
		panel.add(exportQueueLPanel);
		panel.add(stationScrollPane);
		panel.add(exportStationPanel);
		panel.add(scrollPane);
		panel.add(exportCarPanel);
		this.add(panel);
	}

	/*
	 * Export the queue length with respect of time data in excel
	 */
	public void exporteQueueTime(Map<String, StationPoint> sp) {
		JFileChooser c = new JFileChooser();
		c.setCurrentDirectory(new File(System.getProperty("user.dir")));
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV/EXCEL Files", "csv", "xls", "xlsx");
		c.setFileFilter(filter);
		c.setSelectedFile(new File("SimulatorReport_" + "Station" + "_" + timeStamp + ".csv"));
		c.setDialogTitle("Please choose the saving directoty");
		int rVal = c.showSaveDialog(Report.this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String file_name = c.getSelectedFile().getName();
			String dir = c.getCurrentDirectory().toString();
			try {
				queueTimeToExcel(sp, new File(dir + "\\" + file_name));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "File exported successfully", "Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {

		}
	}

	/*
	 * Export the station table or car table
	 */
	public void exportCSV(DefaultTableModel model, String type) {
		JFileChooser c = new JFileChooser();
		c.setCurrentDirectory(new File(System.getProperty("user.dir")));
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV/EXCEL Files", "csv", "xls", "xlsx");
		c.setFileFilter(filter);
		c.setSelectedFile(new File("SimulatorReport_" + type + "_" + timeStamp + ".csv"));
		c.setDialogTitle("Please choose the saving directoty");
		int rVal = c.showSaveDialog(Report.this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String file_name = c.getSelectedFile().getName();
			String dir = c.getCurrentDirectory().toString();
			System.out.println(file_name);
			System.out.println(dir);
			System.out.println(dir + "\\" + file_name);

			toExcel(model, new File(dir + "\\" + file_name));

			JOptionPane.showMessageDialog(null, "File exported successfully", "Message",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {

		}
	}

	public Report(int cSize) {

		setSize(800, 600);
		System.out.println("add");
		DefaultTableModel model = new DefaultTableModel();

		JTable table = new JTable(model);
		table.setFont(new Font("Arial", Font.PLAIN, 20));
		table.setRowHeight(30);
		table.setPreferredScrollableViewportSize(new Dimension(500, 200));
		JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane);

		JPanel infoPanel = new JPanel();
		JTextField dirPathTextField = new JTextField(26);
		JButton displayDirButton = new JButton("Display Directory");

		infoPanel.add(dirPathTextField);
		infoPanel.add(displayDirButton);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,

				infoPanel, scrollPane);

		splitPane.setDividerLocation(50);

		splitPane.setEnabled(false);
		this.add(splitPane);

	}

	public void queueTimeToExcel(Map<String, StationPoint> sps, File file) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		Iterator<Entry<String, StationPoint>> it1 = sps.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<String, StationPoint> pair = it1.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());
			StationPoint sp = (StationPoint) pair.getValue();
			HSSFSheet sheet = workbook.createSheet("Station " + sp.getID() + " sheet");
			HSSFRow firstRow = sheet.createRow(0);
			firstRow.createCell(0).setCellValue("Time");
			firstRow.createCell(1).setCellValue("Queue Length");

			for (int r = 0; r < sp.getQt().size(); r++) {

				HSSFRow row = sheet.createRow(r + 1);
				// iterating c number of columns
				HSSFCell cell0 = row.createCell(0);
				cell0.setCellValue(sp.getQt().get(r).getTime());
				HSSFCell cell1 = row.createCell(1);
				cell1.setCellValue(sp.getQt().get(r).getQueueLength());

			}

		}
		FileOutputStream fileOut = new FileOutputStream(file);
		// write this workbook to an Outputstream.
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
		workbook.close();
		System.out.println("Excel written successfully..");
	}

	public void toExcel(DefaultTableModel model, File file) {

		try {

			FileWriter excel = new FileWriter(file);

			for (int i = 0; i < model.getColumnCount(); i++) {

				excel.write(model.getColumnName(i) + ",");

			}

			excel.write("\n");

			for (int i = 0; i < model.getRowCount(); i++) {

				for (int j = 0; j < model.getColumnCount(); j++) {

					excel.write(model.getValueAt(i, j).toString() + ",");

				}

			
				excel.write("\n");

			}

			excel.close();

		} catch (IOException e) {
			System.out.println(e);
		}

	}

}
