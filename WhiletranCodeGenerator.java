package csci366.lmc.whiletran;

import csci366.lmc.whiletran.tree.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class WhiletranCodeGenerator {

    int labelNum = 0;
    Set<String> variables = new LinkedHashSet<>();
    Set<Integer> numbers = new LinkedHashSet<>();

    public String generateCode(WhiletranProgram program) {
        StringBuilder code = new StringBuilder();
        generateCode(program, code);
        code.append("HLT\n");
        generateData(code);
        return code.toString();
    }

    private void generateCode(WhiletranElement elt, StringBuilder code) {
        if(elt instanceof WhiletranProgram zp) {
            for (WhiletranStatement child : zp.children()) {
                generateCode(child, code);
            }
        } else if(elt instanceof WriteStatement ws) {
            generateCode(ws.expression(), code);
            code.append("OUT\n");
        } else if(elt instanceof AssignmentStatement as) {
            generateCode(as.expression(), code);
            variables.add(as.variable());
            code.append("STA ").append(as.variable()).append("\n");

        } else if(elt instanceof IfStatement is) {
            String elseLabel = nextLabel();
            String endLabel = nextLabel();
            generateCode(is.condition(), code);
            code.append("BRZ ").append(elseLabel).append("\n");
            generateCode(is.thenBody(), code);
            code.append("BRA ").append(endLabel).append("\n");
            code.append(elseLabel).append(" ").append(genNoOp()).append("\n");
            if (is.elseBody() != null) {
                generateCode(is.elseBody(), code);
            }
            code.append(endLabel).append(" ").append(genNoOp()).append("\n");

        } else if(elt instanceof DoWhileLoopStatement dl) {
            String startLabel = nextLabel();
            String endLabel = nextLabel();
            code.append(startLabel).append(" ").append(genNoOp()).append("\n");
            generateCode(dl.body(), code);
            generateCode(dl.condition(), code);
            code.append("BRP ").append(startLabel).append("\n");
            code.append(endLabel).append(" ").append(genNoOp()).append("\n");

        } else if(elt instanceof ConditionalExpression ce) {
            generateCode(ce.lhs(), code);
            code.append("SUB ").append(getLabelFor(ce.rhs())).append("\n");

        } else if(elt instanceof ReadExpression) {
            code.append("INP\n");

        } else if(elt instanceof NumberExpression ne) {
            code.append("LDA ").append(getLabelFor(ne.num())).append("\n");

        } else if(elt instanceof BooleanExpression be) {
            int val = be.value() ? 1 : 0;
            code.append("LDA ").append(getLabelFor(val)).append("\n");

        } else if(elt instanceof VariableExpression ie) {
            variables.add(ie.name());
            code.append("LDA ").append(ie.name()).append("\n");

        } else if(elt instanceof AdditiveExpression ae) {
            generateCode(ae.lhs(), code);
            if (ae.op().equals("+")) {
                code.append("ADD ").append(getLabelFor(ae.rhs())).append("\n");
            } else {
                code.append("SUB ").append(getLabelFor(ae.rhs())).append("\n");
            }
        } else {
            throw new IllegalArgumentException("Don't know how to generate code for " + elt);
        }
    }

    // adding zero is a no-op, used for labels
    private String genNoOp() {
        return " ADD " + getLabelFor(0);
    }

    private String getLabelFor(int num) {
        numbers.add(num);
        return "N_" + ((num < 0) ? "NEG_" : "") + Math.abs(num);
    }

    private String getLabelFor(WhiletranExpression rhs) {
        String label;
        if(rhs instanceof VariableExpression ie) {
            label = ie.name();
        } else if (rhs instanceof NumberExpression ne) {
            label = getLabelFor(ne.num());
        } else {
            throw new IllegalStateException("Bad element : " + rhs);
        }
        return label;
    }

    private String nextLabel() {
        return "LABEL_" + labelNum++;
    }

    private void generateData(StringBuilder code) {
        for (Integer number : numbers) {
            code.append(getLabelFor(number)).append(" DAT ").append(number).append("\n");
        }
        for (String variable : variables) {
            code.append(variable).append(" DAT 0\n");
        }
    }
}
