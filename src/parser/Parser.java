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
    private List<String> erroresSintacticos = new ArrayList<>();
    private ArrayList<String> derivacion = new ArrayList<>();
    private Nodos raiz = null;    
    private List<Nodos> arboles = new ArrayList<>();
    private List<List<String>> todasDerivaciones = new ArrayList<>();
    private int numDerivaciones = 0; 

    public List<Nodos> getArboles() {
        return arboles;
    }

    public ArrayList<String> getDerivacion() {
        ArrayList<String> todas = new ArrayList<>();
        for (List<String> d : todasDerivaciones) {
            todas.addAll(d);
            todas.add(""); // línea en blanco separadora
        }
        return todas;
    }
        
    public List<String> getErroresSintacticos() {
        return erroresSintacticos;
    }
    public Nodos getRaiz() { 
        return raiz; 
    }
    
    private static class ParseException extends RuntimeException {}
    
    public Parser(List<Token> tokens){
        this.tokens = tokens;
    }
    
    private Token verActual(){
        if(actual >= tokens.size()){
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(actual);
    }
    
    private String nombreAmigable(TokenType tipo) {
        switch (tipo) {
            case ASSIGN: return "el signo de igual '='";
            case ARROW: return "la flecha '->' para separar reactivos y productos";
            case PLUS: return "el signo de suma '+'";
            case SEMICOLON: return "el punto y coma ';' al final de la instrucción";
            case IDENTIFIER: return "el nombre de una variable (ej: _mezcla)";
            case ELEMENT: return "un elemento químico con mayúscula (ej: H, Na)";
            case LPAREN: return "abrir un paréntesis '('";
            case RPAREN: return "cerrar el paréntesis ')'";
            case COEFFICIENT: return "un coeficiente numérico (ej: 2H2O)";
            case SUBSCRIPT: return "un subíndice numérico";
            case REACTION: return "el comando 'reaction'";
            case BALANCE: return "el comando 'balance'";
            case COMPARE: return "el comando 'compare'";
            case MASS: return "el comando 'mass'";
            case VALIDATE: return "el comando 'validate'";
            case EOF: return "el final de la instrucción";
            default: return tipo.toString();
        }
    }
    
    private void validar(TokenType tokenEsperado){
        if(verActual().getType() == tokenEsperado){
            actual++;
        }else{
            erroresSintacticos.add("Error de sintaxis: Falta " + nombreAmigable(tokenEsperado) + 
                                   ". Se encontró algo inesperado: '" + verActual().getLexeme() + "'.");
            throw new ParseException();
        }
    }
    
    private void sincronizar() {
        // Avanzamos tirando a la basura los tokens malos hasta topar con un ';'
        while (verActual().getType() != TokenType.EOF) {
            if (verActual().getType() == TokenType.SEMICOLON) {
                actual++; // Consumimos el punto y coma para empezar la siguiente línea limpios
                derivacion = new ArrayList<>();
                break;
            }
            actual++;
        }
    }
    
    //GIC
    public void parse() {
        raiz = new Nodos("PROGRAMA");
        while (verActual().getType() != TokenType.EOF) {
            try {
                derivacion = new ArrayList<>();
                numDerivaciones ++;
                todasDerivaciones.add(derivacion);
                Nodos nodoS = analizarS();
                arboles.add(nodoS);
                raiz.agregarHijo(nodoS);
            } catch (ParseException e) {
                sincronizar();
            }
        }
    }
    
    //S -> D id= E | Q id | Q L | id = L
    private Nodos analizarS() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("S");
        derivacion.add("Derivación #" + numDerivaciones);

        if (tipoActual == TokenType.REACTION  || tipoActual == TokenType.COMPARE) {
            derivacion.add("S → D id = E ;");
            nodo.agregarHijo(analizarD());
            
            Token idToken = verActual();           // captura ANTES de avanzar
            
            validar(TokenType.IDENTIFIER);
            nodo.agregarHijo(new Nodos("id(" + idToken.getLexeme() + ")"));
            
            validar(TokenType.ASSIGN);
            nodo.agregarHijo(new Nodos("="));
           
            nodo.agregarHijo(analizarE());

        } else if (tipoActual == TokenType.MASS || tipoActual == TokenType.VALIDATE|| tipoActual == TokenType.BALANCE) {
            nodo.agregarHijo(analizarQ());
            
            if (verActual().getType() == TokenType.IDENTIFIER) {
                derivacion.add("S → Q id ;");
                
                Token idToken = verActual();                 
                validar(TokenType.IDENTIFIER);
                nodo.agregarHijo(new Nodos("id(" + idToken.getLexeme() + ")"));
                
            } else {
                derivacion.add("S → Q L ;");
                nodo.agregarHijo(analizarL());
            }

        } else if (tipoActual == TokenType.IDENTIFIER) {
            derivacion.add("S → id = L ;");
            
            Token idToken = verActual(); 
            validar(TokenType.IDENTIFIER);
            nodo.agregarHijo(new Nodos("id(" + idToken.getLexeme() + ")"));
            
            validar(TokenType.ASSIGN);
            nodo.agregarHijo(new Nodos("="));
            
            nodo.agregarHijo(analizarL());

        } else {
            erroresSintacticos.add("Error de sintaxis: Toda instrucción debe iniciar con un comando (ej: reaction, mass) o asignando una variable (ej: _mezcla =).");
            throw new ParseException();
        }

        // Final de sentencia obligatorio
        validar(TokenType.SEMICOLON);
        nodo.agregarHijo(new Nodos(";"));       

        return nodo;
    }

    // D -> reaction | compare
    private Nodos analizarD() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("D");
        
        if (tipoActual == TokenType.REACTION) {
            derivacion.add("D → reaction");
            validar(TokenType.REACTION);
            nodo.agregarHijo(new Nodos("reaction"));            
        
        } else if (tipoActual == TokenType.COMPARE) {
            derivacion.add("D → compare");
            validar(TokenType.COMPARE);
            nodo.agregarHijo(new Nodos("compare"));  
        
        } else {
            erroresSintacticos.add("Error de sintaxis: Se esperaba un comando de declaración ('reaction', 'balance' o 'compare').");
            throw new ParseException();
        }
        return nodo;
    }

    // Q -> mass | validate | balance
    private Nodos analizarQ() {
        TokenType tipoActual = verActual().getType();
        Nodos nodo = new Nodos("Q");
        
        if (tipoActual == TokenType.MASS) {
            derivacion.add("Q → mass");
            validar(TokenType.MASS);
            nodo.agregarHijo(new Nodos("mass"));
            
            
        } else if (tipoActual == TokenType.VALIDATE) {
            derivacion.add("Q → validate");
            validar(TokenType.VALIDATE);
            nodo.agregarHijo(new Nodos("validate"));
            
            
        }  else if (tipoActual == TokenType.BALANCE) {
            derivacion.add("Q → balance");
            validar(TokenType.BALANCE);
            nodo.agregarHijo(new Nodos("balance"));
            
            
        }else {
            erroresSintacticos.add("Error de sintaxis: Se esperaba un comando de consulta ('mass' o 'validate').");
            throw new ParseException();
        }
        return nodo;
    }

    // E -> L -> L
    private Nodos analizarE() {
        Nodos nodo = new Nodos("E");
        derivacion.add("E → L → L");
        nodo.agregarHijo(analizarL());
        
        validar(TokenType.ARROW);
        nodo.agregarHijo(new Nodos("→"));
        
        nodo.agregarHijo(analizarL());
        return nodo;
    }

    // L -> M | M + L
    private Nodos analizarL() {
        Nodos nodo = new Nodos("L");
        nodo.agregarHijo(analizarM());
        
        if (verActual().getType() == TokenType.PLUS) {
            derivacion.add("L → M + L");
            
            validar(TokenType.PLUS);
            nodo.agregarHijo(new Nodos("+"));
            
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
            
            validar(TokenType.COEFFICIENT);
            nodo.agregarHijo(new Nodos("coef(" + verActual().getLexeme() + ")"));
            
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
            erroresSintacticos.add("Error de sintaxis en la molécula: Un compuesto debe iniciar con un Elemento Químico o abriendo un paréntesis '('.");
            throw new ParseException();
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
