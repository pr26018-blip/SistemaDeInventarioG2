package sistemadeinventariog2.interfaz;

import javax.swing.*;
import java.awt.*;

import sistemadeinventariog2.servicios.AutenticacionManager;

public class VentanaRegistroUsuario extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JComboBox<String> cmbRol;

    private JButton btnRegistrar;
    private JButton btnLimpiar;
    private JButton btnRegresar;
    private String usuarioSolicitante;

    private final AutenticacionManager autenticacion =
            new AutenticacionManager();

    public VentanaRegistroUsuario(String usuarioSolicitante) {

        this.usuarioSolicitante = usuarioSolicitante;

        setTitle("Registro de usuarios");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("REGISTRO DE USUARIOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBounds(90, 25, 300, 30);

        JLabel lblUsuario = new JLabel("Nombre de usuario:");
        lblUsuario.setBounds(50, 85, 150, 25);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 110, 330, 30);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 155, 150, 25);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(50, 180, 330, 30);

        JLabel lblRol = new JLabel("Rol del usuario:");
        lblRol.setBounds(50, 225, 150, 25);

        cmbRol = new JComboBox<>(
                new String[]{"USUARIO", "ADMIN"}
        );
        cmbRol.setBounds(50, 250, 330, 30);

        btnRegistrar = new JButton("REGISTRAR");
        btnRegistrar.setBounds(40, 310, 120, 35);
        btnRegistrar.addActionListener(e -> registrarUsuario());

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setBounds(165, 310, 120, 35);
        btnLimpiar.addActionListener(e -> limpiarCampos());

        btnRegresar = new JButton("REGRESAR");
        btnRegresar.setBounds(290, 310, 120, 35);
        btnRegresar.addActionListener(e -> dispose());

        panel.add(lblTitulo);
        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(lblRol);
        panel.add(cmbRol);
        panel.add(btnRegistrar);
        panel.add(btnLimpiar);
        panel.add(btnRegresar);

        add(panel);
    }

    private void registrarUsuario() {

        String nombreUsuario = txtUsuario.getText().trim();

        String contrasena = new String(
                txtContrasena.getPassword()
        );
        if (contrasena.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña es obligatoria.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String rol = (String) cmbRol.getSelectedItem();

        if (rol == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un rol.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {

            autenticacion.registrarUsuario(
                    nombreUsuario,
                    contrasena,
                    rol,
                    usuarioSolicitante
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarCampos();

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error de registro",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar usuario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {

        txtUsuario.setText("");
        txtContrasena.setText("");
        cmbRol.setSelectedIndex(0);

    }
}