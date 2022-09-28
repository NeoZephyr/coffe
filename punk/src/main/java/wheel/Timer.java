package wheel;

import java.util.List;

public class Timer {
    String key;
    long expire = 0;
    long period = 0;
    List params;
    Runnable runner;
}
