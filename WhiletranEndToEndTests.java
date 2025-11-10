package csci366.lmc.whiletran;

import csci366.lmc.asm.LittleManAssembler;
import csci366.lmc.emulator.LittleManComputer;
import csci366.lmc.whiletran.tree.WhiletranProgram;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhiletranEndToEndTests {

    @Test
    public void testEndToEndWrite1() {
        var output = compileAndRun("""
                WRITE 1
                WRITE 100
                WRITE -100
                """);
        assertEquals(1, output.poll());
        assertEquals(100, output.poll());
        assertEquals(-100, output.poll());
    }

    @Test
    public void testEndToEndVariable() {
        var output = compileAndRun("""
                X = 10
                WRITE X
                """);
        assertEquals(10, output.poll());
    }

    @Test
    public void testEndToEndBoolean() {
        var output = compileAndRun("""
                WRITE TRUE
                WRITE FALSE
                """);
        assertEquals(1, output.poll());
        assertEquals(0, output.poll());
    }

    @Test
    public void testEndToEndIfTrue() {
        var output = compileAndRun("""
                IF TRUE
                  WRITE 22
                ELSE
                  WRITE 21
                END
                """);
        assertEquals(22, output.poll());
    }

    @Test
    public void testEndToEndIfFalse() {
        var output = compileAndRun("""
                IF FALSE
                  WRITE 22
                ELSE
                  WRITE 21
                END
                """);
        assertEquals(21, output.poll());
    }

    @Test
    public void testEndToEndComparisonTrue() {
        var output = compileAndRun("""
                WRITE 1 >= 0
                """);
        assertEquals(1, output.poll());
    }

    @Test
    public void testEndToEndComparisonFalse() {
        var output = compileAndRun("""
                WRITE 0 >= 1
                """);
        assertEquals(0, output.poll());
    }


    @Test
    public void testEndToEndEqualityTrue() {
        var output = compileAndRun("""
                WRITE 1 == 1
                """);
        assertEquals(1, output.poll());
    }

    @Test
    public void testEndToEndEqualityFalse() {
        var output = compileAndRun("""
                WRITE 0 == 1
                """);
        assertEquals(0, output.poll());
    }

    @Test
    public void testEndToEndInvert1to0() {
        var output = compileAndRun("""
                N = READ
                IF N == 0
                    WRITE 1
                ELSE
                    WRITE 0
                END""", 1);
        assertEquals(0, output.poll());
    }

    @Test
    public void testEndToEndInvert0to1() {
        var output = compileAndRun("""
                N = READ
                IF N == 0
                    WRITE 1
                ELSE
                    WRITE 0
                END""", 0);
        assertEquals(1, output.poll());
    }

    @Test
    public void testEndToEndMax() {
        var output = compileAndRun("""
                A = READ
                B = READ
                IF A >= B
                    WRITE A
                ELSE
                    WRITE B
                END""", 10, 20);
        assertEquals(20, output.poll());
    }

    @Test
    public void testEndToEndDivision() {
        var output = compileAndRun("""
                A = READ
                B = READ
                DIV = -1
                DO WHILE A >= 0
                    DIV = DIV + 1
                    A = A - B
                END
                WRITE DIV""", 50, 10);
        assertEquals(5, output.poll());
    }

    @Test
    public void testEndToEndDivision2() {
        var output = compileAndRun("""
                A = READ
                B = READ
                DIV = -1
                DO WHILE A >= 0
                    DIV = DIV + 1
                    A = A - B
                END
                WRITE DIV""", 59, 10);
        assertEquals(5, output.poll());
    }

    @Test
    public void testEndToEndDivision3() {
        var output = compileAndRun("""
                A = READ
                B = READ
                DIV = -1
                DO WHILE A >= 0
                    DIV = DIV + 1
                    A = A - B
                END
                WRITE DIV""", 60, 10);
        assertEquals(6, output.poll());
    }

    @Test
    public void testEndToEndFib() {
        List<Integer> fibSequence = List.of(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144);
        for (int i = 0; i < fibSequence.size(); i++) {
            Integer fib = fibSequence.get(i);
            var output = compileAndRun("""
                N = READ
                F1 = 0
                F2 = 1
                DO WHILE N >= 1
                  TMP = F1 + F2
                  F2 = F1
                  F1 = TMP
                  N = N - 1
                END
                WRITE F1""", i);
            assertEquals(fib, output.poll());
        }
    }

    private static LinkedList<Integer> compileAndRun(String src, Integer... inputs) {

        // parse the program
        WhiletranParser parser = new WhiletranParser();
        WhiletranProgram program = parser.parse(src);

        // generate the assembly
        WhiletranCodeGenerator whiletranCodeGenerator = new WhiletranCodeGenerator();
        String asmSrc = whiletranCodeGenerator.generateCode(program);
        //System.out.println(asmSrc); // uncomment to print assembly

        // assemble into machine instructions
        LittleManAssembler asm = new LittleManAssembler();
        int[] instructions = asm.assemble(asmSrc);

        // load the instructions and run them
        LittleManComputer computer = new LittleManComputer();
        computer.load(instructions);
        computer.pushInputs(inputs);
        computer.run();

        LinkedList<Integer> output = computer.getOutput();
        return output;
    }
}
