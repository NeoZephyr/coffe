package compile.craft.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceMessageListener implements MessageListener {
    @Override
    public void messageReceived(Message message) {
        MessageType type = message.type;
        Object[] body = (Object[]) message.body;

        switch (type) {
            case SOURCE_LINE:
                int lineno = (int) body[0];
                String line = (String) body[1];
                log.info(String.format("%03d %s", lineno, line));
                break;
        }
    }
}
