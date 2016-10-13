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

/**
 * A class to create a log stream. May be printed to console or a panel.
 */
public abstract class LogText {
	
	private static String log = "";
	
	/**
	 * Set to false if generated log will be too big. This way the memory
	 * does not get chewed up; also there is a saving in processing time.
	 */
	private static Boolean generateLog = true;
	
	/**
	 * Append message for latter printing.
	 * @param log
	 */
	public static void appendMessage(String message){
		
		if (generateLog) {
			StringBuilder strBuilder = new StringBuilder(log);
			
			strBuilder.append(message + "\n");
			
			log = strBuilder.toString();
		}		
		
	}
	
	/**
	 * Get message.
	 * @return log
	 */
	public static String getMessage(){
		return log;
	}
	
	/**
	 * Clear message.
	 */
	public static void clearLog(){
		log = "";
	}

	public static Boolean getGenerateLog() {
		return generateLog;
	}

	public static void setGenerateLog(Boolean generateLog) {
		LogText.generateLog = generateLog;
	}
}
