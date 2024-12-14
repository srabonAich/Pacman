package pacman;

import javax.swing.*;
//import pacman.PacMan;

public class Main {
    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount=19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true);
        frame.setSize(boardWidth,boardHeight);
        frame.setLocationRelativeTo(null);//in the center//
        frame.setResizable(false);//user cant resize window//
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//X terminates program//

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);//to add panel to window//
        frame.pack();//to get the full size //
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}
 