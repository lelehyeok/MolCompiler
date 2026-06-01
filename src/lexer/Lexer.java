/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

import java.util.*;

/**
 *
 * @author akmh1
 */
public class Lexer {
  private final String input;
    private int position;
    private char currentChar;
    private TokenType lastTokenType;
    private List<String> erroresLexicos = new ArrayList<>();

    
    public List<String> getErroresLexicos() {
        return erroresLexicos;
    }
    // El constructor recibe la fórmula (ej: "2Ca(OH)2") y prepara el primer caracter
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }    

    // Función para avanzar un paso en nuestra máquina de estados
    private void advance() {
        position++;
        if (position >= input.length()) {
            currentChar = '\0'; // \0 significa que llegamos al final del texto
        } else {
            currentChar = input.charAt(position);
        }
    }
    private Token createToken(TokenType type, String lexeme) {
        this.lastTokenType = type;
        return new Token(type, lexeme);
    }

    // El motor principal 
    public Token getNextToken() {
        while (currentChar != '\0') {
            
            // 1. FILTRADO: Ignorar espacios en bco o saltos de línea
            if (Character.isWhitespace(currentChar)) {
                advance();
                continue;
            }

            // 2. Automata para flecha y signos simples
            if (currentChar == '-') {
                if (position + 1 < input.length() && input.charAt(position + 1) == '>') {
                    advance(); 
                    advance();
                    return createToken(TokenType.ARROW, "->");
                }
            }
            if (currentChar == '(') {
                advance();
                return createToken(TokenType.LPAREN, "(");
            }
            if (currentChar == ')') {
                advance();
                return createToken(TokenType.RPAREN, ")");
            }
            if (currentChar == '+') {
                advance();
                return createToken(TokenType.PLUS, "+");
            }
            if (currentChar == '=') {
                advance();
                return createToken(TokenType.ASSIGN, "=");
            }
            if (currentChar == ';') {
                advance();
                return createToken(TokenType.SEMICOLON, ";");
            }
            
            
            // 3. Automata para coeficientes y subindices
                if (Character.isDigit(currentChar)) {
                StringBuilder sb = new StringBuilder();
                while (currentChar != '\0' && Character.isDigit(currentChar)) {
                    sb.append(currentChar);
                    advance();
                }
                // Analiza el contexto previo
                if (lastTokenType == TokenType.ELEMENT || lastTokenType == TokenType.RPAREN) {
                    return createToken(TokenType.SUBSCRIPT, sb.toString());
                } else {
                    return createToken(TokenType.COEFFICIENT, sb.toString());
                }
            }

            // 4. Automata de identificadores (Ej: _mezcla, _M1)
                if (currentChar == '_') {
                    StringBuilder sb = new StringBuilder();
                    sb.append(currentChar);
                    advance(); // Consumimos el guion bajo inicial
    
            // El siguiente caracter DEBE ser una letra
                if (currentChar != '\0' && Character.isLetter(currentChar)) {
                    sb.append(currentChar);
                    advance();
                    
            // A partir de aquí (tercer caracter), ya podemos aceptar letras o números
                while (currentChar != '\0' && Character.isLetterOrDigit(currentChar)) {
                    sb.append(currentChar);
                    advance();
                }        
                return createToken(TokenType.IDENTIFIER, sb.toString());
                } else {
                // Si después del '_' hay un número, un espacio o nada, es un error léxico
                    erroresLexicos.add("Error léxico en posición " + position + ": Identificador inválido '" + currentChar + 
                                       "'. Recuerda que las variables deben empezar con '_' seguido inmediatamente de una letra (ejemplo: _mezcla).");
                    advance();
                    continue;
                }
                }
                // 5. Automata para la gramatica de elementos
            if (Character.isUpperCase(currentChar)) {
                StringBuilder sb = new StringBuilder();
                sb.append(currentChar);
                advance();
                
                // Mientras sigan letras minúsculas, forman parte del mismo elemento
                while (currentChar != '\0' && Character.isLowerCase(currentChar)) {
                    sb.append(currentChar);
                    advance();
                }
                return createToken(TokenType.ELEMENT, sb.toString());
            }
            
            // 6. Automata de palabras reservadas
            if (Character.isLowerCase(currentChar)) {
                StringBuilder sb = new StringBuilder();
                //las palabras definidas serán minusculas
                    while (currentChar != '\0' && (Character.isLowerCase(currentChar) || Character.isDigit(currentChar))) {
                    sb.append(currentChar);
                    advance();
                }
                
                String word = sb.toString();
                switch (word) {
                    case "reaction": return createToken(TokenType.REACTION, word);
                    case "balance":  return createToken(TokenType.BALANCE, word);
                    case "compare":  return createToken(TokenType.COMPARE, word);
                    case "mass":     return createToken(TokenType.MASS, word);
                    case "validate": return createToken(TokenType.VALIDATE, word);
                    default:
                        String sugerencia = "";
                        if (word.startsWith("reaction")) {
                            sugerencia = "¿Quisiste decir 'reaction'? Revisa la ortografia .Los comandos no llevan números ni mayusculas.";
                        } else if (word.startsWith("balance") || word.startsWith("compare") || word.startsWith("mass") || word.startsWith("validate")) {
                            sugerencia = "Revisa la ortografía. Los comandos van en minúsculas y sin números.";
                        } else {
                            sugerencia = "¿Olvidaste poner el guion bajo '_' al inicio de tu variable (ej: _" + word + ")? Si intentabas usar un comando, los válidos son: reaction, balance, compare, mass, validate.";
                        }

                        erroresLexicos.add("Error léxico en posición " + position + ": Palabra no reconocida '" + word + "'. " + sugerencia);
                        continue;
                }
            }
           // 7. Si lee un símbolo inválido (ej: @, %)
            erroresLexicos.add("Error léxico en posición " + position + ": El símbolo '" + currentChar + "' no pertenece al alfabeto de este lenguaje químico.");
            advance(); 
            continue;
            
        }
        // fin del archivo o fila
        return new Token(TokenType.EOF, "");
    }  
}
