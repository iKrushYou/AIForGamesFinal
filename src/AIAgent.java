/**
 * Created by alex on 5/14/17.
 */
public abstract class AIAgent {
    public BoardState boardState;
    public int player;
    public String moveValues;
    public int nodesExplored;
    public int depthCutoff;
    public Boolean cutoffOccurred;
    public int timeCutoff;
    public int depthReached;
    public long startTime;

    public abstract int getMove();
}
