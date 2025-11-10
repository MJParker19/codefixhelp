package csci366.lmc.whiletran;

import csci366.lmc.whiletran.tree.WhiletranProgram;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhiletranCodeGeneratorTest {

    @Test
    public void testWriteInteger() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("WRITE 1");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                OUT
                HLT
                N_1 DAT 1
                """, asm);
    }

    @Test
    public void testWriteBoolean() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("WRITE TRUE");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                OUT
                HLT
                N_1 DAT 1
                """, asm);
    }

    @Test
    public void testAssignment() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("X = 10");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_10
                STA X
                HLT
                N_10 DAT 10
                X DAT 0
                """, asm);
    }

    @Test
    public void testWriteVariable() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("X = 11 WRITE X");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_11
                STA X
                LDA X
                OUT
                HLT
                N_11 DAT 11
                X DAT 0
                """, asm);
    }

    @Test
    public void testIfStatementNoElse() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("IF TRUE WRITE 1 END");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                BRZ LABEL_1
                LDA N_1
                OUT
                BRA LABEL_0
                LABEL_1 ADD N_0
                LABEL_0 ADD N_0
                HLT
                N_1 DAT 1
                N_0 DAT 0
                """, asm);
    }

    @Test
    public void testIfStatementWithElse() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("IF TRUE WRITE 1 ELSE WRITE 0 END");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                BRZ LABEL_1
                LDA N_1
                OUT
                BRA LABEL_0
                LABEL_1 ADD N_0
                LDA N_0
                OUT
                LABEL_0 ADD N_0
                HLT
                N_1 DAT 1
                N_0 DAT 0
                """, asm);
    }

    @Test
    public void testEqualityExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("WRITE 1 == 0");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                SUB N_0
                BRZ LABEL_0
                LDA N_0
                BRA LABEL_1
                LABEL_0 LDA N_1
                LABEL_1 ADD N_0
                OUT
                HLT
                N_1 DAT 1
                N_0 DAT 0
                """, asm);
    }

    @Test
    public void testGreaterThanExpression() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("WRITE 1 >= 0");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LDA N_1
                SUB N_0
                BRP LABEL_0
                LDA N_0
                BRA LABEL_1
                LABEL_0 LDA N_1
                LABEL_1 ADD N_0
                OUT
                HLT
                N_1 DAT 1
                N_0 DAT 0
                """, asm);
    }    @Test

    public void testDoWhileLoop() {
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse("DO WHILE TRUE WRITE 1 END");
        WhiletranCodeGenerator codeGen = new WhiletranCodeGenerator();
        String asm = codeGen.generateCode(program);
        assertEquals("""
                LABEL_0 LDA N_1
                BRZ LABEL_1
                LDA N_1
                OUT
                BRA LABEL_0
                LABEL_1 ADD N_0
                HLT
                N_1 DAT 1
                N_0 DAT 0
                """, asm);
    }
}