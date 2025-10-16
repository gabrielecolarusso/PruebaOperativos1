/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainPackage;

/**
 *
 * @author david
 */
public class ProcesoConfig {
    // Mantenemos los mismos campos que antes
    private String nombre;
    private String tipo;
    private int instrucciones;
    private int ciclosExcepcion;
    private int duracionExcepcion;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public int getInstrucciones() { return instrucciones; }
    public void setInstrucciones(int instrucciones) { this.instrucciones = instrucciones; }
    
    public int getCiclosExcepcion() { return ciclosExcepcion; }
    public void setCiclosExcepcion(int ciclosExcepcion) { this.ciclosExcepcion = ciclosExcepcion; }
    
    public int getDuracionExcepcion() { return duracionExcepcion; }
    public void setDuracionExcepcion(int duracionExcepcion) { this.duracionExcepcion = duracionExcepcion; }
}
