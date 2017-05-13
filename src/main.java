import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import java.awt.*;
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

    public static void main(String[] args) {
        player1Win = 0;
        player2Win = 0;
        ties = 0;

        while (true) {
            gameBoard = new GameBoard("Player 1", "Computer", GameBoard.USER);

            Boolean gameOver = false;
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

            while (!gameOver) {
                String labelText = "";
                labelText += "max depth: " + gameBoard.depthReached + "\n";
                labelText += "cutoff: " + gameBoard.cutoffOccurred + "\n";
                labelText += "p1: " + player1Win + " p2: " + player2Win + " t: " + ties + "\n";
                labelText += "moveValues: " + gameBoard.moveValues + "\n";
                label.setText(labelText);
                gameBoard.printBoard();
                updateUI();

                System.out.print("Make your move in board " + gameBoard.boardState.currentBoard + ": ");
                String input = "00";
//                input = in.nextLine();
//                gameBoard.boardState.currentBoard = -1;

                gameBoard.userMoveInput(input);
                gameBoard.printBoard();
                updateUI();
                System.out.println("Computer making move...");
                gameBoard.userMoveInput(input);

                if (gameBoard.getGameWinner() != -1) gameOver = true;
            }

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
        }
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
