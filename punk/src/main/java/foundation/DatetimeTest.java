package foundation;

import java.util.TimeZone;

public class DatetimeTest {

    private static long localTimezoneOffset = TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();

    public static void main(String[] args) {
        System.out.println(getNextRollTs(10, 10));
    }

    public static long getNextRollTs(long ts, long period) {
        period = period * 1000;
        ts = ts + localTimezoneOffset;
        long ret = (ts / period + 1) * period;
        ret = ret - localTimezoneOffset;
        return ret;
    }
}
