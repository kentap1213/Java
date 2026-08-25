import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

/*
 * ==========================================================
 *  SNAKE GAME 
 * ==========================================================
 *  Step 1  -> SnakeGame class (the JFrame / window)
 *  Step 2  -> GamePanel class (the JPanel / game area)
 *  Step 3  -> paintComponent draws the background/grid
 *  Step 4  -> drawing a single block (drawFood / drawSnake use this idea)
 *  Step 5  -> Timer + move() makes the snake move continuously
 *  Step 6  -> KeyListener reads arrow keys
 *  Step 7  -> ArrayList<Point> lets the snake grow longer
 *  Step 8  -> food (red circle) placed with Random
 *  Step 9  -> checkFood() detects eating
 *  Step 10 -> score variable + drawn on screen
 *  Step 11 -> checkWallCollision()
 *  Step 12 -> checkSelfCollision()
 *  Step 13 -> "Game Over" screen
 *  Step 14 -> Restart JButton
 * ==========================================================
 */

// ---------- STEP 1: The main window (JFrame) ----------
public class SnakeGame extends JFrame {

    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();                     // size the window to fit the panel
        setLocationRelativeTo(null); // center the window on screen
        setVisible(true);
    }

    public static void main(String[] args) {
        // Always start Swing apps on the "Event Dispatch Thread"
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

// ---------- STEP 2: The game area (JPanel) ----------
class GamePanel extends JPanel implements ActionListener, KeyListener {

    // ----- grid settings -----
    static final int TILE_SIZE = 25;      // size of one square (snake block / food)
    static final int GRID_WIDTH = 24;      // number of tiles across
    static final int GRID_HEIGHT = 24;     // number of tiles down
    static final int SCREEN_WIDTH = TILE_SIZE * GRID_WIDTH;
    static final int SCREEN_HEIGHT = TILE_SIZE * GRID_HEIGHT;
    static final int DELAY = 120;          // timer delay (ms) -> controls speed

    // ----- STEP 7: snake body stored as a list of points -----
    ArrayList<Point> snakeBody;

    // ----- STEP 8: food position -----
    Point food;
    Random random;

    // ----- movement -----
    char direction; // 'U', 'D', 'L', 'R'

    // ----- game state -----
    boolean running;
    Timer timer;

    // ----- STEP 10: score -----
    int score;

    // ----- STEP 14: restart button -----
    JButton restartButton;

    public GamePanel() {
        random = new Random();
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        setLayout(null); // lets us manually place the restart button later

        startGame();
    }

    // Sets up (or resets) all the game variables
    public void startGame() {
        snakeBody = new ArrayList<>();
        // start with one segment in the middle of the board
        snakeBody.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));

        direction = 'R';   // start moving right
        running = true;
        score = 0;

        spawnFood();

        if (restartButton != null) {
            remove(restartButton);
            restartButton = null;
        }

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this); // STEP 5: Timer drives the game loop
        timer.start();

        requestFocusInWindow(); // make sure key presses are captured
        repaint();
    }

    // ----- STEP 8: place food at a random empty tile -----
    public void spawnFood() {
        boolean validPosition = false;
        while (!validPosition) {
            int foodX = random.nextInt(GRID_WIDTH);
            int foodY = random.nextInt(GRID_HEIGHT);
            Point newFood = new Point(foodX, foodY);

            // make sure food doesn't spawn on top of the snake
            if (!snakeBody.contains(newFood)) {
                food = newFood;
                validPosition = true;
            }
        }
    }

    // ---------- STEP 3 & 4: Drawing everything ----------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            drawGame(g);
        } else {
            drawGameOverScreen(g);
        }
    }

    private void drawGame(Graphics g) {
        // draw food (a red circle) - STEP 8
        g.setColor(Color.RED);
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // draw the snake - STEP 4 (single block) repeated for every segment
        for (int i = 0; i < snakeBody.size(); i++) {
            Point p = snakeBody.get(i);
            if (i == 0) {
                g.setColor(Color.GREEN);       // head = bright green
            } else {
                g.setColor(new Color(0, 180, 0)); // body = darker green
            }
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // draw the score - STEP 10
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
    }

    // ---------- STEP 13: Game over screen ----------
    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String gameOverText = "GAME OVER";
        FontMetrics fm1 = g.getFontMetrics();
        int textX = (SCREEN_WIDTH - fm1.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, textX, SCREEN_HEIGHT / 2 - 50);

        g.setFont(new Font("Arial", Font.PLAIN, 22));
        String scoreText = "Final Score: " + score;
        FontMetrics fm2 = g.getFontMetrics();
        int scoreX = (SCREEN_WIDTH - fm2.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, scoreX, SCREEN_HEIGHT / 2 - 10);

        // STEP 14: show the restart button once, when the game ends
        if (restartButton == null) {
            restartButton = new JButton("Restart");
            restartButton.setBounds(SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 + 20, 100, 35);
            restartButton.addActionListener(e -> startGame());
            add(restartButton);
        }
    }

    // ---------- STEP 5: Move the snake ----------
    public void move() {
        Point head = snakeBody.get(0);
        Point newHead = new Point(head.x, head.y);

        // change position based on current direction
        switch (direction) {
            case 'U': newHead.y -= 1; break;
            case 'D': newHead.y += 1; break;
            case 'L': newHead.x -= 1; break;
            case 'R': newHead.x += 1; break;
        }

        // add the new head to the front of the body
        snakeBody.add(0, newHead);

        // STEP 9: check food BEFORE removing the tail
        if (newHead.equals(food)) {
            score++;
            spawnFood();
            // don't remove the tail -> snake grows by one block
        } else {
            // remove the last segment so the snake appears to "move"
            snakeBody.remove(snakeBody.size() - 1);
        }
    }

    // ---------- STEP 11: Wall collision ----------
    public boolean checkWallCollision(Point head) {
        return head.x < 0 || head.x >= GRID_WIDTH
            || head.y < 0 || head.y >= GRID_HEIGHT;
    }

    // ---------- STEP 12: Self collision ----------
    public boolean checkSelfCollision(Point head) {
        // start from index 1 - skip the head itself
        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                return true;
            }
        }
        return false;
    }

    // ---------- STEP 9/11/12: Called every timer tick ----------
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();

            Point head = snakeBody.get(0);
            if (checkWallCollision(head) || checkSelfCollision(head)) {
                running = false;
                timer.stop();
            }
        }
        repaint(); // triggers paintComponent()
    }

    // ---------- STEP 6: Keyboard controls ----------
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // prevent the snake from reversing directly into itself
        switch (key) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
        }
    }

    // required by KeyListener interface, but not needed here
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}