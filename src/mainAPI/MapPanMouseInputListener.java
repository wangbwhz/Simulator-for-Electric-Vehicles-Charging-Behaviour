package mainAPI;

import java.awt.event.MouseEvent;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.PanMouseInputListener;

public class MapPanMouseInputListener extends PanMouseInputListener {

	public MapPanMouseInputListener(JXMapViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		// TODO Auto-generated method stub
		
//		System.out.println(Simulator.corToGeo(evt.getX(),evt.getY()));
		Simulator.updateGeoCenter();
		super.mouseReleased(evt);
	}

}
