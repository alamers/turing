package nl.aardbeitje.turing;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.filter.LinearCalibrationFilter;
import lejos.robotics.filter.MeanFilter;

/**
 * Represents the Lego Turing Machine which has a few simple instructions to be used.
 * It needs calibration values. It has an internal calibration mechanism but it can also load the values from a File.
 *
 */
public class TuringMachine {

	private static final String CALIBRATION_FILE = "legoturingmachine.calibrate";
	private static final Port TAPE_PORT = MotorPort.A;
	private static final Port WRITER_PORT = MotorPort.B;
	private static final Port READER_ARM_PORT = MotorPort.C;
	private static final Port READER_PORT = SensorPort.S1;

	private static final int WRITE_ANGLE = 180;
	private static final int READ_ANGLE = 49;
	private static final int READER_ARM_SPEED = 80;
	private static final int MEAN_SIZE = 30;
	private static final int DEGREES_PER_BIT = 1800;

	private final RegulatedMotor writer;
	private final RegulatedMotor readerArm;
	private final LinearCalibrationFilter reader;
	private final RegulatedMotor tape;
	
	private boolean calibrated;

	public TuringMachine() {
		calibrated = false;
		
		writer = new EV3LargeRegulatedMotor(WRITER_PORT);
		readerArm = new EV3LargeRegulatedMotor(READER_ARM_PORT);
		tape = new EV3MediumRegulatedMotor(TAPE_PORT);
		readerArm.setSpeed(READER_ARM_SPEED);

		EV3ColorSensor sensor = new EV3ColorSensor(READER_PORT);
		sensor.setCurrentMode("Red");
		MeanFilter mean = new MeanFilter(sensor, MEAN_SIZE);
		reader = new LinearCalibrationFilter(mean);
		reader.setCalibrationType(LinearCalibrationFilter.OFFSET_AND_SCALE_CALIBRATION);
		reader.setScaleCalibration(-1, 1);
	}

	public void calibrate() {
		Button.LEDPattern(6);
		final int samplesPerWrite = 2;
		final int writes = 3;

		reader.startCalibration();
		reader.suspendCalibration();

		float[] tmp = new float[reader.sampleSize()];

		for (int j = 0; j < writes; j++) {
			for (int i = 0; i < samplesPerWrite; i++) {
				readerArm.rotate(READ_ANGLE);

				reader.resumeCalibration();
				for (int k = 0; k < MEAN_SIZE; k++) {
					reader.fetchSample(tmp, 0);
				}
				reader.suspendCalibration();
				for (int k = 0; k < reader.getScaleCorrection().length; k++) {
					LCD.drawString(String.format("c: %2.3f / %2.3f", reader.getScaleCorrection()[k],
							reader.getOffsetCorrection()[k]), 0, k + 1);
				}
				readerArm.rotate(-READ_ANGLE);
			}
			write1();

			for (int i = 0; i < samplesPerWrite; i++) {
				readerArm.rotate(READ_ANGLE);
				reader.resumeCalibration();
				for (int k = 0; k < MEAN_SIZE; k++) {
					reader.fetchSample(tmp, 0);
				}
				reader.suspendCalibration();
				readerArm.rotate(-READ_ANGLE);
			}

			write0();
		}

		reader.stopCalibration();
		
		Button.LEDPattern(0);
		
		calibrated = true;
	}
	
	public void saveCalibration() {
		reader.save(CALIBRATION_FILE);
	}

	public void loadCalibration() {
		reader.open(CALIBRATION_FILE);
		calibrated = true;

	}

	/**
	 * Reads the current bit on the tape. Moves the reader arm in, reads
	 * MEAN_SIZE samples and feeds it through the linear calibration filter to
	 * normalize to (-1,1).
	 * 
	 * @return true for a 1, false for a 0
	 */
	public boolean read() {
		readerArm.rotate(READ_ANGLE);
		float[] tmp = new float[reader.sampleSize()];
		for (int k = 0; k < MEAN_SIZE; k++) {
			reader.fetchSample(tmp, 0);
		}
		LCD.drawString(String.format("read: %2.1f = %b", tmp[0], tmp[0] < 0), 0, 3);
		readerArm.rotate(-READ_ANGLE);
		return tmp[0] < 0;
	}

	/** 
	 * Writes a 1. Pass along the current value which is checked to either write or do nothing.
	 */
	public void write1(boolean current) {
		if(!current) {
			write1();
		}
	}

	/** 
	 * Writes a 0. Pass along the current value which is checked to either write or do nothing.
	 */
	public void write0(boolean current) {
		if(current) {
			write1();
		}
	}

	private void write1() {
		writer.rotate(WRITE_ANGLE);
	}

	/** 
	 * Writes a 0. Make sure there is a 1 currently written or else we'll have a mechanical failure.
	 */
	private void write0() {
		writer.rotate(-WRITE_ANGLE);
	}

	/**
	 * Move the tape 1 position back to the beginning.
	 */
	public void backward() {
		tape.rotate(-DEGREES_PER_BIT);
	}
	
	/**
	 * Move the tape 1 position further up the tape.
	 */
	public void forward() {
		tape.rotate(DEGREES_PER_BIT);
	}
	
}
