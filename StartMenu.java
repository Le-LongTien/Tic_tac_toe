import javax.swing.*;
import java.awt.*;

public class StartMenu extends JFrame {
    private JTextField player1Field;
    private JTextField player2Field;
    private JSpinner boardSizeSpinner;

    public StartMenu() {
        setTitle("Gomoku Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 250);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label1 = new JLabel("Player 1:");
        player1Field = new JTextField("Player 1");

        JLabel label2 = new JLabel("Player 2:");
        player2Field = new JTextField("Player 2");

        JLabel label3 = new JLabel("Board Size (3 to 18):");
        SpinnerModel model = new SpinnerNumberModel(18, 3, 18, 1);
        boardSizeSpinner = new JSpinner(model);

        panel.add(label1);
        panel.add(player1Field);
        panel.add(label2);
        panel.add(player2Field);
        panel.add(label3);
        panel.add(boardSizeSpinner);

        JButton startButton = new JButton("Start 2 Players");
        startButton.addActionListener(e -> {
            String player1 = player1Field.getText();
            String player2 = player2Field.getText();
            int boardSize = (Integer) boardSizeSpinner.getValue();
            new GomokuGame(this, player1, player2, boardSize, false);
            setVisible(false);
        });

        JButton startAIButton = new JButton("Start with AI");
        startAIButton.addActionListener(e -> {
            String player1 = player1Field.getText();
            String player2 = "AI";
            int boardSize = (Integer) boardSizeSpinner.getValue();
            new GomokuGame(this, player1, player2, boardSize, true);
            setVisible(false);
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(startAIButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
