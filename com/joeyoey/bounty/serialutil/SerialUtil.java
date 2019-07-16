package com.joeyoey.bounty.serialutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;


public class SerialUtil {

	
	public static void writeToFile(HashMap<UUID, Double> input, File file) throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		
		out.writeObject(input);
		
		out.close();
	}
	
	
	@SuppressWarnings("unchecked")
	public static HashMap<UUID, Double> readFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		HashMap<UUID, Double> out = new HashMap<UUID, Double>();
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		
		out = (HashMap<UUID, Double>) in.readObject();
		
		in.close();
		
		return out;
	}
	
	
}
