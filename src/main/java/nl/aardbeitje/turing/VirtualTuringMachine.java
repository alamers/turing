package nl.aardbeitje.turing;

public class VirtualTuringMachine  implements TuringMachine {

	private String tape;
	private int pos;

	public VirtualTuringMachine(String initialTape, int pos) {
		this.tape = initialTape;
		this.pos = pos;
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
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<pos; i++) {
			sb.append(" ");
		}
		sb.append("^");
		System.out.println(sb.toString()); 
	}
	
	private void pause() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readPosition() {
		pause();
	}
}
