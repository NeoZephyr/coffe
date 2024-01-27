package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import static compile.craft.frontend.CharUtils.isDigit;
import static compile.craft.frontend.ErrorCode.RANGE_DECIMAL;
import static compile.craft.frontend.ErrorCode.RANGE_FLOAT;
import static java.lang.Float.MAX_EXPONENT;

public class NumberToken extends Token {

    public NumberToken(Source source) {
        super(source);
    }

    @SneakyThrows
    @Override
    protected void extract() {
        StringBuilder sb = new StringBuilder();
        String wholeDigits = extractDigits(sb);
        String fractionDigits = null;
        String exponentDigits = null;
        char exponentSign = '+';
        boolean moreDot = false;
        kind = TokenKind.DECIMAL_LITERAL;

        if (StringUtils.isEmpty(wholeDigits)) {
            kind = TokenKind.ERROR;
            text = sb.toString();
            return;
        }

        if (source.current() == '.') {
            if (source.peek() == '.') {
                moreDot = true;
            } else {
                sb.append(source.current());
                source.advance(); // consume decimal point
                fractionDigits = extractDigits(sb);

                if (StringUtils.isEmpty(fractionDigits)) {
                    kind = TokenKind.ERROR;
                    text = sb.toString();
                    return;
                }

                kind = TokenKind.FLOAT_LITERAL;
            }
        }

        if (!moreDot && (source.current() == 'E' || source.current() == 'e')) {
            kind = TokenKind.FLOAT_LITERAL;
            sb.append(source.current());
            char c = source.advance();

            if (c == '+' || c == '-') {
                sb.append(c);
                exponentSign = c;
                source.advance();
            }

            exponentDigits = extractDigits(sb);

            if (StringUtils.isEmpty(exponentDigits)) {
                kind = TokenKind.ERROR;
                text = sb.toString();
                return;
            }
        }

        if (kind == TokenKind.DECIMAL_LITERAL) {
            int number = computeDecimal(wholeDigits);

            if (kind != TokenKind.ERROR) {
                value = number;
            }
        } else {
            float number = computeFloat(wholeDigits, fractionDigits, exponentDigits, exponentSign);

            if (kind != TokenKind.ERROR) {
                value = (double) number;
            }
        }

        text = sb.toString();
    }

    @SneakyThrows
    private String extractDigits(StringBuilder sb) {
        StringBuilder digits = new StringBuilder();
        char c = source.current();

        while (isDigit(c)) {
            sb.append(c);
            digits.append(c);
            c = source.advance();
        }

        return digits.toString();
    }

    private int computeDecimal(String digits) {
        if (digits == null) {
            return 0;
        }

        int curValue = 0;
        int prevValue = -1; // overflow occurred if prevValue > value
        int index = 0;

        while ((index < digits.length()) && (curValue >= prevValue)) {
            prevValue = curValue;
            curValue = 10 * curValue + Character.getNumericValue(digits.charAt(index++));
        }

        if (curValue >= prevValue) {
            return curValue;
        } else {
            kind = TokenKind.ERROR;
            value = RANGE_DECIMAL;
            return 0;
        }
    }

    private float computeFloat(String wholeDigits, String fractionDigits,
                               String exponentDigits, char exponentSign) {
        double floatValue = 0.0;
        int exponentValue = computeDecimal(exponentDigits);
        String digits = wholeDigits;

        if (exponentSign == '-') {
            exponentValue = -exponentValue;
        }

        if (fractionDigits != null) {
            exponentValue -= fractionDigits.length();
            digits += fractionDigits;
        }

        if (Math.abs(exponentValue + wholeDigits.length()) > MAX_EXPONENT) {
            kind = TokenKind.ERROR;
            value = RANGE_FLOAT;
            return 0.0f;
        }

        int index = 0;

        while (index < digits.length()) {
            floatValue = 10 * floatValue + Character.getNumericValue(digits.charAt(index++));
        }

        if (exponentValue != 0) {
            floatValue *= Math.pow(10, exponentValue);
        }

        return (float) floatValue;
    }
}
