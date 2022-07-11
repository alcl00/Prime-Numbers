import java.lang.Math;
import java.util.LinkedList;

/**
 * Contains methods that can determine whether or not a number is prime 
 * and factorize a number.
 * 
 * @author Alex Levinson
 * @version 03.30.2022
 */
public class Factors {
	
	/**
	 * Takes an integer as a parameter and returns a boolean that determines whether the number
	 * is prime of not.
	 * 
	 * @param number - An integer that will be tested to see if it is prime or not.
	 * @return True if the number is prime, false if it is not.
	 */
	public boolean isPrimeNumber(Integer number)
	{
		double squareRoot = Math.sqrt(number);
		
		for(int i = 2; i <= squareRoot; i++)
		{
			if(number % i == 0)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Takes an integer as a parameter and returns an array of factors.
	 * 
	 * @param number - An integer value that will be factorized.
	 * @return An integer array of the number's factors.
	 */
	public Integer[] factorize(Integer number)
	{
		LinkedList<Integer> factors = new LinkedList<Integer>(); 
		
		for(int i = 1; i <= number; i++)
		{
			if(number % i == 0)
			{
				factors.add(i);
			}
		}
		
		return factors.toArray(new Integer[factors.size()]);
	}
}
