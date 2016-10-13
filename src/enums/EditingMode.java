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
package enums;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

/**
 * Defines the current mode of editing. Each mode has an associated cursor.
 */
public enum EditingMode {
	PLACE("/userInteraction/icons/place.png", "Place"),
	TRANSITION("/userInteraction/icons/transition.png", "Transition"),
	ARC("/userInteraction/icons/arc.png", "Arc"),
	SELECT(Cursor.getDefaultCursor()),
	SIMULATE("/userInteraction/icons/play.png", "Simulate");
	
	private final Cursor cursor;

	private EditingMode(String fileName, String label){		
		URL path = EditingMode.class.getResource(fileName);
				
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		
		Image image = toolkit.getImage(path);
        
        Point hotspot; // an offset
        if (label.equals("Arc")){
        	hotspot = new Point(4, 4);
        }
        else {
        	hotspot = new Point(16, 16);
        }
        
        this.cursor = toolkit.createCustomCursor(image, hotspot, label);        		
	}
	
	private EditingMode(Cursor cursor){
		this.cursor = cursor;
	}
	
	public Cursor getAssociatedCursor(){
		return this.cursor;
	}
}
