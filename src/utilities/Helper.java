/**
 * The MIT License (MIT)

Copyright (c) 2016 Pedro Henrique Nascimento Vieira

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package utilities;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class with some helper methods.
 */
public abstract class Helper {
	
	/**
	 * Checks if the input string does not begin with a number.
	 * <br>
	 * This method exists to avoid the use of regex.
	 * @param string
	 * @return true or false
	 */
	public static boolean notNumber(String input){
		boolean notNumber = false;
		try {
			// must not start with a number
			@SuppressWarnings("unused")
			double checker = Double.valueOf(input.substring(0,1));
		}
		catch (Exception e) {
			notNumber = true;			
		}
		return notNumber;
	}

	/**
	 * Tests if the string is not empty and not null.
	 * @param s
	 * @return true or false
	 */
	public static boolean notNullNorEmpty(String s){
		if ((s != null) && (!s.isEmpty())){
    		return true;
    	}
		else {
			return false;
		}
	}
	
	/**
	 * Return the middle point between p1 and p2.
	 * @param pointPlace
	 * @param pointTransition
	 * @return middle point
	 */
	public static Point2D findMiddlePoint(Point2D p1, Point2D p2){
		double x = (p1.getX() + p2.getX())/2;
		
		double y = (p1.getY() + p2.getY())/2;
		
		return (new Point2D.Double(x, y));		
	}
	
	/**
	 * Return the middle point between p1 and p2.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return middle point
	 */
	public Point2D findMiddlePoint(double x1, double y1, double x2, double y2){
		Point2D p1 = new Point2D.Double(x1, y1);
		Point2D p2 = new Point2D.Double(x2, y2);
		return (findMiddlePoint(p1, p2));
	}
	
	/**
	 * Modified from
	 * http://stackoverflow.com/questions/13053061/circle-line-intersection-points
	 * @param pointA
	 * @param pointB
	 * @param center
	 * @param radius
	 * @return intersection points list
	 */
	public static List<Point2D> getCircleLineIntersectionPoint(Point2D pointA,
        Point2D pointB, Point2D center, double radius) {
        double baX = pointB.getX() - pointA.getX();
        double baY = pointB.getY() - pointA.getY();
        double caX = center.getX() - pointA.getY();
        double caY = center.getY() - pointA.getY();

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point2D p1 = new Point2D.Double(pointA.getX() - baX * abScalingFactor1,
        								pointA.getY() - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point2D p2 = new Point2D.Double(pointA.getX() - baX * abScalingFactor2,
        								pointA.getY() - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

}
