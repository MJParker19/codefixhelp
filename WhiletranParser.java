package csci366.lmc.whiletran;

import csci366.lmc.emulator.LittleManComputer;
import csci366.lmc.emulator.Utils;
import csci366.lmc.whiletran.tree.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;

public class WhiletranParser {

    LinkedList<String> tokens;
    LinkedList<String> variables = new LinkedList<>();

    public WhiletranProgram parse(String src) {
        src = src.replaceAll(Matcher.quoteReplacement("![^\n]*"), ""); // remove comments
        tokens = Utils.tokenize(src);
        WhiletranProgram program = new WhiletranProgram(new LinkedList<>());
        while (moreTokens()) {
            program.children().add(parseStatement());
        }
        return program;
    }

    public WhiletranExpression parseAsExpression(String src) {
        tokens = Utils.tokenize(src);
        WhiletranExpression expression = parseExpression();
        if (tokens.size() > 0) {
            throw new IllegalArgumentException("Unconsumed tokens: " + tokens);
        }
        return expression;
    }

    private WhiletranStatement parseStatement() {
        WhiletranStatement write = parseWriteStatement();
        if (write != null) {
            return write;
        }

        WhiletranStatement doLoop = parseDoLoop();
        if (doLoop != null) {
            return doLoop;
        }

        WhiletranStatement ifStatement = parseIfStatement();
        if (ifStatement != null) {
            return ifStatement;
        }

        WhiletranStatement assignment = parseAssignment();
        if (assignment != null) {
            return assignment;
        }

        return error("Unknown Token: " + tokens.poll());
    }

    private WhiletranStatement parseWriteStatement() {
        if (matchString("WRITE")) {
            String token = takeToken();
            WhiletranExpression expression  = parseExpression();
            return new WriteStatement(expression);
        }
        return null;
    }

    private WhiletranStatement parseDoLoop() {
        if (matchString("DO")) {
            takeToken();
            LinkedList<WhiletranStatement> body = new LinkedList<>();
            while (!matchString("WHILE")) {
                body.add(parseStatement());
            }
            requireString("WHILE");
            WhiletranExpression condition = parseExpression();
            requireString("ENDDO");
            return new DoWhileLoopStatement(body, condition);
        }
        return null;
    }

    private WhiletranStatement parseIfStatement() {
        if (matchString("IF")) {
            takeToken();
            WhiletranExpression condition = parseExpression();
            requireString("THEN");
            WhiletranProgram thenBody = new WhiletranProgram(new LinkedList<>());
            while (!matchString("ENDIF") && !matchString("ELSE")) {
                thenBody.children().add(parseStatement());
            }

            WhiletranProgram elseBody = null;
            if (matchString("ELSE")) {
                takeToken();
                elseBody = new WhiletranProgram(new LinkedList<>());
                while (!matchString("ENDIF")) {
                    elseBody.children().add(parseStatement());
                }
            }

            requireString("ENDIF");
            return new IfStatement(condition, thenBody.children(), elseBody == null ? null : elseBody.children());
        }
        return null;
    }

    private WhiletranStatement parseAssignment() {
        if (matchIdentifier()) {
            String var = takeToken();
            requireString("=");
            WhiletranExpression rhs = parseExpression();
            variables.add(var);
            return new AssignmentStatement(var, rhs);
        }
        return null;
    }
    
    private WhiletranExpression parseExpression() {
        ReadExpression readExpression = parseReadExpression();
        if (readExpression != null) {
            return readExpression;
        } else {
            return parseAdditiveOrConditionalExpression();
        }
    }

    private WhiletranExpression parseAdditiveOrConditionalExpression() {
        WhiletranExpression expression = parsePrimaryExpression();
        if (matchString("+") || matchString("-")) {
            String op = takeToken();
            WhiletranExpression rhs = parsePrimaryExpression();
            expression = new AdditiveExpression(op, expression, rhs);
        } else if (matchString(">=") || matchString("==")) {
            String op = takeToken();
            WhiletranExpression rhs = parsePrimaryExpression();
            expression = new ConditionalExpression(op, expression, rhs);
        }
        return expression;
    }


    private ReadExpression parseReadExpression() {
        if (matchString("READ")) {
            String token = takeToken();
            return new ReadExpression();
        }
        return null;
    }

    private WhiletranExpression parsePrimaryExpression() {
        if (matchIdentifier()) {
            String identifier = takeToken();
            if (identifier.equals("TRUE")) {
                return new BooleanExpression(true);
            } else if (identifier.equals("FALSE")) {
                return new BooleanExpression(false);
            } else {
                variables.add(identifier);
                return new VariableExpression(identifier);
            }
        } else {
            String token = tokens.poll();
            if (token == null || !token.matches("-?[0-9]*")) {
                error("Expected variable or number");
            }
            int num = Integer.parseInt(token);
            if(num < LittleManComputer.MIN_VALUE || LittleManComputer.MAX_VALUE < num) {
                error("Number out of range: " + num);
            }
            return new NumberExpression(num);
        }
    }


    private void requireString(String expected) {
        if (!matchString(expected)) {
            error("Expected '" + expected + "' but got '" + tokens.peek() + "'");
        } else {
            tokens.pop();
        }
    }

    private boolean matchString(String string) {
        String peek = tokens.peek();
        return Objects.equals(peek, string);
    }

    private String takeToken() {
        return tokens.poll();
    }

    private boolean matchIdentifier() {
        String peek = tokens.peek();
        return peek != null && peek.matches("[A-Z][A-Z0-9]*");
    }

    private boolean moreTokens() {
        return !tokens.isEmpty();
    }


    private <T> T error(String err) {
        throw new WhiletranParseException(err);
    }

}
