/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;
import parser.Parser;
import semantic.SemanticAnalyzer;
import java.util.*;
/**
 *
 * @author akmh1
 */
public class Main {
    public static void main(String[] args) {
        String testInput = "reaction _pirita = FeS2 + O2 -> Fe2O3 + SO2; balance _pirita;";
        
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
        }
        
        //Semantico
        SemanticAnalyzer semantico = new SemanticAnalyzer();
        
        // Le pasamos la raíz del árbol (AST) que construyó el Parser
        semantico.analizar(parser.getRaiz());
        
        if (!semantico.getErroresSemanticos().isEmpty()) {
            System.err.println("--- ERRORES SEMANTICOS ENCONTRADOS ---");
            for (String err : semantico.getErroresSemanticos()) {
                System.err.println(err);
            }
            System.err.println("Analisis finalizado con errores semanticos.");
        } else {
            System.out.println("Analisis completado con exito. La estructura y la quimica son correctas.");
        }
        
    }
}