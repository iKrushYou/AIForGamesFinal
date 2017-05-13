/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
    public int maxValuePruning = 0;
    public int minValuePruning = 0;

    public int timeCutoff = 10000;
    public int depthCutoff = 0;
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

    private int otherPlayer(int player) {
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
        cutoffOccurred = false;
        nodesExplored = 1;
        depthReached = 0;
        maxValuePruning = 0;
        minValuePruning = 0;

        startTime = System.currentTimeMillis();

        int move = 0;
        if (type == 0) {
            move = getMinimaxMove(boardState, player);
        } else {
            move = getRandomMove(boardState);
        }

        boardState.makeMove(player, move);

        totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Computer move took " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds to explore " + addCommas(nodesExplored) + " nodes.");

        switchPlayers();

        updateMessage();
    }

    private int getMinimaxMove(BoardState state, int player) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        ArrayList<Integer> moveChoices = new ArrayList<>();

        int bestMoveValue = Integer.MIN_VALUE;
        moveValues = "";
        for (int i = 0; i < possibleMoves.size(); i++) {
            int moveValue = minValue(possibleMoves.get(i), -1000, 1000, 0, otherPlayer(player));
            System.out.print(moveValue + ", ");
            moveValues += moveValue + ", ";
            if (moveValue > bestMoveValue) {
                moveChoices = new ArrayList();
                moveChoices.add(i);
                bestMoveValue = moveValue;
            } else if (moveValue == bestMoveValue) {
                moveChoices.add(i);
            }
        }

        Random random = new Random();
        return possibleMoves.get(moveChoices.get(random.nextInt(moveChoices.size()))).lastMove.y;
    }

    private int getRandomMove(BoardState state) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(currentPlayer);

        Random random = new Random();
        return possibleMoves.get(random.nextInt(possibleMoves.size())).lastMove.y;
    }

    // function to add 1000s commas to integers
    public String addCommas(long input) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String numberAsString = numberFormat.format(input);
        return numberAsString;
    }

    private Boolean terminalTest(BoardState state) {
        return state.checkTieGame() || state.checkWinGame(USER) || state.checkWinGame(COMPUTER);
    }

    private int utilityValue(BoardState state, int depth) {
        if (state.checkWinGame(COMPUTER)) {
            return 1000 - depth; // adjust the value with the depth
        }

        if (state.checkWinGame(USER)) {
            return depth - 1000; // adjust the value with the depth
        }

        if (state.checkTieGame()) {
            return 0;
        }

        return 0;
    }

    private int maxValue(BoardState state, int a, int b, int currentDepth, int player) {
        nodesExplored++;

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        if (terminalTest(state)) return utilityValue(state, currentDepth);

        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, player, currentDepth);
        }

        if (currentDepth > depthReached) depthReached = currentDepth;

        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, player, currentDepth);
        }

        int v = Integer.MIN_VALUE;

        for (int i = 0; i < possibleMoves.size(); i++) {
            v = Math.max(v, minValue(possibleMoves.get(i), a, b, currentDepth++, otherPlayer(player)));
            if (v >= b) {
                maxValuePruning++;
                return v;
            }
            a = Math.max(a, v);
        }

        return v;
    }

    private int minValue(BoardState state, int a, int b, int currentDepth, int player) {
        nodesExplored++;

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        if (terminalTest(state)) return utilityValue(state, currentDepth);

        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, otherPlayer(player), currentDepth);
        }

        if (currentDepth > depthReached) depthReached = currentDepth;

        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, otherPlayer(player), currentDepth);
        }

        int v = Integer.MAX_VALUE;

        for (int i = 0; i < possibleMoves.size(); i++) {
            v = Math.min(v, maxValue(possibleMoves.get(i), a, b, currentDepth++, otherPlayer(player)));
            if (v <= a) {
                minValuePruning++;
                return v;
            }
            b = Math.min(b, v);
        }

        return v;
    }

    // evaluation function
    private int evaluateBoard(BoardState boardState, int player, int depth) {
//        return 10;
        int X3 = boardState.checkNumPlays(player, 3);
        int X2 = boardState.checkNumPlays(player, 2);
        int X1 = boardState.checkNumPlays(player, 1);

        int O3 = boardState.checkNumPlays(otherPlayer(player), 3);
        int O2 = boardState.checkNumPlays(otherPlayer(player), 2);
        int O1 = boardState.checkNumPlays(otherPlayer(player), 1);

        int value = (100 + depth) * X3 + (10 + depth) * X2 + depth * X1 - ((100 + depth) * O3 + (10 + depth) * O2 + depth * O1);

        return value;
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
                computerMove(USER, 1);
            } else {
                computerMove();
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
            gameWinner = 0;
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
