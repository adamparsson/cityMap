import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
//player.getTransforms().add(new Rotate(30));

public class Main extends Application {
	private	HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
	private ArrayList<Node> buildings = new ArrayList<Node>();
	private ArrayList<Node> roadNodes = new ArrayList<Node>();
	private ArrayList<Node> roads = new ArrayList<Node>();
	private Pane appRoot = new Pane();
	private Pane gameRoot = new Pane();
	private Node player;
	private Random random = new Random();
	private boolean running = true;

	//methods
	private void update() {
		if (isPressed(KeyCode.W)) {movePlayerY(-2);};
		if (isPressed(KeyCode.S)) {movePlayerY(2);};
		if (isPressed(KeyCode.A)) {movePlayerX(-2);};
		if (isPressed(KeyCode.D)) {movePlayerX(2);};
		
		if (isPressed(KeyCode.N)) {placeRoadNode();};
		if (isPressed(KeyCode.L)) {placeRoad();};

		if (isPressed(KeyCode.H)) {placeBuilding();};
	}

	private void movePlayerX(int delta) {
		player.setTranslateX(player.getTranslateX() + delta);
	}

	private void movePlayerY(int delta) {
		player.setTranslateY(player.getTranslateY() + delta);
	}

	private void placeBuilding() {
		int x = (int)roadNodes.get(roadNodes.size()-1).getTranslateX();
		int y = (int)roadNodes.get(roadNodes.size()-1).getTranslateY();
		System.out.println(x);
		int rot = randomIntInRange(0,359);
		Node building = createRectangle(x, y, 20, 20, Color.GRAY, rot);
		buildings.add(building);
	}

	private void placeRoadNode() {
		boolean collision = false;
		for (Node roadNode : roadNodes) {
			if (player.getBoundsInParent().intersects(roadNode.getBoundsInParent())) {
				collision = true;	
				break;
			}
		}

		if (collision == false) {
			int x = (int)player.getTranslateX();
			int y = (int)player.getTranslateY();
			Node roadNode = createCircle(x, y, 10, Color.LIGHTGRAY);
			roadNodes.add(roadNode);
		}
	}

	private void placeRoad() {
		double x = roadNodes.get(roadNodes.size()-1).getTranslateX();
		double y = roadNodes.get(roadNodes.size()-1).getTranslateY();
		double x2 = roadNodes.get(roadNodes.size()-2).getTranslateX();
		double y2 = roadNodes.get(roadNodes.size()-2).getTranslateY();
		
		double width = Math.abs(x2-x);
		double height = Math.abs(y2-y);
		double h = Math.hypot(width, height);
		//double angle = Math.toDegrees(Math.asin(height/h)); 
		double angle = 0;

		double w = 10.0;

		Rectangle road = new Rectangle(x2, y2, w, h);
		road.setFill(Color.WHITE);
		road.setStroke(Color.LIGHTGRAY);
		road.setStrokeWidth(2);
		road.getTransforms().add(new Rotate(-45));

		gameRoot.getChildren().add(road);
		roads.add(road);
	}

	private boolean isPressed(KeyCode key) {
		//check if key is pressed, default to false
		return keys.getOrDefault(key, false);
	}

	public void initContent() {
		Rectangle bg = new Rectangle(1280, 720, Color.LIGHTGREEN);
		player = createCircle(100,100, 10, Color.YELLOW);
		appRoot.getChildren().addAll(bg, gameRoot);
	}

	private Node createCircle(int x, int y, int r, Color color) {
		Circle circle = new Circle(r);
		circle.setTranslateX(x);
		circle.setTranslateY(y);
		circle.setFill(color);
		circle.getProperties().put("exists", true);
		gameRoot.getChildren().add(circle);
		return circle;
	}

	private Node createRectangle(int x, int y, int w, int h, Color color, int rot) {
		Rectangle rectangle = new Rectangle(w, h);
		rectangle.setTranslateX(x);
		rectangle.setTranslateY(y);
		rectangle.setFill(color);
		rectangle.getProperties().put("exists", true);
		rectangle.getTransforms().add(new Rotate(rot));
		gameRoot.getChildren().add(rectangle);
		return rectangle;
	}

	private Node createLine(int startX, int startY, int endX, int endY) {
		Line line = new Line(startX, startY, endX, endY);
		line.setStroke(Color.WHITE);
		line.setStrokeWidth(9);
		line.setStrokeType(StrokeType.OUTSIDE);
		line.setStrokeLineCap(StrokeLineCap.ROUND);
		line.getProperties().put("exists", true);
		gameRoot.getChildren().add(line);
		return line;
	}

	private int randomIntInRange(int min, int max) {
		int n = max - min + 1;
		int i = random.nextInt() % n;
		return min + i;
	}

	@Override
	public void start(Stage primaryStage) {
		initContent();
		Scene scene = new Scene(appRoot);
		scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
		scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
		primaryStage.setTitle("cityMap i guess");
		primaryStage.setScene(scene);
		primaryStage.show();

		AnimationTimer timer = new AnimationTimer() {
			public void handle(long now) {
				if (running) {
					update();
				}
			}
		};
		timer.start();
	}
};
