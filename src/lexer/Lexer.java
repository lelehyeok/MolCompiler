/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lexer;

/**
 *
 * @author akmh1
 */
public class Lexer {
  private final String input;
    private int position;
    private char currentChar;
    private TokenType lastTokenType;

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

            // Automata elementos, palabras reservadas
            
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
                throw new RuntimeException("Error léxico: Un identificador debe llevar una letra después del guion bajo. Encontrado: '" + currentChar + "' en la posición " + position);
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
                while (currentChar != '\0' && Character.isLowerCase(currentChar)) {
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
                        // Si no está en el catálogo, lanzamos error
                        throw new RuntimeException("Error léxico: Comando no reconocido '" + word + "' en la posición " + position);
                }
            }
           // 7. MANEJO DE ERRORES: Si lee un símbolo inválido (ej: @, %)
            throw new RuntimeException("Error léxico: Caracter no reconocido '" + currentChar + "' en la posición " + position);
            
        }
        // fin del archivo o fila
        return new Token(TokenType.EOF, "");
    }  
}
