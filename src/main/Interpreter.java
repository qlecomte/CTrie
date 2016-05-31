package main;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import ctrie.CTrie;
import ctrie.Result;
import ctrie.ValueResult;

public class Interpreter {
	
	public static void scanner(){
		Scanner sc = new Scanner(System.in);
		
		while (true){
			System.out.print("> ");
			String commande = sc.nextLine();
			if (interpreteurCommande(commande) == -1){
				break;
			}
		}
		
		System.out.println("Exited the program");
		sc.close();
	}
	
	/**
	 * @param commandLine
	 * @return -1 if the command is "exit" (to exit the program), 0 otherwise
	 */
	private static int interpreteurCommande(String commandLine){
		String[] parse = commandLine.split(" ");
		String command = parse[0];
		String[] arg = Arrays.copyOfRange(parse,1,parse.length);
		
		/* Exit command */
		if (command.equalsIgnoreCase("exit")){
			return -1;
		} 
		
		/* Help command */
		else if (command.equalsIgnoreCase("help")){
			displayHelp();
		} 
		
		/* Insert key value command */
		else if (command.equalsIgnoreCase("insert")){
			if (arg.length == 2){
				insert(arg[0], arg[1]);
			} else if (arg.length == 0){
				System.out.println("Key & Value are missing.");
			} else if (arg.length == 1){
				System.out.println("Value is missing.");
			} else{
				System.out.println("Too much arguments, you need to put only one key and one value (2 arguments).");
			}
		} 
		
		/* Lookup key command */
		else if (command.equalsIgnoreCase("lookup")){
			if (arg.length == 1){
				lookup(arg[0]);
			} else if (arg.length == 0){
				System.out.println("Key is missing.");
			} else{
				System.out.println("Too much arguments, you need to put only one key.");
			}
		} 
		
		/* Remove key command */
		else if (command.equalsIgnoreCase("remove")){
			if (arg.length == 1){
				remove(arg[0]);
			} else if (arg.length == 0){
				System.out.println("Key is missing.");
			} else{
				System.out.println("Too much arguments, you need to put only one key.");
			}
		} 
		
		/* Benchmark command */
		else if (command.equalsIgnoreCase("benchmark")){
			benchmark();
		} 
		
		/* Unknown command */
		else {
			System.out.println("Command unknown, type help to display help.");
		}
		
		return 0;
	}
	
	
	
	private static void displayHelp(){
		System.out.println("Help : ");
		System.out.println("exit - quit the program");
		System.out.println("insert key value - Insert the pair {key, value}");
		System.out.println("lookup key - Return the value corresponding to the key");
		System.out.println("remove key - Delete the pair that has this key");
		System.out.println("benchmark - Execute the benchmark");
	}
	
	private static void insert(String key, String value){
		if(CTrie.getInstance().insert(key, value) == Result.OK){
			System.out.println("Pair {" + key + ", " + value + "} is inserted in the CTrie.");
		}else{
			System.out.println("Pair {" + key + ", " + value + "} has not been inserted in the CTrie, please try again.");
		}
	}
	
	private static void lookup(String key){
		ValueResult<String> result = CTrie.getInstance().lookup(key);
		
		if (result.getRes() == Result.OK){
			System.out.println("Key " + key + " has " + result.getValue() + " for value in the CTrie.");
		}else if(result.getRes() == Result.NOTFOUND){
			System.out.println("Key " + key + " has not been found in the CTrie.");
		}else{
			System.out.println("Unknown error.");
		}
	}
	
	private static void remove(String key){
		ValueResult<String> result = CTrie.getInstance().lookup(key);
		
		if (result.getRes() == Result.OK){
			System.out.println("Key " + key + " has been deleted from the CTrie.");
		}else if(result.getRes() == Result.NOTFOUND){
			System.out.println("Key " + key + " has not been found in the CTrie.");
		}else{
			System.out.println("Unknown error.");
		}
	}
	
	private static void benchmark(){
		System.out.println("Begin benchmark");

		int threadNumber = 2;
		Vector<Thread> vectorThread = new Vector<>();
		
		for (int i = 0; i<threadNumber; i++){
			Thread t = new ThreadBenchmark(5);
			vectorThread.add(t);
			
			t.start();
		}
		
		
		
		
	}
}
