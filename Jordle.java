import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import java.util.Random;
import java.util.Arrays;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.PauseTransition;
import javafx.scene.layout.GridPane;

// javac --module-path javafx-sdk-11.0.2/lib/ --add-modules=javafx.controls Jordle.java
// java --module-path javafx-sdk-11.0.2/lib/ --add-modules=javafx.controls Jordle
/**
 * A class representing the Jordle Application.
 * @author Gal Ovadia
 * @version 1.0
 */
public class Jordle extends Application {
    // Declaring some variables for later:
    int currentCol;
    int currentRow;
    int length = 5;
    GridPane grid;
    String answer = "AUDIO";
    String[] answerArray = answer.split("");
    boolean gameActive;
    Text title = new Text("Jordle");
    Label subTitle = new Label("Guess a word!");
    boolean validWordCheck = true;
    VBox layout = new VBox(20);
    HBox bottomLayout = new HBox(20);
    Color greenColor = Color.rgb(121, 167, 107);
    Color yellowColor = Color.rgb(198, 180, 102);
    String[] words = {"HELLO", "APPLE", "AUDIO", "SOARE", "BRICK", "JUMPY"};

    /**
     * Main method for Jordle.
     * @param args String array of arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method that initializes the game.
     * @param primaryStage main stage on which Jordle is played.
     */
    public void start(Stage primaryStage) {
        currentCol = 0;
        currentRow = 0;
        gameActive = true;
        randomizeAnswer();
        primaryStage.setTitle("Jordle");
        title.setFont(Font.font("Roboto Bold;", 60));
        title.setStyle("-fx-font-weight: bold");
        subTitle.setFont(Font.font("Helvetica", 25));

        Button instructionBtn = new Button("Instructions");
        instructionBtn.setFocusTraversable(false);

        // Anonymous inner class for instructions
        instructionBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Stage popup = new Stage();
                instructionBtn.setDisable(true);
                Text instructionText = new Text("Welcome to Jordle! You have 6 tries to guess the secret word (^O^)"
                        + "\n\nJust like in the real Wordle, for each guess you make the letter will be marked gray"
                        + "\nif it is not present in the secret word, yellow if it is present in the secret word but"
                        + " not\nin the correct position, and green if the letter is present and in the"
                        + " correct position.\n\nBest of luck, and happy Jordling!\n\n-Gal Ovadia");
                VBox instructionLayout = new VBox();
                instructionLayout.setAlignment(Pos.CENTER);
                instructionLayout.getChildren().add(instructionText);
                Scene instructionScene = new Scene(instructionLayout, 550, 200);
                popup.setScene(instructionScene);
                popup.setAlwaysOnTop(true);
                popup.show();
                popup.setResizable(false);
                instructionBtn.setDisable(true);
                popup.focusedProperty().addListener((a, whenHidden, whenShown) -> {
                    if (!popup.isFocused()) {
                        popup.close();
                        instructionBtn.setDisable(false);
                    }
                });
            }
        });

        // Lambda function for resetting Jordle
        Button resetBtn = new Button("Reset");
        resetBtn.setFocusTraversable(false);
        resetBtn.setOnAction(e1 -> {
            randomizeAnswer();
            primaryStage.setMinWidth(Math.max(85 * length + 175, 600));
            primaryStage.setWidth(Math.max(85 * length + 175, 600));
            restart();
        });

        // Lambda function for creating custom Jordle
        Button customBtn = new Button("Make your own!");
        customBtn.setFocusTraversable(false);
        customBtn.setOnAction(e -> {

            Stage popup = new Stage();
            customBtn.setDisable(true);
            Label instructionText = new Label("Want to make your own custom Jordle to challenge your friend?\nEnter "
                    + "a word down below, and make your own wordle!\n\n(Hint: your word doesn't need to "
                    + "be exactly 5 letters!)");
            instructionText.setAlignment(Pos.CENTER);
            TextField textField = new TextField();
            textField.setMaxWidth(100);
            textField.setAlignment(Pos.CENTER);
            Label errorText = new Label("");
            errorText.setTextFill(Color.RED);
            Button submitBtn = new Button("Start!");
            submitBtn.setFocusTraversable(false);
            submitBtn.setOnAction(e2 -> {
                textField.requestFocus();
                String formatted = textField.getText().trim().toUpperCase();
                textField.setText("");
                boolean valid = true;
                String[] formattedArray = formatted.split("");
                for (String letter : formattedArray) {
                    if (!("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(letter))) {
                        valid = false;
                        break;
                    }
                }
                if (valid && formatted.length() != 0 && formatted.length() < 11) {
                    length = formatted.length();
                    restart();
                    primaryStage.setMinWidth(Math.max(85 * length + 175, 600));
                    primaryStage.setWidth(Math.max(85 * length + 175, 600));
                    answer = formatted;
                    answerArray = answer.split("");
                    popup.close();
                } else if (formatted.length() > 9) {
                    errorText.setText("That word is too long!");
                } else {
                    errorText.setText("Invalid word!");
                }
            });

            VBox popuplayout = new VBox(10);
            popuplayout.setAlignment(Pos.CENTER);
            popuplayout.getChildren().addAll(instructionText, textField, errorText, submitBtn);

            Scene popupScene = new Scene(popuplayout, 400, 200);
            popup.setScene(popupScene);
            popup.setAlwaysOnTop(true);
            popup.setResizable(false);
            customBtn.setDisable(true);
            popup.focusedProperty().addListener((a, whenHidden, whenShown) -> {
                if (!popup.isFocused()) {
                    popup.close();
                    customBtn.setDisable(false);
                }
            });
            popup.show();

            textField.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
                if (key.getCode().toString().equals("ENTER")) {
                    submitBtn.fire();
                }
            });
        });

        // Put everything on the screen
        grid = createGrid();
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.getChildren().addAll(instructionBtn, resetBtn, customBtn);
        layout.getChildren().addAll(title, subTitle, createGrid(), bottomLayout);
        layout.setAlignment(Pos.CENTER);
        Scene mainScene = new Scene(layout, 600, 700);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(Math.max(85 * length + 175, 600));
        primaryStage.setMinHeight(770);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Event handler for when a key is pressed
        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (gameActive) {
                if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(key.getCode().toString())) {
                    keyPressed(key.getCode().toString());
                } else if (key.getCode().toString().equals("BACK_SPACE")) {
                    backSpacePressed();
                } else if (key.getCode().toString().equals("ENTER")) {
                    enterPressed();
                }
            }

        });
    }


    /**
     * A method that manages Jordle when a letter key is pressed.
     * @param key String representing the letter input (uppercase).
     */
    private void keyPressed(String key) {
        if (currentCol < length && currentRow < 6) {
            StackPane stack = (StackPane) getNodeFromCoordinate(layout.getChildren().get(2), currentRow, currentCol);
            Label letter = (Label) stack.getChildren().get(1);
            letter.setText(key);
            ScaleTransition st1 = new ScaleTransition(Duration.millis(0), stack);
            st1.setToX(0.7);
            st1.setToY(0.7);
            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), stack);
            st2.setToX(1);
            st2.setToY(1);
            SequentialTransition seqT = new SequentialTransition(st1, st2);
            seqT.play();
            currentCol++;
        }
    }

    /**
     * A method that manages Jordle when backspace is pressed.
     */
    private void backSpacePressed() {
        if (currentCol > 0) {
            currentCol--;
            StackPane stack = (StackPane) getNodeFromCoordinate(layout.getChildren().get(2), currentRow, currentCol);
            Label letter = (Label) stack.getChildren().get(1);
            letter.setText("");
            ScaleTransition st1 = new ScaleTransition(Duration.millis(0), stack);
            st1.setToX(0.7);
            st1.setToY(0.7);
            ScaleTransition st2 = new ScaleTransition(Duration.millis(100), stack);
            st2.setToX(1);
            st2.setToY(1);
            SequentialTransition seqT = new SequentialTransition(st1, st2);
            seqT.play();
        }
    }

    /**
     * A method that manages Jordle when enter is pressed.
     */
    private void enterPressed() {

        if (currentCol == length) {
            subTitle.setText("Guess a word!");
            String[] guess = getGuess();
            validateGuess(getGuess());
            currentRow++;
            currentCol = 0;
        } else {
            ParallelTransition pt = new ParallelTransition();
            for (int i = 0; i < length; i++) {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(30),
                        getNodeFromCoordinate(layout.getChildren().get(2), currentRow, i));
                tt1.setByX(20);
                tt1.setAutoReverse(true);
                tt1.setCycleCount(1);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(10),
                        getNodeFromCoordinate(layout.getChildren().get(2), currentRow, i));
                tt2.setByX(-40);
                tt2.setAutoReverse(true);
                tt2.setCycleCount(1);
                TranslateTransition tt3 = new TranslateTransition(Duration.millis(30),
                        getNodeFromCoordinate(layout.getChildren().get(2), currentRow, i));
                tt3.setByX(20);
                tt3.setAutoReverse(true);
                tt3.setCycleCount(1);
                SequentialTransition seqT = new SequentialTransition(tt1, tt2, tt3);
                pt.getChildren().add(seqT);
            }
            PauseTransition waitForHallOfFame = new PauseTransition(Duration.millis(150));
            pt.getChildren().add(waitForHallOfFame);
            pt.play();
            subTitle.setText("That word isn't long enough!");

        }
    }

    /**
     * A method that randomizes the current answer.
     */
    private void randomizeAnswer() {
        int random = new Random().nextInt(words.length);
        answer = words[random].toUpperCase();
        length = answer.length();
        answerArray = answer.split("");
    }

    /**
     * A method that wipes the current game board clean.
     */
    private void restart() {
        currentCol = 0;
        currentRow = 0;
        gameActive = true;
        subTitle.setText("Guess a word!");
        layout.getChildren().setAll(title, subTitle, createGrid(), bottomLayout);
    }

    /**
     * A method that checks the correctness of a guess, and changes letter colors according (+ plays animations).
     * @param guess String[] of the players guess.
     */
    private void validateGuess(String[] guess) {

        Color[] letterStatus = new Color[length];
        Arrays.fill(letterStatus, Color.GRAY);
        answerArray = answer.split("");

        for (int i = 0; i < guess.length; i++) {
            if (guess[i].equals(answerArray[i])) {
                letterStatus[i] = greenColor;
                answerArray[i] = "_";
                guess[i] = "*";
            }
        }

        for (int i = 0; i < guess.length; i++) {
            for (int j = 0; j < answerArray.length; j++) {
                if (guess[i].equals(answerArray[j])) {
                    letterStatus[i] = yellowColor;
                    answerArray[j] = "_";
                    break;
                }
            }
        }

        SequentialTransition seqT = new SequentialTransition();
        for (int i = 0; i < letterStatus.length; i++) {
            StackPane stack = (StackPane) getNodeFromCoordinate(layout.getChildren().get(2), currentRow, i);
            Label label = (Label) stack.getChildren().get(1);
            Rectangle rect = (Rectangle) stack.getChildren().get(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(190), stack);
            st.setByY(-1);
            seqT.getChildren().add(st);

            FillTransition ft = new FillTransition(Duration.millis(50), rect, Color.WHITE, letterStatus[i]);
            ft.setOnFinished(e -> {
                label.setTextFill(Color.WHITE);
            });
            seqT.getChildren().add(ft);


            StrokeTransition strokeTrans = new StrokeTransition(Duration.millis(50),
                    rect, Color.GRAY, Color.TRANSPARENT);
            seqT.getChildren().add(strokeTrans);


            ScaleTransition st2 = new ScaleTransition(Duration.millis(190), stack);
            st2.setByY(1);
            seqT.getChildren().add(st2);
        }

        boolean x = true;
        if (winCheck(letterStatus)) {
            for (int i = 0; i < length; i++) {
                StackPane stack = (StackPane) getNodeFromCoordinate(layout.getChildren().get(2), currentRow, i);
                TranslateTransition upTransition = new TranslateTransition(Duration.millis(100), stack);
                upTransition.setByY(-15);
                seqT.getChildren().add(upTransition);

                TranslateTransition downTransition = new TranslateTransition(Duration.millis(100), stack);
                downTransition.setByY(15);
                seqT.getChildren().add(downTransition);
            }
            endGame(true);
        } else {
            if (currentRow == 5) {
                gameActive = false;
                endGame(false);
            }
        }
        seqT.play();


    }

    /**
     * A method that manages ending the game based on if the player won or not, and in how many guesses.
     * @param victory boolean of whether the player has won or not.
     */
    private void endGame(boolean victory) {
        if (victory) {
            switch (currentRow) {
            case 0:
                subTitle.setText("Genius! You got the word in just 1 guess.");
                break;
            case 1:
                subTitle.setText("Magnificent! You got the word in 2 guesses.");
                break;
            case 2:
                subTitle.setText("Impressive! You got the word in 3 guesses.");
                break;
            case 3:
                subTitle.setText("Splendid! You got the word in 4 guesses.");
                break;
            case 4:
                subTitle.setText("Great! You got the word in 5 guesses.");
                break;
            case 5:
                subTitle.setText("Phew! You got the word in 6 guesses.");
                break;
            default:
                subTitle.setText("If you are seeing this text, something has gone srriously wrong!");
                break;
            }
        } else {
            subTitle.setText("Sorry, you lost! The word was: " + answer.toUpperCase());
        }
    }

    /**
     * A method that checks if the player correctly guessed.
     * @param letterStatus Color[] of current letter's color.
     * @return whether the player won or not.
     */
    private boolean winCheck(Color[] letterStatus) {
        boolean winCondition = true;
        for (Color color : letterStatus) {
            if (color != greenColor) {
                winCondition = false;
            }
        }
        if (winCondition) {
            gameActive = false;
            return true;
        } else {
            return false;
        }

    }

    /**
     * A helper method to access specific Nodes inside of the main GridPane.
     * @param relevantGrid GridPane that is currently shown on screen.
     * @param row int representing the row of the desired Node.
     * @param col int representing the column of the desired Node.
     * @return Node the desired Node.
     */
    private Node getNodeFromCoordinate(Node relevantGrid, int row, int col) {
        GridPane castedGrid = (GridPane) relevantGrid;
        for (Node node : castedGrid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return node;
            }
        }
        return null;
    }

    /**
     * A helper method that generates a fresh grid upon resetting the game.
     * @return GridPane that has been newly created.
     */
    private GridPane createGrid() {
        GridPane newGrid = new GridPane();
        newGrid.setHgap(5);
        newGrid.setVgap(5);
        for (int row = 0; row <= 5; row++) {
            for (int col = 0; col < length; col++) {
                StackPane tile = new StackPane();
                Rectangle rect = new Rectangle(80, 80);
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.GRAY);
                rect.setStrokeWidth(3);
                Label text = new Label("");
                text.setFont(Font.font("Helvetica", 50.0));
                text.setTextFill(Color.BLACK);
                tile.getChildren().addAll(rect, text);
                newGrid.add(tile, col, row);
            }
        }
        newGrid.setAlignment(Pos.CENTER);
        return newGrid;
    }

    /**
     * A helper method for getting a string array comprised of the letters of the current guess.
     * @return String[] containing the letters of the player's most recent guess.
     */
    private String[] getGuess() {
        String[] output = new String[length];
        for (int col = 0; col < length; col++) {
            output[col] = ((Label) ((StackPane) getNodeFromCoordinate(layout.getChildren().get(2),
                    currentRow, col)).getChildren().get(1)).getText();
        }
        return output;
    }

}
