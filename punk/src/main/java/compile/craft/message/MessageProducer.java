package compile.craft.message;

/**
 * Parser/Lexer/Compiler/Interpreter implements interface MessageProducer, owns a MessageHandler
 */
public interface MessageProducer {
    void addMessageListener(MessageListener listener);
    void removeMessageListener(MessageListener listener);
    void sendMessage(Message message);
}
