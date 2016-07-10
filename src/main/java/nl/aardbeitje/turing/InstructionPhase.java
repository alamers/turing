package nl.aardbeitje.turing;

public class InstructionPhase {

	public enum Phase {
		READING, DECIDING, WRITING, MOVING, CHANGING
	}
	private final boolean readOne;
	private final Phase phase;
	
	
	public InstructionPhase(Phase p) {
		if (p!=Phase.READING) {
			throw new IllegalArgumentException("Any non-reading state requires the read param to be set");
		}
		this.readOne = false;
		this.phase = p;
	}

	public InstructionPhase(Phase p, boolean readOne) {
		this.phase = p;
		this.readOne = readOne;
	}

	public boolean isReadOne() {
		return readOne;
	}

	public Phase getPhase() {
		return phase;
	}
}
