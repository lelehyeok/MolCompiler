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
        // Cadena a propósito con errores léxicos (@) y sintácticos (falta flecha)
        String testInput = "reaction _agua =  H2 + O2  2H2O; mass _agua;";
        
        System.out.println("Analizando: " + testInput);
        
        Lexer lexer = new Lexer(testInput);
        List<Token> listaDeTokens = new ArrayList<>();
        Token token = lexer.getNextToken();
        
        while (token.getType() != TokenType.EOF) {
            listaDeTokens.add(token);
            token = lexer.getNextToken(); 
        }
        listaDeTokens.add(token); // EOF
    // 1. Checar errores de arrastre
        if (!lexer.getErroresLexicos().isEmpty()) {
            System.err.println("--- ERRORES LEXICOS ENCONTRADOS ---");
            for (String err : lexer.getErroresLexicos()) {
                System.err.println(err);
            }
            System.err.println("Analisis abortado: No se puede iniciar el analisis sintactico debido a errores lexicos previos.");
            return; 
        }
        
        // 2. Arrancamos el Parser.
        Parser parser = new Parser(listaDeTokens);
        parser.parse(); 
        
        // 3. Modo Pánico
        if (!parser.getErroresSintacticos().isEmpty()) {
            System.err.println("--- ERRORES SINTACTICOS ENCONTRADOS ---");
            for (String err : parser.getErroresSintacticos()) {
                System.err.println(err);
            }
            System.out.println("Analisis sintactico finalizado con errores.");
        } else {
            System.out.println("Analisis completado con exito. Todo el codigo es correcto.");
        }
    }
}