package sistemadeinventariog2.interfaz;

import javax.swing.*;
import java.awt.*;
import sistemadeinventariog2.servicios.AutenticacionManager;
import sistemadeinventariog2.modelo.Usuario;

public class VentanaLogin extends JFrame {

    // Componentes de la ventana
    private JLabel lblTitulo;
    private JLabel lblUsuario;
    private JLabel lblContrasena;
    private final AutenticacionManager autenticacion =
            new AutenticacionManager();

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;

    private JButton btnIniciarSesion;

    public VentanaLogin() {

        // Configuración de la ventana
        setTitle("Sistema de Inventario G2");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Crear panel principal
        JPanel panel = new JPanel();

        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Título
        lblTitulo = new JLabel("SISTEMA DE INVENTARIO G2");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(55, 35, 300, 30);

        // Etiqueta usuario
        lblUsuario = new JLabel("Nombre de usuario:");
        lblUsuario.setBounds(50, 100, 150, 25);

        // Campo usuario
        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 125, 290, 30);

        // Etiqueta contraseña
        lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 170, 150, 25);

        // Campo contraseña
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(50, 195, 290, 30);

        // Botón iniciar sesión
        btnIniciarSesion = new JButton("Iniciar sesión");
        btnIniciarSesion.setBounds(110, 250, 180, 35);
        btnIniciarSesion.addActionListener(e -> iniciarSesion());

        // Agregar componentes al panel
        panel.add(lblTitulo);
        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContrasena);
        panel.add(txtContrasena);
        panel.add(btnIniciarSesion);

        // Agregar panel a la ventana
        add(panel);
    }
    private void iniciarSesion() {

        String nombreUsuario = txtUsuario.getText().trim();

        String contrasena = new String(
                txtContrasena.getPassword()
        );

        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes ingresar el usuario y la contraseña.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Usuario usuario = autenticacion.autenticar(
                nombreUsuario,
                contrasena
        );

        if (usuario != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "¡Bienvenido al Sistema de Inventario G2!",
                    "Acceso correcto",
                    JOptionPane.INFORMATION_MESSAGE
            );

            VentanaPrincipal ventana = new VentanaPrincipal(
                    usuario.getNombreUsuario(),
                    usuario.getRol()
            );

            ventana.setVisible(true);

            this.dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Usuario o contraseña incorrectos.",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE
            );

            txtContrasena.setText("");
            txtUsuario.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VentanaLogin ventana = new VentanaLogin();

            ventana.setVisible(true);

        });
    }
}
