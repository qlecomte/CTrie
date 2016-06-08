package main;

import ctrie.CTrie;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadBenchmark extends Thread{
	
	public static enum Type{
		INSERT, LOOKUP, REMOVE
	}
	

	public ThreadBenchmark(int operation, Type t){
		this.type = t;
		this.operation = operation;
		
		setup();
	}
	
	public ThreadBenchmark(int operation, Type t, Vector<String> keys){
		this.type = t;
		this.operation = operation;
		
		this.keys = keys;
	}
	
	private void setup(){
		charTable = new char[62];
		for (char i = 0; i < 26; ++i){
			charTable[i] = (char) (i + 'A');
			charTable[i+26] = (char) (i + 'a');
		}
		for (char i = 0; i < 10; ++i){
			charTable[i+52] = (char) (i + '0');
		}
		
		keys = new Vector<>();
		values = new Vector<>();
		for (int i = 0; i < operation; ++i){
			keys.add(randomKey());
			values.add(randomValue());
		}
	}
	
	@Override
	public void run() {
		if (type == Type.INSERT){	
			for (int i = 0; i < operation; i++){	
				String key = keys.elementAt(i);
				String value = values.elementAt(i);
				
				CTrie.getInstance().insert(key, value);
			}
		}else if(type == Type.LOOKUP){
			for (int i = 0; i < operation; i++){
				String key = getRandomKey();
				CTrie.getInstance().lookup(key);
			}
		}else if(type == Type.REMOVE){
			for (int i = 0; i < operation; i++){
				String key = getRandomKey();
				CTrie.getInstance().remove(key);
			}
		}
		
		
	} 
	
	public Vector<String> getKeys(){
		return keys;
	}
	
	/**
	 * Generate a 8-char random String key
	 * @return A random key, in ASCII
	 */
	private String randomKey(){
		return randomString(8);
	}
	
	/**
	 * Retrieve a random key from the vector keys
	 * @return A random key, in ASCII
	 */
	private String getRandomKey(){
		return keys.elementAt(ThreadLocalRandom.current().nextInt(0, keys.size()-1));
	}
	
	/**
	 * Generate a 12-char random String value
	 * @return
	 */
	private String randomValue(){
		return randomString(12);
	}

	/**
	 * Generate a random string, using ThreadLocalRandom class
	 * @param charNb Number of caracters of the string to return
	 * @return Randomized string
	 */
	private String randomString(int charNb){
		String stringRandomized = "";
		for (int i = 0; i < charNb; ++i){
			int k = ThreadLocalRandom.current().nextInt(0, 61); // Using [A-Z],[a-z] & [0-9]
			stringRandomized += charTable[k];
		}
		
		return stringRandomized;
	}
	
	private char[] charTable;
	private int operation;
	private Type type;	
	
	private Vector<String> keys;
	private Vector<String> values;
}
