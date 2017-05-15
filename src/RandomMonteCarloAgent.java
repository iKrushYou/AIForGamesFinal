import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by alex on 5/14/17.
 */


public class RandomMonteCarloAgent extends AIAgent {

    Map<BoardState, Integer> visited = new HashMap<>();
    Map<BoardState, Integer> wins = new HashMap<>();
    Map<BoardState, Integer> visits = new HashMap<>();
    BoardState original;
    boolean firstMove = true;


    public RandomMonteCarloAgent(String name, int maxDepth, int maxTime) {
        this.name = name;
        this.depthCutoff = maxDepth;
        this.timeCutoff = maxTime;
    }

    public int getMove(BoardState boardState, int player) {
        if(firstMove){
            ArrayList<BoardState> possibleMoves = boardState.getPossibleMoves(player);

            Random random = new Random();
            firstMove = false;
            return possibleMoves.get(random.nextInt(possibleMoves.size())).lastMove.y;

        }
        else{
            return getMonteCarloMove(boardState, player);
        }

    }

    private int getMonteCarloMove(BoardState state, int player) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);


        ArrayList<Integer> moveChoices = new ArrayList<>();



        float bestMoveValue = Integer.MIN_VALUE;
        moveValues = "";
        for (int i = 0; i < possibleMoves.size(); i++) {

            visited.put(possibleMoves.get(i), 0);
            wins.put(possibleMoves.get(i), 0);
            visits.put(possibleMoves.get(i), 0);

            int value = evaluateBoardAdvance(possibleMoves.get(i), player, 1);
            visited.put(possibleMoves.get(i),1);
            if (value > 0){
                visits.put(original, visits.get(possibleMoves.get(i))+1);
                wins.put(original, wins.get(possibleMoves.get(i))+value);
            }
            else{
                visits.put(original, visits.get(possibleMoves.get(i))+1);
            }


            original = possibleMoves.get(i);
            possibleMoves.get(i).initialComputerMove = new Point(possibleMoves.get(i).lastMove);
            float moveValue = randomValue(possibleMoves.get(i), -1000000, 1000000, 1, GameBoard.otherPlayer(player));
            if(visits.get(original) != 0) {
                moveValue = wins.get(original) / visits.get(original);
                System.out.print(moveValue + ", ");
            }
            else{
                moveValue = 0;
            }
            moveValues += moveValue + ", ";
            if (moveValue > bestMoveValue) {
                moveChoices = new ArrayList();
                moveChoices.add(i);
                bestMoveValue = moveValue;

            } else if(moveValue == bestMoveValue) {
                moveChoices.add(i);
            }


        }

        Random random = new Random();
        return possibleMoves.get(moveChoices.get(random.nextInt(moveChoices.size()))).lastMove.y;

    }



    private int randomValue(BoardState state, int a, int b, int currentDepth, int player) {
        nodesExplored++;
        if (terminalTest(state)) return utilityValue(state, currentDepth);

        if (depthCutoff > 0 && currentDepth >= depthCutoff) {
            cutoffOccurred = true;
            return evaluateBoardAdvance(state, GameBoard.otherPlayer(player), currentDepth);
        }

        if (currentDepth > depthReached) depthReached = currentDepth;

        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return evaluateBoardAdvance(state, GameBoard.otherPlayer(player), currentDepth);
        }

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        for (int i = 0; i < possibleMoves.size(); i++){

            int value = evaluateBoardAdvance(possibleMoves.get(i), player, currentDepth);
            if (value > 0){
                visits.put(original, visits.get(original)+1);
                wins.put(original, wins.get(original)+value);
            }
            else{
                visits.put(original, visits.get(original)+1);
            }
        }

        Random random = new Random();
        return randomValue(possibleMoves.get(random.nextInt(possibleMoves.size())), a, b, currentDepth++, GameBoard.otherPlayer(player));
    }

    private int evaluateBoardAdvance(BoardState boardState, int player, int depth) {
//        return 10;


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
}



