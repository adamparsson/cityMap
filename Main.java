import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
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
	private boolean running = true;

	//methods
	private void update() {
		if (isPressed(KeyCode.W)) {movePlayerY(-2);};
		if (isPressed(KeyCode.S)) {movePlayerY(2);};
		if (isPressed(KeyCode.A)) {movePlayerX(-2);};
		if (isPressed(KeyCode.D)) {movePlayerX(2);};
		
		if (isPressed(KeyCode.L)) {placeRoad();};
	}

	private void movePlayerX(int delta) {
		player.setTranslateX(player.getTranslateX() + delta);
	}

	private void movePlayerY(int delta) {
		player.setTranslateY(player.getTranslateY() + delta);
	}

	private void placeRoad() {
		int x = (int)player.getTranslateX();
		int y = (int)player.getTranslateY();

		//instansiate a new road object
		Rectangle newRoad = new Rectangle(x, y, 500, 20);
		newRoad.setFill(Color.WHITE);
		newRoad.setStroke(Color.LIGHTGRAY);
		newRoad.setStrokeWidth(2);
		if (isCanBuildRoad(newRoad) == true) {reallyBuildRoad(newRoad);};
	}

	private void reallyBuildRoad(Rectangle newRoad) {
		gameRoot.getChildren().add(newRoad);
		roads.add(newRoad);
		placeAdressesOnNewRoad(newRoad);
	}

	private boolean isCanBuildRoad(Rectangle newRoad) {
		//check for collision with other roads
		for (Node otherRoad : roads) {
			if (newRoad.getBoundsInParent().intersects(otherRoad.getBoundsInParent())) {
				return false;
			}
		}
		return true;
	}

	private void placeAdressesOnNewRoad(Rectangle newRoad) {
		int offset = 25;
		int distance = 50;
		int roadX = (int)newRoad.getX();
		int roadY = (int)newRoad.getY();
		int width = (int)newRoad.getWidth();
		for (int i = roadX+offset; i < roadX+width; i+=distance) {placeAdress(i, roadY);};
	}

	private void placeAdress(int x, int y){
			System.out.println("new adress at " + x + ", " + y);
			Rectangle newAdress = new Rectangle(x, y-1, 2, 2);
			newAdress.setFill(Color.ORANGE);
			adresses.add(newAdress);
			gameRoot.getChildren().add(newAdress);

			placeEstate(newAdress);
	};

	private void placeEstate(Rectangle newAdress) {
		int x = (int)newAdress.getX()-20;
		int y = (int)newAdress.getY()-60;
		Rectangle newEstate = new Rectangle(x, y, 40, 60);
		newEstate.setFill(Color.DARKGREEN);
		estates.add(newEstate);
		gameRoot.getChildren().add(newEstate);
		placeBuilding(newEstate);
	}

	private void placeBuilding(Rectangle newEstate) {
		int estateX = (int)newEstate.getX();
		int estateY = (int)newEstate.getY();
		int estateWidth = (int)newEstate.getWidth();
		int estateHeight = (int)newEstate.getHeight();

		int width = (int)randomIntInRange(20, estateWidth-20);
		int height = (int)randomIntInRange(20, estateHeight-20);
		int x = estateX + randomIntInRange(0, estateWidth-width);
		int y = estateY + randomIntInRange(0, estateHeight-height);

		Rectangle newBuilding = new Rectangle(x, y, width, height);
		newBuilding.setFill(Color.GRAY);
		buildings.add(newBuilding);
		gameRoot.getChildren().add(newBuilding);
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

	private int randomIntInRange(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randomNum;
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
