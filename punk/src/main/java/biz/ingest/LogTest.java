package biz.ingest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {

    static Logger logger = LoggerFactory.getLogger("abc");

    public static void main(String[] args) {
        logger.info("abc");
    }
}
