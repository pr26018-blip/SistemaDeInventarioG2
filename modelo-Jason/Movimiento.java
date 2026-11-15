package sistemadeinventariog2.modelo;

import java.time.LocalDateTime;

public class Movimiento {

    private String codigoProducto;
    private String tipo;
    private int cantidad;
    private LocalDateTime fecha;

    // Constructor con fecha y hora automática
    public Movimiento(String codigoProducto, String tipo, int cantidad) {

        this.codigoProducto = codigoProducto == null
                ? null
                : codigoProducto.trim();

        this.tipo = tipo == null
                ? null
                : tipo.trim().toUpperCase();

        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();

        validarDatos();
    }

    // Constructor con fecha personalizada
    public Movimiento(String codigoProducto, String tipo,
                      int cantidad, LocalDateTime fecha) {

        this.codigoProducto = codigoProducto == null
                ? null
                : codigoProducto.trim();

        this.tipo = tipo == null
                ? null
                : tipo.trim().toUpperCase();

        this.cantidad = cantidad;
        this.fecha = fecha;

        validarDatos();
    }

    // Validación de datos
    private void validarDatos() {

        if (codigoProducto == null || codigoProducto.isEmpty()) {
            throw new IllegalArgumentException(
                    "El código del producto es obligatorio."
            );
        }

        if (tipo == null ||
                (!tipo.equals("ENTRADA") &&
                        !tipo.equals("SALIDA"))) {

            throw new IllegalArgumentException(
                    "El tipo debe ser ENTRADA o SALIDA."
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha del movimiento es obligatoria."
            );
        }
    }

    // Métodos Getters
    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
