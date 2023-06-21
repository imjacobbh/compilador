package compilador;
import java.util.ArrayList;
import java.util.List;

class Token {
    private String lexeme;
    private String type;

    public Token(String lexeme, String type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getType() {
        return type;
    }
}

class SyntaxAnalyzer {
    private List<Token> tokens;
    private int currentTokenIndex;
    private int indentLevel;

    public SyntaxAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.indentLevel = 0;
    }

    public void parse() {
        program();
        if (currentTokenIndex != tokens.size()) {
            throw new RuntimeException("Syntax error: Unexpected token");
        }
        System.out.println("Syntax analysis completed successfully.");
    }

    private void program() {
        printIndent();
        System.out.println("Program");
        indentLevel++;
        match("main");
        match("{");
        declarations();
        statements();
        match("}");
        indentLevel--;
    }

    private void declarations() {
        if (check("int") || check("float")) {
            printIndent();
            System.out.println("Declarations");
            indentLevel++;
            declaration();
            declarations();
            indentLevel--;
        }
    }

    private void declaration() {
        printIndent();
        System.out.println("Declaration");
        indentLevel++;
        type();
        idList();
        match(";");
        indentLevel--;
    }

    private void type() {
        if (check("int")) {
            match("int");
        } else if (check("float")) {
            match("float");
        } else {
            throw new RuntimeException("Syntax error: Expected int or float");
        }
    }

    private void idList() {
        printIndent();
        System.out.println("IdList");
        indentLevel++;
        matchIdentifier();
        if (check(",")) {
            match(",");
            idList();
        }
        indentLevel--;
    }

    private void statements() {
        printIndent();
        System.out.println("Statements");
        indentLevel++;
        matchIdentifier();
        assignment();
        match(";");
        while (checkIdentifier()) {
            assignment();
            match(";");
        }
        if (check("while")) {
            match("while");
            match("(");
            expression();
            match(")");
            match("{");
            statements();
            match("}");
        }
        indentLevel--;
    }

    private void assignment() {
        printIndent();
        System.out.println("Assignment");
        indentLevel++;
        match("=");
        expression();
        indentLevel--;
    }

    private void expression() {
        printIndent();
        System.out.println("Expression");
        indentLevel++;
        simpleExpr();
        if (checkRelationalOperator()) {
            matchRelationalOperator();
            simpleExpr();
        }
        indentLevel--;
    }

    private void simpleExpr() {
        printIndent();
        System.out.println("SimpleExpr");
        indentLevel++;
        term();
        while (checkAdditiveOperator()) {
            matchAdditiveOperator();
            term();
        }
        indentLevel--;
    }

    private void term() {
        printIndent();
        System.out.println("Term");
        indentLevel++;
        factor();
        while (checkMultiplicativeOperator()) {
            matchMultiplicativeOperator();
            factor();
        }
        indentLevel--;
    }

    private void factor() {
        printIndent();
        System.out.println("Factor");
        indentLevel++;
        if (checkIdentifier()) {
            matchIdentifier();
        } else if (checkNumber()) {
            matchNumber();
        } else if (check("(")) {
            match("(");
            expression();
            match(")");
        } else {
            throw new RuntimeException("Syntax error: Unexpected factor");
        }
        indentLevel--;
    }

    private boolean check(String expectedToken) {
        if (currentTokenIndex < tokens.size()) {
            Token currentToken = tokens.get(currentTokenIndex);
            return currentToken.getLexeme().equals(expectedToken);
        }
        return false;
    }

    private boolean checkIdentifier() {
        return checkType("identifier");
    }

    private boolean checkNumber() {
        return checkType("integer") || checkType("float");
    }

    private boolean checkType(String expectedType) {
        if (currentTokenIndex < tokens.size()) {
            Token currentToken = tokens.get(currentTokenIndex);
            return currentToken.getType().equals(expectedType);
        }
        return false;
    }

    private boolean checkAdditiveOperator() {
        return check("+") || check("-");
    }

    private boolean checkMultiplicativeOperator() {
        return check("*") || check("/");
    }

    private boolean checkRelationalOperator() {
        return check("<=") || check("==") || check("!=") || check(">") || check(">=");
    }

    private void match(String expectedToken) {
        if (check(expectedToken)) {
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected " + expectedToken);
        }
    }

    private void matchIdentifier() {
        if (checkIdentifier()) {
            printIndent();
            System.out.println("Identifier: " + tokens.get(currentTokenIndex).getLexeme());
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected identifier");
        }
    }

    private void matchNumber() {
        if (checkNumber()) {
            printIndent();
            System.out.println("Number: " + tokens.get(currentTokenIndex).getLexeme());
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected number");
        }
    }

    private void matchAdditiveOperator() {
        if (checkAdditiveOperator()) {
            printIndent();
            System.out.println("Additive Operator: " + tokens.get(currentTokenIndex).getLexeme());
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected + or -");
        }
    }

    private void matchMultiplicativeOperator() {
        if (checkMultiplicativeOperator()) {
            printIndent();
            System.out.println("Multiplicative Operator: " + tokens.get(currentTokenIndex).getLexeme());
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected * or /");
        }
    }

    private void matchRelationalOperator() {
        if (checkRelationalOperator()) {
            printIndent();
            System.out.println("Relational Operator: " + tokens.get(currentTokenIndex).getLexeme());
            currentTokenIndex++;
        } else {
            throw new RuntimeException("Syntax error: Expected <=, ==, !=, >, or >=");
        }
    }

    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }
    }
}