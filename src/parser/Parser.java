/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;
import lexer.Token;
import lexer.TokenType;
import java.util.List;
/**
 *
 * @author akmh1
 */
public class Parser {
    private List<Token> tokens;
    private int actual = 0;
    
    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }
    
    private Token verActual(){
        if(actual >= tokens.size()){
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(actual);
    }
    
    private void validar(TokenType tokenEsperado){
        if(verActual().getType() == tokenEsperado){
            actual++;
        }else{
            throw new RuntimeException("Error de sintaxis: Se esperaba el token '"+tokenEsperado+
                                       "' pero se encontro '"+ verActual().getType() +
                                       "' con el lexema '"+ verActual().getLexeme()+ "'");
        }
    }
    
    //GIC
    //Iniciar analisis sintactico
    public void parse(){
        analizarS();
    }
    
    //S -> D id= E | Q id | Q L | id = L
    private void analizarS(){
        TokenType tipoActual = verActual().getType();
        
        //D id  = E
        if (tipoActual == TokenType.REACTION || tipoActual == TokenType.BALANCE || tipoActual == TokenType.COMPARE) {
            analizarD();
            validar(TokenType.IDENTIFIER);
            validar(TokenType.ASSIGN);
            analizarE(); 
        }
        
        //Q id | Q L
        else if (tipoActual == TokenType.MASS || tipoActual == TokenType.VALIDATE) {
            analizarQ();
            // Si el siguiente token es un identificador, va a buscar en memoria
            if (verActual().getType() == TokenType.IDENTIFIER) {
                validar(TokenType.IDENTIFIER);
            } else {
                // Si no, analiza una molécula o compuesto directo "al vuelo"
                analizarL(); 
            }
        }
        
        //id = L
        else if (tipoActual == TokenType.IDENTIFIER) {
            validar(TokenType.IDENTIFIER);
            validar(TokenType.ASSIGN);
            analizarL(); 
        } 
        else {
            throw new RuntimeException("Error de Sintaxis: Estructura de instrucción no válida al inicio.");
        }
    }//analizarS
    
    private void analizarD(){
        //D -> reaction | balance | compare
        TokenType tipoActual = verActual().getType();
        if (tipoActual == TokenType.REACTION) {
            validar(TokenType.REACTION);
        } else if (tipoActual == TokenType.BALANCE) {
            validar(TokenType.BALANCE);
        } else if (tipoActual == TokenType.COMPARE) {
            validar(TokenType.COMPARE);
        } else {
            throw new RuntimeException("Error de sintaxis: Se esperaba un comando de declaración (reaction, balance o compare).");
        }
    }//analizar D
    
    // Q -> mass | validate
    private void analizarQ(){
        TokenType tipoActual = verActual().getType();
        if (tipoActual == TokenType.MASS) {
            validar(TokenType.MASS);
        } else if (tipoActual == TokenType.VALIDATE) {
            validar(TokenType.VALIDATE);
        } else {
            throw new RuntimeException("Error de sintaxis: Se esperaba un comando de consulta (mass o validate).");
        }
    }//analizar Q
    
    // E -> L -> L
    private void analizarE(){
        analizarL();
        validar(TokenType.ARROW);
        analizarL();
    }//analizar E
    
    //L -> M | M+L
    private void analizarL(){
        //Primera opcion
        analizarM();
        
        //If recursivo
        if (verActual().getType() == TokenType.PLUS) {
            validar(TokenType.PLUS);
            analizarL();
        }
        
    }//analizar L
    
    //M -> coefP | P
    private void analizarM(){
        //Si empieza con un numero es coeficiente
        if (verActual().getType() == TokenType.COEFFICIENT) {
            validar(TokenType.COEFFICIENT);
            analizarP();
        } else {
            //Segundo caso
            analizarP();
        }
    }//analizar M
    
    //P -> G | GP
    private void analizarP(){
        analizarG(); //Empieza analizando G si o si 
        TokenType tipoSiguiente = verActual().getType();
        // Si hay otro elemento o parentesis, recursaividad
        if (tipoSiguiente == TokenType.ELEMENT || tipoSiguiente == TokenType.LPAREN) {
            analizarP();
        }
    }//analizar P
    
    //G -> elemU | (P)U
    private void analizarG(){
        TokenType tipoActual = verActual().getType();
        
        //Inicia con elemento
        if (tipoActual == TokenType.ELEMENT) {
            validar(TokenType.ELEMENT);
            analizarU();                
        } 
        //Inicia con parentesis
        else if (tipoActual == TokenType.LPAREN) {
            validar(TokenType.LPAREN);  //Abrir parentesis
            analizarP();                //Analizar lo que hay dentro
            validar(TokenType.RPAREN);  //Cerrar parentesis
            analizarU();                //Revisamos subindice
        } 
        // Camino de error
        else {
            throw new RuntimeException("Error de Sintaxis: Se esperaba un elemento o abrir un paréntesis '(', pero se encontro '" + verActual().getLexeme() + "'");
        }
    }//analizar G
    
    //U -> sub | cadena vacia
    private void analizarU(){
        //revisa el subindice
        if (verActual().getType() == TokenType.SUBSCRIPT) {
            validar(TokenType.SUBSCRIPT); 
        }
    }//analzar U
    
}
