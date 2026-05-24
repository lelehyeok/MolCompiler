/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;
import parser.Parser;
import java.util.*;
/**
 *
 * @author akmh1
 */
public class Main {
    public static void main(String[] args) {
        // Prueba con una cadena correcta
        String testInput = "massH20";
        
        System.out.println("Analizando la entrada: " + testInput);
        System.out.println("--------------------------------------------------");
        
        Lexer lexer = new Lexer(testInput);
        List<Token> listaDeTokens = new ArrayList<>();
        Token token = lexer.getNextToken();
        
        while (token.getType() != TokenType.EOF) {
            System.out.println("Lexer -> Tipo: " + token.getType() + " | Lexema: '" + token.getLexeme() + "'");
            listaDeTokens.add(token);
            token = lexer.getNextToken(); 
        }
        
        System.out.println("Lexer -> Tipo: " + token.getType() + " | Lexema: '" + token.getLexeme() + "'");
        listaDeTokens.add(token);
        
        System.out.println("--------------------------------------------------");
        System.out.println("Analisis lexico exitoso. Tokens listos: " + listaDeTokens.size());
        System.out.println("--------------------------------------------------");
        
        Parser parser = new Parser(listaDeTokens);
        
        try {
            parser.parse();
            System.out.println("Analisis sintactico con exito");
        } catch (RuntimeException e) {
            System.err.println("Error " + e.getMessage());
        }
    }
}
