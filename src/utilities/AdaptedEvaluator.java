/**
The MIT License (MIT)

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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.Function;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;
import com.fathzer.soft.javaluator.StaticVariableSet;

/**
 * An adapted Double evaluator from javaluator package.
 * <p>
 * It searches for the literal in a variables map before throwing the 
 * Illegal Argument Exception in the toValue method.
 * <p>
 * This way, there is no need to explicitly declare the variable set.
 * <p>
 * There are two additional functions: sign and sqrt.
 * <p>
 * @note Java recognizes 2E2 = 2*10^2 . <br> e = euler's number.
 */
public class AdaptedEvaluator extends DoubleEvaluator {
	
	public static final Function SQRT = new Function("sqrt", 1);
	
	public static final Function SIGN = new Function("sign", 1);
	
	public static final Function EXP = new Function("exp", 1);
	
	public static final Operator E10 = new Operator("E", 2, Operator.Associativity.LEFT, 3);
	
	private static final Parameters PARAMS;
	
	  static {
		    // Gets the default DoubleEvaluator's parameters
		    PARAMS = DoubleEvaluator.getDefaultParameters();
		    // add the new sqrt function to these parameters
		    PARAMS.add(SQRT);
		    PARAMS.add(SIGN);
		    PARAMS.add(E10);
		    PARAMS.add(EXP);
		  }
	
	private static final ThreadLocal<NumberFormat> FORMATTER = new ThreadLocal<NumberFormat>() {
		  @Override
		  protected NumberFormat initialValue() {
		  	return NumberFormat.getNumberInstance(Locale.US);
		  }
	};
	
	private static StaticVariableSet<Double> variables = new StaticVariableSet<Double>();
	
	private static Map <String, Double> variableMap;
	
	public static void setMap(Map<String, Double> markingsMap) {
		AdaptedEvaluator.variableMap = markingsMap;
	}
	
	public AdaptedEvaluator() {
		super(PARAMS);
	}
	
	public AdaptedEvaluator(Parameters parameters) {
		super(parameters);
	}
	
	/**
	 * If literal is not identified, substitute it for it's equivalent
	 * in the variable map.
	 */
	@Override
	protected Double toValue(String literal, Object evaluationContext) {
		
		ParsePosition p = new ParsePosition(0);
		
		Number result = FORMATTER.get().parse(literal, p);
		
		if (p.getIndex()==0 || p.getIndex()!=literal.length()) {
			
			if (variableMap.containsKey(literal)){
				// declare variable
				variables.set(literal, variableMap.get(literal));
				
				literal = variableMap.get(literal).toString();
								
				result = FORMATTER.get().parse(literal, p);
			}			
			else {
				throw new IllegalArgumentException( literal + 
						" is not a number, nor a variable in the variable map");
			}
			
		}
		
		return result.doubleValue();
	}
	
	@Override
	protected Double evaluate(Function function, Iterator <Double> arguments, 
											Object evaluationContext) {
	  if (function == SQRT) {
	    // Implements the new function
	    return Math.sqrt((double) arguments.next());
	    
	  } else if (function == SIGN) {
		// Implements the new function
		    return Math.signum((double) arguments.next());
		    
	  } else if (function == EXP) {
			// Implements the new function
		    return Math.exp((double) arguments.next());
		    
	  } else {
	    // If it's another function, pass it to DoubleEvaluator
	    return super.evaluate(function, arguments, evaluationContext);
	  }
	}
	
	@Override
	protected Double evaluate(Operator operator, Iterator<Double> operands, Object evaluationContext) {
		if (E10.equals(operator)){
			return (operands.next() * Math.pow(10.0, operands.next()));
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
	}
	 
}
