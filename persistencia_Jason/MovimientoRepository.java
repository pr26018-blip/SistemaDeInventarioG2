package sistemadeinventariog2.persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import sistemadeinventariog2.ConexionMongoDB;
import sistemadeinventariog2.modelo.Movimiento;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import com.mongodb.client.model.Sorts;

public class MovimientoRepository {

    private final MongoCollection<Document> coleccion;

    public MovimientoRepository() {

        MongoDatabase db = ConexionMongoDB.obtenerConexion();

        coleccion = db.getCollection("movimientos");
    }

    public void guardarMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo."
            );
        }

        Document documento = new Document(
                "codigoProducto", movimiento.getCodigoProducto()
        )
                .append("tipo", movimiento.getTipo())
                .append("cantidad", movimiento.getCantidad())
                .append("fecha", movimiento.getFecha().toString());

        coleccion.insertOne(documento);
    }
    public List<Movimiento> listarMovimientos() {

        List<Movimiento> movimientos = new ArrayList<>();

        for (Document documento : coleccion.find().sort(
                Sorts.descending("fecha")
        )) {

            String codigo = documento.getString("codigoProducto");
            String tipo = documento.getString("tipo");
            int cantidad = documento.getInteger("cantidad");

            LocalDateTime fecha = LocalDateTime.parse(
                    documento.getString("fecha")
            );

            Movimiento movimiento = new Movimiento(
                    codigo,
                    tipo,
                    cantidad,
                    fecha
            );

            movimientos.add(movimiento);
        }

        return movimientos;
    }
    public boolean tieneMovimientos(String codigoProducto) {
        if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
            return false;
        }

        codigoProducto = codigoProducto.trim();

        Document filtro = new Document(
                "codigoProducto", codigoProducto
        );

        return coleccion.find(filtro).first() != null;
    }
}