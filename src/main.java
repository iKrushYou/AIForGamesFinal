import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by alex on 5/12/17.
 */
public class main {
    static JButton[] buttons;
    static GameBoard gameBoard;
    static JTextArea label;
    static int player1Win;
    static int player2Win;
    static int ties;
    static long startTime;
    static ArrayList<Long> gameTimes;
    static int playerMove;
    static int[] positionMap = {0,1,2,9,10,11,18,19,20,3,4,5,12,13,14,21,22,23,6,7,8,15,16,17,24,25,26,27,28,29,36,37,38,45,46,47,30,31,32,39,40,41,48,49,50,33,34,35,42,43,44,51,52,53,54,55,56,63,64,65,72,73,74,57,58,59,66,67,68,75,76,77,60,61,62,69,70,71,78,79,80};

    public static void main(String[] args) {
        player1Win = 0;
        player2Win = 0;
        ties = 0;

        Scanner in = new Scanner(System.in);

        JFrame frame = new JFrame("Ultimate Tic Tac Toe");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        buttons = new JButton[81];
        setUI();
        for (JButton button : buttons) {
            panel.add(button);
        }
        panel.add(label);

        frame.add(panel);
        frame.setSize(482, 482 + 128);
//        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gameTimes = new ArrayList<>();

        while (true) {
            Boolean playerMode = true;

            startTime = System.currentTimeMillis();
            AIAgent miniMaxAgent = new MiniMaxAgent("MiniMaxABAgent", 3, 10000);
            AIAgent miniMaxABAgent = new MiniMaxABAgent("MiniMaxABAgent", 3, 10000);
            AIAgent agMiniMaxABAgent = new AGMiniMaxABAgent("AGMiniMaxABAgent", 3, 10000);
            AIAgent negaMaxAgent = new NegaMaxAgent("NegaMaxAgent", 3, 10000);
            gameBoard = new GameBoard(negaMaxAgent, agMiniMaxABAgent, GameBoard.PLAYER1);
            playerMove = -1;

            Boolean gameOver = false;
            while (!gameOver) {
                setLabelText();
                gameBoard.printBoard();
                updateUI();

                System.out.print("Make your move in board " + gameBoard.boardState.currentBoard + ": ");

                if (playerMode) {
                    Boolean checkMove = false;
                    while (!checkMove) {
                        while (playerMove == -1) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        checkMove = gameBoard.boardState.checkMove(playerMove);
                        if (!checkMove)  {
                            playerMove = -1;
                        }
                    }
                    gameBoard.userMoveInput(playerMove);
                    playerMove = -1;
                } else {
                    gameBoard.computerMove(GameBoard.PLAYER1);
                }

                setLabelText();
                gameBoard.printBoard();
                updateUI();
                System.out.println("Computer making move...");
                gameBoard.computerMove(GameBoard.PLAYER2);

                if (gameBoard.getGameWinner() != -1) gameOver = true;
            }

            setLabelText();
            updateUI();

            switch (gameBoard.getGameWinner()) {
                case 1:
                    player1Win++;
                    break;
                case 2:
                    player2Win++;
                    break;
                case 3:
                    ties++;
                    break;
                default:
                    break;
            }

            long gameTime = System.currentTimeMillis() - startTime;
            gameTimes.add(gameTime);
        }
    }

    public static double calculateAverage(ArrayList <Long> marks) {
        Long sum = Long.valueOf(0);
        if(!marks.isEmpty()) {
            for (Long mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public static void setLabelText() {
        String labelText = "";
        labelText += gameBoard.player1.name + "(" + gameBoard.player1.depthCutoff + ") vs " + gameBoard.player2.name + "(" + gameBoard.player2.depthCutoff + ")\n";
        labelText += "Player1 Wins: " + player1Win + " Player2 Wins: " + player2Win + " Ties: " + ties + "\n";
        labelText += "Nodes Explored: " + gameBoard.addCommas(gameBoard.nodesExplored) + "\n";
        labelText += "Game Time: " + (System.currentTimeMillis() - startTime)/1000.0 + " seconds\n";
        labelText += "Average Game Time: " + calculateAverage(gameTimes)/1000.0 + " seconds\n";
        label.setText(labelText);
    }

    public static void setUI() {
        int position = 0;
        int startX = 24;
        int startY = 24;
        int smallSpacing = 4;
        int largeSpacing = 12;
        int width = 42;
        int height = 42;
        int x = startX;
        int y = startY;

        for (int l = 0; l < 3; l++) {
            for (int k = 0; k < 3; k++) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        JButton button = new JButton(" ");
                        button.setBounds(x, y, width, height);
                        button.setOpaque(true);
                        button.setMargin(new Insets(0,0,0,0));
                        button.setFont(new Font("Arial", Font.PLAIN, 24));
                        button.setActionCommand(i + "");
                        button.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                buttonPressed(e);
                            }
                        } );

                        buttons[position] = button;

                        x += width + smallSpacing;

                        position++;
                    }
                    x += largeSpacing;
                    position += 6;
                }
                x = startX;
                y += height + smallSpacing;
                position -= 24;
            }
            y += largeSpacing;
            position += 18;
        }

        label = new JTextArea("Text");
        label.setBounds(24, 482, 434, 128);

    }

    public static void buttonPressed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        int x = (int)((button.getLocation().getX() - 24) / 42);
        if (x > 6) x -=1;
        int y = (int)((button.getLocation().getY() - 24) / 42);
        if (y > 6) y -=1;
        int position = y * 9 + x;
        System.out.println("button pressed: " + positionMap[position]);
        playerMove = positionMap[position];
    }

    public static void updateUI() {
        for (int i = 0; i < 81; i++) {
            int board = i / 9;
            buttons[i].setText(gameBoard.getMoveForPlayer(gameBoard.getMoves()[i]));
            if (gameBoard.boardState.currentBoard == -1 || board == gameBoard.boardState.currentBoard) buttons[i].setBackground(Color.gray);
            else buttons[i].setBackground(Color.white);

            if (gameBoard.boardState.wins[board] != 0) {
                if (gameBoard.boardState.wins[board] == 1) buttons[i].setBackground(Color.blue);
                if (gameBoard.boardState.wins[board] == 2) buttons[i].setBackground(Color.red);
                if (gameBoard.boardState.wins[board] == 3) buttons[i].setBackground(Color.green);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int lastMove = gameBoard.boardState.lastMove.y;
        if (lastMove > -1) {
            int player = gameBoard.boardState.lastMove.x;
            if (player == 1)
                buttons[gameBoard.boardState.lastMove.y].setBackground(Color.blue);
            else
                buttons[gameBoard.boardState.lastMove.y].setBackground(Color.red);
        }
    }
}
