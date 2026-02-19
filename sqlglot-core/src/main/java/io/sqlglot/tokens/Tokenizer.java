package io.sqlglot.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base SQL tokenizer with dialect-overridable methods.
 * Supports keyword recognition, string literals, numbers, identifiers, and comments.
 */
public class Tokenizer {
    private final Trie keywordTrie;
    protected final Map<String, Character> stringQuotes;
    protected final Map<String, Character> identifierQuotes;
    protected final String commentStart;
    protected final String commentMultiStart;
    protected final String commentMultiEnd;

    /**
     * Creates a tokenizer with standard SQL keywords and dialect-specific settings.
     */
    public Tokenizer() {
        this.keywordTrie = buildKeywordTrie();
        this.stringQuotes = getStringQuotes();
        this.identifierQuotes = getIdentifierQuotes();
        this.commentStart = "--";
        this.commentMultiStart = "/*";
        this.commentMultiEnd = "*/";
    }

    /**
     * Builds the keyword trie. Override in subclasses to add dialect-specific keywords.
     */
    protected Trie buildKeywordTrie() {
        Map<String, TokenType> keywords = new HashMap<>();

        // Keywords - DML/DDL
        for (TokenType type : TokenType.values()) {
            if (type.isKeyword() && type.getText() != null) {
                keywords.put(type.getText(), type);
            }
        }

        return Trie.build(keywords);
    }

    /**
     * Returns dialect-specific string quotes. Override in subclasses.
     * Map key is the quote character, value is the escape character.
     */
    protected Map<String, Character> getStringQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("'", '\'');  // Standard SQL single quote, escaped by doubling
        quotes.put("\"", '\\');  // Double quote, escaped by backslash
        return quotes;
    }

    /**
     * Returns dialect-specific identifier quotes. Override in subclasses.
     */
    protected Map<String, Character> getIdentifierQuotes() {
        Map<String, Character> quotes = new HashMap<>();
        quotes.put("`", '`');   // MySQL backtick
        quotes.put("[", ']');   // SQL Server brackets
        quotes.put("\"", '\\'); // Standard SQL double quote
        return quotes;
    }

    /**
     * Tokenizes the input SQL string into a list of tokens.
     */
    public List<Token> tokenize(String sql) {
        if (sql == null) {
            sql = "";
        }

        TokenizerState state = new TokenizerState(sql);
        tokenizeInternal(state);
        state.emitToken(TokenType.EOF, "", state.getPos(), state.getLine(), state.getCol());
        return state.getTokens();
    }

    /**
     * Internal tokenization loop.
     */
    private void tokenizeInternal(TokenizerState state) {
        while (!state.isAtEnd()) {
            int startPos = state.getPos();
            int startLine = state.getLine();
            int startCol = state.getCol();
            char current = state.currentChar();

            // Whitespace
            if (Character.isWhitespace(current)) {
                state.advance();
                continue;
            }

            // Comments
            if (current == '-' && state.peek(1) == '-') {
                consumeLineComment(state);
                continue;
            }

            if (current == '/' && state.peek(1) == '*') {
                consumeBlockComment(state);
                continue;
            }

            // String literals
            if (stringQuotes.containsKey(String.valueOf(current))) {
                String text = consumeString(state, String.valueOf(current));
                state.emitToken(TokenType.STRING, text, startPos, startLine, startCol);
                continue;
            }

            // Identifier quotes
            if (identifierQuotes.containsKey(String.valueOf(current))) {
                String text = consumeQuotedIdentifier(state, String.valueOf(current));
                state.emitToken(TokenType.IDENTIFIER, text, startPos, startLine, startCol);
                continue;
            }

            // Numbers
            if (Character.isDigit(current)) {
                String text = consumeNumber(state);
                state.emitToken(TokenType.NUMBER, text, startPos, startLine, startCol);
                continue;
            }

            // Identifiers and keywords
            if (Character.isLetter(current) || current == '_') {
                String text = consumeIdentifierOrKeyword(state);
                TokenType type = keywordTrie.lookup(text);
                if (type == null) {
                    type = TokenType.IDENTIFIER;
                }
                state.emitToken(type, text, startPos, startLine, startCol);
                continue;
            }

            // Operators and punctuation
            TokenType opType = matchOperatorOrPunctuation(state);
            if (opType != null) {
                String opText = getOperatorText(state, opType);
                state.emitToken(opType, opText, startPos, startLine, startCol);
                state.advance(opText.length());
                continue;
            }

            // Unknown character
            state.emitToken(TokenType.UNKNOWN, String.valueOf(current), startPos, startLine, startCol);
            state.advance();
        }
    }

    private void consumeLineComment(TokenizerState state) {
        int startPos = state.getPos();
        while (!state.isAtEnd() && state.currentChar() != '\n') {
            state.advance();
        }
        String comment = state.substring(startPos, state.getPos());
        state.emitComment(comment);
    }

    private void consumeBlockComment(TokenizerState state) {
        int startPos = state.getPos();
        state.advance(); // /
        state.advance(); // *

        while (!state.isAtEnd()) {
            if (state.currentChar() == '*' && state.peek(1) == '/') {
                state.advance(); // *
                state.advance(); // /
                break;
            }
            state.advance();
        }

        String comment = state.substring(startPos, state.getPos());
        state.emitComment(comment);
    }

    private String consumeString(TokenizerState state, String quote) {
        StringBuilder sb = new StringBuilder();
        char quoteChar = quote.charAt(0);
        state.advance(); // consume opening quote

        while (!state.isAtEnd() && state.currentChar() != quoteChar) {
            if (state.currentChar() == '\\') {
                state.advance();
                if (!state.isAtEnd()) {
                    sb.append(state.currentChar());
                    state.advance();
                }
            } else if (state.currentChar() == quoteChar && state.peek(1) == quoteChar) {
                // Handle doubled quotes ('' for escaping)
                sb.append(quoteChar);
                state.advance();
                state.advance();
            } else {
                sb.append(state.currentChar());
                state.advance();
            }
        }

        if (!state.isAtEnd() && state.currentChar() == quoteChar) {
            state.advance(); // consume closing quote
        }

        return sb.toString();
    }

    private String consumeQuotedIdentifier(TokenizerState state, String quote) {
        char quoteChar = quote.charAt(0);
        char closeChar = identifierQuotes.get(quote);
        state.advance(); // consume opening quote

        StringBuilder sb = new StringBuilder();
        while (!state.isAtEnd() && state.currentChar() != closeChar) {
            if (state.currentChar() == closeChar && state.peek(1) == closeChar) {
                // Handle doubled quotes for escaping
                sb.append(closeChar);
                state.advance();
                state.advance();
            } else {
                sb.append(state.currentChar());
                state.advance();
            }
        }

        if (!state.isAtEnd() && state.currentChar() == closeChar) {
            state.advance(); // consume closing quote
        }

        return sb.toString();
    }

    private String consumeNumber(TokenizerState state) {
        StringBuilder sb = new StringBuilder();

        while (!state.isAtEnd() && (Character.isDigit(state.currentChar()) || state.currentChar() == '.')) {
            sb.append(state.currentChar());
            state.advance();
        }

        // Handle scientific notation
        if (!state.isAtEnd() && (state.currentChar() == 'e' || state.currentChar() == 'E')) {
            sb.append(state.currentChar());
            state.advance();

            if (!state.isAtEnd() && (state.currentChar() == '+' || state.currentChar() == '-')) {
                sb.append(state.currentChar());
                state.advance();
            }

            while (!state.isAtEnd() && Character.isDigit(state.currentChar())) {
                sb.append(state.currentChar());
                state.advance();
            }
        }

        return sb.toString();
    }

    private String consumeIdentifierOrKeyword(TokenizerState state) {
        StringBuilder sb = new StringBuilder();

        while (!state.isAtEnd() && (Character.isLetterOrDigit(state.currentChar()) || state.currentChar() == '_')) {
            sb.append(state.currentChar());
            state.advance();
        }

        return sb.toString();
    }

    private String getOperatorText(TokenizerState state, TokenType type) {
        return switch (type) {
            case SPACESHIP, LTE, GTE, NEQ, NEQ2, ARROW, DARROW, LDARROW, DOUBLE_COLON, LSHIFT, RSHIFT, DPIPE
                    -> state.substring(state.getPos(), Math.min(state.getPos() + 2, state.getInput().length()));
            default -> String.valueOf(state.currentChar());
        };
    }

    private TokenType matchOperatorOrPunctuation(TokenizerState state) {
        char current = state.currentChar();
        char next = state.peek(1);
        char next2 = state.peek(2);

        // Three-character operators
        String three = "" + current + next + next2;
        if (three.equals("<=>")) {
            return TokenType.SPACESHIP;
        }

        // Two-character operators
        String two = "" + current + next;
        switch (two) {
            case "<=":
                return TokenType.LTE;
            case ">=":
                return TokenType.GTE;
            case "<>":
                return TokenType.NEQ;
            case "!=":
                return TokenType.NEQ2;
            case "=>":
                return TokenType.ARROW;
            case "->":
                return TokenType.DARROW;
            case "<-":
                return TokenType.LDARROW;
            case "::":
                return TokenType.DOUBLE_COLON;
            case "<<":
                return TokenType.LSHIFT;
            case ">>":
                return TokenType.RSHIFT;
            case "||":
                return TokenType.DPIPE;
        }

        // Single-character operators and punctuation
        return switch (current) {
            case '(' -> TokenType.L_PAREN;
            case ')' -> TokenType.R_PAREN;
            case '[' -> TokenType.L_BRACKET;
            case ']' -> TokenType.R_BRACKET;
            case '{' -> TokenType.L_BRACE;
            case '}' -> TokenType.R_BRACE;
            case ',' -> TokenType.COMMA;
            case '.' -> TokenType.DOT;
            case ';' -> TokenType.SEMICOLON;
            case '*' -> TokenType.STAR;
            case '?' -> TokenType.PLACEHOLDER;
            case '@' -> TokenType.AT;
            case ':' -> TokenType.COLON;
            case '#' -> TokenType.HASH;
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '/' -> TokenType.SLASH;
            case '%' -> TokenType.PERCENT;
            case '&' -> TokenType.AMPERSAND;
            case '|' -> TokenType.PIPE;
            case '^' -> TokenType.CARET;
            case '~' -> TokenType.TILDA;
            case '=' -> TokenType.EQ;
            case '<' -> TokenType.LT;
            case '>' -> TokenType.GT;
            case '`' -> TokenType.TICK;
            default -> null;
        };
    }
}
