package nl.aardbeitje.turing;

import lejos.hardware.ev3.EV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * Represents the Lego Turing Machine which has a few simple instructions to be
 * used. It needs calibration values. It has an internal calibration mechanism
 * but it can also load the values from a File.
 *
 */
public class LocalLegoTuringMachine extends EV3TuringMachine implements TuringMachine {

	private final EV3 ev3;
	private final RegulatedMotor writer;
	private final RegulatedMotor readerArm;
	private final RegulatedMotor tape;
	private final EV3ColorSensor sensor;

	public LocalLegoTuringMachine(EV3 ev3) {
		this.ev3 = ev3;

		writer = new EV3LargeRegulatedMotor(ev3.getPort(WRITER_PORT.getName()));
		readerArm = new EV3LargeRegulatedMotor(ev3.getPort(READER_ARM_PORT.getName()));
		tape = new EV3MediumRegulatedMotor(ev3.getPort(TAPE_PORT.getName()));
		readerArm.setSpeed(READER_ARM_SPEED);
		sensor = new EV3ColorSensor(ev3.getPort(READER_PORT.getName()));
		sensor.setCurrentMode("Red");
	}

	@Override
	protected SampleProvider getSensor() {
		return sensor;
	}
	
	@Override
	protected void drawString(String txt, int x, int y) {
		ev3.getTextLCD().drawString(txt, x, y);
	}	
	@Override
	protected void setLEDPattern(int i) {
		ev3.getLED().setPattern(i);
	}
	
	@Override
	public void readPosition() {
			readerArm.rotate(READ_ANGLE);
	}

	@Override
	public void readReturn() {
			readerArm.rotate(-READ_ANGLE);
	}

	@Override
	protected void write1() {
			writer.rotate(WRITE_ANGLE);
	}

	/**
	 * Writes a 0. Make sure there is a 1 currently written or else we'll have a
	 * mechanical failure.
	 */
	@Override
	protected void write0() {
			writer.rotate(-WRITE_ANGLE);
	}

	/**
	 * Move the tape 1 position back to the beginning.
	 */
	@Override
	public void backward() {
			tape.rotate(-DEGREES_PER_BIT);
	}

	/**
	 * Move the tape 1 position further up the tape.
	 */
	@Override
	public void forward() {
			tape.rotate(DEGREES_PER_BIT);
	}

}
