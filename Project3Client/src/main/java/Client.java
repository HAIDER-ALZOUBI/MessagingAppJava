import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

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

public class Client extends Thread{
	
	Socket socketClient;
	ObjectOutputStream out;
	ObjectInputStream in;

	private String username;
	private ArrayList<String> onlineUsersList = new ArrayList<>();
	private HashMap<String, ArrayList<String>> friendsMap;	// string username and string ArrayList of chat

	private String currentChatter;
	private Group currentGroupChatter;

	private ArrayList<Group> groupsList = new ArrayList<>();

	private ArrayList<String> currentGroupCreating;

	private ArrayList<String> globalChatLog = new ArrayList<>();

	private Message messageReceived;
	private Consumer<Serializable> callback;

	Client(Consumer<Serializable> call){
		callback = call;
		friendsMap = new HashMap<>();
	}
	
	public void run() {
		try {
			socketClient= new Socket("127.0.0.1",5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {

		}

		while(true){
			try{
				if(username != null) {
					messageReceived = (Message) in.readObject();

					if (messageReceived != null && messageReceived.getCommand() != null) {
						if (Objects.equals(messageReceived.getCommand(), "receiveGlobalChatLog")) {
							System.out.println("Received from server command: receiveGlobalChatLog");

							globalChatLog.addAll(messageReceived.getStringArrayList());
						} else if (Objects.equals(messageReceived.getCommand(), "userLeft")) {
							System.out.println("Received from server command: userLeft");

							handleUserLeaveServer();
							requestOnlineUsersUpdate();
						} else if (Objects.equals(messageReceived.getCommand(), "userJoined") && !Objects.equals(messageReceived.getSender(), username)) {
							System.out.println("Received from server command: userJoined");

							handleUserJoinServer();
							requestOnlineUsersUpdate();
						} else if (Objects.equals(messageReceived.getCommand(), "viewOnlineUsers")) {
							System.out.println("Received from server command: viewOnlineUsers");

							handleOnlineUsersUpdate();
						} else if (Objects.equals(messageReceived.getCommand(), "newFriendAdd")) {
							System.out.println("Received from server command: newFriendAdd");

							handleNewFriendAdd();
						} else if (Objects.equals(messageReceived.getCommand(), "friendMessage")) {
							System.out.println("Received from server command: friendMessage");

							handleMessageFromFriend();
						} else if (Objects.equals(messageReceived.getCommand(), "globalMessage")) {
							System.out.println("Received from server command: globalMessage");

							handleMessageFromGlobalChat();
						}
						else if (Objects.equals(messageReceived.getCommand(), "groupAdd")) {
							System.out.println("Received from server command: groupAdd");

							handleGroupAdd();
						}
						else if (Objects.equals(messageReceived.getCommand(), "groupMessage")) {
							System.out.println("Received from server command: groupMessage");

							handleMessageFromGroup();
						}
						else {
							System.out.println("INVALID COMMAND!!!");
						}
					}
				}

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
    }

	public void handleMessageToGroup(String messageString){
		String groupName = currentGroupChatter.getGroupName();
		ArrayList<String> groupMembers = currentGroupChatter.getMembers();

		Message messageToSend = new Message(messageString, username, groupName, groupMembers, "groupMessage");

		try {
			out.reset();
			out.writeObject(messageToSend);
			out.flush();

			System.out.println("Sent message to " + currentGroupChatter.getGroupName());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < groupsList.size(); i++){
			if(Objects.equals(groupsList.get(i).getGroupName(), groupName)){
				groupsList.get(i).getChatLog().add(username + ": " + messageString);
				callback.accept(new Message(groupsList.get(i).getChatLog(), "groupMessage"));

				break;
			}
		}
	}

	public void handleMessageFromGroup(){
		String groupName = messageReceived.getReceiver();
		String messageSender = messageReceived.getSender();
		String messageSent = messageReceived.getMessage();
		ArrayList<String> groupMembers = messageReceived.getStringArrayList();

		System.out.println("[GROUP_MESSAGE] Received message from " + messageSender + " in group " + groupName);

		for(int i = 0; i < groupsList.size(); i++){
			if(Objects.equals(groupsList.get(i).getGroupName(), groupName)){
				groupsList.get(i).getChatLog().add(messageSender + ": " + messageSent);

				if(currentGroupChatter != null) {
					if (Objects.equals(currentGroupChatter.getGroupName(), groupName)) {
						callback.accept(new Message(groupsList.get(i).getChatLog(), "groupMessage"));
					}
				}
				break;
			}
		}
	}

	private void handleGroupAdd(){
		String groupName = messageReceived.getMessage();
		ArrayList<String> groupMembers = messageReceived.getStringArrayList();

		groupsList.add(new Group(groupName, groupMembers));

		System.out.println("[GROUP_ADDED] New group was added to groupsList and named: " + groupName);
		System.out.println("[GROUP_MEMBERS] List: " + groupMembers);

		requestOnlineUsersUpdate();
	}

	public Group findGroupByName(String groupName) {
		for (Group group : groupsList) {
			if (Objects.equals(group.getGroupName(), groupName)) {
				return group;
			}
		}
		return null;
	}

	public void createGroup(String groupName, ArrayList<String> groupMembers) {
		Group newGroup = new Group(groupName, groupMembers);
		groupsList.add(newGroup);

		System.out.println("[GROUP_CREATED] New group was added to groupsList and named: " + groupName);
		System.out.println("[GROUP_MEMBERS] List: " + groupMembers);

		try {
			System.out.println("[GROUP_ADD] Trying to notify the new members to be added to group");

			Message commandToSend = new Message(groupMembers, groupName, "groupAdd");
			out.reset();
			out.writeObject(commandToSend);
			out.flush();

			System.out.println("Sent to server command: groupAdd");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleMessageFromGlobalChat(){
		String messageReceivedFromGlobalChat = messageReceived.getMessage();
		String theSender = messageReceived.getSender();

		if(!Objects.equals(theSender, username)){
			globalChatLog.add(theSender + ": " + messageReceivedFromGlobalChat);
		}

		if(!Objects.equals(theSender, username) && Objects.equals(currentChatter, "Global-Chat")){
			callback.accept(new Message(globalChatLog, "globalMessage"));
		}
	}

	public void requestToMessageGlobalChat(String message){
		try {
			out.reset();
			out.writeObject(new Message(message, username, null, "globalMessage"));
			out.flush();

			System.out.println("Sent to server command: globalMessage");

		} catch (IOException e) {
			e.printStackTrace();
		}

		globalChatLog.add(username + ": " + message);
		callback.accept(new Message(globalChatLog, "globalMessage"));

	}

	private void handleNewFriendAdd(){
		String newFriendAddName = messageReceived.getSender();

		if (!friendsMap.containsKey(newFriendAddName)) {
			friendsMap.put(newFriendAddName, new ArrayList<>());
			System.out.println("User \"" + messageReceived.getSender() + "\" added you as a friend");

			requestOnlineUsersUpdate();
		}
	}

	private void handleUserLeaveServer(){
		String userWhoLeft = messageReceived.getSender();
		onlineUsersList.remove(userWhoLeft);

		System.out.println("User \"" + messageReceived.getSender() + "\" left the server");

		for(int i = 0; i < friendsMap.size(); i++){
			if(friendsMap.containsKey(messageReceived.getSender())) {
				friendsMap.remove(messageReceived.getSender());
				System.out.println("Friend \"" + messageReceived.getSender() + "\" was removed from friends map");
				break;
			}
		}

		callback.accept(new Message("viewOnlineUsers"));
	}

	private void handleOnlineUsersUpdate() {
		ArrayList<String> updatedUsers = messageReceived.getStringArrayList();

		onlineUsersList.clear();
		onlineUsersList.addAll(updatedUsers);

		System.out.println("Online users updated");

		if (onlineUsersList != null && !onlineUsersList.isEmpty()) {
			System.out.println("Online Users: " + onlineUsersList);
		} else {
			System.out.println("There are no online users");
		}

		callback.accept(new Message(onlineUsersList, "viewOnlineUsers"));
	}

	public void requestOnlineUsersUpdate() {
		try {
			Message commandToSend = new Message(username, "viewOnlineUsers");
			out.reset();
			out.writeObject(commandToSend);
			out.flush();

			System.out.println("Sent to server command: viewOnlineUsers");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleMessageToFriend(String messageString) {
		if (checkCurrentChatterOnline()) {
			Message messageToSend = new Message(messageString, username, currentChatter, false, "friendMessage");

			try {
				out.reset();
				out.writeObject(messageToSend);
				out.flush();

				System.out.println("Sent message to " + currentChatter);
			} catch (IOException e) {
				e.printStackTrace();
			}

			friendsMap.get(currentChatter).add(username + ": " + messageString);
			callback.accept(new Message(friendsMap.get(currentChatter), "friendMessage"));
		}
		else{
			ArrayList<String> userLeftMessage = new ArrayList<>();
			userLeftMessage.add(currentChatter + " HAS LEFT THE SERVER (USER UNAVAILABLE)");
			callback.accept(new Message(userLeftMessage, "friendMessage"));

			System.out.println(currentChatter + " left, cannot chat to them");
		}
	}

	public void handleMessageFromFriend(){
		String messageString = messageReceived.getMessage();
		String friendReceivedFrom = messageReceived.getSender();

		System.out.println("Received message from " + friendReceivedFrom);

		friendsMap.get(friendReceivedFrom).add(friendReceivedFrom + ": " + messageString);

		if(Objects.equals(currentChatter, friendReceivedFrom)){
			callback.accept(new Message(friendsMap.get(friendReceivedFrom), "friendMessage"));
		}
	}

	public boolean checkCurrentChatterOnline(){
		return friendsMap.containsKey(currentChatter);
	}

	private void handleUserJoinServer(){
		if(!Objects.equals(username, messageReceived.getSender())){
			System.out.println("User \"" + messageReceived.getSender() + "\" joined the server");
		}
		else{
			System.out.println("Welcome to the server!");
		}
	}

	public int addUser(String usernameToAdd){
		System.out.println("[Client] User is trying to add a user");

		if(onlineUsersList.contains(usernameToAdd)){
			friendsMap.put(usernameToAdd, new ArrayList<String>());

			System.out.println("Added the user: \"" + usernameToAdd + "\" to friends map");
			System.out.println("Friends List: " + friendsMap.keySet());

			try {
				out.reset();
				out.writeObject(new Message(null, username, usernameToAdd, false, "newFriendAdd"));
				out.flush();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			return 1;
		}
		else{
			System.out.println("User \"" + usernameToAdd + "\" does not exist or invalid input");
		}

		return -1;
	}

	public int setUsername(String username){
		System.out.println("[Client] User is trying to set username");

		Message usernameResult;
		Message usernameToSet = new Message(username, null);

		int result = -1;

		try {
			out.reset();
			out.writeObject(usernameToSet);
			out.flush();

			// Read response from server
			usernameResult = (Message) in.readObject();
			System.out.println("Username result: " + usernameResult.getMessage());

			if(Objects.equals(usernameResult.getMessage(), "good username")){
				this.username = username;
				System.out.println("Username set to: " + username);

				result = 1;
			}
			else if(Objects.equals(usernameResult.getMessage(), "existing username")){
				this.username = null;
				result = 2;
			}
		}
		catch (ClassNotFoundException | IOException e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	public ArrayList<String> getOnlineUsersList() {
		return onlineUsersList;
	}

	public String getUsername() {
		return username;
	}

	public HashMap<String, ArrayList<String>> getFriendsMap() {
		return friendsMap;
	}

	public void setCurrentChatter(String currentChatter) {
		this.currentChatter = currentChatter;
	}

	public String getCurrentChatter() {
		return currentChatter;
	}

	public void setCurrentGroupChatter(Group currentGroupChatter) {
		this.currentGroupChatter = currentGroupChatter;
	}

	public Group getCurrentGroupChatter() {
		return currentGroupChatter;
	}

	public ArrayList<String> getGlobalChatLog() {
		return globalChatLog;
	}

	public ArrayList<Group> getGroupsList() {
		return groupsList;
	}

	public ArrayList<String> getCurrentGroupCreating() {
		return currentGroupCreating;
	}

	public void setCurrentGroupCreating(ArrayList<String> currentGroupCreating) {
		this.currentGroupCreating = currentGroupCreating;
	}

}
