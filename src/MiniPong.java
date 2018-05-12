/* --------------------------- IMPORTS --------------------------*/

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 * @author enrique
 * @version 1.0
 * TODO ver como lo tienen hecho los profesores, traducir todo al inglés y optimizar el proyecto y documentar y explicar el modo de instalación de JavaFX en eclipse
 */

public class MiniPong extends Application {


	/* -------------------------- ATRIBUTOS -------------------------------*/


	private Pane panel;
	private Circle pelota;
	private Label scoreBoard, pointsBoard, velocityBoard, changeVBoard, ControlsRight, ControlsLeft;
	private Rectangle barra, bot;
	private Text GameOver;

	private double dxBall =  1.4, dyBall = -2, ballSpeed = 1.25;
	private int dxBar = 4, ballRadius = 10, WIDTH_BAR_HUMAN = 60, WIDTH_BAR_BOT = 60, HEIGHT_BAR = 10, points = 0;
	private final int WIDTH = 600, HEIGHT = 400;
	AnimationTimer timer ;

	private List<Color> colores = new ArrayList<>();
	private List<String> input = new ArrayList<>();


	/*---------------------------- MÉTODOS ------------------------------*/


	public void quitarElemento(Node nodo) {
		FadeTransition quitar = new FadeTransition(Duration.millis(2300), nodo);
		quitar.setFromValue(1.0);
		quitar.setToValue(0.0);
		quitar.play();
	}


	public void showGameOver() {
		GameOver = new Text();
		GameOver.setFill(Color.GRAY);
		GameOver.setLayoutX(WIDTH/2-150);
		GameOver.setLayoutY(HEIGHT/2);
		GameOver.setFont(Font.font("Lucida Console", FontWeight.EXTRA_LIGHT, 50));
		FadeTransition ft = new FadeTransition(Duration.millis(1500), GameOver);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.setCycleCount(Timeline.INDEFINITE);
		ft.setAutoReverse(true);

		quitarElemento(pelota);
		quitarElemento(barra);
		quitarElemento(bot);
		quitarElemento(ControlsLeft);
		quitarElemento(ControlsRight);
		quitarElemento(scoreBoard);
		quitarElemento(changeVBoard);
		quitarElemento(pointsBoard);
		quitarElemento(velocityBoard);

		panel.getChildren().add(GameOver);
		ft.play();
		GameOver.setText("GAME OVER");
	}


	public void aumetarVelocidadPelota() {
		dxBall *= ballSpeed;
		dyBall *= ballSpeed;
	}


	public void moverBarra() {
		if(input.contains("LEFT") && (barra.getLayoutX() > 0))
			barra.setLayoutX(barra.getLayoutX()-dxBar);
		if(input.contains("RIGHT") && (barra.getLayoutX()+WIDTH_BAR_HUMAN < WIDTH))	
			barra.setLayoutX(barra.getLayoutX()+dxBar);
	}


	public void moverBot() {
		bot.setLayoutX(pelota.getLayoutX()-15);
		double posIniBotX = bot.getLayoutX();
		double posFinBotX = bot.getLayoutX()+WIDTH_BAR_BOT;
		double posFinBotY = bot.getLayoutY()+HEIGHT_BAR;
		double posXPelota = pelota.getLayoutX();
		double posYPelota = pelota.getLayoutY();
		boolean limiteX = (posXPelota >= posIniBotX && posXPelota <= posFinBotX);
		boolean limiteY = (posYPelota-ballRadius <= posFinBotY);
		if(limiteX &&  limiteY) {
			dyBall = -dyBall;
		}
	}


	public void testPelota() {
		//comprobación límites de la escena en el eje X
		if(pelota.getLayoutX() > WIDTH-ballRadius || pelota.getLayoutX() < ballRadius) {
			dxBall = -dxBall;
		}
		//comprobación límites de la escena en el eje Y
		if(pelota.getLayoutY() < ballRadius)
			dyBall = -dyBall;
		//comprobación límites de la escena para terminar el juego
		if(pelota.getLayoutY() > HEIGHT-ballRadius) {
			showGameOver();
			timer.stop();
		}
		//comprobación de interacción barra-pelota (NO ES CON ÁNGULOS)
		double posBar = barra.getLayoutX();
		double posInicBar = posBar;
		double posMitadBar = posBar+WIDTH_BAR_HUMAN/2;
		double posFinBar = posBar+WIDTH_BAR_HUMAN;
		boolean limiteY = (pelota.getLayoutY()+ballRadius >= barra.getLayoutY());
		boolean limiteX = (pelota.getLayoutX() >= posInicBar && pelota.getLayoutX() <= posFinBar);
		boolean primeraMitad = (pelota.getLayoutX() >= posMitadBar && pelota.getLayoutX() <= posFinBar);
		if(limiteY && limiteX) {
			if(primeraMitad) {
				dxBall = -dxBall;
			}

			dyBall = -dyBall;
			points++;
			pointsBoard.setText(String.valueOf(points));

			if((points%4 == 0) && !colores.isEmpty()) {
				Color setColor = colores.get(0);
				colores.remove(0);
				aumetarVelocidadPelota();
				velocityBoard.setText("Increased Velocity:");
				velocityBoard.setLayoutX(WIDTH-185);
				changeVBoard.setText(String.valueOf(Math.abs(dxBall)));
				pelota.setFill(setColor);
			}
		}
		//movimiento de la pelota
		pelota.setLayoutX(pelota.getLayoutX()+dxBall);
		pelota.setLayoutY(pelota.getLayoutY()+dyBall);
	}
	
	
	public void setNodeEffects(Node nodo, Color color, int x,int y, int z) {
		Light.Point light = new Light.Point();
		light.setColor(color);
		light.setX(x);
		light.setY(y);
		light.setZ(z);
		Lighting lighting = new Lighting();
		lighting.setLight(light);
		nodo.setEffect(lighting);
	}


	public Parent create() {
		panel = new Pane();
		panel.setPrefSize(WIDTH, HEIGHT);
		panel.setStyle("-fx-background-color: black");

		scoreBoard = new Label("ScoreBoard:");
		scoreBoard.setLayoutX(WIDTH-140);
		scoreBoard.setLayoutY(10);

		pointsBoard =  new Label("0");
		pointsBoard.setLayoutX(WIDTH-54);
		pointsBoard.setLayoutY(10);

		velocityBoard = new Label("Velocity: ");
		velocityBoard.setLayoutX(WIDTH-117);
		velocityBoard.setLayoutY(30);

		changeVBoard = new Label(String.valueOf(dxBall));
		changeVBoard.setLayoutX(WIDTH-54);
		changeVBoard.setLayoutY(30);

		pelota = new Circle();
		pelota.setFill(Color.WHITE);//*****
		pelota.setRadius(ballRadius);
		pelota.setLayoutX(WIDTH/2);
		pelota.setLayoutY(HEIGHT/2);

		barra = new Rectangle();
		barra.setFill(Color.WHITE);//*****
		barra.setWidth(WIDTH_BAR_HUMAN);
		barra.setHeight(HEIGHT_BAR);
		barra.setLayoutX(WIDTH/2-15);
		barra.setLayoutY(380);

		bot = new Rectangle();
		bot.setFill(Color.WHITE);
		bot.setWidth(WIDTH_BAR_BOT);
		bot.setHeight(HEIGHT_BAR);
		bot.setLayoutX(pelota.getLayoutX());
		bot.setLayoutY(20);

		setNodeEffects(barra, Color.NAVAJOWHITE, WIDTH_BAR_HUMAN/2, HEIGHT_BAR/2, 20);
		setNodeEffects(bot, Color.DARKCYAN, WIDTH_BAR_BOT/2, HEIGHT_BAR/2, 100);
		setNodeEffects(pelota, Color.WHITE, 0, 0, 20);

		ControlsRight = new Label("Move right press: ⇒");
		ControlsRight.setLayoutX(10);
		ControlsRight.setLayoutY(10);

		ControlsLeft = new Label("Move left press: ⇐");
		ControlsLeft.setLayoutX(20);
		ControlsLeft.setLayoutY(30);

		panel.getChildren().addAll(ControlsRight, ControlsLeft, pelota, barra, bot, scoreBoard, pointsBoard, velocityBoard, changeVBoard);

		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				moverBarra();
				moverBot();
				testPelota();
			}
		};
		timer.start();

		return panel;
	}


	public void setArrayColours() {
		colores.add(Color.YELLOWGREEN);
		colores.add(Color.ORANGE);
		colores.add(Color.ORANGERED);
		colores.add(Color.DARKRED);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Mini Pong");

		setArrayColours();

		Scene scene = new Scene(create());
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String code = event.getCode().toString();
				if(!input.contains(code))
					input.add( code );
			}
		}); 

		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String code = event.getCode().toString();
				input.remove( code );
			}
		});

	}

	public static void main(String[] args) {
		launch(args);
	}

}