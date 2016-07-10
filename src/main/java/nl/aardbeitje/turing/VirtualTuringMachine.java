package nl.aardbeitje.turing;

public class VirtualTuringMachine  implements TuringMachine {

	private String tape;
	private int pos = 0;

	public VirtualTuringMachine(String initialTape) {
		this.tape = initialTape;
	}

	@Override
	public boolean readOnly() {
		pause();
		boolean tmp = tape.charAt(pos)=='1';
		System.out.println("read " + (tmp?"1":"0"));
		return tmp;
	}

	@Override
	public void readReturn() {
		pause();
	}

	@Override
	public void write(boolean current, boolean newValue) {
		pause();
		String before = tape.substring(0, pos);
		String after = tape.substring(pos+1, tape.length());
		tape = before + (newValue?"1":0) + after;
		System.out.println(tape);
	}

	@Override
	public void move(boolean forward) {
		pause();
		if (forward) {
			pos++;
		} else {
			pos--;
		}
		System.out.println(String.format("%" + pos + "s^", "")); 
	}
	
	private void pause() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
