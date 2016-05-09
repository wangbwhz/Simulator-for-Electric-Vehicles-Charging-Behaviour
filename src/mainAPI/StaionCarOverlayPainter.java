package mainAPI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JButton;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;


/**
 * Painter used to paint the stations and cars
 * @author wangb
 *
 */
public class StaionCarOverlayPainter extends WaypointPainter<StationPoint> {
	public  void addStation(Set<StationPoint> stations){
		this.setWaypoints(stations);
	}
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer jxMapViewer, int width, int height) {
        for (StationPoint staionPoint : getWaypoints()) {
            Point2D point = jxMapViewer.getTileFactory().geoToPixel(
                    staionPoint.getPosition(), jxMapViewer.getZoom());
            Rectangle rectangle = jxMapViewer.getViewportBounds();
            int buttonX = (int)(point.getX() - rectangle.getX());
            int buttonY = (int)(point.getY() - rectangle.getY());
            JButton button = staionPoint.getButton();
//            button.setLocation(buttonX - button.getWidth() / 2, buttonY - button.getHeight() / 2);

            button.setLocation(buttonX , buttonY );
            
    		Iterator<Entry<String, StationPoint>> it = Simulator.getStations().entrySet().iterator();
    		while (it.hasNext()) {
    			Entry<String, StationPoint> pair = it.next();
    			// System.out.println(pair.getKey() + " = " + pair.getValue());
    			StationPoint s = (StationPoint) pair.getValue();
    			for(int i=0;i<s.getMovingQueue().size();i++)
    			{
    				Car c=s.getMovingQueue().get(i);
    				
    				Point p=Simulator.geoToCor(c.getLatitude(),c.getLongtitude());
    				Ellipse2D.Double circle = new Ellipse2D.Double(p.getX(), p.getY(), 10, 10);
    				g.setColor(Color.black);
    	            g.fill(circle);
    			}

    			
    		}

        }
    }
}