package csci366.lmc.whiletran;

import csci366.lmc.whiletran.tree.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WhiletranParserTest {

    @Test
    public void testParseReadExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("READ");
        assertInstanceOf(ReadExpression.class, expr);
    }

    @Test
    public void testParseNumericExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("1");
        assertInstanceOf(NumberExpression.class, expr);
        NumberExpression number = (NumberExpression) expr;
        assertEquals(1, number.num());
    }

    @Test
    public void testVariableExpression() {
        WhiletranParser parser = new WhiletranParser();
        parser.variables.add("X");
        WhiletranExpression expr = parser.parseAsExpression("X");
        assertInstanceOf(VariableExpression.class, expr);
        VariableExpression variable = (VariableExpression) expr;
        assertEquals("X", variable.name());
    }

    @Test
    public void testTrueExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("TRUE");
        assertInstanceOf(BooleanExpression.class, expr);
        BooleanExpression bool = (BooleanExpression) expr;
        assertEquals(true, bool.value());
    }

    @Test
    public void testFalseExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("FALSE");
        assertInstanceOf(BooleanExpression.class, expr);
        BooleanExpression bool = (BooleanExpression) expr;
        assertEquals(false, bool.value());
    }

    @Test
    public void testParseAddExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("1 + 2");
        assertInstanceOf(AdditiveExpression.class, expr);
        AdditiveExpression add = (AdditiveExpression) expr;
        assertInstanceOf(NumberExpression.class, add.lhs());
        assertInstanceOf(NumberExpression.class, add.rhs());
    }

    @Test
    public void testParseSubExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("1 - 2");
        assertInstanceOf(AdditiveExpression.class, expr);
        AdditiveExpression add = (AdditiveExpression) expr;
        assertInstanceOf(NumberExpression.class, add.lhs());
        assertInstanceOf(NumberExpression.class, add.rhs());
    }

    @Test
    public void testParseEqualityExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("1 == 2");
        assertInstanceOf(ConditionalExpression.class, expr);
        ConditionalExpression add = (ConditionalExpression) expr;
        assertInstanceOf(NumberExpression.class, add.lhs());
        assertInstanceOf(NumberExpression.class, add.rhs());
    }

    @Test
    public void testParseComparisonExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranExpression expr = parser.parseAsExpression("1 >= 2");
        assertInstanceOf(ConditionalExpression.class, expr);
        ConditionalExpression add = (ConditionalExpression) expr;
        assertInstanceOf(NumberExpression.class, add.lhs());
        assertInstanceOf(NumberExpression.class, add.rhs());
    }

    @Test
    public void testCommentsAreIgnored() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("! comment 1\nA = 1 ! comment 2");
        assertEquals(1, program.children().size());
        AssignmentStatement assignment = (AssignmentStatement) program.children().get(0);
        assertEquals("A", assignment.name());
    }

    @Test
    public void testParseWriteStatement() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("WRITE 1");
        assertEquals(1, program.children().size());
        WriteStatement assignment = (WriteStatement) program.children().get(0);
        assertNotNull(assignment);
    }

    @Test
    public void testParseDoLoopStatement() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("DO WHILE TRUE WRITE 1 END");
        assertEquals(1, program.children().size());
        DoWhileLoopStatement loop = (DoWhileLoopStatement) program.children().get(0);
        assertNotNull(loop);
        assertEquals(1, loop.statements().size());
        assertTrue(loop.conditional() instanceof BooleanExpression);
    }

    @Test
    public void testIfStatementNoElse() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("IF TRUE WRITE 1 END");
        assertEquals(1, program.children().size());
        IfStatement ifStatement = (IfStatement) program.children().get(0);
        assertNotNull(ifStatement);
        assertEquals(1, ifStatement.trueStatements().size());
        assertEquals(0, ifStatement.falseStatements().size());
        assertTrue(ifStatement.conditional() instanceof BooleanExpression);
    }

    @Test
    public void testIfStatementWithElse() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("IF TRUE WRITE 1 ELSE WRITE 0 END");
        assertEquals(1, program.children().size());
        IfStatement ifStatement = (IfStatement) program.children().get(0);
        assertNotNull(ifStatement);
        assertEquals(1, ifStatement.trueStatements().size());
        assertEquals(1, ifStatement.falseStatements().size());
        assertInstanceOf(BooleanExpression.class, ifStatement.conditional());
    }

    @Test
    public void testParseAssignmentStatement() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("A = 1");
        assertEquals(1, program.children().size());
        AssignmentStatement assignment = (AssignmentStatement) program.children().get(0);
        assertEquals("A", assignment.name());
    }

}
