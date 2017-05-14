import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 5/14/17.
 */
public class MiniMaxABAgent extends AIAgent {
    public MiniMaxABAgent(String name, int maxDepth, int maxTime) {
        this.name = name;
        this.depthCutoff = maxDepth;
        this.timeCutoff = maxTime;
    }

    public int getMove(BoardState boardState, int player) {
        return getMinimaxMove(boardState, player);
    }

    private int getMinimaxMove(BoardState state, int player) {
        startTime = System.currentTimeMillis();

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        ArrayList<Integer> moveChoices = new ArrayList<>();

        int bestMoveValue = Integer.MIN_VALUE;
        moveValues = "";
        for (int i = 0; i < possibleMoves.size(); i++) {
            int moveValue = minValue(possibleMoves.get(i), -1000, 1000, 1, GameBoard.otherPlayer(player));
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

    private int minValue(BoardState state, int a, int b, int currentDepth, int player) {
        nodesExplored++;

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        if (terminalTest(state)) return utilityValue(state, currentDepth);

        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, GameBoard.otherPlayer(player), currentDepth);
        }

        if (currentDepth > depthReached) depthReached = currentDepth;

        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoard(state, GameBoard.otherPlayer(player), currentDepth);
        }

        int v = Integer.MAX_VALUE;

        for (int i = 0; i < possibleMoves.size(); i++) {
            v = Math.min(v, maxValue(possibleMoves.get(i), a, b, currentDepth++, GameBoard.otherPlayer(player)));
            if (v <= a) {
                return v;
            }
            b = Math.min(b, v);
        }

        return v;
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
            v = Math.max(v, minValue(possibleMoves.get(i), a, b, currentDepth++, GameBoard.otherPlayer(player)));
            if (v >= b) {
                return v;
            }
            a = Math.max(a, v);
        }

        return v;
    }

    public static Boolean terminalTest(BoardState state) {
        return state.checkTieGame() || state.checkWinGame(GameBoard.PLAYER1) || state.checkWinGame(GameBoard.PLAYER2);
    }

    public static int utilityValue(BoardState state, int depth) {
        if (state.checkWinGame(GameBoard.PLAYER2)) {
            return 1000 - depth; // adjust the value with the depth
        }

        if (state.checkWinGame(GameBoard.PLAYER1)) {
            return depth - 1000; // adjust the value with the depth
        }

        if (state.checkTieGame()) {
            return 0;
        }

        return 0;
    }

    private int evaluateBoard(BoardState boardState, int player, int depth) {
        int X3 = boardState.checkNumPlays(player, 3);
        int X2 = boardState.checkNumPlays(player, 2);
        int X1 = boardState.checkNumPlays(player, 1);

        int O3 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 3);
        int O2 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 2);
        int O1 = boardState.checkNumPlays(GameBoard.otherPlayer(player), 1);

        int value = (100 + depth) * X3 + (10 + depth) * X2 + depth * X1 - ((100 + depth) * O3 + (10 + depth) * O2 + depth * O1);



        return value;
    }
}
