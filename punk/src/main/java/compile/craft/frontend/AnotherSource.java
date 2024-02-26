package compile.craft.frontend;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * peek 还可以 next + retreat 实现
 * 每次 next 的时候缓存到 cache1 里面，cache1 空间固定，类似一个窗口
 * retreat 的时候，将 cache1 里面的转移到 cache2 里面
 * next 的时候，先从 cache2 中获取，如果没有，再从 stream 里面获取
 */
public class AnotherSource {

    public BufferedReader reader;
    public int lineno = 1;
    public int column = 1;
    public int p = 0;
    public char c;
    public boolean readAll = false;

    public LinkedList<Character> lookahead = new LinkedList<>();

    @SneakyThrows
    public AnotherSource(BufferedReader reader) {
        this.reader = reader;
        int r = reader.read();

        if (r == -1) {
            c = CharUtils.EOF;
            readAll = true;
        } else {
            c = (char) r;
        }
    }

    public char current() {
        return c;
    }

    public boolean eof() {
        return c == CharUtils.EOF;
    }

    public char advance() throws IOException {
        if (c == CharUtils.EOF) {
            return c;
        }

        if (c == '\n') {
            lineno++;
            column = 0;
        }

        if (lookahead.isEmpty()) {
            int r = reader.read();

            if (r == -1) {
                c = CharUtils.EOF;
            } else {
                c = (char) r;
            }
        } else {
            c = lookahead.poll();
        }

        p++;
        column++;

        return c;
    }

    public char peek() throws IOException {
        return peek(1);
    }

    public char peek(int k) throws IOException {
        if (c == CharUtils.EOF) {
            return c;
        }

        if (lookahead.size() >= k) {
            return lookahead.get(k - 1);
        }

        int i = lookahead.size();
        char tmp = 0;

        while (i < k) {
            int r = reader.read();

            if (r == -1) {
                tmp = CharUtils.EOF;
                break;
            } else {
                tmp = (char) r;
            }

            lookahead.addLast(tmp);
            i++;
        }

        return tmp;
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
