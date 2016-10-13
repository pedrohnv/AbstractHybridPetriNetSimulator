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
package hybridPetriNet.places;

import java.io.File;
import java.io.IOException;

import enums.PlaceType;
import hybridPetriNet.Evolution;
import utilities.AdaptedEvaluator;
import utilities.CsvInput;

/**
 * A place that takes it's values from a csv file. This place has no
 * specific capacity; as such, any markings value is valid. <p>
 * The values are read at each iteration update.<p> 
 * If the simulation is stopped, the setFileToRead method should be invoked
 * again, because nowhere there is a reset to the CsvInput reader.
 */
public class ExternalPlace extends Place {

	private CsvInput reader;
	
	protected static AdaptedEvaluator evaluator = new AdaptedEvaluator();
	
	public ExternalPlace(String name) {
		super(name);
		this.type = PlaceType.EXTERNAL;
	}
	
	/**
	 * Sets a file to read values from along with the column to be referenced
	 * as time values and the column of the markings in the place.
	 * @param file
	 * @param timeColumn
	 * @param valueColumn
	 * @throws IOException 
	 */
	public void setFileToRead(File file, int timeColumn, int valueColumn)
			throws IOException{
		this.reader = new CsvInput(file, timeColumn, valueColumn);
		timeUpdate();
	}
	
	@Override
	public boolean checkValidMarkings(double newValue){
		// any value is acceptable
		return true;
	}
		
	@Override
	public void timeUpdate(){
		if (reader != null){
			String[] values;
			
			try {
				values = reader.getNextValue();
				
				double time = evaluator.evaluate(values[0]);
				
				if (time >= Evolution.getTime()){
					double newMarking = evaluator.evaluate(values[1]);

					this.changeMarkings(newMarking);
				}
			}
			catch (NullPointerException e){
				// usually this means there are no more values to read
				// do nothing
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
