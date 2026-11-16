package sistemadeinventariog2.interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import sistemadeinventariog2.servicios.AutenticacionManager;
import sistemadeinventariog2.modelo.Usuario;
import java.util.List;

public class VentanaUsuarios extends JFrame {

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    private final String usuarioAdministrador;
    private final AutenticacionManager autenticacionManager =
            new AutenticacionManager();

    public VentanaUsuarios(String usuarioAdministrador) {

        this.usuarioAdministrador = usuarioAdministrador;

        setTitle("Administración de usuarios");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel(
                "ADMINISTRACIÓN DE USUARIOS",
                SwingConstants.CENTER
        );

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre de usuario", "Rol"},
                0
        );

        tablaUsuarios = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaUsuarios);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

// Panel de botones
        JPanel panelBotones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        JButton btnModificar = new JButton("MODIFICAR USUARIO");
        JButton btnContrasena = new JButton("CAMBIAR CONTRASEÑA");
        JButton btnEliminar = new JButton("ELIMINAR USUARIO");
        JButton btnActualizar = new JButton("ACTUALIZAR TABLA");

        panelBotones.add(btnModificar);
        panelBotones.add(btnContrasena);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        btnModificar.addActionListener(e -> modificarUsuario());
        btnContrasena.addActionListener(e -> cambiarContrasena());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnActualizar.addActionListener(e -> cargarUsuarios());

        panel.add(panelBotones, BorderLayout.SOUTH);

        add(panel);
        cargarUsuarios();
    }
    private void cargarUsuarios() {

        try {

            modeloTabla.setRowCount(0);

            List<Usuario> usuarios =
                    autenticacionManager.listarUsuarios(usuarioAdministrador);

            for (Usuario usuario : usuarios) {

                modeloTabla.addRow(new Object[]{
                        usuario.getId(),
                        usuario.getNombreUsuario(),
                        usuario.getRol()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar los usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void modificarUsuario() {

        int fila = tablaUsuarios.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un usuario de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String id = modeloTabla.getValueAt(fila, 0).toString();
        String nombreActual = modeloTabla.getValueAt(fila, 1).toString();
        String rolActual = modeloTabla.getValueAt(fila, 2).toString();

        JTextField txtNombre = new JTextField(nombreActual);

        String[] roles = {"ADMIN", "USUARIO"};

        JComboBox<String> cmbRol = new JComboBox<>(roles);
        cmbRol.setSelectedItem(rolActual);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));

        formulario.add(new JLabel("Nombre de usuario:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Rol:"));
        formulario.add(cmbRol);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Modificar usuario " + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            boolean actualizado = autenticacionManager.actualizarUsuario(
                    id,
                    txtNombre.getText(),
                    cmbRol.getSelectedItem().toString(),
                    usuarioAdministrador
            );

            if (actualizado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Usuario actualizado correctamente."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el usuario seleccionado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al modificar usuario",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void cambiarContrasena() {

        int fila = tablaUsuarios.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un usuario de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String id = modeloTabla.getValueAt(fila, 0).toString();
        String nombre = modeloTabla.getValueAt(fila, 1).toString();

        JPasswordField txtContrasena = new JPasswordField();
        JPasswordField txtConfirmar = new JPasswordField();

        JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));

        formulario.add(new JLabel("Nueva contraseña:"));
        formulario.add(txtContrasena);
        formulario.add(new JLabel("Confirmar contraseña:"));
        formulario.add(txtConfirmar);

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Cambiar contraseña de " + nombre,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        String nuevaContrasena = new String(txtContrasena.getPassword());
        String confirmarContrasena = new String(txtConfirmar.getPassword());

        if (nuevaContrasena.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña es obligatoria.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Las contraseñas no coinciden.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            boolean actualizado = autenticacionManager.actualizarContrasena(
                    id,
                    nuevaContrasena,
                    usuarioAdministrador
            );

            if (actualizado) {
                JOptionPane.showMessageDialog(
                        this,
                        "La contraseña se actualizó correctamente."
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el usuario seleccionado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al cambiar contraseña",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void eliminarUsuario() {

        int fila = tablaUsuarios.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un usuario de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String id = modeloTabla.getValueAt(fila, 0).toString();
        String nombre = modeloTabla.getValueAt(fila, 1).toString();

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de eliminar al usuario "
                        + nombre + " (" + id + ")?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean eliminado = autenticacionManager.eliminarUsuario(
                    id,
                    usuarioAdministrador
            );

            if (eliminado) {

                JOptionPane.showMessageDialog(
                        this,
                        "El usuario fue eliminado correctamente."
                );

                cargarUsuarios();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el usuario seleccionado.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error al eliminar usuario",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
