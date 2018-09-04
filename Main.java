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
	private ArrayList<Node> roadNodes = new ArrayList<Node>();
	private ArrayList<Node> roads = new ArrayList<Node>();
	private ArrayList<Node> adresses = new ArrayList<Node>();
	private ArrayList<Node> estates = new ArrayList<Node>();
	private ArrayList<Node> buildings = new ArrayList<Node>();
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
		//declare position and size of new road
		int x = (int)player.getTranslateX();
		int y = (int)player.getTranslateY();
		int width = 500;
		int height = 20;

		//instansiate a new road and set colors + stroke
		Rectangle newRoad = new Rectangle(x, y, width, height);
		newRoad.setFill(Color.WHITE);
		newRoad.setStroke(Color.LIGHTGRAY);
		newRoad.setStrokeWidth(2);

		//check if new road overlaps another road
		boolean collision = false;
		for (Node otherRoad : roads) {
			if (newRoad.getBoundsInParent().intersects(otherRoad.getBoundsInParent())) {
				collision = true;	
				break;
			}
		}

		//add road to gameRoot and roads ArrayList
		if (collision == false) {
			gameRoot.getChildren().add(newRoad);
			roads.add(newRoad);

			//add adresses to the new road on left side
			for (int i = x; i < x+width; i+=50) {
				Rectangle newAdress = createAdress(i, y);
				gameRoot.getChildren().add(newAdress);
			};
			//add adresses to the new road on right side
			for (int i = x; i < x+width; i+=50) {
				Rectangle newAdress = createAdress(i, y+20);
				gameRoot.getChildren().add(newAdress);
			};

		}
	}

	private Rectangle createAdress(int x, int y) {
		//declare constant size of new adress
		int width = 2;
		int height = 2;

		//instansiate a new adress and set color
		Rectangle newAdress = new Rectangle(x, y-1, width, height);
		newAdress.setFill(Color.ORANGE);

		//add to adresses ArrayList and return new adress
		adresses.add(newAdress);
		return newAdress;
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
