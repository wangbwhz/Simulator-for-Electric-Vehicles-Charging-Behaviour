package mainAPI;

import java.awt.event.MouseWheelEvent;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;

public class ZoomWheelListener extends ZoomMouseWheelListenerCursor {

	public ZoomWheelListener(JXMapViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt) {
//		System.out.println("Zoom Level Changed "+Simulator.getZoomLevel());
		Simulator.updateZoomlbl();
		// TODO Auto-generated method stub
		super.mouseWheelMoved(evt);
	}
	

}
