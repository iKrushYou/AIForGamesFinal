/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 *
 * @author alex
 */
public class GameBoard {
    private int firstMove; // which player went first
    private int currentPlayer; // keeps track of current player

    public final String player1Name; // self-explanatory
    private final String player2Name; // self-explanatory

    BoardState boardState; // current board state held in object
    private int gameWinner; // did someone win (-1 for no, 1 or 2 for yes)

    public static final int USER = 1; // define int value for user
    public static final int COMPUTER = 2; // definte int value for computer
    public static final int TIE = 3; // definte int value for computer

    public static final int WIDTH = 9; // game board width
    public static final int HEIGHT = 9; // game board height

    private String gameMessage = ""; // message displayed at the top

    // globals for game statistics
    public long totalTime = 0;
    public Boolean cutoffOccurred = false;
    public int depthReached = 0;
    public long nodesExplored = 0;

    public int timeCutoff = 100000;
    public int depthCutoff = 3;
    public long startTime = 0;

    public String moveValues = "";

    public int difficulty = 3;

    public GameBoard(String player1Name, String player2Name, int firstMove) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        resetGame(firstMove);
    }

    public void resetGame() {
        resetGame(0);
    }

    // reset the game board
    public final void resetGame(int firstMove) {
        this.firstMove = firstMove;
        this.currentPlayer = firstMove;
        this.boardState = new BoardState(WIDTH, HEIGHT);
        this.gameWinner = -1;

        updateMessage();
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentPlayerName() {
        return getPlayerName(currentPlayer);
    }

    public String getPlayerName(int player) {
        if (player == USER) return player1Name;
        else return player2Name;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public String getGameMessage() {
        return gameMessage;
    }

    public int[] getMoves() {
        return boardState.moves;
    }

    public int getGameWinner() {
        return gameWinner;
    }

    public static int otherPlayer(int player) {
        if (player == USER) return COMPUTER;
        else return USER;
    }

    public void switchPlayers() {
        currentPlayer = otherPlayer(currentPlayer);
    }

    // get X or O for the current player
    public String getMoveForPlayer(int player) {
        if (player == USER) return "O";
        if (player == COMPUTER) return "X";
        return " ";
    }

    public void playerMove(int position) {
        if (boardState.makeMove(USER, position)) {
            switchPlayers();
        }

        updateMessage();
    }

    // algorith for computer move using A-B pruning
    public void computerMove() {
        computerMove(COMPUTER);
    }

    public void computerMove(int player) {
        computerMove(player, 0);
    }

    public void computerMove(int player, int type) {
        if (gameWinner != -1) return;

        totalTime = 0;

        startTime = System.currentTimeMillis();

        AIAgent agent;
        if (type == 0) {
            agent = new MiniMaxAgent(boardState, player);
            agent.depthCutoff = depthCutoff;
            agent.timeCutoff = timeCutoff;
        } else if (type == 1) {
            agent = new RandomAgent(boardState, player);
        } else {
            agent = new MiniMaxAgentAG(boardState, player);
            agent.depthCutoff = depthCutoff;
            agent.timeCutoff = timeCutoff;
        }

        int move = agent.getMove();

        cutoffOccurred = agent.cutoffOccurred;
        nodesExplored = agent.nodesExplored;
        depthReached = agent.depthReached + 1;

        boardState.makeMove(player, move);

        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Computer move took " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds to explore " + addCommas(nodesExplored) + " nodes.");

        switchPlayers();

        updateMessage();
    }

    // function to add 1000s commas to integers
    public String addCommas(long input) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String numberAsString = numberFormat.format(input);
        return numberAsString;
    }

    // button pressed handler
    // if no winner, let the player move
    // after player moves, call again to trigger computer move
    public void buttonPressed(int c, int r) {
//        System.out.println("button pressed (" + c + ", " + r + ")");

        if (gameWinner == -1) {
            if (currentPlayer == USER) {
                playerMove(c + r * WIDTH);
            } else {
                computerMove();
            }
        }

        updateMessage();
    }

    public void userMoveInput(String move) {
        int position = getPositionForMove(move);
        if (gameWinner == -1) {
            if (currentPlayer == USER) {
//                playerMove(position);
                computerMove(USER, 0);
            } else {
                computerMove(COMPUTER, 2);
            }
        }

        updateMessage();
    }

    public int getPositionForMove(String move) {
        int board = Integer.parseInt(move.substring(0, 1)); //Integer.parseInt(move.substring(0, 1), 34) - 10;
        int position = Integer.parseInt(move.substring(1, 2));

        return board * HEIGHT + position;
    }

    // update the global message based on the game stats
    private void updateMessage() {
        for (int i = 0; i < 9; i++) {
            if (boardState.checkWinBoard(USER, i)) boardState.wins[i] = USER;
            else if (boardState.checkWinBoard(COMPUTER, i)) boardState.wins[i] = COMPUTER;
            else if (boardState.checkTieBoard(i)) boardState.wins[i] = TIE;
            else boardState.wins[i] = 0;
        }

        if (boardState.checkWinGame(USER)) {
            gameWinner = USER;
            System.out.println("USER wins");
        } else if (boardState.checkWinGame(COMPUTER)) {
            gameWinner = COMPUTER;
            System.out.println("COMPUTER wins");
        } else if (boardState.checkTieGame()) {
            gameWinner = 3;
            System.out.println("It's a tie");
        }

        switch (gameWinner) {
            case USER:
            case COMPUTER:
                gameMessage = getPlayerName(gameWinner) + " has won!";
                break;
            case 0:
                gameMessage = "It's a tie";
                break;
            default:
                gameMessage = "It is " + getPlayerName(currentPlayer) + "'s turn";
                break;
        }
    }

    public void printBoard() {
        int position = 0;
        for (int l = 0; l < 3; l++) {
            for (int k = 0; k < 3; k++) {
                String line = "";
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        String out = getMoveForPlayer(getMoves()[position]);
                        line += out;
                        if (j < 2) line += " | ";
                        else line += " ";
                        position++;

                    }
                    position += 6;
                }
                System.out.println(line);
                if (k < 2) {
                    for (int i = 0; i < line.length() - 1; i++) {
                        if (i != 9 && i != 19)
                            System.out.print("-");
                        else
                            System.out.print(" ");
                    }
                }
                System.out.println();
                position -= 24;
            }
            position += 18;
        }
    }
}
