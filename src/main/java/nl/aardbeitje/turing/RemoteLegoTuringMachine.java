package nl.aardbeitje.turing;

import java.rmi.RemoteException;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.SampleProvider;

/**
 * Represents the Lego Turing Machine which has a few simple instructions to be
 * used. It needs calibration values. It has an internal calibration mechanism
 * but it can also load the values from a File.
 *
 */
public class RemoteLegoTuringMachine extends EV3TuringMachine implements TuringMachine {

	private static final Port TAPE_PORT = MotorPort.A;
	private static final Port WRITER_PORT = MotorPort.B;
	private static final Port READER_ARM_PORT = MotorPort.C;
	private static final Port READER_PORT = SensorPort.S1;

	private static final int WRITE_ANGLE = 180;
	private static final int READ_ANGLE = 55;
	private static final int READER_ARM_SPEED = 80;
	private static final int DEGREES_PER_BIT = 1800;

	private final RemoteEV3 ev3;
	private final RMIRegulatedMotor writer;
	private final RMIRegulatedMotor readerArm;
	private final RMIRegulatedMotor tape;
	private final EV3ColorSensor sensor;

	public RemoteLegoTuringMachine(RemoteEV3 ev3) {
		this.ev3 = ev3;

		writer = ev3.createRegulatedMotor(WRITER_PORT.getName(), 'L');
		readerArm = ev3.createRegulatedMotor(READER_ARM_PORT.getName(), 'L');
		tape = ev3.createRegulatedMotor(TAPE_PORT.getName(), 'M');

		try {
			readerArm.setSpeed(READER_ARM_SPEED);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}

		sensor = new EV3ColorSensor(ev3.getPort(READER_PORT.getName()));
		sensor.setCurrentMode("Red");
	}

	@Override
	public void readPosition() {
		try {
			readerArm.rotate(READ_ANGLE);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void readReturn() {
		try {
			readerArm.rotate(-READ_ANGLE);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void write1() {
		try {
			writer.rotate(WRITE_ANGLE);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes a 0. Make sure there is a 1 currently written or else we'll have a
	 * mechanical failure.
	 */
	@Override
	protected void write0() {
		try {
			writer.rotate(-WRITE_ANGLE);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Move the tape 1 position back to the beginning.
	 */
	@Override
	public void backward() {
		try {
			tape.rotate(-DEGREES_PER_BIT);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Move the tape 1 position further up the tape.
	 */
	@Override
	public void forward() {
		try {
			tape.rotate(DEGREES_PER_BIT);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void drawString(String txt, int x, int y) {
		ev3.getTextLCD().drawString(txt, x, y);
	}

	@Override
	protected SampleProvider getSensor() {
		return sensor;
	}

	@Override
	protected void setLEDPattern(int i) {
		ev3.getLED().setPattern(i);
	}

}
