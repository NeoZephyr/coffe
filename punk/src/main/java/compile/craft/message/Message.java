package compile.craft.message;

public class Message {
    public MessageType type;
    public Object body;

    public Message(MessageType type, Object body) {
        this.type = type;
        this.body = body;
    }
}
