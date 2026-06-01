/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parser;
import lexer.Token;
import lexer.TokenType;
import java.util.*;
/**
 *
 * @author akmh1
 */
public class Parser {
    private List<Token> tokens;
    private int actual = 0;
    
    private ArrayList<String> derivacion = new ArrayList<>();
    private Nodos raiz = null;

    public ArrayList<String> getDerivacion() { 
        return derivacion; 
    }
    public Nodos getRaiz() { 
        return raiz; 
    }
    
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
            throw new RuntimeException("Error de sintaxis. Se esperaba el token '"+tokenEsperado+
                                       "' pero se encontró '"+ verActual().getType() +
                                       "' con el lexema '"+ verActual().getLexeme()+ "'");
        }
    }
    
    //GIC
    //Iniciar analisis sintactico
    public void parse(){
        raiz = analizarS();
    }
    
    //S -> D id= E | Q id | Q L | id = L
    private Nodos analizarS() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("S");

        if (tipoActual == TokenType.REACTION || tipoActual == TokenType.BALANCE || tipoActual == TokenType.COMPARE) {
            derivacion.add("S → D id = E ;");
            nodo.agregarHijo(analizarD());
            nodo.agregarHijo(new Nodos("id(" + verActual().getLexeme() + ")"));
            validar(TokenType.IDENTIFIER);
            nodo.agregarHijo(new Nodos("="));
            validar(TokenType.ASSIGN);
            nodo.agregarHijo(analizarE());

        } else if (tipoActual == TokenType.MASS || tipoActual == TokenType.VALIDATE) {
            nodo.agregarHijo(analizarQ());
            if (verActual().getType() == TokenType.IDENTIFIER) {
                derivacion.add("S → Q id ;");
                nodo.agregarHijo(new Nodos("id(" + verActual().getLexeme() + ")"));
                validar(TokenType.IDENTIFIER);
            } else {
                derivacion.add("S → Q L ;");
                nodo.agregarHijo(analizarL());
            }

        } else if (tipoActual == TokenType.IDENTIFIER) {
            derivacion.add("S → id = L ;");
            nodo.agregarHijo(new Nodos("id(" + verActual().getLexeme() + ")"));
            validar(TokenType.IDENTIFIER);
            nodo.agregarHijo(new Nodos("="));
            validar(TokenType.ASSIGN);
            nodo.agregarHijo(analizarL());

        } else {
            throw new RuntimeException("Error de sintaxis. Estructura de instrucción no válida al inicio.");
        }

        // Final de sentencia obligatorio
        nodo.agregarHijo(new Nodos(";"));
        validar(TokenType.SEMICOLON);

        return nodo;
    }

    // D -> reaction | balance | compare
    private Nodos analizarD() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("D");
        if (tipoActual == TokenType.REACTION) {
            derivacion.add("D → reaction");
            nodo.agregarHijo(new Nodos("reaction"));
            validar(TokenType.REACTION);
        } else if (tipoActual == TokenType.BALANCE) {
            derivacion.add("D → balance");
            nodo.agregarHijo(new Nodos("balance"));
            validar(TokenType.BALANCE);
        } else if (tipoActual == TokenType.COMPARE) {
            derivacion.add("D → compare");
            nodo.agregarHijo(new Nodos("compare"));
            validar(TokenType.COMPARE);
        } else {
            throw new RuntimeException("Error de sintaxis. Se esperaba un comando de declaración (reaction, balance o compare).");
        }
        return nodo;
    }

    // Q -> mass | validate
    private Nodos analizarQ() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("Q");
        if (tipoActual == TokenType.MASS) {
            derivacion.add("Q → mass");
            nodo.agregarHijo(new Nodos("mass"));
            validar(TokenType.MASS);
        } else if (tipoActual == TokenType.VALIDATE) {
            derivacion.add("Q → validate");
            nodo.agregarHijo(new Nodos("validate"));
            validar(TokenType.VALIDATE);
        } else {
            throw new RuntimeException("Error de sintaxis. Se esperaba un comando de consulta (mass o validate).");
        }
        return nodo;
    }

    // E -> L -> L
    private Nodos analizarE() {
        Nodos nodo = new Nodos("E");
        derivacion.add("E → L → L");
        nodo.agregarHijo(analizarL());
        nodo.agregarHijo(new Nodos("→"));
        validar(TokenType.ARROW);
        nodo.agregarHijo(analizarL());
        return nodo;
    }

    // L -> M | M + L
    private Nodos analizarL() {
        Nodos nodo = new Nodos("L");
        nodo.agregarHijo(analizarM());
        if (verActual().getType() == TokenType.PLUS) {
            derivacion.add("L → M + L");
            nodo.agregarHijo(new Nodos("+"));
            validar(TokenType.PLUS);
            nodo.agregarHijo(analizarL());
        } else {
            derivacion.add("L → M");
        }
        return nodo;
    }

    // M -> coef P | P
    private Nodos analizarM() {
        Nodos nodo = new Nodos("M");
        if (verActual().getType() == TokenType.COEFFICIENT) {
            derivacion.add("M → coef P");
            nodo.agregarHijo(new Nodos("coef(" + verActual().getLexeme() + ")"));
            validar(TokenType.COEFFICIENT);
            nodo.agregarHijo(analizarP());
        } else {
            derivacion.add("M → P");
            nodo.agregarHijo(analizarP());
        }
        return nodo;
    }

    // P -> G | G P
    private Nodos analizarP() {
        Nodos nodo = new Nodos("P");
        nodo.agregarHijo(analizarG());
        TokenType tipoSiguiente = verActual().getType();
        if (tipoSiguiente == TokenType.ELEMENT || tipoSiguiente == TokenType.LPAREN) {
            derivacion.add("P → G P");
            nodo.agregarHijo(analizarP());
        } else {
            derivacion.add("P → G");
        }
        return nodo;
    }

    // G -> elem U | ( P ) U
    private Nodos analizarG() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("G");
        if (tipoActual == TokenType.ELEMENT) {
            derivacion.add("G → elem U");
            nodo.agregarHijo(new Nodos("elem(" + verActual().getLexeme() + ")"));
            validar(TokenType.ELEMENT);
            nodo.agregarHijo(analizarU());
        } else if (tipoActual == TokenType.LPAREN) {
            derivacion.add("G → ( P ) U");
            nodo.agregarHijo(new Nodos("("));
            validar(TokenType.LPAREN);
            nodo.agregarHijo(analizarP());
            nodo.agregarHijo(new Nodos(")"));
            validar(TokenType.RPAREN);
            nodo.agregarHijo(analizarU());
        } else {
            throw new RuntimeException("Error de sintaxis. Se esperaba un elemento o '(', pero se encontró '" + verActual().getLexeme() + "'");
        }
        return nodo;
    }

    // U -> sub | ε
    private Nodos analizarU() {
        Nodos nodo = new Nodos("U");
        if (verActual().getType() == TokenType.SUBSCRIPT) {
            derivacion.add("U → sub");
            nodo.agregarHijo(new Nodos("sub(" + verActual().getLexeme() + ")"));
            validar(TokenType.SUBSCRIPT);
        } else {
            derivacion.add("U → ε");
            nodo.agregarHijo(new Nodos("ε"));
        }
        return nodo;
    }
}
