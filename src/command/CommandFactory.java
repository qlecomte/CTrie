package command;

import java.util.HashMap;
import java.util.Vector;

import ctrie.CTrie;
import ctrie.Result;
import ctrie.ValueResult;
import main.ThreadBenchmark;

public class CommandFactory {

	private final HashMap<String, Command>	commands;
	
	private CommandFactory() {
		this.commands = new HashMap<>();
	}

	public void addCommand(String name, Command command) {
		this.commands.put(name, command);
	}
	
	public void executeCommand(String name, String[] args) {
		if ( this.commands.containsKey(name) ) {
			this.commands.get(name).apply(args);
		}
	}

	public void listCommands() {
		// using stream (Java 8)
		System.out.println("Commands enabled :");
		this.commands.keySet().stream().forEach(System.out::println);
	}
	
	public static CommandFactory init() {
		CommandFactory cf = new CommandFactory();
		// commands are added here using lambda. It also possible to dynamically add commands without editing code.
		cf.addCommand("exit", (args) -> cf.exit());
		cf.addCommand("help", (args) -> cf.displayHelp());
		cf.addCommand("insert", (args) -> cf.insert(args));
		cf.addCommand("lookup", (args) -> cf.lookup(args));
		cf.addCommand("remove", (args) -> cf.remove(args));
		cf.addCommand("benchmark", (args) -> cf.benchmark());
		return cf;
	}
	
	public void exit(){
		System.out.println("Program exited");
		System.exit(0);
	}
	
	public void displayHelp(){
		System.out.println("Help : ");
		System.out.println("help - Display help");
		System.out.println("exit - Quit the program");
		System.out.println("insert key value - Insert the pair {key, value}");
		System.out.println("lookup key - Return the value corresponding to the key");
		System.out.println("remove key - Delete the pair that has this key");
		System.out.println("benchmark - Execute the benchmark");
	}
	
	public void insert(String[] args){
		
		if (args.length == 2){
			if(CTrie.getInstance().insert(args[0], args[1]) == Result.OK){
				System.out.println("Info - Insert : Pair {" + args[0] + ", " + args[1] + "} is inserted in the CTrie.");
			}else{
				System.out.println("Error - Insert : Pair {" + args[0] + ", " + args[1] + "} has not been inserted in the CTrie, please try again.");
			}
		}	
		else{
			System.out.println("Error - Insert : You have to write one key and one value to insert");
		}
		
	}
	
	public void lookup(String[] args){	
		if (args.length == 1){
			ValueResult<String> result = CTrie.getInstance().lookup(args[0]);
			if (result.getRes() == Result.OK){
				System.out.println("Info - Lookup : Key " + args[0] + " has " + result.getValue() + " for value in the CTrie.");
			}else if(result.getRes() == Result.NOTFOUND){
				System.out.println("Error - Lookup : Key " + args[0] + " has not been found in the CTrie.");
			}else{
				System.out.println("Error - Lookup : Unknown error");
			}
		}else {
			System.out.println("Error - Lookup : You have to write one key to look up");
		}
	}
	
	public void remove(String[] args){
		if (args.length == 1){
			ValueResult<String> result = CTrie.getInstance().remove(args[0]);
			
			if (result.getRes() == Result.OK){
				System.out.println("Key " + args[0] + " has been deleted from the CTrie.");
			}else if(result.getRes() == Result.NOTFOUND){
				System.out.println("Key " + args[0] + " has not been found in the CTrie.");
			}else{
				System.out.println("Unknown error.");
			}
		}else {
			System.out.println("Error - Remove : You have to write one key to remove");
		}
	}
	
	public void benchmark(){
		System.out.println("Begin benchmark\n");
		
		Vector<String> keys = new Vector<>();
		Vector<Integer> thNbBenchmark = new Vector<>();
		Vector<Integer> operations = new Vector<>();
		
		thNbBenchmark.add(1);
		thNbBenchmark.add(4);
		thNbBenchmark.add(20);
		thNbBenchmark.add(100);
		//thNbBenchmark.add(1000);
		
		operations.add(100);
		operations.add(1000);
		operations.add(10000);
			
		String separateur = "|---------------|---------------|---------------|---------------|";
		
		
		// Insert
		System.out.println(separateur);
		System.out.println("|    Insert\t|   100 ope\t|   1000 ope\t|   10000 ope\t|");
		System.out.println(separateur);
		
		for(int nbThread : thNbBenchmark){
			System.out.print("|  " + nbThread + " threads\t|");
			for (int operation : operations){
				
				if (nbThread > operation){
					System.out.print("  ///////////  |");
					continue;
				}
				
				CTrie.getInstance().clear();
				long timeExec = insertBenchmark(nbThread, operation);
				System.out.print("   " + (timeExec / 1000) + " µs\t|");
			}
			System.out.println("");
			System.out.println(separateur);
		}
		System.out.println("\n\n");
		
		
		// Lookup
		System.out.println(separateur);
		System.out.println("|    Lookup\t|  100 lookup\t|  1000 lookup\t|  10000 lookup\t|");
		System.out.println(separateur);
		
		
		for(int nbThread : thNbBenchmark){
			System.out.print("|  " + nbThread + " threads\t|");
			for (int operation : operations){
				if (nbThread > operation){
					System.out.print("  ///////////  |");
					continue;
				}
				
				CTrie.getInstance().clear();
				keys = setupInsert(4, 3000);
				
				long timeExec = lookupBenchmark(nbThread, operation, keys);
				System.out.print("   " + (timeExec/1000) + " µs\t|");
			}
			System.out.println("");
			System.out.println(separateur);
		}
		System.out.println("\n\n");
		
		
		// Remove
		System.out.println(separateur);
		System.out.println("|    Remove\t|  100 remove\t|  1000 remove\t|  10000 remove\t|");
		System.out.println(separateur);
		
		
		for(int nbThread : thNbBenchmark){
			System.out.print("|  " + nbThread + " threads\t|");
			for (int operation : operations){
				if (nbThread > operation){
					System.out.print("  ///////////  |");
					continue;
				}
				
				CTrie.getInstance().clear();
				keys = setupInsert(10, operation);
				
				long timeExec = removeBenchmark(nbThread, operation, keys);
				System.out.print("   " + (timeExec/1000) + " µs\t|");
			}
			System.out.println("");
			System.out.println(separateur);
		}
		
		System.out.println("\nEnd Benchmark");
	}
	
	private long insertBenchmark(int nbThread, int operation){
		// Instanciate Threads
		Vector<Thread> threads = new Vector<>();
		for (int i = 0; i<nbThread; i++){
			Thread t = new ThreadBenchmark(operation/nbThread, ThreadBenchmark.Type.INSERT);
			threads.add(t);
		}
		
		long startTime = System.nanoTime();
		
		// Start Threads
		for (Thread thread : threads){
			thread.start();
		}
		
		// Join all the threads
		for (Thread thread : threads) {
			  try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long endTime = System.nanoTime();
		
		return endTime-startTime;
	}
	
	private long lookupBenchmark(int nbThread, int operation, Vector<String> keys){
		// Instanciate Threads
		Vector<Thread> threads = new Vector<>();
		for (int i = 0; i<nbThread; i++){
			Thread t = new ThreadBenchmark(operation/nbThread, ThreadBenchmark.Type.LOOKUP, keys);
			threads.add(t);
		}
		
		long startTime = System.nanoTime();
		
		// Start Threads
		for (Thread thread : threads){
			thread.start();
		}
		
		// Join all the threads
		for (Thread thread : threads) {
			  try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long endTime = System.nanoTime();
		
		return endTime-startTime;
	}

	private long removeBenchmark(int nbThread, int operation, Vector<String> keys){
		// Instanciate Threads
		Vector<Thread> threads = new Vector<>();
		for (int i = 0; i<nbThread; i++){
			Thread t = new ThreadBenchmark(operation/nbThread, ThreadBenchmark.Type.REMOVE, keys);
			threads.add(t);
		}
		
		long startTime = System.nanoTime();
		
		// Start Threads
		for (Thread thread : threads){
			thread.start();
		}
		
		// Join all the threads
		for (Thread thread : threads) {
			  try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long endTime = System.nanoTime();
		
		return endTime-startTime;
	}
	
	private Vector<String> setupInsert(int th, int nb){
		Vector<Thread> threads = new Vector<>();
		Vector<String> keys = new Vector<>();
		
		for (int i = 0; i < th; i++){
			ThreadBenchmark t = new ThreadBenchmark(nb, ThreadBenchmark.Type.INSERT);
			threads.add(t);
			t.start();
		}
		
		for (Thread t : threads){
			try {
				t.join();
				keys.addAll( ((ThreadBenchmark)t).getKeys() );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return keys;
	}
}
