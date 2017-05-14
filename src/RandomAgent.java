import java.util.ArrayList;
import java.util.Random;

/**
 * Created by alex on 5/14/17.
 */
public class RandomAgent extends AIAgent {
    public RandomAgent(BoardState boardState, int player) {
        this.boardState = boardState;
        this.player = player;
    }
    public int getMove() {
        ArrayList<BoardState> possibleMoves = boardState.getPossibleMoves(player);

        Random random = new Random();
        return possibleMoves.get(random.nextInt(possibleMoves.size())).lastMove.y;
    }
}
