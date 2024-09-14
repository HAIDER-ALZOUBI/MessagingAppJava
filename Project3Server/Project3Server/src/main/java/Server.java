import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;


public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

	HashMap<String, ClientThread> usernamesMap = new HashMap<>();
	ArrayList<String> onlineUsers = new ArrayList<String>();

	private ArrayList<String> globalChatLog = new ArrayList<>();


	TheServer server;
	private Consumer<Serializable> callback;


	Server(Consumer<Serializable> call){
		callback = call;
		server = new TheServer();
		server.start();
	}


	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");

				while(true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("New Client has connected to server: Client #" + count);
					clients.add(c);
					c.start();

					count++;
				}
			}
			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}
	}
	class ClientThread extends Thread{
		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;

		String username;
		int count;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

		public void updateClients(Message message, String sender, String receiver) {
			if (usernamesMap.containsKey(sender)) {
				if (usernamesMap.containsKey(receiver)) {

					ClientThread t = usernamesMap.get(receiver);

					try {
						t.out.reset();
						t.out.writeObject(message);
						t.out.flush();
					} catch (Exception e) {
					}

					System.out.println("[updateClients] sender \"" + sender + "\" sent to \"" + receiver + "\"");

				} else {
					System.out.println("[updateClients] receiver \"" + receiver + "\" doesn't exist");
				}
			}
			else{
				System.out.println("[updateClients] sender \"" + sender + "\" doesn't exist");
			}
		}

		public void updateClients(Message message) {
			for (int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);

				if(usernamesMap.containsValue(t)) {
					try {
						t.out.reset();
						t.out.writeObject(message);
						t.out.flush();

					} catch (Exception e) {}
				}
			}

			System.out.println("updateClients sent to all online users! " + usernamesMap.keySet());
		}

		public void run(){
			// Setting Streams
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);

				out.reset();
				out.flush();
			}
			catch(Exception e) {
				System.out.println("Streams are not open!");
			}

			// SETTING USERNAME
			try {
				while (true) {
					System.out.println("[Server] Client #" + count + " called command: setUsername");

					Message usernameToGetMessage = (Message) in.readObject();
					username = usernameToGetMessage.getSender();

					System.out.println("Client #" + count + " set username to " + username);

					// Check if the username already exists
					if (usernamesMap.containsKey(username)) {
						out.reset();
						out.writeObject(new Message("existing username", null, null, false));
						out.flush();
					} else {
						updateClients(new Message(username, "userJoined"));

						out.reset();
						out.writeObject(new Message("good username", null, null, false));
						out.flush();

						usernamesMap.put(username, this);
						onlineUsers.add(username);

						break;
					}
				}
			}
			catch(Exception e){

			}

			updateClients(new Message(globalChatLog, "receiveGlobalChatLog"), username, username);

			while (true) {
				try {
					Message commandReceived = (Message) in.readObject();
					System.out.println("[Server] Client #" + count + " called command: " + commandReceived.getCommand());

					// viewOnlineUsers
					if (Objects.equals(commandReceived.getCommand(), "viewOnlineUsers")) {
						System.out.println("Client #" + count + " is trying to view viewers");

						updateClients(new Message(onlineUsers, "viewOnlineUsers"), commandReceived.getSender(), commandReceived.getSender()); // sent to their self

						System.out.println("Server sent online users list to Client #" + count);

					// newFriendAdd
					} else if (Objects.equals(commandReceived.getCommand(), "newFriendAdd")) {
						System.out.println("User \"" + commandReceived.getSender() + "\" is trying to add: \"" + commandReceived.getReceiver() + "\"");
						updateClients(new Message(commandReceived.getSender(), "newFriendAdd"), commandReceived.getSender(), commandReceived.getReceiver()); // sent from sender to receiver

						callback.accept(commandReceived.getSender() + " added " + commandReceived.getReceiver());

					} else if (Objects.equals(commandReceived.getCommand(), "friendMessage")) {
						System.out.println("User \"" + commandReceived.getSender() + "\" is trying to Message: \"" + commandReceived.getReceiver() + "\"");
						updateClients(new Message(commandReceived.getMessage(), commandReceived.getSender(), commandReceived.getReceiver(), commandReceived.getIsGlobal(), "friendMessage"), commandReceived.getSender(), commandReceived.getReceiver()); // sent from sender to receiver

						callback.accept(commandReceived.getSender() + " said to " + commandReceived.getReceiver() + ": " + commandReceived.getMessage());

					} else if (Objects.equals(commandReceived.getCommand(), "globalMessage")) {
						System.out.println("User \"" + commandReceived.getSender() + "\" is trying to Message in global chat");
						updateClients(commandReceived);

						globalChatLog.add(commandReceived.getSender() + ": " + commandReceived.getMessage());

						callback.accept(commandReceived.getSender() + " said to everyone: " + commandReceived.getMessage());
					} else if (Objects.equals(commandReceived.getCommand(), "groupMessage")) {
						String groupName = commandReceived.getReceiver();
						String messageSender = commandReceived.getSender();
						String messageSent = commandReceived.getMessage();
						ArrayList<String> groupMembers = commandReceived.getStringArrayList();

						System.out.println("User \"" + messageSender + "\" is trying send message in group: " + groupName);

						for(int i = 0; i < groupMembers.size(); i++){
							if(!Objects.equals(groupMembers.get(i), messageSender)) {
								updateClients(commandReceived, messageSender, groupMembers.get(i));
							}
						}

						callback.accept(messageSender + " said to group \"" + groupName + "\": " + messageSent);
					} else if (Objects.equals(commandReceived.getCommand(), "groupAdd")) {
						String groupName = commandReceived.getMessage();
						ArrayList<String> groupMembers = commandReceived.getStringArrayList();
						String groupCreator = groupMembers.get(0);

						System.out.println("User \"" + groupCreator + "\" is trying create a group named: " + groupName);

						for(int i = 1; i < groupMembers.size(); i++){
							System.out.println("User \"" + groupCreator + "\" added member: " + groupMembers.get(i));
							updateClients(commandReceived, groupCreator, groupMembers.get(i));
						}

						callback.accept("New Group: \"" + groupName + "\" With Members " + groupMembers);
					} else {
						System.out.println("INVALID COMMAND!!!");
					}
				}
				catch (Exception e) {
					callback.accept("Client #" + count + " has left the server!");
					System.out.println("Client #" + count + " has left the server!");
					updateClients(new Message(username, "userLeft")); // sender is the person who left

					clients.remove(this);
					usernamesMap.remove(username);
					onlineUsers.remove(username);

					break;
				}
			}
		}//end of run
	}//end of client thread
}






