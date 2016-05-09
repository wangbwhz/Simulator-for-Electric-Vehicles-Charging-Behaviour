
package mainAPI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.ArrayList;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.painter.Painter;

public class ZonePainter implements Painter<JXMapViewer> {


	static BufferedImage transparent;




	public static void imageGeneration()
	{
		ArrayList<GeoPosition> boudryPointCollection=Zone.getBoudryPointCollection();
		if(boudryPointCollection.size()==0)
		{
			return;
		}
		JXMapViewer jxMapViewer=Simulator.getMapViewer();
		 int width = jxMapViewer.getWidth();
		 int height = jxMapViewer.getHeight();
		BufferedImage I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<boudryPointCollection.size();i++)
		{
			 Point2D point = jxMapViewer.getTileFactory().geoToPixel(
					 boudryPointCollection.get(i), jxMapViewer.getZoom());
		        Rectangle rectangle = jxMapViewer.getViewportBounds();
	            int pointX = (int)(point.getX() - rectangle.getX());
	            int pointY = (int)(point.getY() - rectangle.getY());
//			 System.out.println("GeoLocation Point is "+boudryPointCollection.get(i));
//			 
//			 System.out.println("Draw Point is "+new Point(pointX,pointY));
			 if((pointX>0&&pointX<width)&&(pointY>0&&pointY<height))
			 {
				I.setRGB((int)pointX, (int)pointY, Color.RED.getRGB());
			 }

		}
		BufferedImage source = I;


		
		
		
		
		int color = source.getRGB(0, 0);

		Image image = makeColorTransparent(source, new Color(color));

		transparent = imageToBufferedImage(image);
		
		
	}
	@Override
	public void paint(Graphics2D g, JXMapViewer map, int w, int h) {
		g = (Graphics2D) g.create();
		imageGeneration();
		g.drawImage(transparent, null, null);
		g.dispose();
		// g.setColor(Color.RED);
		// for (int i = 0; i < zoneCount; i++) {
		// g.fill(new Ellipse2D .Double(points[i].getX() - 2.5, points[i].getY()
		// - 2.5, 5, 5));
		// }
//		System.out.println(pointCollection.get(0).size());

	}

	private static BufferedImage imageToBufferedImage(Image image) {

		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return bufferedImage;

	}

	public static Image makeColorTransparent(BufferedImage im, final Color color) {
		ImageFilter filter = new RGBImageFilter() {

			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};
		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	static double distance(int x1, int x2, int y1, int y2) {
		double d;
		d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); // Euclidian
		// d = Math.abs(x1 - x2) + Math.abs(y1 - y2); // Manhattan
		// d = Math.pow(Math.pow(Math.abs(x1 - x2), p) + Math.pow(Math.abs(y1 -
		// y2), p), (1 / p)); // Minkovski
		return d;
	}

}
