package sistemadeinventariog2.servicios;

import sistemadeinventariog2.modelo.Producto;
import sistemadeinventariog2.modelo.Movimiento;
import sistemadeinventariog2.persistencia.ProductoRepository;
import sistemadeinventariog2.persistencia.MovimientoRepository;
import java.util.List;
import java.util.Comparator;



public class InventarioManager {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public InventarioManager() {
        productoRepository = new ProductoRepository();
        movimientoRepository = new MovimientoRepository();
    }

    public void registrarMovimiento(String codigo, String tipo, int cantidad) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código del producto es obligatorio."
            );
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El tipo de movimiento es obligatorio."
            );
        }

        codigo = codigo.trim();
        tipo = tipo.trim().toUpperCase();

        Producto producto = productoRepository.buscarPorCodigo(codigo);

        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe un producto con el código: " + codigo
            );
        }

        if (tipo == null ||
                (!tipo.equalsIgnoreCase("ENTRADA") &&
                        !tipo.equalsIgnoreCase("SALIDA"))) {

            throw new IllegalArgumentException(
                    "El tipo de movimiento debe ser ENTRADA o SALIDA."
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        int cantidadActual = producto.getCantidad();
        int nuevaCantidad;

        if (tipo.equals("ENTRADA")) {

            try {
                nuevaCantidad = Math.addExact(cantidadActual, cantidad);

            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(
                        "La cantidad resultante supera el límite permitido."
                );
            }

        } else {

            if (cantidad > cantidadActual) {
                throw new IllegalArgumentException(
                        "No hay suficientes existencias para realizar la salida."
                );
            }

            nuevaCantidad = cantidadActual - cantidad;
        }

        // en este bloque lo que hacemos es actualizar las existensias, guardamos el movimiento y si el almacenamiento falla recuperamos la cantidad
        //anterior al intento de actualizacion
        producto.setCantidad(nuevaCantidad);

        boolean actualizado = productoRepository.actualizar(producto);

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No se pudo actualizar el inventario."
            );
        }

        try {

            Movimiento movimiento = new Movimiento(codigo, tipo, cantidad);

            movimientoRepository.guardarMovimiento(movimiento);

        } catch (RuntimeException e) {

            // Intentar restaurar las existencias anteriores
            producto.setCantidad(cantidadActual);

            boolean restaurado = productoRepository.actualizar(producto);

            if (!restaurado) {
                throw new IllegalStateException(
                        "Falló el registro del movimiento y también la restauración del inventario.",
                        e
                );
            }

            throw new IllegalStateException(
                    "No se pudo guardar el movimiento. Se restauraron las existencias anteriores.",
                    e
            );
        }

    }
    public List<Movimiento> obtenerHistorialMovimientos() {

        return movimientoRepository.listarMovimientos();

    }
    public Producto registrarProducto(String nombre,
                                      String descripcion,
                                      String categoria,
                                      double precio,
                                      int cantidad) {

        // Generar el código automáticamente
        String codigo = productoRepository.generarCodigoProducto();

        // Crear el producto con el código generado
        Producto producto = new Producto(
                codigo,
                nombre,
                descripcion,
                categoria,
                precio,
                cantidad
        );

        // Guardar el producto en MongoDB
        productoRepository.guardar(producto);

        return producto;
    }
    public boolean modificarProducto(String codigo,
                                     String nombre,
                                     String descripcion,
                                     String categoria,
                                     double precio) {

        // Buscar el producto por su código
        Producto producto = productoRepository.buscarPorCodigo(codigo);

        // Comprobar que el producto exista
        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe un producto con el código: " + codigo
            );
        }

        // Actualizar los datos del producto
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);

        // Guardar los cambios en MongoDB
        return productoRepository.actualizar(producto);
    }
    public boolean eliminarProducto(String codigo) {

        // Buscar el producto
        Producto producto = productoRepository.buscarPorCodigo(codigo);

        // Comprobar que exista
        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe un producto con el código: " + codigo
            );
        }
        // Comprobar si el producto tiene movimientos registrados
        if (movimientoRepository.tieneMovimientos(codigo)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el producto porque tiene movimientos registrados."
            );
        }

        // Eliminar el producto de MongoDB
        return productoRepository.eliminar(codigo);
    }
    public Producto consultarProducto(String codigo) {

        Producto producto = productoRepository.buscarPorCodigo(codigo);

        if (producto == null) {
            throw new IllegalArgumentException(
                    "No existe un producto con el código: " + codigo
            );
        }

        return producto;
    }
    public java.util.List<Producto> listarProductos() {

        return productoRepository.listarTodos();
    }
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre);
    }
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.buscarPorCategoria(categoria);
    }
    public List<Producto> ordenarPorPrecio(boolean ascendente) {

        List<Producto> productos = productoRepository.listarTodos();

        if (ascendente) {
            productos.sort(Comparator.comparingDouble(Producto::getPrecio));
        } else {
            productos.sort(Comparator.comparingDouble(Producto::getPrecio).reversed());
        }

        return productos;
    }
    public List<Producto> ordenarPorCantidad(boolean ascendente) {

        List<Producto> productos = productoRepository.listarTodos();

        if (ascendente) {
            productos.sort(Comparator.comparingInt(Producto::getCantidad));
        } else {
            productos.sort(Comparator.comparingInt(Producto::getCantidad).reversed());
        }

        return productos;
    }
}
