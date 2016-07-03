package nl.aardbeitje.turing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class Program {

	private final Map<String,Instruction> instructions;
	public Program(InputStream in) throws IOException {
		instructions = new LinkedHashMap<>();
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line = r.readLine();
		while (line!=null) {
			parseLine(line);
			line = r.readLine();
		}
	}
	private void parseLine(String line) {
		
		if (line.isEmpty() ||line.startsWith("#")) {
			return;
		}
		
		line = line.replace("\t", " ");
		while (line.contains("  ")) {
			line = line.replace("  ", " ");
		}

		String[] parts = line.split(" ");
		String state = parts[0].substring(0, parts[0].length()-1);
		if (!parts[0].substring(parts[0].length()-1, parts[0].length()).equals(":")) {
			throw new IllegalArgumentException("Expected ':' to terminate state name in state '"+state+"'");
		}
		String action0 = parts[1];
		String action1 = parts[2];
		String special = (parts.length>3)? parts[3] : null;

		getInstructions().put( state, new Instruction(state, action0, action1, special));
		
	}
	public Map<String,Instruction> getInstructions() {
		return instructions;
	}
	
}