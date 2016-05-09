package mainAPI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jxmapviewer.viewer.GeoPosition;
/**
 * 
 * @author wangb
 *
 */
public class PopUpPanel extends JPanel {

	

	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private JLabel cWaitingNoLabel;
	private JLabel chargingLstlbl;
	public JLabel getChargingLstlbl() {
		return chargingLstlbl;
	}

	private int stationNo;
	private GeoPosition geoPosition;
	private JPanel inforPanel;
	public JLabel getcWaitingNoLabel() {
		return cWaitingNoLabel;
	}

	public JLabel getcChargingNoLabel() {
		return cChargingNoLabel;
	}

	

	private JLabel cChargingNoLabel;
	private JLabel cChargingTimeNoLabel;

	public JLabel getcChargingTimeNoLabel() {
		return cChargingTimeNoLabel;
	}


	private int oldX;
	private int oldY;
	private int newX;
	private int newY;

	public PopUpPanel(final int index,GeoPosition geoPosition) {
     
		

		width = 200;
		height = 200;
		stationNo=index;
		// super(new BorderLayout());
		setLayout(null);
		Font fontValue = new Font("Arial", Font.PLAIN, 14);
		Font fontTitle = new Font("Arial", Font.BOLD, 14);
		// settingPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		// setBorder(BorderFactory.createTitledBorder("Information"));
		// setPreferredSize(new Dimension(220, 768));
		inforPanel = new JPanel();
		inforPanel.setBorder(BorderFactory.createTitledBorder("Station "+stationNo+" Information"));
//		inforPanel.setBounds(Simulator.getStationX(0) + Simulator.getStationWidth() + 10, Simulator.getStationY(0),
//				width, height);
		this.geoPosition=geoPosition;
		Point p=Simulator.geoToCor(geoPosition.getLatitude(), geoPosition.getLongitude());
		inforPanel.setBounds((int)p.getX()+StationPoint.width , (int)p.getY(),width, height);
		inforPanel.setBackground(new Color(135, 206, 235));
		inforPanel.setLayout(new GridLayout(8, 1, 1, 1));

		JLabel cWaitingLabel = new JLabel("Number of cars Waiting:");
		cWaitingNoLabel = new JLabel("0");
		cWaitingLabel.setFont(fontTitle);
		cWaitingNoLabel.setFont(fontValue);

		JLabel cChargingLabel = new JLabel("Number of cars Charging:");
		cChargingNoLabel = new JLabel("0");
		cChargingLabel.setFont(fontTitle);
		cChargingNoLabel.setFont(fontValue);

		JLabel cChargingTimeLabel = new JLabel("Charging List(s):");
		chargingLstlbl = new JLabel("");
		cChargingTimeLabel.setFont(fontTitle);
//		cChargingTimeNoLabel.setFont(fontValue);

		// defaultLabel.setBounds(20, 20, 40, 40);
		inforPanel.add(cWaitingLabel);
		inforPanel.add(cWaitingNoLabel);
		inforPanel.add(cChargingLabel);
		inforPanel.add(cChargingNoLabel);
		inforPanel.add(cChargingTimeLabel);
		inforPanel.add(chargingLstlbl);

		JButton closeButton = new JButton("Close");
		// closeButton.setBounds(width-80-20, height-30-20, 80, 30);
		inforPanel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display/center the jdialog when the button is pressed
//				this.setVisible(false);
				Simulator.getPopUpStationPanel().get(stationNo).setVisible(false);
			}

		
		});
		
		inforPanel.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                int posDiffX = e.getX()-oldX;
                int posDiffY = e.getY()-oldY;

                 newX=(int) (inforPanel.getLocation().getX()+posDiffX);
                 newY=(int) (inforPanel.getLocation().getY()+posDiffY);
                 inforPanel.setLocation(newX,newY);
                
                repaint();
            }

            public void mouseMoved(MouseEvent e) {
                
            }
        });
		inforPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			 public void mousePressed(MouseEvent e) {
                oldX=e.getX();
                oldY=e.getY();
                
            }

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				inforPanel.setLocation(newX,newY);

			}

        

           

           
        });

		add(inforPanel);

		this.setVisible(true);


	}
	/**
	 * move the popup window to a new place
	 */
	public void rePosition()
	{
		Point p=Simulator.geoToCor(geoPosition.getLatitude(), geoPosition.getLongitude());
		inforPanel.setBounds((int)p.getX()+StationPoint.width , (int)p.getY(),width, height);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}
