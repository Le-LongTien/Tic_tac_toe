import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayWithAiGomoku extends JPanel {
    private int size;
    private int p;
    private final int TILE_SIZE = 40; // Fixed tile size
    private Seed[][] board;
    private boolean player1Turn;
    private JLabel turnLabel;
    private JFrame frame;
    private String player1Name;
    private String player2Name;
    private boolean isPlayingWithAI;
    private int player1Score=0;
    private int player2Score=0;
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel turn;
    private void updateScoreLabels() {
        player1ScoreLabel.setText(player1Name + ": " + player1Score);
        player2ScoreLabel.setText(player2Name + ": " + player2Score);
    }
    
    public PlayWithAiGomoku(JFrame frame, String player1Name, String player2Name, int size, boolean isPlayingWithAI) {
        this.frame = frame;
        this.size = size;
        this.board = new Seed[size][size];
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.isPlayingWithAI = isPlayingWithAI;
        this.player1Turn = true;
        adjustTileSize();
        player1ScoreLabel = new JLabel(player1Name + ": " + player1Score);
        player1ScoreLabel.setBounds(20, size * TILE_SIZE + 20, 100, 20); // Adjust position as needed
        add(player1ScoreLabel);
        
        player2ScoreLabel = new JLabel(player2Name + ": " + player2Score);
        player2ScoreLabel.setBounds(size * TILE_SIZE - 120, size * TILE_SIZE + 20, 100, 20); // Adjust position as needed
        add(player2ScoreLabel);

       
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isPlayingWithAI && !player1Turn) {
                    return; // Ignore clicks during AI's turn
                }

                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;
                if (x < size && y < size && board[x][y] == Seed.EMPTY) {
                    makeMove(x, y);
                    repaint();
                    if (hasWon(x, y, board[x][y])) {
                        showWinner(board[x][y]);
                        resetBoard();
                    } else if (isBoardFull()) {
                        showDraw();
                        resetBoard();
                    } else if (isPlayingWithAI && !player1Turn) {
                        makeAIMove();
                    }
                }
                
            }
        });

        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustTileSize();
                repaint();
            }
        });

        updateTurnLabel();
        initBoard();
    }

    private void adjustTileSize() {
        updateFrameSize();
    }

    private void updateFrameSize() {
        Insets insets = frame.getInsets();
        int buttonPanelHeight = 50; // Height of button panel at the bottom
        int mapWidth = size * TILE_SIZE + insets.left + insets.right;
        int mapHeight = size * TILE_SIZE + insets.top + insets.bottom + buttonPanelHeight;
        frame.setSize(mapWidth, mapHeight);
    }

    private void initBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Seed.EMPTY;
            }
        }
    }
    private void updateTurn(String currentPlayerName) {
        player1Turn = !player1Turn;
        updateTurnLabel();
    }
    
    private void makeMove(int row, int col) {
        if (player1Turn) {
            board[row][col] = Seed.CROSS; // Assuming CROSS is player 1's Seed
            updateTurn(player2Name); // Update turn to player 2 or AI
        } else {
            board[row][col] = Seed.NOUGHT; // Assuming NOUGHT is player 2's Seed
            updateTurn(player1Name); // Update turn to player 1
        }
    
        repaint(); // Redraw the board after each move
        updateTurnLabel(); // Update the turn label
        updateScoreLabels(); // Update the score labels
        // Check for win/draw conditions and handle them
    }
    
    
    


    private void makeAIMove() {
        HeuristicBot bot = new HeuristicBot(size, size, Seed.NOUGHT, Seed.CROSS);
        String moveStr = bot.getPoint(board);
        String[] move = moveStr.split(" ");
        int x = Integer.parseInt(move[0]);
        int y = Integer.parseInt(move[1]);
        makeMove(x, y);
        repaint();
        if (hasWon(x, y, board[x][y])) {
            showWinner(board[x][y]);
            resetBoard();
        } else if (isBoardFull()) {
            showDraw();
            resetBoard();
        }
    }
    

    private void updateTurnLabel() {
        if (turnLabel != null) {
            turnLabel.setText(player1Turn ? player1Name + "'s Turn" : player2Name + "'s Turn");
        }
    }

    private void showWinner(Seed player) {
        String winner = (player == Seed.CROSS) ? player1Name : player2Name;
        JOptionPane.showMessageDialog(this, winner + " wins!");
        if (player1Turn) {
            player2Score++; // Increase player 1's score
        } else {
            player1Score++; // Increase player 2's score
        }
    }

    private void showDraw() {
        JOptionPane.showMessageDialog(this, "It's a draw!");
    }

    private boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == Seed.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean hasWon(int rowSelected, int colSelected, Seed theSeed) {
        int winLength = size <= 4 ? size : 5;
        return checkDirection(rowSelected, colSelected, theSeed, 1, 0, winLength) || // Horizontal
               checkDirection(rowSelected, colSelected, theSeed, 0, 1, winLength) || // Vertical
               checkDirection(rowSelected, colSelected, theSeed, 1, 1, winLength) || // Diagonal
               checkDirection(rowSelected, colSelected, theSeed, 1, -1, winLength);  // Opposite diagonal
    }

    private boolean isBlockedAtEnds(int row, int col, Seed theSeed, int dx, int dy, int length) {
        boolean blockedAtStart = false;
        boolean blockedAtEnd = false;
    
        // Check in the negative direction
        int newRow = row - dx;
        int newCol = col - dy;
        if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
            blockedAtStart = board[newRow][newCol] != Seed.EMPTY && board[newRow][newCol] != theSeed;
        } else {
            blockedAtStart = true;
        }
    
        // Check in the positive direction
        newRow = row + length * dx;
        newCol = col + length * dy;
        if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
            blockedAtEnd = board[newRow][newCol] != Seed.EMPTY && board[newRow][newCol] != theSeed;
        } else {
            blockedAtEnd = true;
        }
    
        return blockedAtStart && blockedAtEnd;
    }
    
    private boolean isNearWin(int row, int col, Seed theSeed) {
        int nearWinLength = size == 3 ? 2 : (size == 4 ? 3 : 4);
        return checkDirection(row, col, theSeed, 1, 0, nearWinLength) || // Horizontal
               checkDirection(row, col, theSeed, 0, 1, nearWinLength) || // Vertical
               checkDirection(row, col, theSeed, 1, 1, nearWinLength) || // Diagonal
               checkDirection(row, col, theSeed, 1, -1, nearWinLength);  // Opposite diagonal
    }
    
    private boolean checkDirection(int row, int col, Seed theSeed, int dx, int dy, int nearWinLength) {
        int count = 1;
        boolean blockedAtStart = false;
        boolean blockedAtEnd = false;
    
        // Check in the positive direction
        int newRow = row + dx;
        int newCol = col + dy;
        while (count < nearWinLength && newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && board[newRow][newCol] == theSeed) {
            count++;
            newRow += dx;
            newCol += dy;
        }
        if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && board[newRow][newCol] != Seed.EMPTY) {
            blockedAtEnd = true;
        }
    
        // Check in the negative direction
        newRow = row - dx;
        newCol = col - dy;
        while (count < nearWinLength && newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && board[newRow][newCol] == theSeed) {
            count++;
            newRow -= dx;
            newCol -= dy;
        }
        if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size && board[newRow][newCol] != Seed.EMPTY) {
            blockedAtStart = true;
        }
    
        // Check if the sequence is not blocked at both ends
        return count >= nearWinLength && (!blockedAtStart || !blockedAtEnd);
    }
    
    
   
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw grid
    g2.setColor(Color.LIGHT_GRAY);
    for (int i = 0; i <= size; i++) {
        g2.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, size * TILE_SIZE);
        g2.drawLine(0, i * TILE_SIZE, size * TILE_SIZE, i * TILE_SIZE);
    }

    // Draw pieces
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (board[i][j] == Seed.CROSS) {
                if (isNearWin(i, j, Seed.CROSS)) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(6)); // Thicker stroke for near win
                } else {
                    g2.setColor(Color.RED.darker());
                    g2.setStroke(new BasicStroke(3));
                }
                g2.drawLine(i * TILE_SIZE + 5, j * TILE_SIZE + 5, (i + 1) * TILE_SIZE - 5, (j + 1) * TILE_SIZE - 5);
                g2.drawLine((i + 1) * TILE_SIZE - 5, j * TILE_SIZE + 5, i * TILE_SIZE + 5, (j + 1) * TILE_SIZE - 5);
            } else if (board[i][j] == Seed.NOUGHT) {
                if (isNearWin(i, j, Seed.NOUGHT)) {
                    g2.setColor(Color.BLUE);
                    g2.setStroke(new BasicStroke(6)); // Thicker stroke for near win
                } else {
                    g2.setColor(Color.BLUE.darker());
                    g2.setStroke(new BasicStroke(3));
                }
                g2.drawOval(i * TILE_SIZE + 5, j * TILE_SIZE + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            }
        }
    }
}


public void resetBoard() {
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            board[i][j] = Seed.EMPTY;
        }
    }
    player1Turn = true;
    updateTurnLabel();
    updateScoreLabels(); // Update score labels
    repaint();
}


    public void setTurnLabel(JLabel turnLabel) {
        this.turnLabel = turnLabel;
    }

    // Enum Seed để đại diện cho trạng thái của từng ô trong bảng
    public enum Seed {
        EMPTY, CROSS, NOUGHT
    }

    // Bot theo thuật toán Heuristic
    public static class HeuristicBot {
        static int rows, cols;
        static Seed bot;
        static Seed player;

        static int[] mangTC = new int[]{0, 10, 600, 3500, 40000000, 70000, 1000000};
        static int[] mangPN = new int[]{0, 7, 700, 4000, 10000, 67000, 500000};
        static long MAX_INT = 100000000;

        public HeuristicBot(int rows, int cols, Seed botType, Seed playerType) {
            HeuristicBot.rows = rows;
            HeuristicBot.cols = cols;
            HeuristicBot.bot = botType;
            HeuristicBot.player = playerType;
        }

        public static String getPoint(Seed[][] board) {
            long checkTC = 0;
            long checkPT = 0;
            long max = 0;
            String vTri = new String();
            List<String> list = new ArrayList<>();
            Random rand = new Random();

            for (int i = 0; i < rows; i++) {
                for (int ii = 0; ii < cols; ii++) {
                    if (board[i][ii] == Seed.EMPTY) {
                        checkTC = checkNgang(ii, i, board, bot) + checkDoc(ii, i, board, bot) + checkCheoPhai(i, ii, board, bot) + checkCheoTrai(i, ii, board, bot);
                        checkPT = ptNgang(ii, i, board, player) + ptDoc(ii, i, board, player) + ptPhai(i, ii, board, player) + ptTrai(i, ii, board, player);
                        long tmp = checkPT + checkTC;
                        if (tmp > max) {
                            list = new ArrayList<>();
                            max = tmp;
                            vTri = i + " " + ii;
                            list.add(vTri);
                        }
                        if (tmp == max) {
                            vTri = i + " " + ii;
                            list.add(vTri);
                        }
                    }
                }
            }
            return list.get(rand.nextInt(list.size()));
        }

        public static long checkNgang(int pos, int rowNow, Seed[][] board, Seed type) {
            int ta = 0, count = 0, dich = 0;
            boolean flag = false;
            for (int i = pos + 1; i < cols; i++) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[rowNow][i] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            flag = false;
            for (int i = pos - 1; i >= 0; i--) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[rowNow][i] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
            if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
            if (ta <= 3 && dich == 1 && count == 0) return 0;
            if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT / 250;
            if (ta >= 4) return MAX_INT;
            return (mangTC[ta] * 3) / 2 - count * 100;
        }

        public static long checkDoc(int colNow, int pos, Seed[][] board, Seed type) {
            int ta = 0, count = 0, dich = 0;
            boolean flag = false;
            for (int i = pos + 1; i < rows; i++) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i][colNow] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            flag = false;
            for (int i = pos - 1; i >= 0; i--) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i][colNow] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
            if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
            if (ta <= 3 && dich == 1 && count == 0) return 0;
            if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT / 250;
            if (ta >= 4) return MAX_INT;
            return (mangTC[ta] * 3) / 2 - count * 100;
        }

        public static long checkCheoPhai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0, count = 0, dich = 0;
            boolean flag = false;
            int i = pos_col, ii = pos_row;
            while (i + 1 < rows && ii + 1 < cols) {
                if (board[i + 1][ii + 1] == type) ta++;
                else if (board[i + 1][ii + 1] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i + 1][ii + 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i + 1;
                ii = ii + 1;
            }
            i = pos_col;
            ii = pos_row;
            flag = false;
            while (i - 1 >= 0 && ii - 1 >= 0) {
                if (board[i - 1][ii - 1] == type) ta++;
                else if (board[i - 1][ii - 1] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i - 1][ii - 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i - 1;
                ii = ii - 1;
            }
            if (ta == 0) return 0;
            if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
            if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
            if (ta <= 3 && dich == 1 && count == 0) return 0;
            if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT / 250;
            if (ta >= 4) return MAX_INT;
            return (mangTC[ta] * 3) - count * 100;
        }

        public static long checkCheoTrai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0, count = 0, dich = 0;
            boolean flag = false;
            int i = pos_col, ii = pos_row;
            while (i + 1 < rows && ii - 1 >= 0) { 
                if (board[i + 1][ii - 1] == type) ta++;
                else if (board[i + 1][ii - 1] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i + 1][ii - 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i + 1;
                ii = ii - 1;
            }
            i = pos_col;
            ii = pos_row;
            flag = false;
            while (i - 1 >= 0 && ii + 1 < rows) {
                if (board[i - 1][ii + 1] == type) ta++;
                else if (board[i - 1][ii + 1] == Seed.EMPTY && !flag) {
                    count++;
                    flag = true;
                    break;
                } else if (board[i - 1][ii + 1] == player) {
                    dich++;
                    break;
                } else {
                    break;
                }
                i = i - 1;
                ii = ii + 1;
            }
            if (ta == 0) return 0;
            if (ta == 3 && dich == 2 && (count == 0 || count == 1)) return 0;
            if (ta == 2 && dich == 2 && (count == 0 || count == 1 || count == 2)) return 0;
            if (ta <= 3 && dich == 1 && count == 0) return 0;
            if (ta == 3 && (dich == 0 || dich == 1)) return MAX_INT / 250;
            if (ta >= 4) return MAX_INT;
            return (mangTC[ta] * 3) - count * 100;
        }

        public static long ptNgang(int pos, int rowNow, Seed[][] board, Seed type) {
            int ta = 0, count = 1, dich = 0;
            for (int i = pos + 1; i < cols; i++) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY) break;
                else if (board[rowNow][i] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            for (int i = pos - 1; i >= 0; i--) {
                if (board[rowNow][i] == type) ta++;
                else if (board[rowNow][i] == Seed.EMPTY) break;
                else if (board[rowNow][i] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT / 2;
            if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT / 1000;
            return (mangPN[ta + 1] * 6) / 4 - count;
        }

        public static long ptDoc(int colNow, int pos, Seed[][] board, Seed type) {
            int ta = 0, count = 1, dich = 0;
            for (int i = pos + 1; i < rows; i++) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY) break;
                else if (board[i][colNow] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            for (int i = pos - 1; i >= 0; i--) {
                if (board[i][colNow] == type) ta++;
                else if (board[i][colNow] == Seed.EMPTY) break;
                else if (board[i][colNow] == bot) {
                    dich++;
                    break;
                } else {
                    break;
                }
            }
            if (ta == 0) return 0;
            if (ta >= 4) return MAX_INT / 2;
            if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT / 1000;
            return (mangPN[ta + 1] * 6) / 4 - count;
        }

        public static long ptPhai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0, count = 1, dich = 0;
            int i = pos_col, ii = pos_row;
            while (i + 1 < rows && ii + 1 < cols) {
                if (board[i + 1][ii + 1] == type) ta++;
                else if (board[i + 1][ii + 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i + 1;
                ii = ii + 1;
            }
            i = pos_col;
            ii = pos_row;
            while (i - 1 >= 0 && ii - 1 >= 0) {
                if (board[i - 1][ii - 1] == type) ta++;
                else if (board[i - 1][ii - 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i - 1;
                ii = ii - 1;
            }
            if (ta >= 4) return MAX_INT / 2;
            if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT / 1000;
            return (mangPN[ta + 1] * 6) / 4 - count;
        }

        public static long ptTrai(int pos_col, int pos_row, Seed[][] board, Seed type) {
            int ta = 0, count = 1, dich = 0;
            int i = pos_col, ii = pos_row;
            while (i + 1 < rows && ii - 1 >= 0) {
                if (board[i + 1][ii - 1] == type) ta++;
                else if (board[i + 1][ii - 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i + 1;
                ii = ii - 1;
            }
            i = pos_col;
            ii = pos_row;
            while (i - 1 >= 0 && ii + 1 < rows) {
                if (board[i - 1][ii + 1] == type) ta++;
                else if (board[i - 1][ii + 1] == Seed.EMPTY) break;
                else {
                    dich++;
                    break;
                }
                i = i - 1;
                ii = ii + 1;
            }
            if (ta >= 4) return MAX_INT / 2;
            if (ta == 3 && (dich == 1 || dich == 0)) return MAX_INT / 1000;
            return (mangPN[ta + 1] * 6) / 4 - count;
        }
    }
}