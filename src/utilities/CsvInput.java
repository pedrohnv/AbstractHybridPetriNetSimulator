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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVReader;

/**
 * A class to read a csv file as input to the Petri Net.The expected format
 * of the csv file is each line corresponding to reading, and each column
 * to a variable (tidy data).<p>
 * It is used by a "External Place". It reads two values from a line (in the
 * csv file), one being the time and the other the value of interest.<br>
 * If the current time is equal or greater than the value in the line,
 * get the value. That value will be the markings of the place.<p>
 * If the time column to read is '0', then it is considered that all time
 * values will be zero.<p>
 * TODO possible resource leak as the CSVReader is never closed in the code.
 * <p>
 * This class uses the opencsv-3.8 library, which is released under the Apache
 * 2.0 license.
 * @See http://opencsv.sourceforge.net/license.html
 */
public class CsvInput {

	private CSVReader reader;
	
	private Integer timeColumn;
	
	private Integer valueColumn;
	
	/**
	 * First line is assumed to be a header. It is always skipped.<p>
	 * If the time column to read is '0', then it is considered that all time
	 * values will be zero.
	 * @param file
	 * @param timeColumn
	 * @param valueColumn
	 * @throws IOException
	 */
	public CsvInput(File file, int timeColumn, int valueColumn)
			throws IOException {
		this.reader = new CSVReader(new FileReader(file));
		// 0th index is first column.
		this.timeColumn = timeColumn - 1;		
		this.valueColumn = valueColumn - 1;
		reader.readNext();
	}
	
	/**
	 * Get the value of the next line in the csv reader. It will be returned
	 * a string array in which return[0] is time and return[1] is the value
	 * corresponding to that time.<p>
	 * If the time column to read is '0', then it is considered that all time
	 * values will be zero.
	 * @return String[] = {time, value}
	 * @throws IOException
	 */
	public String[] getNextValue() throws IOException{
		
		String [] nextLine; // nextLine[] is an array of values from the line
		
		nextLine = reader.readNext();
		
		String[] values = {nextLine[timeColumn], nextLine[valueColumn]};
		
		if (this.timeColumn == -1){
			values[0] = "0";
		}
		
		return (values);
	}

}
