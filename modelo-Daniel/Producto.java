package sistemadeinventariog2.modelo;

/**
 * Autor: Daniel Solórzano
 * Descripción: Clase entidad que representa el modelo de Producto 
 * dentro de la arquitectura del sistema de inventario.
 */

public class Producto {
//Atributos principales del producto
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precio;
    private int cantidad;

    public Producto(String codigo, String nombre, String descripcion,
                    String categoria, double precio, int cantidad) {

        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;

        validarDatos();
    }
    private void validarDatos() {

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }

        if (!Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio debe ser válido y no negativo.");
        }

        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
    }
    //metodos Getters
    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }
    // metodos setters
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public void setDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        this.descripcion = descripcion.trim();
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
        this.categoria = categoria.trim();
    }

    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio debe ser válido y no negativo.");
        }
        this.precio = precio;
    }

    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
        this.cantidad = cantidad;
    }
}
