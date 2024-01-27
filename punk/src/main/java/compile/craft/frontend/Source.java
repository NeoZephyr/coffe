package compile.craft.frontend;

import java.io.BufferedReader;
import java.io.IOException;

public class Source {
    public static final int CAPACITY = 16;

    public BufferedReader reader;
    public char[] buffer = new char[CAPACITY];
    public int size;
    public int lineno = 1;
    public int column = 1;
    public int p = 0;
    public char c;
    public boolean readAll = false;

    public Source(BufferedReader reader) throws IOException {
        this.reader = reader;
        size = reader.read(buffer);

        if (size <= 0) {
            c = CharUtils.EOF;
            readAll = true;
        } else {
            c = buffer[p];
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

        if (p + 1 >= size) {
            load();

            if (p + 1 >= size) {
                c = CharUtils.EOF;
                return c;
            }
        }

        p++;
        column++;
        c = buffer[p];
        return c;
    }

    public char peek() throws IOException {
        return peek(1);
    }

    public char peek(int k) throws IOException {
        if (p + k >= size) {
            load();

            if (p + k >= size) {
                return CharUtils.EOF;
            }
        }

        return buffer[p + k];
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    private void load() throws IOException {
        int remain = size - p;
        System.arraycopy(buffer, p, buffer, 0, remain);
        int n = reader.read(buffer, remain, CAPACITY - remain);

        if (n <= 0) {
            readAll = true;
            size = remain;
        } else {
            size = remain + n;
        }

        p = 0;
    }
}