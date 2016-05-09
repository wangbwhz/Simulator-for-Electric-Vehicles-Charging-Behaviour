
package mainAPI;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

public class Zone  {
	static int zoneCount;
	static Point[] points;
	static int[] color;

	private static ArrayList<ArrayList<Point>> pointCollection;
	public static ArrayList<ArrayList<Point>> getPointCollection() {
		return pointCollection;
	}

	static BufferedImage transparent;
	private static ArrayList<GeoPosition> boudryPointCollection;

	public static ArrayList<GeoPosition> getBoudryPointCollection() {
		return boudryPointCollection;
	}

	public static void setThiessenPolygon(Point[] pt) {
		points = pt;

		zoneCount = pt.length;
		Random rand = new Random();

		color = new int[zoneCount];
		for (int i = 0; i < zoneCount; i++) {
			color[i] = rand.nextInt(16777215);
		}
		boudryPointCollection=new ArrayList<GeoPosition>();
		zoneGeneration();
	}

	public static void zoneGeneration() {
		JXMapViewer jxMapViewer=Simulator.getMapViewer();
		 int width = jxMapViewer.getWidth();
		 int height = jxMapViewer.getHeight();
		pointCollection = null;
		pointCollection = new ArrayList<ArrayList<Point>>();
		BufferedImage I;
		int n = 0;
		I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int previousColor = 0;
		int currentColor = 0;

		for (int i = 0; i < zoneCount; i++) {
			ArrayList<Point> a = new ArrayList<Point>();
			pointCollection.add(a);
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				n = 0;
				// I.setRGB(x, y, Color.WHITE.getRGB());

				for (byte i = 0; i < zoneCount; i++) {
					if (distance((int) (points[i].getX()), x, (int) points[i].getY(),
							y) < distance((int) points[n].getX(), x, (int) points[n].getY(), y)) {
						n = i;
					}
				}
				// put into zone array list
				pointCollection.get(n).add(new Point(x, y));
				if (x == 0) {
					previousColor = n;
					currentColor = n;
				} else {
					currentColor = n;
					if (currentColor != previousColor) {
						I.setRGB(x, y, Color.RED.getRGB());
					}
					previousColor = currentColor;
				}
				// I.setRGB(x, y, color[n]);
			}

		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				n = 0;

				for (byte i = 0; i < zoneCount; i++) {
					if (distance((int) (points[i].getX()), x, (int) points[i].getY(),
							y) < distance((int) points[n].getX(), x, (int) points[n].getY(), y)) {
						n = i;

					}
				}
				if (y == 0) {
					previousColor = n;
					currentColor = n;
				} else {
					currentColor = n;
					if (currentColor != previousColor) {
						I.setRGB(x, y, Color.RED.getRGB());

					}
					previousColor = currentColor;

				}

			}
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (I.getRGB(x, y) == Color.RED.getRGB()) {
					GeoPosition geo = Simulator.corToGeo(x, y);
					boudryPointCollection.add(geo);
				}
			
			}
		}
		
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
