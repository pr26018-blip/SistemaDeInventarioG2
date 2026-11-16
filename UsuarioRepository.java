package sistemadeinventariog2.persistencia;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;

import sistemadeinventariog2.ConexionMongoDB;
import sistemadeinventariog2.modelo.Usuario;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private final MongoCollection<Document> coleccion;
    private final MongoCollection<Document> contadores;

    public UsuarioRepository() {

        MongoDatabase db = ConexionMongoDB.obtenerConexion();

        coleccion = db.getCollection("usuarios");
        contadores = db.getCollection("contadores");
        coleccion.createIndex(
                Indexes.ascending("id"),
                new IndexOptions().unique(true)
        );

        coleccion.createIndex(
                Indexes.ascending("nombreUsuario"),
                new IndexOptions().unique(true)
        );

    }
    public String generarCodigoUsuario() {

        Document contador = contadores.findOneAndUpdate(
                new Document("_id", "usuarios"),
                new Document("$inc", new Document("secuencia", 1)),
                new FindOneAndUpdateOptions()
                        .upsert(true)
                        .returnDocument(ReturnDocument.AFTER)
        );

        int numero = contador.getInteger("secuencia");

        return String.format("U%04d", numero);
    }
    public void guardarUsuario(Usuario usuario) {

        Document documento = new Document(
                "id", usuario.getId()
        )
                .append("nombreUsuario", usuario.getNombreUsuario())
                .append("contrasena", usuario.getContrasena())
                .append("rol", usuario.getRol());

        coleccion.insertOne(documento);
    }
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return null;
        }

        nombreUsuario = nombreUsuario.trim();

        Document documento = coleccion.find(
                new Document("nombreUsuario", nombreUsuario)
        ).first();

        if (documento == null) {
            return null;
        }

        return new Usuario(
                documento.getString("id"),
                documento.getString("nombreUsuario"),
                documento.getString("contrasena"),
                documento.getString("rol")
        );
    }
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList<>();

        for (Document documento : coleccion.find()) {

            usuarios.add(new Usuario(
                    documento.getString("id"),
                    documento.getString("nombreUsuario"),
                    documento.getString("contrasena"),
                    documento.getString("rol")
            ));
        }

        return usuarios;
    }
    public Usuario buscarPorId(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        Document documento = coleccion.find(
                new Document("id", id.trim())
        ).first();

        if (documento == null) {
            return null;
        }

        return new Usuario(
                documento.getString("id"),
                documento.getString("nombreUsuario"),
                documento.getString("contrasena"),
                documento.getString("rol")
        );
    }
    public boolean actualizarUsuario(
            String id,
            String nuevoNombreUsuario,
            String nuevoRol,
            String idUsuarioSesion) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio."
            );
        }

        if (nuevoNombreUsuario == null ||
                nuevoNombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio."
            );
        }

        if (nuevoRol == null || nuevoRol.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El rol seleccionado es obligatorio."
            );
        }

        nuevoNombreUsuario = nuevoNombreUsuario.trim();
        nuevoRol = nuevoRol.trim().toUpperCase();

        if (!nuevoRol.equals("ADMIN") &&
                !nuevoRol.equals("USUARIO")) {
            throw new IllegalArgumentException(
                    "El rol seleccionado no es válido."
            );
        }

        id = id.trim();

        Usuario usuarioActual = buscarPorId(id);

        if (usuarioActual == null) {
            return false;
        }

        boolean esPropiaCuenta = idUsuarioSesion != null &&
                id.equals(idUsuarioSesion.trim());

        if (esPropiaCuenta &&
                !usuarioActual.getNombreUsuario().equals(nuevoNombreUsuario)) {
            throw new IllegalArgumentException(
                    "No puedes cambiar tu propio nombre de usuario " +
                            "mientras tienes una sesión activa."
            );
        }

        if (usuarioActual.getRol().equals("ADMIN") &&
                nuevoRol.equals("USUARIO")) {

            long administradores = coleccion.countDocuments(
                    new Document("rol", "ADMIN")
            );

            if (administradores <= 1) {
                throw new IllegalArgumentException(
                        "No puedes cambiar el rol del último administrador."
                );
            }

            if (esPropiaCuenta) {
                throw new IllegalArgumentException(
                        "No puedes quitarte tus propios permisos de administrador."
                );
            }
        }

        Document filtro = new Document("id", id);

        Document cambios = new Document(
                "$set",
                new Document("nombreUsuario", nuevoNombreUsuario)
                        .append("rol", nuevoRol)
        );

        var resultado = coleccion.updateOne(filtro, cambios);

        return resultado.getMatchedCount() > 0;
    }
    public boolean actualizarContrasena(String id, String nuevaContrasena) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio."
            );
        }

        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        Document filtro = new Document("id", id.trim());

        Document cambios = new Document(
                "$set",
                new Document("contrasena", nuevaContrasena)
        );

        var resultado = coleccion.updateOne(filtro, cambios);

        return resultado.getMatchedCount() > 0;
    }
    public boolean eliminarUsuario(String id, String idUsuarioSesion) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio."
            );
        }

        if (idUsuarioSesion != null &&
                id.trim().equals(idUsuarioSesion.trim())) {

            throw new IllegalArgumentException(
                    "No puedes eliminar tu propia cuenta."
            );
        }

        Usuario usuario = buscarPorId(id);

        if (usuario == null) {
            return false;
        }

        if (usuario.getRol().equals("ADMIN")) {

            long administradores = coleccion.countDocuments(
                    new Document("rol", "ADMIN")
            );

            if (administradores <= 1) {
                throw new IllegalArgumentException(
                        "No puedes eliminar al último administrador."
                );
            }
        }

        var resultado = coleccion.deleteOne(
                new Document("id", id.trim())
        );

        return resultado.getDeletedCount() > 0;
    }
}
