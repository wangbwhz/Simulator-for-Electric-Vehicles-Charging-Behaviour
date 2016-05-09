package mainAPI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import org.jxmapviewer.painter.Painter;

/**
 * Paints a route
 * @author Martin Steiger
 */
public class RoutePainter implements Painter<JXMapViewer>
{
	private boolean antiAlias = true;
	private ArrayList<Road> roadLst;
	public class Road{
		private PointList track;
		private Color color;
		public PointList getTrack() {
			return track;
		}
		public Color getColor() {
			return color;
		}
		public Road(PointList track,Color color)
		{
			this.track=track;
			this.color=color;
		}
	}
	/**
	 * 
	 */
	public RoutePainter()
	{
		// copy the list so that changes in the 
		// original list do not have an effect here
		roadLst=new ArrayList<Road>();
		
	}
	public void addRoute(PointList posList,Color color)
	{
		Road road=new Road(posList,color);
		roadLst.add(road);
	}
	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h)
	{
		g = (Graphics2D) g.create();

		// convert from viewport to world bitmap
		Rectangle rect = map.getViewportBounds();
		g.translate(-rect.x, -rect.y);

		if (antiAlias)
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setStroke(new BasicStroke(3));

		drawRoute(g, map);

		g.dispose();
	}

	/**
	 * @param g the graphics object
	 * @param map the map
	 */
	private void drawRoute(Graphics2D g, JXMapViewer map)
	{

//		System.out.println("Draw Route");
		int lastX = 0;
		int lastY = 0;
		
		boolean first = true;
		for (Road r:roadLst)
		{
		for (GHPoint ghP : r.getTrack())
		{	
			GeoPosition gp=new GeoPosition(ghP.getLat(),ghP.getLon());
			g.setColor(r.getColor());

			// convert geo-coordinate to world bitmap pixel
			Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());
			if (first)
			{
				first = false;
			}
			else
			{

                int zoomLevel=Simulator.getZoomLevel();
//                System.out.println("Zoom Level is "+zoomLevel);
                int stroke = 0;
                if(zoomLevel<=1)
                {
             	   stroke=14;
                }
                else if(zoomLevel<=2)
               {
            	   stroke=9;
               }
               else if(zoomLevel<=4)
               {
            	   stroke=7;

               }
               else if(zoomLevel<=6)
               {
            	   stroke=3;

               } 
               else if(zoomLevel<=8)
               {
            	   stroke=3;
 
               }
               else if(zoomLevel<=10)
               {
            	   stroke=3;

               }      
               else if(zoomLevel<=12)
               {
            	   stroke=3;
               }
                g.setStroke(new BasicStroke(stroke));
                g.draw(new Line2D.Float(lastX, lastY, (int) pt.getX(), (int) pt.getY()));
			}
			
			lastX = (int) pt.getX();
			lastY = (int) pt.getY();
		}
		 first = true;

		}
	}
}
