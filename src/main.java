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

    public static void main(String[] args) {
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

        frame.add(panel);
        frame.setSize(500, 500);
//        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while (!gameOver) {
            gameBoard.printBoard();
            updateUI();

            System.out.print("Make your move in board " + gameBoard.boardState.currentBoard + ": ");
            String input = "00"; //in.nextLine();

            gameBoard.userMoveInput(input);
            gameBoard.printBoard();
            updateUI();
            System.out.println("Computer making move...");
            gameBoard.userMoveInput(input);

            if (gameBoard.getGameWinner() != -1) gameOver = true;
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
    }

    public static void updateUI() {
        for (int i = 0; i < 81; i++) {
            int board = i / 9;
            buttons[i].setText(gameBoard.getMoveForPlayer(gameBoard.getMoves()[i]));
            if (gameBoard.boardState.currentBoard == -1 || board == gameBoard.boardState.currentBoard) buttons[i].setBackground(Color.gray);
            else buttons[i].setBackground(Color.white);

            if (gameBoard.boardState.wins[board] != 0) {
                System.out.println("boardwin: " + gameBoard.boardState.wins[board]);
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
        System.out.println("lastmove: " + lastMove);
        if (lastMove > -1) {
            int player = gameBoard.boardState.lastMove.x;
            if (player == 1)
                buttons[gameBoard.boardState.lastMove.y].setBackground(Color.blue);
            else
                buttons[gameBoard.boardState.lastMove.y].setBackground(Color.red);
        }
    }
}
