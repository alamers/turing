package nl.aardbeitje.turing;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.LinearCalibrationFilter;
import lejos.robotics.filter.MeanFilter;

/**
 * Represents the Lego Turing Machine which has a few simple instructions to be
 * used. It needs calibration values. It has an internal calibration mechanism
 * but it can also load the values from a File.
 *
 */
abstract class EV3TuringMachine implements TuringMachine {

	private static final String CALIBRATION_FILE = "legoturingmachine";
	protected static final Port TAPE_PORT = MotorPort.A;
	protected static final Port WRITER_PORT = MotorPort.B;
	protected static final Port READER_ARM_PORT = MotorPort.C;
	protected static final Port READER_PORT = SensorPort.S1;

	protected static final int WRITE_ANGLE = 180;
	protected static final int READ_ANGLE = 55;
	protected static final int READER_ARM_SPEED = 80;
	protected static final int MEAN_SIZE = 30;
	protected static final int DEGREES_PER_BIT = 1800;

	private boolean calibrated;
	private LinearCalibrationFilter reader;

	public EV3TuringMachine() {
		calibrated = false;
	}
	
	private void createCalibrationFilter() {
		MeanFilter mean = new MeanFilter(getSensor(), MEAN_SIZE);
		reader = new LinearCalibrationFilter(mean);
		reader.setCalibrationType(LinearCalibrationFilter.OFFSET_AND_SCALE_CALIBRATION);
		reader.setScaleCalibration(-1, 1);
	}

	public void calibrate() {
		createCalibrationFilter();
		setLEDPattern(6);
		final int samplesPerWrite = 2;
		final int writes = 3;

		reader.startCalibration();
		reader.suspendCalibration();

		float[] tmp = new float[reader.sampleSize()];

		for (int j = 0; j < writes; j++) {
			for (int i = 0; i < samplesPerWrite; i++) {
				readPosition();

				reader.resumeCalibration();
				for (int k = 0; k < MEAN_SIZE; k++) {
					reader.fetchSample(tmp, 0);
				}
				reader.suspendCalibration();
				for (int k = 0; k < reader.getScaleCorrection().length; k++) {
					drawString(String.format("c: %2.3f / %2.3f", reader.getScaleCorrection()[k],
							reader.getOffsetCorrection()[k]), 0, k + 1);
				}
				readReturn();
			}
			write1();

			for (int i = 0; i < samplesPerWrite; i++) {
				readPosition();
				reader.resumeCalibration();
				for (int k = 0; k < MEAN_SIZE; k++) {
					reader.fetchSample(tmp, 0);
				}
				reader.suspendCalibration();
				readReturn();
			}

			write0();
		}

		reader.stopCalibration();

		setLEDPattern(0);
		calibrated = true;
	}

	public void saveCalibration() {
		reader.save(CALIBRATION_FILE);
	}

	public void loadCalibration() {
		createCalibrationFilter();
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
	@Override
	public boolean readOnly() {
		if (!calibrated) {
			throw new IllegalStateException("TuringMachine not yet calibrated");
		}
		readPosition();
		float[] tmp = new float[reader.sampleSize()];
		for (int k = 0; k < MEAN_SIZE; k++) {
			reader.fetchSample(tmp, 0);
		}
		drawString(String.format("read: %2.1f = %b", tmp[0], tmp[0] < 0), 0, 3);
		return tmp[0] < 0;
	}

	/**
	 * Writes the new value. Pass along the current value which is checked to
	 * either write or do nothing.
	 */
	@Override
	public void write(boolean current, boolean newValue) {
		if (!(current == newValue)) {
			if (newValue) {
				write1();
			} else {
				write0();
			}
		}
	}

	/**
	 * Move the tape.
	 * 
	 * @param forward
	 *            true for forward, false for backward
	 */
	@Override
	public void move(boolean forward) {
		if (forward) {
			forward();
		} else {
			backward();
		}
	}
	
	protected abstract void forward();
	protected abstract void backward();
	protected abstract void write1();
	protected abstract void write0();
	protected abstract SampleProvider getSensor();
	protected abstract void setLEDPattern(int i);
	protected abstract void drawString(String txt, int x, int y);

}
