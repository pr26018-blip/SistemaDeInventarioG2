package sistemadeinventariog2.servicios;

import org.mindrot.jbcrypt.BCrypt;
import sistemadeinventariog2.modelo.Usuario;
import sistemadeinventariog2.persistencia.UsuarioRepository;
import com.mongodb.MongoWriteException;
import java.util.List;

public class AutenticacionManager {

    private final UsuarioRepository usuarioRepository =
            new UsuarioRepository();
    public void registrarUsuario(
            String nombreUsuario,
            String contrasena,
            String rol,
            String usuarioSolicitante
    ) {// Verificar que quien solicita el registro sea ADMIN
        Usuario solicitante = usuarioRepository.buscarPorNombreUsuario(
                usuarioSolicitante
        );

        if (solicitante == null ||
                !"ADMIN".equalsIgnoreCase(solicitante.getRol())) {

            throw new SecurityException(
                    "No tienes permisos para registrar usuarios."
            );
        }

        // Validar nombre de usuario
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio."
            );
        }
        // Validar contraseña
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        // Validar rol
        if (rol == null ||
                (!rol.equalsIgnoreCase("ADMIN") &&
                        !rol.equalsIgnoreCase("USUARIO"))) {

            throw new IllegalArgumentException(
                    "El rol debe ser ADMIN o USUARIO."
            );
        }

        // Normalizar espacios
        nombreUsuario = nombreUsuario.trim();
        rol = rol.trim().toUpperCase();

        // Generar hash de la contraseña
        String hash = generarHash(contrasena);

        // Generar ID automáticamente
        String id = usuarioRepository.generarCodigoUsuario();

        Usuario usuario = new Usuario(
                id,
                nombreUsuario,
                hash,
                rol
        );

        try {

            usuarioRepository.guardarUsuario(usuario);

        } catch (MongoWriteException e) {

            if (e.getError().getCode() == 11000) {
                throw new IllegalArgumentException(
                        "El ID o nombre de usuario ya existe."
                );
            }

            throw e;
        }
    }
    public boolean iniciarSesion(
            String nombreUsuario,
            String contrasena
    ) {

        // Validar que los datos no estén vacíos
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }

        if (contrasena == null || contrasena.isEmpty()) {
            return false;
        }

        // Buscar el usuario en MongoDB
        Usuario usuario =
                usuarioRepository.buscarPorNombreUsuario(nombreUsuario.trim());

        // Comprobar si el usuario existe
        if (usuario == null) {
            return false;
        }

        // Verificar la contraseña
        return verificarContrasena(
                contrasena,
                usuario.getContrasena()
        );
    }
    public Usuario autenticar(
            String nombreUsuario,
            String contrasena
    ) {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return null;
        }

        if (contrasena == null || contrasena.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioRepository.buscarPorNombreUsuario(
                nombreUsuario.trim()
        );

        if (usuario == null) {
            return null;
        }

        boolean contrasenaCorrecta = verificarContrasena(
                contrasena,
                usuario.getContrasena()
        );

        if (contrasenaCorrecta) {
            return usuario;
        }

        return null;
    }

    public String generarHash(String contrasena) {

        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        return BCrypt.hashpw(contrasena, BCrypt.gensalt());
    }
    public boolean verificarContrasena(
            String contrasena,
            String hash
    ) {

        if (contrasena == null || hash == null ||
                contrasena.isEmpty() || hash.isEmpty()) {
            return false;
        }

        return BCrypt.checkpw(contrasena, hash);
    }
    private Usuario validarAdministrador(String nombreUsuario) {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new SecurityException(
                    "No se pudo identificar al usuario."
            );
        }

        Usuario solicitante =
                usuarioRepository.buscarPorNombreUsuario(nombreUsuario.trim());

        if (solicitante == null ||
                !"ADMIN".equalsIgnoreCase(solicitante.getRol())) {

            throw new SecurityException(
                    "No tienes permisos para administrar usuarios."
            );
        }

        return solicitante;
    }
    public List<Usuario> listarUsuarios(String usuarioSolicitante) {

        validarAdministrador(usuarioSolicitante);

        return usuarioRepository.listarUsuarios();
    }
    public boolean actualizarUsuario(
            String id,
            String nuevoNombreUsuario,
            String nuevoRol,
            String usuarioSolicitante) {

        Usuario solicitante =
                validarAdministrador(usuarioSolicitante);

        try {

            return usuarioRepository.actualizarUsuario(
                    id,
                    nuevoNombreUsuario,
                    nuevoRol,
                    solicitante.getId()
            );

        } catch (MongoWriteException e) {

            if (e.getError().getCode() == 11000) {
                throw new IllegalArgumentException(
                        "Ese nombre de usuario ya está registrado. " +
                                "Por favor, utiliza otro nombre."
                );
            }

            throw e;
        }
    }
    public boolean eliminarUsuario(
            String id,
            String usuarioSolicitante) {

        Usuario solicitante =
                validarAdministrador(usuarioSolicitante);

        return usuarioRepository.eliminarUsuario(
                id,
                solicitante.getId()
        );
    }
    public boolean actualizarContrasena(
            String id,
            String nuevaContrasena,
            String usuarioSolicitante) {

        validarAdministrador(usuarioSolicitante);

        if (nuevaContrasena == null ||
                nuevaContrasena.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "La nueva contraseña es obligatoria."
            );
        }

        String hash = generarHash(nuevaContrasena);

        return usuarioRepository.actualizarContrasena(
                id,
                hash
        );
    }
}
