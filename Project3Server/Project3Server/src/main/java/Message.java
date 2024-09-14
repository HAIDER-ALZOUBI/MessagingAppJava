import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    private String message;

    private String sender;
    private String receiver;

    private boolean isGlobal;

    private String command;

    private ArrayList<String> stringArrayList;

    public Message(String message, String sender, String receiver, boolean isGlobal){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isGlobal = isGlobal;
    }

    public Message(String message, String sender, String receiver, boolean isGlobal, String command){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isGlobal = isGlobal;
        this.command = command;
    }

    public Message(String sender, String command){
        this.sender = sender;
        this.command = command;
    }

    public Message(ArrayList<String> stringArrayList, String command){
        this.stringArrayList = stringArrayList;
        this.command = command;
    }

    public Message(ArrayList<String> stringArrayList, String message, String command){
        this.stringArrayList = stringArrayList;
        this.message = message;
        this.command = command;
    }

    public Message(String message, String sender, String receiver){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message(String message, String sender, String receiver, String command){
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.command = command;
    }

    public Message(String command){
        this.command = command;
    }

    public Message(ArrayList<String> stringArrayList){
        this.stringArrayList = stringArrayList;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public boolean getIsGlobal(){
        return isGlobal;
    }

    public String getCommand(){
        return command;
    }

    public ArrayList<String> getStringArrayList() {
        return stringArrayList;
    }
}
