package sistemadeinventariog2.modelo;

public class Usuario {

    private String id;
    private String nombreUsuario;
    private String contrasena;
    private String rol;

    public Usuario(String id, String nombreUsuario,
                   String contrasena, String rol) {

        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;

        validarDatos();

    }
    private void validarDatos() {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID del usuario es obligatorio."
            );
        }

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio."
            );
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        if (rol == null ||
                (!rol.equalsIgnoreCase("ADMIN") &&
                        !rol.equalsIgnoreCase("USUARIO"))) {

            throw new IllegalArgumentException(
                    "El rol debe ser ADMIN o USUARIO."
            );
        }

    }
    public String getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }


}
