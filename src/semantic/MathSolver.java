/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semantic;
import java.util.*;
/**
 *
 * @author akmh1
 */
public class MathSolver {public static String balancear(List<Map<String, Integer>> reactivos, List<Map<String,Integer>> productos){
        // El "log" será nuestra bitácora paso a paso
        StringBuilder log = new StringBuilder();
        
        List<String> elementos = new ArrayList<>();
        for(Map<String, Integer> mol : reactivos){
            for(String e : mol.keySet()) if (!elementos.contains(e)) elementos.add(e);
        }//for each mol : reactivos
        
        for(Map<String, Integer> mol : productos){
            for(String e : mol.keySet()) if(!elementos.contains(e)) elementos.add(e);
        }
        
        int numFilas = elementos.size();
        int numColumnas = reactivos.size() + productos.size();
        
        //Crear matriz
        double [][] matriz = new double [numFilas][numColumnas];
        
        //Probar
        for(int i = 0; i < numFilas; i++){
            String elemento = elementos.get(i);
            
            //Reactivos
            for(int j = 0; j < reactivos.size(); j++){
                matriz[i][j] = reactivos.get(j).getOrDefault(elemento, 0);
            }//for reactivos
            
            //Productos
            for(int j = 0; j < productos.size(); j++){
                matriz[i][reactivos.size() + j] = -productos.get(j).getOrDefault(elemento, 0);
            }//for productos
        }//for de la matriz
        
        // --- [PASO 1] Documentamos la matriz inicial ---
        log.append("<br><br><font color='#4C7A4C'><b>[PASO 1] Construcción de la matriz inicial (Sistema Homogéneo):</b></font><br>");
        // La etiqueta <pre> nos garantiza que use una fuente monospace y respete los espacios
        log.append("<pre style='color: #4C7A4C; font-family: Consolas, monospace; font-size: 15px;'>");
        for(int i = 0; i < numFilas; i++){
            log.append(String.format(Locale.US, "%-6s", elementos.get(i)+":"));
            for(int j = 0; j < numColumnas; j++){
                log.append(String.format(Locale.US, "%7.1f ", matriz[i][j]));
            }//columnas
            log.append("<br>");
        }//for de mostrar la matriz
        log.append("</pre>");
        
        //Resolver por Gauss-Jordan (le pasamos el log y los elementos para que siga escribiendo)
        return resolverGaussJordan(matriz, numFilas, numColumnas, reactivos, productos, log, elementos);
    }//balancear
    
    public static String resolverGaussJordan(double [][] matriz, int filas, int columnas, List<Map<String, Integer>> reactivos, List<Map<String, Integer>> productos, StringBuilder log, List<String> elementos){
        int lead = 0;
        for(int r=0; r<filas; r++){
            if(columnas<= lead) break;
            int i = r;
            while(matriz[i][lead] == 0){
                i++;
                if(filas==i){
                    i = r;
                    lead++;
                    if(columnas == lead) return "Error matematico al balancear la ecuacion.";
                }//if
            }//while
            
            //Intercambiar filas
            double[] temp = matriz[i];
            matriz[i] = matriz[r];
            matriz[r] = temp;
                  
            //Normalizar fila pivote
            double val = matriz[r][lead];
            for(int j = 0; j < columnas; j++) matriz[r][j] /=val;
                
            //Hacer ceros la columna del pivote
            for(int i2 = 0; i2 < filas; i2++){
                if(i2!=r){
                    double val2 = matriz[i2][lead];
                    for(int j = 0; j < columnas; j++){
                        matriz[i2][j] -= val2*matriz[r][j];
                    }//for
                }//if
            }//for ceros
            lead++;
        }//for
        
        // --- [PASO 2] Documentamos la matriz después de Gauss-Jordan ---
        log.append("<font color='#4C7A4C'><b>[PASO 2] Matriz escalonada reducida (Gauss-Jordan aplicado):</b></font><br>");
        log.append("<pre style='color: #4C7A4C; font-family: Consolas, monospace; font-size: 15px;'>");
        for(int i = 0; i < filas; i++){
            log.append(String.format(Locale.US, "%-6s", elementos.get(i)+":"));
            for(int j = 0; j < columnas; j++){
                log.append(String.format(Locale.US, "%7.2f ", matriz[i][j]));
            }
            log.append("<br>");
        }
        log.append("</pre>");
        
        //Resultados
        double [] resultados = new double[columnas];
        resultados[columnas - 1] = 1.0;
        for(int i = 0; i<filas; i++){
            //Buscar pivote de cada fila
            for(int j = 0; j < columnas; j++){
                if(Math.abs(matriz[i][j] - 1.0) < 0.0001){
                    resultados[j] = -matriz[i][columnas-1];
                    break;
                }
            }
        }
        
        // Pasamos a estandarizar
        return estandarizarEnteros(resultados, reactivos, productos, log);
    }//Gauss-Jordan
    
    public static String estandarizarEnteros(double[] resultados, List<Map<String, Integer>> reactivos, List<Map<String, Integer>> productos, StringBuilder log){
        
        // --- [PASO 3] Documentamos el despeje ---
        log.append("<font color='#4C7A4C'><b>[PASO 3] Extracción y estandarización a enteros:</b></font><br>");
        log.append("<font color='#4C7A4C'>Coeficientes base obtenidos: [ ");
        for(double r : resultados) log.append(String.format(Locale.US, "%.2f ", r));
        log.append("]</font><br>");
        
        //Multiplicar por factores
        int multiplicador = 1;
        boolean todosEnteros = false;
        
        while(!todosEnteros && multiplicador < 50){
            todosEnteros = true;
            for(double res : resultados){
                double valorMult = res * multiplicador;
                //Verificar si la parte decimal es casi cero
                if(Math.abs(valorMult - Math.round(valorMult)) > 0.0001){
                    todosEnteros = false;
                    break;
                }//if
            }//for
            if(!todosEnteros) multiplicador++;
        }//while
        
        if(multiplicador > 1){
            log.append("<font color='#4C7A4C'>Se detectaron fracciones. Multiplicando toda la ecuación por: <b>").append(multiplicador).append("</b></font><br><br>");
        } else {
            log.append("<font color='#4C7A4C'>Todos los coeficientes son enteros exactos. No requiere estandarización.</font><br><br>");
        }
        
        // Construcción del String visual final
        StringBuilder ecuacionVisual = new StringBuilder();

        // Armar Reactivos
        for (int i = 0; i < reactivos.size(); i++) {
            long coef = Math.round(resultados[i] * multiplicador);
            if (coef > 1) ecuacionVisual.append(coef); // Solo pone coeficientes > 1
            ecuacionVisual.append(diccionarioAString(reactivos.get(i)));
            
            if (i < reactivos.size() - 1) ecuacionVisual.append(" + ");
        }

        ecuacionVisual.append(" -> ");

        // Armar Productos
        int offset = reactivos.size();
        for (int i = 0; i < productos.size(); i++) {
            long coef = Math.round(resultados[i + offset] * multiplicador);
            if (coef > 1) ecuacionVisual.append(coef);
            ecuacionVisual.append(diccionarioAString(productos.get(i)));
            
            if (i < productos.size() - 1) ecuacionVisual.append(" + ");
        }

        log.append("<font color='#4C7A4C' size='+1'><b>Resultado final: ").append(ecuacionVisual.toString()).append("</b></font><br>");

        return log.toString();
    }//estandarizar enteros
    
    // Método auxiliar para traducir el mapa de vuelta a letras y números (ej: {H=2, O=1} -> H2O)
    private static String diccionarioAString(Map<String, Integer> molecula) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> elemento : molecula.entrySet()) {
            sb.append(elemento.getKey());
            if (elemento.getValue() > 1) {
                sb.append(elemento.getValue());
            }
        }
        return sb.toString();
    }
}//public class
