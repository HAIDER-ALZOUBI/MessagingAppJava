
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.text.Text;

/*
Note from Haider
When running client, wait for it to
fully build then run another one.
I noticed that sometimes the app wouldn't
build completely and causing it to not
work properly. After looking deeper
into this, I realized that the
mvn clean compile exec:java
has to do with this issue.
I have discussed this with other students
and noticed this issue too.
That is all I have for you, my program
should work perfectly fine with no bugs
or issues or errors.
 */

public class GuiClient extends Application {

	Client clientConnection;

	private Stage primaryStage;
	private HashMap<String, Scene> sceneMap;

	private ListView<String> messageListView;
	private ListView<String> onlineUsersListView;
	private ListView<String> onlineUsersListView2;

	private VBox chatsListBox = new VBox(10);

	private final int sceneWidth = 500;
	private final int sceneHeight = 500;

	private String chatTitle;


	// gui stuff coloring and things of that nature
	private DropShadow shadow = new DropShadow();

	private Image backgroundImage = new Image("LuhCalmBackground.jpg");

	private int buttonWidth = 150;
	private int buttonHeight = 5;



	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		clientConnection = new Client(data -> {
			Platform.runLater(() -> {
				Message message = (Message) data;

				if(message.getCommand().equals("viewOnlineUsers")){
					refreshOnlineUsersListView(message);
					refreshChatsList();
				}
				else if(message.getCommand().equals("friendMessage")){
					refreshChatLogListView(message);
				}
				else if(message.getCommand().equals("groupMessage")){
					refreshChatLogListView(message);
				}
				else if(message.getCommand().equals("globalMessage")){
					refreshChatLogListView(message);
				}
			});
		});

		clientConnection.start();

		messageListView = new ListView<>();
		onlineUsersListView = new ListView<>();
		onlineUsersListView2 = new ListView<>();

		shadow.setColor(Color.rgb(0, 0, 0, 0.5));
		shadow.setOffsetX(2);
		shadow.setOffsetY(2);

		sceneMap = new HashMap<>();
		sceneMap.put("login", createLoginGUI());
		sceneMap.put("main", createMainMenuGUI());
		sceneMap.put("users", createAddUsersGUI());
		sceneMap.put("chats", createChatListGUI());
		sceneMap.put("groups", createGroupCreateGUI());


		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("login"));
		primaryStage.setTitle("Client");
		primaryStage.show();
	}

	public Scene createGroupCreateGUI(){
		// titleText
		Text titleText = new Text("CREATE GROUP");
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");
		titleText.setEffect(shadow);

		//invalidText
		Text invalidText = new Text("");
		invalidText.setFill(Color.RED);
		invalidText.setFont(Font.font("System", FontWeight.BOLD, 15));
		invalidText.setStroke(Color.BLACK);
		invalidText.setStrokeWidth(2.0);
		invalidText.setStrokeType(StrokeType.OUTSIDE);
		invalidText.setWrappingWidth(150);

		// mainMenuButton
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		mainMenuButton.setMaxWidth(buttonWidth);
		mainMenuButton.setMaxHeight(buttonHeight);
		mainMenuButton.setEffect(shadow);
		mainMenuButton.setOnMouseEntered(event -> {
			mainMenuButton.setStyle("-fx-background-color: #c20000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnMouseExited(event -> {
			mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnAction(e -> {
			invalidText.setText("");
			primaryStage.setScene(sceneMap.get("main"));
		});

		// goToChatsButton
		Button goToChatsButton = new Button("View Chats");
		goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		goToChatsButton.setMaxWidth(buttonWidth);
		goToChatsButton.setMaxHeight(buttonHeight);
		goToChatsButton.setEffect(shadow);
		goToChatsButton.setOnMouseEntered(event -> {
			goToChatsButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnMouseExited(event -> {
			goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnAction(e -> {
			refreshChatsList();

			invalidText.setText("");
			primaryStage.setScene(sceneMap.get("chats"));
		});

		// usernameTextField
		TextField usernameTextField = new TextField();
		usernameTextField.setPromptText("Username");
		usernameTextField.setStyle("-fx-background-color: #fff200; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20; -fx-prompt-text-fill: rgba(0,0,0,0.73);");
		usernameTextField.setMaxWidth(buttonWidth);
		usernameTextField.setMaxHeight(buttonHeight);
		usernameTextField.setEffect(shadow);

		// addFriendButton
		Button addFriendButton = new Button("Add Friend");
		addFriendButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		addFriendButton.setMaxWidth(buttonWidth);
		addFriendButton.setMaxHeight(buttonHeight);
		addFriendButton.setEffect(shadow);
		addFriendButton.setOnMouseEntered(event -> {
			addFriendButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		addFriendButton.setOnMouseExited(event -> {
			addFriendButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		addFriendButton.setOnAction(e -> {
			invalidText.setFill(Color.RED);

			String usernameToAdd = usernameTextField.getText();

			if(usernameToAdd.isEmpty()){
				invalidText.setText("Username can't be empty");
			}
			else if(usernameToAdd.equals(clientConnection.getUsername())){
				invalidText.setText("You can't add yourself!");
			}
			else if(!clientConnection.getFriendsMap().containsKey(usernameToAdd)){
				invalidText.setText("You can only add FRIENDS to a group!");
			}
			else if(clientConnection.getCurrentGroupCreating().contains(usernameToAdd)){
				invalidText.setText(usernameToAdd + " is already added to the group!");
			}
			else {
				clientConnection.getCurrentGroupCreating().add(usernameToAdd);

				invalidText.setFill(Color.GREEN);
				invalidText.setText("Added Friends to Group: " + clientConnection.getCurrentGroupCreating());
			}
		});

		// groupNameTextField
		TextField groupNameTextField = new TextField();
		groupNameTextField.setPromptText("Group Name");
		groupNameTextField.setStyle("-fx-background-color: #fff200; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20; -fx-prompt-text-fill: rgba(0,0,0,0.73);");
		groupNameTextField.setMaxWidth(buttonWidth);
		groupNameTextField.setMaxHeight(buttonHeight);
		groupNameTextField.setEffect(shadow);

		// confirmGroupCreateButton
		Button confirmGroupCreateButton = new Button("Confirm");
		confirmGroupCreateButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		confirmGroupCreateButton.setMaxWidth(buttonWidth);
		confirmGroupCreateButton.setMaxHeight(buttonHeight);
		confirmGroupCreateButton.setEffect(shadow);
		confirmGroupCreateButton.setOnMouseEntered(event -> {
			confirmGroupCreateButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		confirmGroupCreateButton.setOnMouseExited(event -> {
			confirmGroupCreateButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		confirmGroupCreateButton.setOnAction(e -> {
			invalidText.setFill(Color.RED);

			String groupNameToAdd = groupNameTextField.getText();

			if(groupNameToAdd.isEmpty()){
				invalidText.setText("Group Name can't be empty");
			}
			else if(groupNameToAdd.contains("-")){
				invalidText.setText("Group Name can't contain '-'");
			}
			else if(clientConnection.getCurrentGroupCreating().size() < 3){
				invalidText.setText("Minimum of at least 3 people in a group, there are only " + clientConnection.getCurrentGroupCreating().size());
			}
			else if(clientConnection.findGroupByName(groupNameToAdd) != null){
				invalidText.setText("Group Name already Exists!");
			}
			else {
				clientConnection.createGroup(groupNameToAdd, clientConnection.getCurrentGroupCreating());

				// in case want to make another group
				clientConnection.setCurrentGroupCreating(new ArrayList<>());
				clientConnection.getCurrentGroupCreating().add(clientConnection.getUsername());

				invalidText.setFill(Color.GREEN);
				invalidText.setText("Created Group: \"" + groupNameToAdd + "\". You can still create more groups");
			}
		});

		// innerButtonsVBox
		VBox innerButtonsVBox = new VBox(10);
		innerButtonsVBox.getChildren().addAll(usernameTextField, addFriendButton, groupNameTextField, confirmGroupCreateButton, invalidText);

		// usersListBox
		VBox usersListBox = new VBox(5);
		Text onlineUsersTitle = new Text("Online Users");
		usersListBox.getChildren().addAll(onlineUsersTitle, onlineUsersListView2);

		//hbox
		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(innerButtonsVBox, usersListBox);

		//vbox
		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, hbox, goToChatsButton, mainMenuButton);
		vbox.setAlignment(Pos.CENTER);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	public Scene createChatListGUI() {
		//titleText
		Text titleText = new Text("CHATS");
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");
		titleText.setEffect(shadow);

		//mainMenuButton
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		mainMenuButton.setMaxWidth(buttonWidth);
		mainMenuButton.setMaxHeight(buttonHeight);
		mainMenuButton.setEffect(shadow);
		mainMenuButton.setOnMouseEntered(event -> {
			mainMenuButton.setStyle("-fx-background-color: #c20000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnMouseExited(event -> {
			mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("main"));
		});

		chatsListBox.setAlignment(Pos.CENTER);

		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, chatsListBox, mainMenuButton);
		vbox.setAlignment(Pos.CENTER);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	public Scene createAddUsersGUI() {
		// titleText
		Text titleText = new Text("ADD USERS");
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");
		titleText.setEffect(shadow);

		//invalidText
		Text invalidText = new Text("");
		invalidText.setFill(Color.RED);
		invalidText.setFont(Font.font("System", FontWeight.BOLD, 15));
		invalidText.setStroke(Color.BLACK);
		invalidText.setStrokeWidth(2.0);
		invalidText.setStrokeType(StrokeType.OUTSIDE);
		invalidText.setWrappingWidth(150);

		// mainMenuButton
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		mainMenuButton.setMaxWidth(buttonWidth);
		mainMenuButton.setMaxHeight(buttonHeight);
		mainMenuButton.setEffect(shadow);
		mainMenuButton.setOnMouseEntered(event -> {
			mainMenuButton.setStyle("-fx-background-color: #c20000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnMouseExited(event -> {
			mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnAction(e -> {
			invalidText.setText("");
			primaryStage.setScene(sceneMap.get("main"));
		});

		// usernameTextField
		TextField usernameTextField = new TextField();
		usernameTextField.setPromptText("Username");
		usernameTextField.setStyle("-fx-background-color: #fff200; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20; -fx-prompt-text-fill: rgba(0,0,0,0.73);");
		usernameTextField.setMaxWidth(buttonWidth);
		usernameTextField.setMaxHeight(buttonHeight);
		usernameTextField.setEffect(shadow);

		// goToChatsButton
		Button goToChatsButton = new Button("View Chats");
		goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		goToChatsButton.setMaxWidth(buttonWidth);
		goToChatsButton.setMaxHeight(buttonHeight);
		goToChatsButton.setEffect(shadow);
		goToChatsButton.setOnMouseEntered(event -> {
			goToChatsButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnMouseExited(event -> {
			goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnAction(e -> {
			refreshChatsList();

			primaryStage.setScene(sceneMap.get("chats"));
		});

		// addUserButton
		Button addUserButton = new Button("Add");
		addUserButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		addUserButton.setMaxWidth(buttonWidth);
		addUserButton.setMaxHeight(buttonHeight);
		addUserButton.setEffect(shadow);
		addUserButton.setOnMouseEntered(event -> {
			addUserButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		addUserButton.setOnMouseExited(event -> {
			addUserButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		addUserButton.setOnAction(e -> {
			invalidText.setFill(Color.RED);

			String usernameToAdd = usernameTextField.getText();

			if(usernameToAdd.isEmpty()){
				invalidText.setText("Username can't be empty");
			}
			else if(usernameToAdd.equals(clientConnection.getUsername())){
				invalidText.setText("You can't add yourself!");
			}
			else if(clientConnection.getFriendsMap().containsKey(usernameToAdd)){
				invalidText.setText("\"" + usernameToAdd + "\" is already added!");
			}
			else {
				if(clientConnection.addUser(usernameToAdd) == 1) {
					invalidText.setFill(Color.GREEN);
					invalidText.setText("Added \"" + usernameToAdd + "\" to Friends and Chats");
					clientConnection.requestOnlineUsersUpdate();
				}
				else{
					invalidText.setText("User \"" + usernameToAdd + "\" does not exist!");
				}
			}
		});

		// innerButtonsVBox
		VBox innerButtonsVBox = new VBox(10);
		innerButtonsVBox.getChildren().addAll(usernameTextField, addUserButton, invalidText);

		// usersListBox
		VBox usersListBox = new VBox(5);
		Text onlineUsersTitle = new Text("Online Users");
		usersListBox.getChildren().addAll(onlineUsersTitle, onlineUsersListView);

		//hbox
		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(innerButtonsVBox, usersListBox);

		//vbox
		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, hbox, goToChatsButton, mainMenuButton);
		vbox.setAlignment(Pos.CENTER);

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	public Scene createLoginGUI() {
		//titleText
		Text titleText = new Text("LOGIN");
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");

		//invalidText
		Text invalidText = new Text("");
		invalidText.setFill(Color.RED);
		invalidText.setFont(Font.font("System", FontWeight.BOLD, 15));
		invalidText.setStroke(Color.BLACK);
		invalidText.setStrokeWidth(2.0);
		invalidText.setStrokeType(StrokeType.OUTSIDE);
		invalidText.setWrappingWidth(150);

		//usernameTextField
		TextField usernameTextField = new TextField();
		usernameTextField.setPromptText("Username");
		usernameTextField.setStyle("-fx-background-color: #fff200; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20; -fx-prompt-text-fill: rgba(0,0,0,0.73);");
		usernameTextField.setMaxWidth(buttonWidth);
		usernameTextField.setMaxHeight(buttonHeight);
		usernameTextField.setEffect(shadow);

		//confirmButton
		Button confirmButton = new Button("Confirm");
		confirmButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		confirmButton.setMaxWidth(buttonWidth);
		confirmButton.setMaxHeight(buttonHeight);
		confirmButton.setEffect(shadow);
		confirmButton.setOnMouseEntered(event -> {
			confirmButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		confirmButton.setOnMouseExited(event -> {
			confirmButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		confirmButton.setOnAction(e -> {
			String username = usernameTextField.getText();

			if(username == null || username.isEmpty()){
				invalidText.setText("Username Invalid.");
			}
			else if(username.contains("-")){
				invalidText.setText("Username Can't contain '-'");
			}
			else{
				int usernameStatus = clientConnection.setUsername(username);

				if (usernameStatus == 1) {
					clientConnection.requestOnlineUsersUpdate();

					primaryStage.setScene(sceneMap.get("main"));
					primaryStage.setTitle("Client - " + username);
				}
				else if (usernameStatus == 2) {
					invalidText.setText("Username Exists.");
				}
				else {
					invalidText.setText("Error with the Server.");
				}
			}
		});

		//vbox
		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, usernameTextField, confirmButton, invalidText);
		vbox.setAlignment(Pos.CENTER);
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	public Scene createMainMenuGUI() {
		//titleText
		Text titleText = new Text("MAIN MENU");
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");
		titleText.setEffect(shadow);

		//viewChatsButton
		Button viewChatsButton = new Button("View Chats");
		viewChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		viewChatsButton.setMaxWidth(buttonWidth);
		viewChatsButton.setMaxHeight(buttonHeight);
		viewChatsButton.setEffect(shadow);
		viewChatsButton.setOnMouseEntered(event -> {
			viewChatsButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		viewChatsButton.setOnMouseExited(event -> {
			viewChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		viewChatsButton.setOnAction(e -> {
			refreshChatsList();

			primaryStage.setScene(sceneMap.get("chats"));
		});

		//viewUsersButton
		Button viewUsersButton = new Button("Add Friends");
		viewUsersButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		viewUsersButton.setMaxWidth(buttonWidth);
		viewUsersButton.setMaxHeight(buttonHeight);
		viewUsersButton.setEffect(shadow);
		viewUsersButton.setOnMouseEntered(event -> {
			viewUsersButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		viewUsersButton.setOnMouseExited(event -> {
			viewUsersButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		viewUsersButton.setOnAction(e -> {
			primaryStage.setScene(sceneMap.get("users"));
		});

		//createGroupButton
		Button createGroupButton = new Button("Create Groups");
		createGroupButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		createGroupButton.setMaxWidth(buttonWidth);
		createGroupButton.setMaxHeight(buttonHeight);
		createGroupButton.setEffect(shadow);
		createGroupButton.setOnMouseEntered(event -> {
			createGroupButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		createGroupButton.setOnMouseExited(event -> {
			createGroupButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		createGroupButton.setOnAction(e -> {
			clientConnection.setCurrentGroupCreating(new ArrayList<String>());
			clientConnection.getCurrentGroupCreating().add(clientConnection.getUsername());

			primaryStage.setScene(sceneMap.get("groups"));
		});

		//exitButton
		Button exitButton = new Button("Exit");
		exitButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		exitButton.setMaxWidth(buttonWidth);
		exitButton.setMaxHeight(buttonHeight);
		exitButton.setEffect(shadow);
		exitButton.setOnMouseEntered(event -> {
			exitButton.setStyle("-fx-background-color: #c20101; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		exitButton.setOnMouseExited(event -> {
			exitButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		exitButton.setOnAction(e -> {
			Platform.exit();
			System.exit(0);
		});

		//vbox
		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, viewChatsButton, viewUsersButton, createGroupButton, exitButton);
		vbox.setAlignment(Pos.CENTER);
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	public Scene createChatScreenGUI() {
		//titleText
		Text titleText = new Text(chatTitle);
		titleText.setStyle("-fx-background-color: #000000; -fx-font-size: 30px; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 20;");
		titleText.setEffect(shadow);

		//invalidText
		Text invalidText = new Text("");
		invalidText.setFill(Color.RED);
		invalidText.setFont(Font.font("System", FontWeight.BOLD, 15));
		invalidText.setStroke(Color.BLACK);
		invalidText.setStrokeWidth(2.0);
		invalidText.setStrokeType(StrokeType.OUTSIDE);
		invalidText.setWrappingWidth(150);

		// messageTextField
		TextField messageTextField = new TextField();
		messageTextField.setPromptText("Message");
		messageTextField.setStyle("-fx-background-color: #fff200; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20; -fx-prompt-text-fill: rgba(0,0,0,0.73);");
		messageTextField.setMaxWidth(buttonWidth);
		messageTextField.setMaxHeight(buttonHeight);
		messageTextField.setEffect(shadow);

		// sendButton
		Button sendButton = new Button("Send");
		sendButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		sendButton.setMaxWidth(buttonWidth);
		sendButton.setMaxHeight(buttonHeight);
		sendButton.setEffect(shadow);
		sendButton.setOnMouseEntered(event -> {
			sendButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		sendButton.setOnMouseExited(event -> {
			sendButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		sendButton.setOnAction(e -> {
			if(messageTextField.getText().isEmpty()){
				invalidText.setText("Message can't be empty");
			}
			else{
				invalidText.setText("");

				if(Objects.equals(chatTitle, "Global-Chat")){
					clientConnection.requestToMessageGlobalChat(messageTextField.getText());
				}
				else if(chatTitle.contains("Group-")){
					clientConnection.handleMessageToGroup(messageTextField.getText());
				}
				else {
					clientConnection.handleMessageToFriend(messageTextField.getText());
				}
			}
		});

		// goToChatsButton
		Button goToChatsButton = new Button("View Chats");
		goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		goToChatsButton.setMaxWidth(buttonWidth);
		goToChatsButton.setMaxHeight(buttonHeight);
		goToChatsButton.setEffect(shadow);
		goToChatsButton.setOnMouseEntered(event -> {
			goToChatsButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnMouseExited(event -> {
			goToChatsButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		goToChatsButton.setOnAction(e -> {
			chatTitle = null;
			clientConnection.setCurrentChatter(null);
			clientConnection.setCurrentGroupChatter(null);

			refreshChatsList();

			invalidText.setText("");
			primaryStage.setScene(sceneMap.get("chats"));
		});

		//mainMenuButton
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		mainMenuButton.setMaxWidth(buttonWidth);
		mainMenuButton.setMaxHeight(buttonHeight);
		mainMenuButton.setEffect(shadow);
		mainMenuButton.setOnMouseEntered(event -> {
			mainMenuButton.setStyle("-fx-background-color: #c20000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnMouseExited(event -> {
			mainMenuButton.setStyle("-fx-background-color: #ff0000; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		mainMenuButton.setOnAction(e -> {
			chatTitle = null;
			clientConnection.setCurrentChatter(null);
			clientConnection.setCurrentGroupChatter(null);

			invalidText.setText("");
			primaryStage.setScene(sceneMap.get("main"));
		});

		// innerButtonsVBox
		VBox innerButtonsVBox = new VBox(10);
		innerButtonsVBox.getChildren().addAll(messageTextField, sendButton, invalidText);

		// chatLogListBox
		VBox chatLogListBox = new VBox(5);
		chatLogListBox.getChildren().addAll(messageListView);

		//hbox
		HBox hbox = new HBox(10);
		hbox.getChildren().addAll(innerButtonsVBox, chatLogListBox);

		VBox vbox = new VBox(20);
		vbox.getChildren().addAll(titleText, hbox, goToChatsButton, mainMenuButton);
		vbox.setAlignment(Pos.CENTER);
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(vbox);

		// stack pane
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(new ImageView(backgroundImage), borderPane);

		return new Scene(stackPane, sceneWidth, sceneHeight);
	}

	// Refreshes the online users ListView
	private void refreshOnlineUsersListView(Message message){
		ArrayList<String> updatedUsers;
		updatedUsers = message.getStringArrayList();

		// set current user to show as "username (Me)"
		if(message.getStringArrayList() != null) {
			if (updatedUsers.contains(clientConnection.getUsername())) {
				for (int i = 0; i < updatedUsers.size(); i++) {
					if (updatedUsers.get(i).equals(clientConnection.getUsername())) {
						updatedUsers.set(i, clientConnection.getUsername() + " (Me)");
					} else if (clientConnection.getFriendsMap().containsKey(updatedUsers.get(i))) {
						updatedUsers.set(i, updatedUsers.get(i) + " (Friend)");
					}
				}
			}

			onlineUsersListView.getItems().clear();
			onlineUsersListView.getItems().addAll(updatedUsers);
			onlineUsersListView2.getItems().clear();
			onlineUsersListView2.getItems().addAll(updatedUsers);
		}
	}

	private void refreshChatLogListView(Message message){
		ArrayList<String> updateChatLog = message.getStringArrayList();

		messageListView.getItems().clear();
		messageListView.getItems().addAll(updateChatLog);
	}

	// Refreshes the CHATS List and buttons
	private void refreshChatsList(){
		chatsListBox.getChildren().clear();

		// Global chat
		Button globalChatButton = new Button("Global-Chat");
		globalChatButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		globalChatButton.setMaxWidth(buttonWidth);
		globalChatButton.setMaxHeight(buttonHeight);
		globalChatButton.setEffect(shadow);
		globalChatButton.setOnMouseEntered(event -> {
			globalChatButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		globalChatButton.setOnMouseExited(event -> {
			globalChatButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
		});
		globalChatButton.setOnAction(e -> {
			messageListView.getItems().clear();
			messageListView.getItems().addAll(clientConnection.getGlobalChatLog());

			clientConnection.setCurrentChatter("Global-Chat");
			chatTitle = "Global-Chat";

			primaryStage.setScene(createChatScreenGUI());
		});

		chatsListBox.getChildren().add(globalChatButton);

		// Group Chats
		Button groupChatButton;

		for (Group currentGroup : clientConnection.getGroupsList()) {
			groupChatButton = new Button("Group-" + currentGroup.getGroupName());
			groupChatButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			groupChatButton.setMaxWidth(buttonWidth);
			groupChatButton.setMaxHeight(buttonHeight);
			groupChatButton.setEffect(shadow);
            Button finalGroupChatButton2 = groupChatButton;
			groupChatButton.setOnMouseEntered(event -> {
				finalGroupChatButton2.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			});
            Button finalGroupChatButton3 = groupChatButton;
			groupChatButton.setOnMouseExited(event -> {
				finalGroupChatButton3.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			});
			groupChatButton.setOnAction(e -> {
				messageListView.getItems().clear();
				messageListView.getItems().addAll(currentGroup.getChatLog());

				clientConnection.setCurrentGroupChatter(currentGroup);
				chatTitle = "Group-" + currentGroup.getGroupName();

				primaryStage.setScene(createChatScreenGUI());
			});
			chatsListBox.getChildren().add(groupChatButton);
		}

		// Friends Chats
		Button userChatButton;

		for (String currentUser : clientConnection.getFriendsMap().keySet()) {
			userChatButton = new Button("Friend-" + currentUser);
			userChatButton.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			userChatButton.setMaxWidth(buttonWidth);
			userChatButton.setMaxHeight(buttonHeight);
			userChatButton.setEffect(shadow);
			Button finalUserChatButton = userChatButton;
			userChatButton.setOnMouseEntered(event -> {
				finalUserChatButton.setStyle("-fx-background-color: #01ec77; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			});
			Button finalUserChatButton1 = userChatButton;
			userChatButton.setOnMouseExited(event -> {
				finalUserChatButton1.setStyle("-fx-background-color: #00ff80; -fx-font-size: 14px; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-background-radius: 20;");
			});
			userChatButton.setOnAction(e -> {
				if (clientConnection.getFriendsMap().containsKey(currentUser)) {
					messageListView.getItems().clear();
					messageListView.getItems().addAll(clientConnection.getFriendsMap().get(currentUser));

					clientConnection.setCurrentChatter(currentUser);
					chatTitle = "Friend-" + currentUser;

					primaryStage.setScene(createChatScreenGUI());
				} else {
					refreshChatsList();
					System.out.println("The chat button for \"" + currentUser + "\" wont work because they left, removing button...");
				}
			});
			chatsListBox.getChildren().add(userChatButton);
		}
	}
}
