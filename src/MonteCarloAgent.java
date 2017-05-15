import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by alex on 5/14/17.
 */


public class MonteCarloAgent extends AIAgent {

    Map<Point, Integer> visited = new HashMap<>();
    Map<Point, Integer> wins = new HashMap<>();
    Map<Point, Integer> visits = new HashMap<>();
    ArrayList<Point> path = new ArrayList<>();

    BoardState original;
    int depth = 0;


    public MonteCarloAgent(String name, int maxDepth, int maxTime) {
        this.name = name;
        this.depthCutoff = maxDepth;
        this.timeCutoff = maxTime;
    }

    public int getMove(BoardState boardState, int player) {
       
            return getMonteCarloMove(boardState, player);


    }

    private int getMonteCarloMove(BoardState state, int player) {
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);







        for (int i = 0; i < possibleMoves.size(); i++) {
            if(!visited.containsKey(possibleMoves.get(i).lastMove)) {
                visited.put(possibleMoves.get(i).lastMove, 0);
                wins.put(possibleMoves.get(i).lastMove, 0);
                visits.put(possibleMoves.get(i).lastMove, 0);
            }

        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            BoardState selectedLeaf = selection(state, player);
            int result = expansionAndRollOut(selectedLeaf, player);
            backPropagation(result);

            if(cutoffOccurred) {
                return makeDecision(state,player);
            }
        }

        return makeDecision(state,player);
    }

    public int makeDecision(BoardState state, int player){
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);
        ArrayList<Integer> moveChoices = new ArrayList<>();
        double bestValue = Integer.MIN_VALUE;
        double moveValue = Integer.MIN_VALUE;
        for (int i = 0; i < possibleMoves.size(); i++) {
            if(visits.get(possibleMoves.get(i).lastMove) != 0) {
                moveValue = wins.get(possibleMoves.get(i).lastMove) / visits.get(possibleMoves.get(i).lastMove);
            }
            else { moveValue = 0;}
            if (moveValue > bestValue) {
                moveChoices = new ArrayList();
                moveChoices.add(i);
                bestValue = moveValue;

            } else if(moveValue == bestValue) {
                moveChoices.add(i);
            }


        }

        Random random = new Random();
        return possibleMoves.get(moveChoices.get(random.nextInt(moveChoices.size()))).lastMove.y;

    }



    public static Boolean terminalTest(BoardState state) {
        return state.checkTieGame() || state.checkWinGame(GameBoard.PLAYER1) || state.checkWinGame(GameBoard.PLAYER2);

    }


    public BoardState selection(BoardState state, int player) {
        if(terminalTest(state)){return state;}
        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);

        for (int i = 0; i < possibleMoves.size(); i++) {
            if (visited.get(possibleMoves.get(i).lastMove) == 0) {
                visited.put(possibleMoves.get(i).lastMove, 1);
                path.add(possibleMoves.get(i).lastMove);
                depth++;
                return possibleMoves.get(i);
            }
        }

        Random random = new Random();
        BoardState nextBoard = possibleMoves.get(random.nextInt(possibleMoves.size()));
        path.add(nextBoard.lastMove);
        depth++;
        return selection(nextBoard, player);

    }

    public int expansionAndRollOut(BoardState state, int player){

        if(terminalTest(state)){
            if(state.checkWinGame(GameBoard.PLAYER1)){
                return 1;
            }
            else return 0;
        }
        if (depthCutoff > 0 && depth >= depthCutoff) {
            cutoffOccurred = true;
            return 0;
        }

        if (depth > depthReached) depthReached = depth;

        if ((System.currentTimeMillis() - startTime) > timeCutoff) {
            cutoffOccurred = true;
            return 0;
        }

        ArrayList<BoardState> possibleMoves = state.getPossibleMoves(player);



        for(int i = 0; i < possibleMoves.size(); i++){
            if (visited.get(possibleMoves.get(i).lastMove) == 0) {
                visited.put(possibleMoves.get(i).lastMove, 1);
                path.add(possibleMoves.get(i).lastMove);
                depth++;
                return expansionAndRollOut(possibleMoves.get(i), player);
            }
        }

        return -1;
    }


    public void backPropagation(int result){
        if (result == -1){
            path.clear();
            return;
        }

        for (int i =0; i < path.size(); i++){
            visits.put(path.get(i),visits.get(path.get(i))+1);
            wins.put(path.get(i),wins.get(path.get(i))+result);

        }

        path.clear();

    }

}



