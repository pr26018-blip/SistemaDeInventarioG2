package sistemadeinventariog2.servicios;

/**
 * Desarrollado por: Maria
 * Descripción: Clase de servicio encargada de gestionar la lógica de negocio
 * y la  obtención de datos para la generacíón de reportes del inventario */

import sistemadeinventariog2.modelo.Producto;
import sistemadeinventariog2.persistencia.ProductoRepository;

import java.util.List;
import java.util.ArrayList;

public class ReportesManager {

    private final ProductoRepository productoRepository;

    public ReportesManager() {
        productoRepository = new ProductoRepository();
    }
    public List<Producto> obtenerInventarioGeneral() {

        return productoRepository.listarTodos();

    }
    public List<Producto> obtenerProductosSinExistencias() {

        List<Producto> productos = productoRepository.listarTodos();

        List<Producto> sinExistencias = new ArrayList<>();

        for (Producto producto : productos) {

            if (producto.getCantidad() == 0) {

                sinExistencias.add(producto);

            }
        }

        return sinExistencias;
    }
// se realiza la consulta de exitencias bajas por medio de mongobd
    public List<Producto> obtenerProductosConExistenciasBajas(int minimo) {

        if (minimo < 0) {
            throw new IllegalArgumentException(
                    "El mínimo de existencias no puede ser negativo."
            );
        }

        return productoRepository.buscarProductosConExistenciasBajas(minimo);
    }
    public double calcularValorTotalInventario() {

        List<Producto> productos = productoRepository.listarTodos();

        double total = 0;

        for (Producto producto : productos) {

            total += producto.getPrecio() * producto.getCantidad();

        }

        return total;
    }

}