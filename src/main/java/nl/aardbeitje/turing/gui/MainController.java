package nl.aardbeitje.turing.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import lejos.hardware.BrickFinder;
import lejos.remote.ev3.RemoteEV3;
import nl.aardbeitje.turing.Instruction;
import nl.aardbeitje.turing.InstructionPhase;
import nl.aardbeitje.turing.InstructionPhase.Phase;
import nl.aardbeitje.turing.Program;
import nl.aardbeitje.turing.ProgramExecutor;
import nl.aardbeitje.turing.RemoteLegoTuringMachine;
import nl.aardbeitje.turing.TuringMachine;
import nl.aardbeitje.turing.TuringViewer;
import nl.aardbeitje.turing.VirtualTuringMachine;

public class MainController implements TuringViewer {
	private static final String family = "Helvetica";

	@FXML
	private TextFlow currentInstructionTextFlow;

	@FXML
	private MenuItem menuTestInstructionPhases;

	@FXML
	private CheckMenuItem menuTestRunOnDummy;

	@FXML
	private MenuItem menuFileOpen;
	@FXML
	private MenuItem menuFileRun;
	@FXML
	private MenuItem menuFileCalibrate;

	@FXML
	private TableView<Instruction> programTable;

	@FXML
	private TableColumn<Instruction, String> stateTableColumn;

	@FXML
	private TableColumn<Instruction, String> on0TableColumn;

	@FXML
	private TableColumn<Instruction, String> on1TableColumn;

	@FXML
	private TableColumn<Instruction, String> specialTableColumn;

	@FXML
	private ListView<String> logListView;
	ObservableList<String> log = FXCollections.observableArrayList();

	private Stage stage;

	private Program program;

	private RemoteLegoTuringMachine machine;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void initialize() {
		menuTestInstructionPhases.setOnAction(e -> testInstructionPhases());
		menuFileOpen.setOnAction(e -> openFile());
		menuFileRun.setOnAction(e -> runProgram());
		menuFileCalibrate.setOnAction(e -> calibrate());
		logListView.setItems(log);
		logListView.getItems().addListener((ListChangeListener<String>) c -> {
			c.next();
			final int size = logListView.getItems().size();
			if (size > 0) {
				logListView.scrollTo(size - 1);
			}
		});
	}

	private void calibrate() {
		machine = new RemoteLegoTuringMachine((RemoteEV3) BrickFinder.getDefault());
		showCalibrationInit();
		machine.calibrate();
		machine.saveCalibration();
	}

	private void runProgram() {
		Thread t = new Thread() {
			@Override
			public void run() {
				TuringMachine machine = instantiateTuringMachine();
				new ProgramExecutor(program, machine, MainController.this).runProgram();
			}
		};
		t.start();
	}

	protected TuringMachine instantiateTuringMachine() {
		if (menuTestRunOnDummy.isSelected()) {
			return new VirtualTuringMachine("1101110000000000");
		} else {
			if (machine == null || !machine.isCalibrated()) {
				showCalibrationWarning();
				throw new IllegalStateException("Machine not calibrated");
			}
			return machine;
		}
	}

	private void showCalibrationInit() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Calibration");
		alert.setHeaderText("Start calibration");
		alert.setContentText("Check if the reader is on a correct position and the tape reads a 0 at that position");
		alert.showAndWait();
	}

	private void showCalibrationWarning() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Calibration");
		alert.setHeaderText("Calibrate first");
		alert.setContentText(
				"The Turing Machine has not been calibrated yet. Please calibrate first, see the File menu.");
		alert.showAndWait();
	}

	public void openFile() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Turing Machine Program");
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Turing Machine Files", "*.tm"),
					new ExtensionFilter("All Files", "*.*"));
			File selectedFile = fileChooser.showOpenDialog(stage);
			openFile(selectedFile);
			menuFileRun.setDisable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openFile(File selectedFile) {
		try {
			program = new Program(new FileInputStream(selectedFile));
			loadProgram(program);
		} catch (Exception e) {
			try {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Open File Exception");
				alert.setHeaderText("Sorry, couldn't open " + selectedFile.getCanonicalPath());
				alert.setContentText("It failed due to: " + e.getMessage() + ". More info in the console.");
				e.printStackTrace();
				alert.showAndWait();
			} catch (IOException f) {
				f.printStackTrace();
			}
		}

	}

	private void loadProgram(Program p) {
		ObservableList<Instruction> instructions = FXCollections
				.observableList(new ArrayList<>(p.getInstructions().values()));
		stateTableColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getState()));
		on0TableColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().toStringFor0()));
		on1TableColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().toStringFor1()));
		specialTableColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSpecial()));
		programTable.setItems(instructions);
	}

	public void currentInstruction(Instruction i, InstructionPhase ip) {
		Platform.runLater(() -> {

			log(i, ip);
			buildInstructionText(i, ip);
			addInstruction(currentInstructionTextFlow, false, i.isWrite1On0(), i.isForwardOn0(), i.getStateOn0(),
					ip.getPhase(), ip.isReadOne());
			addInstruction(currentInstructionTextFlow, true, i.isWrite1On1(), i.isForwardOn1(), i.getStateOn1(),
					ip.getPhase(), ip.isReadOne());

			programTable.getSelectionModel().select(i);
		});
	}

	@Override
	public void start() {
		Platform.runLater(() -> {
			log.clear();
		});
	}

	private void log(Instruction i, InstructionPhase ip) {
		String line;
		switch (ip.getPhase()) {
		case CHANGING:
			line = "I changed to state " + i.getState(ip.isReadOne());
			break;
		case READING:
			line = "I read the current bit on the tape";
			break;
		case DECIDING:
			line = "It was a " + (ip.isReadOne() ? "1" : "0");
			break;
		case MOVING:
			line = "Moving the tape " + (i.isForward(ip.isReadOne()) ? "forward" : "backward");
			break;
		case WRITING:
			line = "Writing a " + (i.isWrite1(ip.isReadOne()) ? "1" : "0");
			break;
		default:
			line = "Something strange happened";
			break;
		}
		log.add(line + "\n");
	}

	private void buildInstructionText(Instruction i, InstructionPhase ip) {
		Text stateText = new Text(i.getState() + ": ");
		stateText.setFont(Font.font(family, FontWeight.BOLD, 50));
		stateText.setFill(Color.BLACK);

		Text on0Text = new Text(i.toStringFor0() + " ");
		on0Text.setFont(Font.font(family, FontWeight.BOLD, 50));
		on0Text.setFill(Color.BLACK);
		on0Text.setUnderline(ip.getPhase() != Phase.READING && !ip.isReadOne());

		Text on1Text = new Text(i.toStringFor1() + "\n");
		on1Text.setFont(Font.font(family, FontWeight.BOLD, 50));
		on1Text.setFill(Color.BLACK);
		on0Text.setUnderline(ip.getPhase() != Phase.READING && ip.isReadOne());

		Text intro = new Text("First, read the tape. Then:\n");
		intro.setFill(Color.DARKGREY);
		intro.setFont(Font.font(family, 25));
		intro.setUnderline(ip.getPhase() == Phase.READING);

		currentInstructionTextFlow.getChildren().clear();
		currentInstructionTextFlow.getChildren().addAll(stateText, on0Text, on1Text, intro);
	}

	@Override
	public void halt() {
		Platform.runLater(() -> {
			Text ready = new Text("Program completed!");
			ready.setFont(Font.font(family, FontWeight.BOLD, 50));
			ready.setFill(Color.BLACK);

			currentInstructionTextFlow.getChildren().clear();
			currentInstructionTextFlow.getChildren().add(ready);
		});
	}

	private void addInstruction(TextFlow textFlow, boolean is1, boolean isWrite1, boolean isForward, String state,
			Phase p, boolean read1) {
		Text a = new Text("On a ");
		a.setFill(Color.DARKGREY);
		a.setFont(Font.font(family, 25));
		a.setUnderline(p == Phase.READING || (is1 == read1 && p == Phase.DECIDING));
		Text b = new Text(is1 ? "1" : "0");
		b.setFill(Color.BLACK);
		b.setFont(Font.font(family, 25));
		b.setUnderline(p == Phase.READING || (is1 == read1 && p == Phase.DECIDING));
		Text c = new Text(", write a ");
		c.setFill(Color.DARKGREY);
		c.setFont(Font.font(family, 25));
		c.setUnderline(p == Phase.WRITING && is1 == read1);
		Text d = new Text(isWrite1 ? "1" : "0");
		d.setFill(Color.BLACK);
		d.setFont(Font.font(family, 25));
		d.setUnderline(p == Phase.WRITING && is1 == read1);
		Text e = new Text(", move tape ");
		e.setFill(Color.DARKGREY);
		e.setFont(Font.font(family, 25));
		e.setUnderline(p == Phase.MOVING && is1 == read1);
		Text f = new Text(isForward ? "FORWARD" : "BACKWARD");
		f.setFill(Color.BLACK);
		f.setFont(Font.font(family, 25));
		f.setUnderline(p == Phase.MOVING && is1 == read1);
		Text g = new Text(", go to line ");
		g.setFill(Color.DARKGREY);
		g.setFont(Font.font(family, 25));
		g.setUnderline(p == Phase.CHANGING && is1 == read1);
		Text h = new Text(state + "\n");
		h.setFill(Color.BLACK);
		h.setFont(Font.font(family, 25));
		h.setUnderline(p == Phase.CHANGING && is1 == read1);

		textFlow.getChildren().addAll(a, b, c, d, e, f, g, h);
	}

	private String fakeExecuteInstruction(Instruction i) {
		currentInstruction(i, new InstructionPhase(Phase.READING));
		pause();
		currentInstruction(i, new InstructionPhase(Phase.DECIDING, true));
		pause();
		currentInstruction(i, new InstructionPhase(Phase.WRITING, true));
		pause();
		currentInstruction(i, new InstructionPhase(Phase.MOVING, true));
		pause();
		currentInstruction(i, new InstructionPhase(Phase.CHANGING, true));
		pause();
		return i.getStateOn1();
	}

	private void testInstructionPhases() {
		Thread t = new Thread() {
			@Override
			public void run() {
				fakeExecuteInstruction(new Instruction("0", "0F1", "1B2", "halt"));
				fakeExecuteInstruction(new Instruction("1", "1F1", "1B2", "halt"));
			}
		};
		t.start();
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
