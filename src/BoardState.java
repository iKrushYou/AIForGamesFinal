/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author alex
 */
public class BoardState {
    int[] moves;
    int[] wins;

    private final int WIDTH; // game width
    private final int HEIGHT; // game height

    Point lastMove; // "coordinates" of last move (player, moveLocation)
    Point initialComputerMove;
    int currentBoard;

    public BoardState(int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;

        this.moves = new int[width*height];
        this.wins = new int[width];

        this.lastMove = new Point(-1, -1);
        this.currentBoard = -1;
        this.initialComputerMove = new Point(-1, -1);
    }

    // copy board state into new one
    public BoardState(BoardState boardState) {
        this.moves = new int[boardState.moves.length];
        this.wins = new int[boardState.wins.length];
        System.arraycopy(boardState.moves, 0, this.moves, 0, boardState.moves.length);
        System.arraycopy(boardState.wins, 0, this.wins, 0, boardState.wins.length);
        this.WIDTH = boardState.WIDTH;
        this.HEIGHT = boardState.HEIGHT;
        this.lastMove = new Point(boardState.lastMove);
        this.initialComputerMove = new Point(boardState.initialComputerMove);
        this.currentBoard = boardState.currentBoard;
    }

    // translate column / row to a position (0 - 15)
    private int getPositionForMove(int c, int r) {
        return c + r * 3;
    }

    // set move in moves array for player
    // if player 1 went in row 3, col 1:
    // moves[8] = 1;
    private void setMove(int player, int position) {
        moves[position] = player;
        lastMove = new Point(player, position);
        currentBoard = determineCurrentBoard();
    }

    private int determineCurrentBoard() {
        int lastPlayer = lastMove.x;
        int lastBoard = lastMove.y / HEIGHT;
        int lastPosition = lastMove.y % HEIGHT;

//        if (checkWinBoard(lastPlayer, lastBoard)) return -1;
//        if (checkTieBoard(lastBoard)) return -1;
        if (checkWinBoard(GameBoard.PLAYER1, lastPosition)) {
            return -1;
        }
        if (checkWinBoard(GameBoard.PLAYER2, lastPosition)) {
            return -1;
        }
        if (checkTieBoard(lastPosition)) {
            return -1;
        }

        return lastPosition;
    }

    // check if move is available
    private Boolean checkMove(int position) {
        int playedBoard = position/9;
        if (currentBoard != -1 && currentBoard != playedBoard) return false;
        if (checkWinBoard(GameBoard.PLAYER1, playedBoard) || checkWinBoard(GameBoard.PLAYER2, playedBoard) || checkTieBoard(playedBoard)) return false;
        return moves[position] == 0;
    }

    // set move if move is available
    public Boolean makeMove(int player, int position) {
        if (!checkMove(position)) return false;

        setMove(player, position);
        if (checkWinBoard(player, position/9)) wins[position/9] = player;
        if (checkTieBoard(position/9)) wins[position/9] = GameBoard.TIE;

        return true;
    }

    public int[] getSmallBoard(int board) {
        int start = board * HEIGHT;
        int[] smallMoves = new int[HEIGHT];
        for (int i = 0; i < HEIGHT; i++) {
            smallMoves[i] = moves[start + i];
        }

        return smallMoves;
    }

    public Boolean checkWinGame(int player) {
        return checkWin(player, wins);
    }

    // check for win cases
    public Boolean checkWinBoard(int player, int board) {
        return checkWin(player, getSmallBoard(board));
    }

    public Boolean checkWin(int player, int[] moves) {
        int count;
        int HEIGHT = 3;
        int WIDTH = 3;

        // check horizontals
        for (int i = 0; i < HEIGHT; i++) {
            count = 0;
            for (int j = 0; j < WIDTH; j++) {
                if (moves[i*HEIGHT + j] == player) count++;
            }

            if (count == WIDTH) return true;
        }

        // check verticals
        for (int i = 0; i < WIDTH; i++) {
            count = 0;
            for (int j = 0; j < HEIGHT; j++) {
                if (moves[j*HEIGHT + i] == player) count++;
            }

            if (count == WIDTH) return true;
        }

        // check diagonals
        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[i*HEIGHT + i] == player) count++;
        }
        if (count == HEIGHT) return true;

        count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[i*HEIGHT + (HEIGHT - i - 1)] == player) count++;
        }
        if (count == HEIGHT) return true;

        return false;
    }

    // check number of rows / columns / diags for player with plays number of moves and no opponents
    // i.e. checkNumPlays(1, 2):
    //      how many rows / cols / diags are there with two Os and no Xs
    // used in the eval function
    public int checkNumPlays(int player, int plays) {
        return checkNumPlaysBoard(player, plays, wins);
    }

    public int checkNumPlaysBoard(int player, int plays, int[] board) {
        int total = 0;
        int count;

        // check horizontals
        for (int i = 0; i < 3; i++) {
            count = 0;
            for (int j = 0; j < 3; j++) {
                if (wins[getPositionForMove(j, i)] == player) count++;
                else if (wins[getPositionForMove(j, i)] == 0) {}
                else {
                    count = 0;
                    break;
                }
            }

            if (count == plays) total++;
        }

        // check verticals
        for (int i = 0; i < 3; i++) {
            count = 0;
            for (int j = 0; j < 3; j++) {
                if (wins[getPositionForMove(i, j)] == player) count++;
                else if (wins[getPositionForMove(i, j)] == 0) {}
                else {
                    count = 0;
                    break;
                }
            }

            if (count == plays) total++;
        }

        // check horizontals
        count = 0;
        for (int i = 0; i < 3; i++) {
            if (wins[getPositionForMove(i, i)] == player) count++;
            else if (wins[getPositionForMove(i, i)] == 0) {}
            else {
                count = 0;
                break;
            }
        }
        if (count == plays) total++;

        count = 0;
        for (int i = 0; i < 3; i++) {
            if (wins[getPositionForMove(i, 3 - i - 1)] == player) count++;
            else if (wins[getPositionForMove(i, 3 - i - 1)] == 0) {}
            else {
                count = 0;
                break;
            }
        }
        if (count == plays) total++;

        return total;
    }

    public Boolean checkTieGame() {
        return checkTie(wins);
    }

    public Boolean checkTieBoard(int board) {
        return checkTie(getSmallBoard(board));
    }

    public Boolean checkTie(int[] moves) {
        int count = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (moves[i] != 0) count++;
        }
        return count == HEIGHT;
    }

    public ArrayList<BoardState> getPossibleMoves(int player) {
        ArrayList<BoardState> possibleMoves = new ArrayList<>();
        if (currentBoard == -1) {
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
                if (checkMove(i)) {
                    BoardState tempState = new BoardState(this);
                    tempState.makeMove(player, i);
                    possibleMoves.add(tempState);
                }
            }
        } else {
            int start = currentBoard * HEIGHT;
            for (int i = start; i < start + 3 * 3; i++) {
                if (checkMove(i)) {
                    BoardState tempState = new BoardState(this);
                    tempState.makeMove(player, i);
                    possibleMoves.add(tempState);
                }
            }
        }

        return possibleMoves;
    }

    // functions to print board to console
    public void printBoard() {
        printBoard(this);
    }

    public static void printBoard(BoardState boardState) {
        for (int i = 0; i < boardState.WIDTH * boardState.HEIGHT; i++) {
            if (i % boardState.WIDTH == 0) System.out.print("\n");
            System.out.print(boardState.moves[i] + " ");
        }

        System.out.print("\n");
    }
}
