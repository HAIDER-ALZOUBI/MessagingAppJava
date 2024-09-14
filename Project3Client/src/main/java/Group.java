import java.util.ArrayList;

public class Group{
    private String groupName;
    private ArrayList<String> members;
    private ArrayList<String> chatLog;

    public Group(String groupName, ArrayList<String> members) {
        this.groupName = groupName;
        this.members = members;
        this.chatLog = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void removeMember(String member) {
        members.remove(member);
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void addMessageToChatLog(String message) {
        chatLog.add(message);
    }

    public ArrayList<String> getChatLog() {
        return chatLog;
    }
}