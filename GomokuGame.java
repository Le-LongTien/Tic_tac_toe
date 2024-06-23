import javax.swing.*;
import java.awt.*;

public class GomokuGame extends JFrame {
    private PlayWithAiGomoku board;
    private StartMenu startMenu;
    private JLabel scoreLabel;
    private JLabel gameStatusLabel;
    private String player1Name;
    private String player2Name;
    private int scorePlayer1;
    private int scorePlayer2;
    private boolean isPlayer1Turn;

    public GomokuGame(StartMenu startMenu, String player1Name, String player2Name, int boardSize, boolean isPlayingWithAI) {
        this.startMenu = startMenu;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        isPlayer1Turn = true;

        board = new PlayWithAiGomoku(this, player1Name, player2Name, boardSize, isPlayingWithAI);
        add(board, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scoreLabel = new JLabel("Score: " + player1Name + " - " + scorePlayer1 + ", " + player2Name + " - " + scorePlayer2);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gameStatusLabel = new JLabel("Turn: " + player1Name); // Initial status: Player 1's turn
        gameStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem newGameItem = new JMenuItem("New Game");
            JMenuItem backItem = new JMenuItem("Back to Menu");

            newGameItem.addActionListener(evt -> board.resetBoard());

            backItem.addActionListener(evt -> {
                startMenu.setVisible(true);
                dispose();
            });

            menu.add(newGameItem);
            menu.add(backItem);
            menu.show(menuButton, 0, menuButton.getHeight());
        });

        bottomPanel.add(menuButton, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 850); // Increased height to accommodate the title label
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateTurn() {
        isPlayer1Turn = !isPlayer1Turn;
        gameStatusLabel.setText("Turn: " + (isPlayer1Turn ? player1Name : player2Name));
    }

    public void updateScore(String winner) {
        if (winner.equals(player1Name)) {
            scorePlayer1++;
        } else if (winner.equals(player2Name)) {
            scorePlayer2++;
        }
        scoreLabel.setText("Score: " + player1Name + " - " + scorePlayer1 + ", " + player2Name + " - " + scorePlayer2);
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }
}