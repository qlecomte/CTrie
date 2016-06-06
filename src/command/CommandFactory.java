package command;

import java.util.HashMap;
import java.util.Vector;

import ctrie.CTrie;
import ctrie.Result;
import ctrie.ValueResult;
import main.Interpreter;
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
		System.out.println("Begin benchmark");

		int threadNumber = 1;
		Vector<Thread> vectorThread = new Vector<>();
		
		for (int i = 0; i<threadNumber; i++){
			Thread t = new ThreadBenchmark(15, i);
			vectorThread.add(t);
			
			t.start();
		}
		
	}
	
}
