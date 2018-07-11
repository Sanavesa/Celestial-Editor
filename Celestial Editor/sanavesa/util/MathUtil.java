/***************************************************************************************************************************
 * Class:		MathUtil.java
 * Author:		Mohammad Alali
 * 
 * Description:	Contains useful math functions that are not implemented in java's Math class.
 * 	
 * Attributes: 	
 * 				N/A
 * 		
 * Methods:		
 * 				static T clamp(T val, T min, T max)
 * 				static double snapValueFloor(double val, double snap)
 * 				static double snapValue(double val, double snap)
 * 
 ***************************************************************************************************************************/
package sanavesa.util;

/**
 * Contains useful math related functions that are not
 * implemented in {@link Math}.
 * <p>
 * The MathUtil class cannot be instantiated.
 * </p>
 * @author Mohammad Alali
 */
public class MathUtil
{
	/** Private constructor to disallow instantiation of this class */
	private MathUtil() {}
	
	/**
	 * Returns value bounded by [min, max]
	 * @param value		The value to be bounded
	 * @param min		The minimum value of the boundary
	 * @param max		The maximum value of the boundary
	 * @return			min if value < min, or max if value > max, else returns value
	 */
	public static <T extends Comparable<T>> T clamp(T value, T min, T max)
	{
		if(value.compareTo(max) > 0)
			return max;
		else if(value.compareTo(min) < 0)
			return min;
		else
			return value;
	}
	
	/**
	 * Returns <b><code>value</code></b> floored to the nearest <b><code>snap</code></b>.
	 * @param value The value to floor
	 * @param snap	The snapping value to snap to
	 * @return	value floored to the nearest snap
	 */
	public static double snapValueFloor(double value, double snap)
	{
		// Returns value to the nearest snap
		return Math.floor(value / snap) * snap;
	}
	
	/**
	 * Returns <b><code>value</code></b> rounded to the nearest <b><code>snap</code></b>.
	 * @param value The value to round
	 * @param snap	The snapping value to snap to
	 * @return	value rounded to the nearest snap
	 */
	public static double snapValue(double value, double snap)
	{
		// Returns value to the nearest snap
		return Math.round(value / snap) * snap;
	}
}
