import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Snake extends Application {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final int TILE_SIZE = 20;
    private static final int NUM_ROWS = HEIGHT / TILE_SIZE;
    private static final int NUM_COLS = WIDTH / TILE_SIZE;

    private List<Position> snake;
    private Direction direction = Direction.RIGHT;
    private Position food;
    private boolean gameOver = false;

    private Random random = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);

        snake = new ArrayList<>();
        snake.add(new Position(NUM_COLS / 2, NUM_ROWS / 2));
        placeFood();

        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP && direction != Direction.DOWN) {
                direction = Direction.UP;
            } else if (e.getCode() == KeyCode.DOWN && direction != Direction.UP) {
                direction = Direction.DOWN;
            } else if (e.getCode() == KeyCode.LEFT && direction != Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (e.getCode() == KeyCode.RIGHT && direction != Direction.LEFT) {
                direction = Direction.RIGHT;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        timeline.play();
    }

    private void run(GraphicsContext gc) {
        if (gameOver) {
            return;
        }

        // Move the snake
        Position head = snake.get(0);
        switch (direction) {
            case UP:
                head = new Position(head.x, head.y - 1);
                break;
            case DOWN:
                head = new Position(head.x, head.y + 1);
                break;
            case LEFT:
                head = new Position(head.x - 1, head.y);
                break;
            case RIGHT:
                head = new Position(head.x + 1, head.y);
                break;
        }

        // Check for collisions
        if (head.x < 0 || head.x >= NUM_COLS || head.y < 0 || head.y >= NUM_ROWS || snake.contains(head)) {
            gameOver = true;
            showGameOverAlert();
            return;
        }

        snake.add(0, head);
        if (head.equals(food)) {
            placeFood();
        } else {
            snake.remove(snake.size() - 1);
        }

        // Draw
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(javafx.scene.paint.Color.GREEN);
        for (Position p : snake) {
            gc.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private void placeFood() {
        int x = random.nextInt(NUM_COLS);
        int y = random.nextInt(NUM_ROWS);
        food = new Position(x, y);
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Game Over! Your score: " + (snake.size() - 1));
        alert.show();
    }

    private static class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Position position = (Position) obj;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
