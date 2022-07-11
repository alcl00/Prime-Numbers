import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;


 /**
 * 
 * Prompts user to choose between a single threaded program or a program with an unbounded thread. The user will 
 * then input a positive number. After the user inputs the number, the program will loop through 
 * integers between 2 and that number. In each iteration, the program will call a
 * method from the Factors class to determine whether the integer is prime or not. If the integer is prime,
 * it will be added to a List of prime numbers. If it is not prime, it will be factorized and added to a 
 * Map of numbers that are not prime and their factors.
 * 
 * @author Alex Levinson
 * @version 05.02.2022
 *
 */
public class Driver {
	
	/**
	 * User is prompted to choose between a single threaded method or
	 * a method with an unbounded thread. Once the user chooses a method, that method is called.
	 * The user can then keep choosing between a single threaded method, an unbounded thread, or
	 * quitting the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Scanner s = new Scanner(System.in);
		
		boolean running = true;
		
		while(running)
		{
			System.out.println("1) Single Threaded");
			System.out.println("2) Unbounded Thread");
			System.out.println("3) Bounded Threadpool");
			System.out.println("4) Stream");
			System.out.print("Enter a number (q to quit): ");
			String input = s.next();
			
			switch(input) {
			
				case "1":
					singleThreadedMethod(s);
					break;
				
				case "2":
					unboundedThread(s);
					break;
				
				case "3":
					boundedThreadpool(s);
					break;
				
				case "4":
					stream(s);
					break;
					
				case "q":
					running = false;
					break;
				
				default :
					System.out.println("Invalid input");
					break;
			}
			
		}
		
		s.close();

	}
	
	/**
	 * Tests if number is prime or not. If the number is prime, it will be added to a list of
	 * prime numbers. If it is not prime, it will be factorized and added to a map, where the number is
	 * a key and its factors are the values.
	 * 
	 * @param number - number to be tested
	 * @param primeNumbers - List of prime numbers
	 * @param factors - Map of non-prime numbers, where the numbers are keys and the  values are their factors
	 */
	private static void testPrime(int number, List<Integer> primeNumbers, Map<Integer,Integer[]> factors)
	{
		Factors f = new Factors();
		
		if(!f.isPrimeNumber(number))
		{
			factors.put(number, f.factorize(number));
		}
		else
		{
			primeNumbers.add(number);
		}
		
	}
	
	/**
	 * User is prompted to input a positive number. The program will then
	 * run a loop that calls the testPrime method for each integer between 2 and the number.
	 */
	private static void singleThreadedMethod(Scanner s) {
		
		System.out.print("Enter a positive number (-1 to exit): ");
		
		Integer number = s.nextInt();

		if(number > 0)
		{	
			List<Integer> primeNumbers = new ArrayList<Integer>();
			Map<Integer,Integer[]> factors = new HashMap<>();
			
			double totalRuntime = 0;
			
			double startTime = System.currentTimeMillis();
			
			for(int i = 2; i <= number; i++) {
				testPrime(i, primeNumbers, factors);
			}
			
			double endTime = System.currentTimeMillis();
			totalRuntime = endTime-startTime;
			
			System.out.printf("Program executed in %.4f milliseconds\n\n", totalRuntime);
		}
		else
		{
			System.out.println("Number must be positive.\n");
		}
		
	}
		
	
	/**
	 * User is prompted to input a positive number. Once the number is entered, the method will loop
	 * through integers from 2 to the number and generated a new thread for each integer. This thread
	 * calls the testPrime method.
	 */
	private static void unboundedThread(Scanner s) {
			
		System.out.print("Enter a positive number (-1 to exit): ");
		
		int number = s.nextInt();
			
		if(number > 0)
		{		
			List<Integer> primeNumbers = Collections.synchronizedList(new ArrayList<Integer>());
			Map<Integer,Integer[]> factors = Collections.synchronizedMap(new HashMap<Integer,Integer[]>());
			
			double startTime = System.currentTimeMillis();
			
			for(int i = 2; i <= number; i++)
			{
				int n = i;
				
				Thread t = new Thread(() -> testPrime(n, primeNumbers, factors));
				
				t.start();
				
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
				
			double endTime = System.currentTimeMillis();
			double totalRuntime = endTime-startTime;
			
			System.out.printf("Program executed in %.4f milliseconds\n\n", totalRuntime);
		}
		else
		{
			System.out.println("Number must be positive.\n");
		}
			
	}
	
	/**
	 * User is prompted to enter a size for a threadpool. If the user enters zero, then the size will be the 
	 * number of cores on the user's machine plus one. After the size of the threadpool is determined, the user is
	 * prompted to input a positive number. The program will then loop through integers from 2 to user's number where a
	 * callable is used to test whether each integer is prime or not and add it to either a list or a map. The program
	 * will then print the time it took for the program to run.
	 * 
	 * @param s - scanner for user input
	 */
	private static void boundedThreadpool(Scanner s) {
		
		int size = Runtime.getRuntime().availableProcessors() + 1;
		
		System.out.print("Please enter size for threadpool (0 for default size): ");
		
		int input = s.nextInt();
		

		if(input >= 0)
		{
			if(input > 0)
			{
				size = input;
			}
			
			System.out.print("Enter a positive number (-1 to exit): ");
			
			int number = s.nextInt();
				
			if(number > 0)
			{		
				ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
				List<Integer> primeNumbers = Collections.synchronizedList(new ArrayList<Integer>());
				Map<Integer,Integer[]> factors = Collections.synchronizedMap(new HashMap<Integer,Integer[]>());
				
				double startTime = System.currentTimeMillis();
				
				for(int i = 2; i <= number; i++)
				{
					int n = i;
					
					CallableMethod c = new CallableMethod(n, primeNumbers, factors);
					executor.submit(c);
				}
				
				executor.shutdown();
				
				try {
					executor.awaitTermination(60, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double endTime = System.currentTimeMillis();
				double totalRuntime = endTime-startTime;
				
				
				System.out.printf("Program executed in %.4f milliseconds\n\n", totalRuntime);
			}
			else
			{
				System.out.println("Number must be positive.\n");
			}
		}
		else
		{
			System.out.println("Input must be 0 or higher.\n");
		}
		
	}
	
	/**
	 * Prompts user to input a positive number. All numbers between two and this number are then added to a list of numbers, 
	 * which is then used for the testPrimes method being run in a parallel stream. Execution time is recorded and then 
	 * printed to the console along with the number of primes between 2 and the user input.
	 * 
	 * @param s - scanner for user input
	 */
	private static void stream(Scanner s) {
		
		System.out.print("Enter a positive number (-1 to exit): ");
		
		int input = s.nextInt();
		
		if(input > 0)
		{
			List<Integer> primeNumbers = Collections.synchronizedList(new ArrayList<Integer>());
			Map<Integer,Integer[]> factors = Collections.synchronizedMap(new HashMap<Integer,Integer[]>());
			
			List<Integer> numberList = IntStream.rangeClosed(2, input).boxed().collect(Collectors.toList());
			
			Stream<Integer> stream = numberList.parallelStream();
			
			double startTime = System.currentTimeMillis();
			
			stream.forEach(number -> testPrime(number, primeNumbers,factors));
			
			double endTime = System.currentTimeMillis();
			double totalRuntime = endTime-startTime;
			
			
			System.out.println("\nNumber of primes between 2 and " + input + ": " + primeNumbers.size());
			System.out.printf("Program executed in %.4f milliseconds\n\n", totalRuntime);
			
		}
		else
		{
			System.out.println("Number must be positive.");
		}
	}

}

/**
 * Implements callable to override call() method, which takes a number
 * and determines whether it is prime or not. If it is prime, then the number is
 * added to a list of prime numbers. IF it is not, then it is added to a Map as a key, with its
 * factors being values.
 * @author aclev
 *
 */
class CallableMethod implements Callable<Integer>{
	
	private int number;
	private List<Integer> primeNumbers;
	private Map<Integer, Integer[]> factors;
	
	/**
	 * Constructor for CallableMethod
	 * @param number - Number to be tested
	 * @param primeNumbers - List of prime numebers that number is added to if prime.
	 * @param factors - Map of integers and their factors. Number and factors are added if not prime.
	 */
	public CallableMethod(int number, List<Integer> primeNumbers, Map<Integer,Integer[]> factors)
	{
		this.number = number;
		this.primeNumbers = primeNumbers;
		this.factors = factors;
	}

	@Override
	/**
	 * Tests if number is prime or not. If the number is prime, then it is added to a list of prime numbers.
	 * If it isn't prime, then it is added to a map of non-prime numbers as a key, and is then factorized with an array of 
	 * the factors being the value.
	 */
	public Integer call() throws Exception {
		
		Factors f = new Factors();
		
		if(!f.isPrimeNumber(number))
		{
			factors.put(number, f.factorize(number));
		}
		else
		{
			primeNumbers.add(number);
		}
		return number;
	}
}
