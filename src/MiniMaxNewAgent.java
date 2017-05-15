import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by alex on 5/14/17.
 */
public class MiniMaxNewAgent extends AIAgent {
    public MiniMaxNewAgent(String name, int maxDepth, int maxTime) {
        this.name = name;
        this.depthCutoff = maxDepth;
        this.timeCutoff = maxTime;
    }

    @Override
    public int getMove(BoardState boardState, int player) {
        this.player = player;

        this.startTime = System.currentTimeMillis();
        this.depthReached = 0;
        this.nodesExplored = 0;
        this.cutoffOccurred = false;
        
        ArrayList<BoardState> possibleMoves = boardState.getPossibleMoves(player);
        ArrayList<Integer> moveChoices = new ArrayList<>();
        this.moveValues = "";
        int bestValue = Integer.MIN_VALUE;
        for (BoardState possibleMove : possibleMoves) {
            int val = alphabeta(possibleMove, player, -100000, 100000, 1);
            moveValues += "[" + possibleMove.lastMove.y + ": " + val + "], ";
            if (val > bestValue) {
                bestValue = val;
                moveChoices = new ArrayList<>();
                moveChoices.add(possibleMove.lastMove.y);
            } else if (val == bestValue) {
                moveChoices.add(possibleMove.lastMove.y);
            }
        }

        System.out.println(this.moveValues);

        Random random = new Random();

        int move = moveChoices.get(random.nextInt(moveChoices.size()));
        System.out.println("move choice: " + move);
        return move;
    }

    public int alphabeta(BoardState boardState, int player, int alpha, int beta, int currentDepth) {
        nodesExplored++;

        if (terminalTest(boardState)) {
            return utilityValue(boardState, currentDepth);
        }

        if (currentDepth >= depthReached) depthReached = currentDepth;

        if ((System.currentTimeMillis() - startTime > timeCutoff) || (depthCutoff > -1 && currentDepth > depthCutoff)) {
            cutoffOccurred = true;
            return evaluateBoardAdvance(boardState, player, currentDepth);
        }

        ArrayList<BoardState> possibleMoves = boardState.getPossibleMoves(player);

        for (BoardState possibleMove : possibleMoves) {
            int val = alphabeta(possibleMove, GameBoard.otherPlayer(player), alpha, beta, currentDepth + 1);

            if (player == this.player) {
                if (val > alpha) {
                    alpha = val;
                }
                if (alpha >= beta) {
                    return beta;
                }
            } else {
                if (val < beta) {
                    beta = val;
                }
                if (beta <= alpha) {
                    return alpha;
                }
            }
        }

        if (player == this.player) {
            return alpha;
        } else {
            return beta;
        }
    }

    private int evaluateBoardAdvance(BoardState boardState, int player, int depth) {
        int X3 = boardState.checkNumPlays(player, 3);
        int X2 = boardState.checkNumPlays(player, 2);
        int X1 = boardState.checkNumPlays(player, 1);

        int O3 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 3);
        int O2 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 2);
        int O1 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 1);

        int x3 = boardState.checkNumPlaysBoard(player, 3, boardState.getSmallBoard(boardState.initialComputerMove.y/9));
        int x2 = boardState.checkNumPlaysBoard(player, 2, boardState.getSmallBoard(boardState.initialComputerMove.y/9));
        int x1 = boardState.checkNumPlaysBoard(player, 1, boardState.getSmallBoard(boardState.initialComputerMove.y/9));

        int o3 = boardState.checkNumPlaysBoard(GameBoard.otherPlayer(player), 3, boardState.getSmallBoard(boardState.initialComputerMove.y/9));
        int o2 = boardState.checkNumPlaysBoard(GameBoard.otherPlayer(player), 2, boardState.getSmallBoard(boardState.initialComputerMove.y/9));
        int o1 = boardState.checkNumPlaysBoard(GameBoard.otherPlayer(player), 1, boardState.getSmallBoard(boardState.initialComputerMove.y/9));

        int value = ((100000 + depth) * X3 + (10000 + depth) * X2 + (depth + 1000) * X1) - ((100000 + depth) * O3 + (10000 + depth) * O2 + (depth + 1000) * O1) + ((100 + depth) * x3 + (10 + depth) * x2 + depth * x1) - ((100 + depth) * o3 + (10 + depth) * o2 + depth * o1);



        return value;
    }

    public static Boolean terminalTest(BoardState state) {
        return state.checkTieGame() || state.checkWinGame(GameBoard.PLAYER1) || state.checkWinGame(GameBoard.PLAYER2);
    }

    public static int utilityValue(BoardState state, int depth) {
        if (state.checkWinGame(GameBoard.PLAYER2)) {
            return 1000000 - depth; // adjust the value with the depth
        }

        if (state.checkWinGame(GameBoard.PLAYER1)) {
            return depth - 1000000; // adjust the value with the depth
        }

        if (state.checkTieGame()) {
            return 0;
        }

        return 0;
    }
}
