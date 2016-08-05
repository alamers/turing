package nl.aardbeitje.turing;

public class Instruction {

	private final String state;
	private final boolean write1On0;
	private final boolean forwardOn0;
	private final String stateOn0;
	private final boolean write1On1;
	private final boolean forwardOn1;
	private final String stateOn1;
	private final String special;
	
	public Instruction(String state, String action0, String action1, String special) {

		this.state = state;
		write1On0 = parseBoolean( action0, 0, "0", "1", "Illegal write1-on-0 in state '"+state+"': ");
		forwardOn0 = parseBoolean( action0, 1, "B", "F", "Illegal forward-on-0 in state '"+state+"': ");
		stateOn0 = action0.substring(2);

		write1On1 = parseBoolean( action1, 0, "0", "1", "Illegal write1-on-1 in state '"+state+"': ");
		forwardOn1 = parseBoolean( action1, 1, "B", "F", "Illegal forward-on-1 in state '"+state+"': ");
		stateOn1 = action1.substring(2);
		
		this.special = special;
	}
	
	private boolean parseBoolean(String action, int pos, String falseToken, String trueToken, String msg) {
		String t = action.substring(pos, pos+1);
		if (t.equals(falseToken)) {
			return false;
		} else if (t.equals(trueToken)) {
			return true;
		} else {
			throw new IllegalArgumentException(msg + t);
		}

	}

	public boolean isWrite1On0() {
		return write1On0;
	}

	public boolean isForwardOn0() {
		return forwardOn0;
	}

	public String getStateOn0() {
		return stateOn0;
	}

	public boolean isWrite1On1() {
		return write1On1;
	}
	
	public boolean isWrite1(boolean readOne) {
		return readOne? isWrite1On1() : isWrite1On0();
	}

	public boolean isForwardOn1() {
		return forwardOn1;
	}

	public boolean isForward(boolean readOne) {
		return readOne? isForwardOn1() : isForwardOn0();
	}

	public String getState(boolean readOne) {
		return readOne? getStateOn1() : getStateOn0();
	}

	public String getStateOn1() {
		return stateOn1;
	}

	public String getSpecial() {
		return special;
	}
	
	public String toStringFor0() {
		return ( write1On0?"1":"0") + (forwardOn0?"F":"B") + stateOn0;
	}

	public String toStringFor1() {
		return (write1On1?"1":"0") + (forwardOn1?"F":"B") + stateOn1;
	}

	@Override
	public String toString() {
		return getState()+":  " + toStringFor0() + " " + //
				toStringFor1() + " " + //
				(special==null?"" : special) ;
	}

	public String getState() {
		return state;
	}



	
	

}
