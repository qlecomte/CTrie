package main;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import command.CommandFactory;
import ctrie.CTrie;
import ctrie.Result;
import ctrie.ValueResult;

public class Interpreter {
	
	CommandFactory cf;
	
	public Interpreter(){
		cf = CommandFactory.init();
	}
	
	public void scanner(){
		Scanner sc = new Scanner(System.in);
		
		while (true){
			System.out.print("> ");
			String commande = sc.nextLine();
			if (interpreteurCommande(commande) == -1){
				break;
			}
		}
		
		sc.close();
	}
	
	/**
	 * @param commandLine
	 * @return -1 if the command is "exit" (to exit the program), 0 otherwise
	 */
	public int interpreteurCommande(String commandLine){
		String[] parse = commandLine.split(" ");
		String command = parse[0];
		String[] args = Arrays.copyOfRange(parse,1,parse.length);
		
		/* Exit command */
		if (command.equalsIgnoreCase("exit")){
			return -1;
		}else {
			cf.executeCommand(command, args);
			return 0;
		}
		
		
	}
}
