package nl.aardbeitje.turing;

public class InstructionPhase {

	public enum Phase {
		READING, DECIDING, WRITING, MOVING, CHANGING
	}
	private final boolean readZero;
	private final Phase phase;
	
	
	public InstructionPhase(Phase p) {
		if (p!=Phase.READING) {
			throw new IllegalArgumentException("Any non-reading state requires the read param to be set");
		}
		this.readZero = false;
		this.phase = p;
	}

	public InstructionPhase(Phase p, boolean readZero) {
		this.phase = p;
		this.readZero = readZero;
	}

	public boolean isReadZero() {
		return readZero;
	}

	public Phase getPhase() {
		return phase;
	}
}
