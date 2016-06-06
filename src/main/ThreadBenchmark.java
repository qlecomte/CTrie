package main;

import ctrie.CTrie;
import ctrie.Result;
import ctrie.ValueResult;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadBenchmark extends Thread{
	
	char[] charTable;
	int insertingOpe;
	int threadNumber;
	
	public ThreadBenchmark(int insertingOpe, int n){
		this.insertingOpe = insertingOpe;
		this.threadNumber = n;
		
		charTable = new char[62];
		for (char i = 0; i < 26; ++i){
			charTable[i] = (char) (i + 'A');
			charTable[i+26] = (char) (i + 'a');
		}
		for (char i = 0; i < 10; ++i){
			charTable[i+52] = (char) (i + '0');
		}
	}
	
	public void run() {
		Vector<String> listKey = new Vector<>();
		
		for (int i = 0; i < insertingOpe; i++){
			String key = randomKey();
			listKey.addElement(key);
			
		    String value = randomValue();
			CTrie.getInstance().insert(key, value);
			//System.out.println(/*threadNumber + ":" + i + */" - Key : " + key + ", Value : " + value);
		}
		
		for (String key : listKey){
			ValueResult<String> res = CTrie.getInstance().lookup(key);
			if (res.getRes() == Result.NOTFOUND){
				System.out.println("---- Not found : " + key);
			}else{
				System.out.println(res.getValue());
			}
		}
	    
		System.out.println("END");
	} 
	
	/**
	 * Generate a 8-char random String key
	 * @return A random key, in ASCII
	 */
	private String randomKey(){
		return randomString(8);
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
	
}
