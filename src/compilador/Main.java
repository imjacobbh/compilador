/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("main", "keyword"));
        tokens.add(new Token("{", "symbol"));
        tokens.add(new Token("int", "keyword"));
        tokens.add(new Token("x", "identifier"));
        tokens.add(new Token(";", "symbol"));
        tokens.add(new Token("a", "identifier"));
        tokens.add(new Token("=", "operator"));
        tokens.add(new Token("2", "integer"));
        tokens.add(new Token(";", "symbol"));
        tokens.add(new Token("}", "symbol"));
       

        SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
        analyzer.parse();
       
    }
}
