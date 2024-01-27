package compile.craft.message;

public class ParserMessageListener implements MessageListener {
    @Override
    public void messageReceived(Message message) {
        MessageType type = message.type;

        switch (type) {
            case TOKEN:
                break;
            case SYNTAX_ERROR:
                break;
            case PARSER_SUMMARY:
                break;
        }
    }
}