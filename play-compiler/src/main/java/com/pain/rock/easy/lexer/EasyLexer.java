package com.pain.rock.easy.lexer;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

public class EasyLexer {

    private List<Token> tokens = null;
    private StringBuilder tokenText = null;
    private TokenType tokenType = null;

    public TokenReader tokenize(String script) {
        if (StringUtils.isBlank(script)) {
            return null;
        }

        tokens = new ArrayList<>();
        CharArrayReader reader = new CharArrayReader(script.toCharArray());
        DFAState state = DFAState.Initial;
        int ic;

        try {
            while ((ic = reader.read()) != -1) {
                char c = (char) ic;

                // process -1 as EOF

                switch (state) {
                    case Initial:
                        state = initToken(c);
                        break;
                    case Id:
                        if (isAlpha(c) || isDigit(c)) {
                            tokenText.append(c);
                        } else {
                            state = initToken(c);
                        }
                        break;
                    case GT:
                        if (c == '=') {
                            tokenText.append(c);
                            tokenType = TokenType.GE;
                            state = DFAState.GE;
                        } else {
                            state = initToken(c);
                        }
                        break;
                    case GE:
                    case Assignment:
                    case Plus:
                    case Minus:
                    case Star:
                    case Slash:
                    case SemiColon:
                    case LeftParenthesis:
                    case RightParenthesis:
                        state = initToken(c);
                        break;
                    case IntLiteral:
                        if (isDigit(c)) {
                            tokenText.append(c);
                        } else {
                            state = initToken(c);
                        }
                        break;
                    case Id_int1:
                        if (c == 'n') {
                            state = DFAState.Id_int2;
                            tokenText.append(c);
                        } else if (isAlpha(c) || isDigit(c)) {
                            state = DFAState.Id;
                            tokenText.append(c);
                        } else {
                            state = initToken(c);
                        }
                        break;
                    case Id_int2:
                        if (c == 't') {
                            state = DFAState.Id_int3;
                            tokenText.append(c);
                        } else if (isAlpha(c) || isDigit(c)) {
                            state = DFAState.Id;
                            tokenText.append(c);
                        } else {
                            state = initToken(c);
                        }
                        break;
                    case Id_int3:
                        if (isBlank(c)) {
                            tokenType = TokenType.Int;
                            state = initToken(c);
                        } else if (isAlpha(c) || isDigit(c)) {
                            state = DFAState.Id;
                            tokenText.append(c);
                        } else {
                            throw new RuntimeException(String.format("token: %s, next c: %c", tokenText, c));
                        }
                    default:
                }
            }

            if (tokenText != null && tokenText.length() > 0) {
                tokens.add(new EasyToken(tokenType, tokenText.toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        EasyTokenReader tokenReader = new EasyTokenReader(tokens);
        tokens = null;
        tokenText = null;
        tokenType = null;
        return tokenReader;
    }

    private DFAState initToken(char c) {

        if (tokenText != null && tokenText.length() > 0) {
            tokens.add(new EasyToken(tokenType, tokenText.toString()));
        }

        tokenText = new StringBuilder();

        if (isAlpha(c)) {
            tokenText.append(c);
            tokenType = TokenType.Identifier;

            if (c == 'i') {
                return DFAState.Id_int1;
            } else {
                return DFAState.Id;
            }
        }

        if (isDigit(c)) {
            tokenText.append(c);
            tokenType = TokenType.IntLiteral;
            return DFAState.IntLiteral;
        }

        if (c == '>') {
            tokenText.append(c);
            tokenType = TokenType.GT;
            return DFAState.GT;
        }

        if (c == '+') {
            tokenText.append(c);
            tokenType = TokenType.Plus;
            return DFAState.Plus;
        }

        if (c == '-') {
            tokenText.append(c);
            tokenType = TokenType.Minus;
            return DFAState.Minus;
        }

        if (c == '*') {
            tokenText.append(c);
            tokenType = TokenType.Star;
            return DFAState.Star;
        }

        if (c == '/') {
            tokenText.append(c);
            tokenType = TokenType.Slash;
            return DFAState.Slash;
        }

        if (c == ';') {
            tokenText.append(c);
            tokenType = TokenType.SemiColon;
            return DFAState.SemiColon;
        }

        if (c == '(') {
            tokenText.append(c);
            tokenType = TokenType.LeftParenthesis;
            return DFAState.LeftParenthesis;
        }

        if (c == ')') {
            tokenText.append(c);
            tokenType = TokenType.RightParenthesis;
            return DFAState.RightParenthesis;
        }

        if (c == '=') {
            tokenText.append(c);
            tokenType = TokenType.Assignment;
            return DFAState.Assignment;
        }

        return DFAState.Initial;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isBlank(char c) {
        return (c == ' ' || c == '\t' || c == '\n');
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class EasyToken implements Token {

        private TokenType type;
        private String text;

        @Override
        public TokenType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    private static class EasyTokenReader implements TokenReader {

        private final List<Token> tokens;
        private int position = 0;

        EasyTokenReader(List<Token> tokens) {
            this.tokens = tokens;
        }

        @Override
        public Token read() {
            if (CollectionUtils.isEmpty(tokens)) {
                return null;
            }

            if (position >= tokens.size()) {
                return null;
            }

            return tokens.get(position++);
        }

        @Override
        public Token peek() {
            if (CollectionUtils.isEmpty(tokens)) {
                return null;
            }

            if (position >= tokens.size()) {
                return null;
            }

            return tokens.get(position);
        }

        @Override
        public void unread() {
            if (position > 0) {
                --position;
            }
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public void setPosition(int position) {
            if (position < 0 || position >= tokens.size()) {
                return;
            }

            this.position = position;
        }
    }
}
