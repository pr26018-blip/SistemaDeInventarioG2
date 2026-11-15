package sistemadeinventariog2.persistencia;

/**
 * Autor: Daniel Solórzano
 * Descripción: Repositorio encargado de gestionar las operaciones CRUD 
 * (Crear, Leer, Actualizar, Eliminar) para la colección de Productos en MongoDB.
 */

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import sistemadeinventariog2.ConexionMongoDB;
import sistemadeinventariog2.modelo.Producto;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import java.util.regex.Pattern;

public class ProductoRepository {

    private final MongoCollection<Document> coleccion;
    private final MongoCollection<Document> contadores;

    public ProductoRepository() {
        MongoDatabase db = ConexionMongoDB.obtenerConexion();
        coleccion = db.getCollection("productos");
        contadores = db.getCollection("contadores");

        // Crear índice único para el código del producto
        coleccion.createIndex(
                Indexes.ascending("codigo"),
                new IndexOptions().unique(true)
        );
    }
    public String generarCodigoProducto() {

        Document contador = contadores.findOneAndUpdate(
                new Document("_id", "productos"),

                new Document("$inc",
                        new Document("secuencia", 1)
                ),

                new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER)
        );

        int numero = contador.getInteger("secuencia");

        return String.format("P%04d", numero);
    }
    public void guardar(Producto producto) {

        // Verificar si el código ya existe
        Producto existente = buscarPorCodigo(producto.getCodigo());

        if (existente != null) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con el código: " + producto.getCodigo()
            );
        }

        // Crear documento para MongoDB
        Document documento = new Document("codigo", producto.getCodigo())
                .append("nombre", producto.getNombre())
                .append("descripcion", producto.getDescripcion())
                .append("categoria", producto.getCategoria())
                .append("precio", producto.getPrecio())
                .append("cantidad", producto.getCantidad());

        // Guardar documento
        coleccion.insertOne(documento);
    }
    public Producto buscarPorCodigo(String codigo) {

        Document documento = coleccion.find(
                new Document("codigo", codigo)
        ).first();

        if (documento == null) {
            return null;
        }

        Producto producto = new Producto(
                documento.getString("codigo"),
                documento.getString("nombre"),
                documento.getString("descripcion"),
                documento.getString("categoria"),
                documento.getDouble("precio"),
                documento.getInteger("cantidad")
        );

        return producto;
    }
    public List<Producto> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Document filtro = new Document(
                "nombre",
                new Document("$regex", Pattern.quote(nombre.trim()))
                        .append("$options", "i")
        );

        List<Producto> productos = new ArrayList<>();

        for (Document documento : coleccion.find(filtro)) {

            Producto producto = new Producto(
                    documento.getString("codigo"),
                    documento.getString("nombre"),
                    documento.getString("descripcion"),
                    documento.getString("categoria"),
                    documento.getDouble("precio"),
                    documento.getInteger("cantidad")
            );

            productos.add(producto);
        }

        return productos;
    }
    public List<Producto> buscarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Document filtro = new Document(
                "categoria",
                new Document("$regex", Pattern.quote(categoria.trim()))
                        .append("$options", "i")
        );

        List<Producto> productos = new ArrayList<>();

        for (Document documento : coleccion.find(filtro)) {

            Producto producto = new Producto(
                    documento.getString("codigo"),
                    documento.getString("nombre"),
                    documento.getString("descripcion"),
                    documento.getString("categoria"),
                    documento.getDouble("precio"),
                    documento.getInteger("cantidad")
            );

            productos.add(producto);
        }

        return productos;
    }
    public List<Producto> buscarProductosConExistenciasBajas(int minimo) {

        Document filtro = new Document(
                "cantidad",
                new Document("$lte", minimo)
        );

        List<Producto> productos = new ArrayList<>();

        for (Document documento : coleccion.find(filtro)) {

            Producto producto = new Producto(
                    documento.getString("codigo"),
                    documento.getString("nombre"),
                    documento.getString("descripcion"),
                    documento.getString("categoria"),
                    documento.getDouble("precio"),
                    documento.getInteger("cantidad")
            );

            productos.add(producto);
        }

        return productos;
    }
    public List<Producto> listarTodos() {

        List<Producto> productos = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            Producto producto = new Producto(
                    documento.getString("codigo"),
                    documento.getString("nombre"),
                    documento.getString("descripcion"),
                    documento.getString("categoria"),
                    documento.getDouble("precio"),
                    documento.getInteger("cantidad")
            );

            productos.add(producto);
        }

        return productos;
    }
    public boolean actualizar(Producto producto) {

        Document filtro = new Document(
                "codigo", producto.getCodigo()
        );

        Document cambios = new Document("$set",
                new Document("nombre", producto.getNombre())
                        .append("descripcion", producto.getDescripcion())
                        .append("categoria", producto.getCategoria())
                        .append("precio", producto.getPrecio())
                        .append("cantidad", producto.getCantidad())
        );

        return coleccion.updateOne(filtro, cambios)
                .getMatchedCount() > 0;
    }
    public boolean eliminar(String codigo) {

        Document filtro = new Document("codigo", codigo);

        return coleccion.deleteOne(filtro)
                .getDeletedCount() > 0;
    }
}
