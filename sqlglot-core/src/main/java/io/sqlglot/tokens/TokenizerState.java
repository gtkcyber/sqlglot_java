package io.sqlglot.tokens;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-call tokenizer state. Package-private, used only by Tokenizer.
 * Maintains mutable state during a single tokenization pass.
 */
class TokenizerState {
    private final String input;
    private final List<Token> tokens = new ArrayList<>();
    private int pos = 0;
    private int line = 1;
    private int col = 1;
    private final List<String> pendingComments = new ArrayList<>();

    TokenizerState(String input) {
        this.input = input != null ? input : "";
    }

    // Accessors
    public String getInput() {
        return input;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public List<String> getPendingComments() {
        return pendingComments;
    }

    // Character access
    public boolean isAtEnd() {
        return pos >= input.length();
    }

    public char currentChar() {
        if (isAtEnd()) {
            return '\0';
        }
        return input.charAt(pos);
    }

    public char peek(int offset) {
        int index = pos + offset;
        if (index >= input.length()) {
            return '\0';
        }
        return input.charAt(index);
    }

    public void advance() {
        if (!isAtEnd()) {
            if (input.charAt(pos) == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
            pos++;
        }
    }

    public void advance(int count) {
        for (int i = 0; i < count; i++) {
            advance();
        }
    }

    public String substring(int start, int end) {
        if (start >= end || end > input.length()) {
            return "";
        }
        return input.substring(start, end);
    }

    public String substring(int start) {
        if (start >= input.length()) {
            return "";
        }
        return input.substring(start);
    }

    // Token emission
    public void emitToken(TokenType type, String text, int startPos, int startLine, int startCol) {
        Token token = new Token(
            type,
            text,
            startLine,
            startCol,
            startPos,
            pos,
            new ArrayList<>(pendingComments)
        );
        tokens.add(token);
        pendingComments.clear();
    }

    public void emitComment(String comment) {
        pendingComments.add(comment);
    }

    public void clearPendingComments() {
        pendingComments.clear();
    }

    @Override
    public String toString() {
        return String.format("TokenizerState(pos=%d, line=%d, col=%d, tokens=%d)",
            pos, line, col, tokens.size());
    }
}
