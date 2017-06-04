package com.zorgan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	private Cell[][] cell = new Cell[3][3];
	private boolean turnOfX = true;
	private boolean canPlay = true;
	private int track = 0;
	private List<Match> matches = new ArrayList<>();
	VBox root = new VBox(20);
	HBox buttonHolder = new HBox(1);
	GridPane tiles = new GridPane();
	Text status = new Text("Next move: X");
	Button restart = new Button("New Game");
	Button saveGame = new Button("Save Game");
	Button loadGame = new Button("Load Game");
	Label heading = new Label("TicTacToe By Mukunda Bhattarai");

	@Override
	public void start(Stage primaryStage) throws Exception {

		//content alignment
		root.setAlignment(Pos.TOP_CENTER);
		tiles.setAlignment(Pos.CENTER);
		heading.setAlignment(Pos.CENTER);
		
		status.setFont(new Font(25));
		heading.setFont(new Font(30));
		heading.setPrefWidth(Double.MAX_VALUE);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				cell[i][j] = new Cell();

				tiles.add(cell[i][j], i, j);

			}
		}
		
		//populate combination
		populateMatches();

		// assigning css classes
		restart.getStyleClass().addAll("button", "indigo");
		saveGame.getStyleClass().addAll("button", "pink");
		loadGame.getStyleClass().addAll("button", "teal");
		tiles.getStyleClass().add("tiles");
		status.getStyleClass().add("status");
		heading.getStyleClass().add("heading");
		
		restart.setPrefWidth(100);
		
		
		//restart game
		restart.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation");
			alert.setHeaderText("Attempting to start a new game");
			alert.setContentText("Current progress will be lost. Are you ok with this?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				restart();
			} else {
				return;
			}

		});

		// save game
		saveGame.setOnAction(e -> {
			String gameData = getCurrentState();
			SaveAndLoad game = new SaveAndLoad();
			FileChooser fileChooser = new FileChooser();

			// Set extension filter
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TicTacToe files (*.ttt)", "*.ttt");
			fileChooser.getExtensionFilters().add(extFilter);

			// Show save file dialog
			File file = fileChooser.showSaveDialog(primaryStage);

			if (file != null) {
				game.saveGame(gameData, file);
			}
		});

		// load game
		loadGame.setOnAction(e -> {
			String[] gameData;
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TicTacToe files (*.ttt)", "*.ttt");
			fileChooser.getExtensionFilters().add(extFilter);
			File selectedFile = fileChooser.showOpenDialog(null);
			if (selectedFile != null) {
				SaveAndLoad game = new SaveAndLoad();
				try {
					gameData = game.loadGame(selectedFile);
				} catch (Exception e1) {
					return;
				}
				if (gameData == null || gameData.length != 11) {
					status.setText("Invalid Operation!");
				} else {
					restart();
					track = Integer.parseInt(gameData[0]);
					if(track == 8) {
						canPlay = false;
					}
					else if (track % 2 == 0) {
						status.setText("Next move: X");
						turnOfX = true;
						canPlay = true;
					} else {
						status.setText("Next move: O");
						turnOfX = false;
						canPlay = true;
					}

					for (int i = 0; i < 3; i++) {
						for (int j = 0; j < 3; j++) {
							Text loadText = (Text) cell[j][i].getChildren().get(0);
							loadText.setText(gameData[i * 3 + j + 1]);
						}
					}
				}
			}

		});

		// add to button holder
		buttonHolder.setAlignment(Pos.CENTER);
		buttonHolder.getChildren().addAll(restart, saveGame, loadGame);
		root.getChildren().addAll(heading, buttonHolder, tiles, status);

		
		//set scene
		Scene scene = new Scene(root, 600, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("TicTacToe by Mukunda Bhattarai");
		primaryStage.setMaximized(true);

		// add stylesheet file
		scene.getStylesheets().add("com/zorgan/assets/styles.css");
		primaryStage.show();

	}

	// restart game
	private void restart() {
		turnOfX = true;
		canPlay = true;
		track = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Text gettext = (Text) cell[i][j].getChildren().get(0);
				gettext.setText(null);
				status.setText("Next Move: X");
			}
		}

	}

	// retrive current game state
	private String getCurrentState() {
		String gameData = Integer.toString(track);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Text states = (Text) cell[j][i].getChildren().get(0);
				gameData += "\n" + states.getText();
			}
		}
		gameData += "\nEOF";
		return gameData;
	}

	//check if the input matches the combination
	private void check() {
		for (Match match : matches) {
			if (match.isComplete()) {
				canPlay = false;
				trackWinner();
				break;
			} else if (!match.isComplete() && track == 9) {
				canPlay = false;
			}
		}
	}

	
	// determine the winner
	private void trackWinner() {
		if (turnOfX == false) {
			status.setText("X has won the game!");
		} else {
			status.setText("O has won the game!");
		}

	}

	// track the steps of game
	private void track() {
		if (track == 8) {
			canPlay = false;
			status.setText("Game over, Match draw!");
		} else {
			++track;
		}
	}

	
	//populate combination
	public void populateMatches() {
		// horizontal
		for (int i = 0; i < 3; i++) {
			matches.add(new Match(cell[0][i], cell[1][i], cell[2][i]));
		}

		// vertical
		for (int j = 0; j < 3; j++) {
			matches.add(new Match(cell[j][0], cell[j][1], cell[j][2]));
		}

		// diagonal
		matches.add(new Match(cell[0][0], cell[1][1], cell[2][2]));
		matches.add(new Match(cell[2][0], cell[1][1], cell[0][2]));
	}

	// class to create grid cell content, extends stackpane
	private class Cell extends StackPane {

		Text fill = new Text();

		public Cell() {
			fill.setFont(new Font(30));
			this.getChildren().addAll(fill);
			this.setPrefSize(100, 100);
			this.setStyle("-fx-border-color: #888;");
			this.setOnMouseClicked(e -> {
				if (!canPlay)
					return;
				if (turnOfX) {
					setX();
					check();

				} else {
					setO();
					check();
				}

			});
		}

		public String getValue() {
			return fill.getText();
		}

		public void setX() {
			if (getValue() == "") {
				fill.setText("X");
				status.setText("Next move: O");
				turnOfX = false;
				track();
			}
		}

		public void setO() {
			if (getValue() == "") {
				fill.setText("O");
				status.setText("Next move: X");
				turnOfX = true;
				track();
			}
		}

	}

	// class to match the rows , columns and diagonals
	private class Match {
		private Cell[] cells;

		public Match(Cell... cells) {
			this.cells = cells;
		}

		public boolean isComplete() {
			if (cells[0].getValue().isEmpty())
				return false;

			return cells[0].getValue().equals(cells[1].getValue()) && cells[0].getValue().equals(cells[2].getValue());
		}

	}

	
	//main method
	public static void main(String[] args) {
		launch(args);

	}

}
