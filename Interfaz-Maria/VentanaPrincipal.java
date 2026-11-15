package sistemadeinventariog2.interfaz;

/**
 * Desarrollado por: Maria
 * Descrpción: interfaz gráfica que representa el menú principal del sistema de inventario
 * permitiendo la navegación hacia los modulos de productos, movimientos, reportes y gestión de usuarios */

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblBienvenida;

    private JButton btnProductos;
    private JButton btnMovimientos;
    private JButton btnReportes;
    private JButton btnCerrarSesion;
    private JButton btnHistorial;
    private JButton btnRegistrarUsuario;
    private JButton btnAdministrarUsuarios;

    private String nombreUsuario;
    private String rolUsuario;

    public VentanaPrincipal(String nombreUsuario, String rolUsuario) {

        this.nombreUsuario = nombreUsuario;
        this.rolUsuario = rolUsuario;

        // Configuración de la ventana
        setTitle("Sistema de Inventario G2");
        setSize(600, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Título
        // Nombre del sistema
        lblTitulo = new JLabel(
                "SISTEMA DE INVENTARIO G2",
                SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBounds(30, 20, 540, 40);

// Mensaje de bienvenida
        lblBienvenida = new JLabel(
                "Bienvenido, " + nombreUsuario + " (" + rolUsuario + ")",
                SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        lblBienvenida.setBounds(30, 70, 540, 35);


        // Botón productos
        btnProductos = new JButton("GESTIÓN DE PRODUCTOS");
        btnProductos.setBounds(150, 130, 300, 45);
        btnProductos.addActionListener(e -> {
            VentanaProductos ventana = new VentanaProductos();
            ventana.setVisible(true);
        });

        // Botón movimientos
        btnMovimientos = new JButton("MOVIMIENTOS DE INVENTARIO");
        btnMovimientos.setBounds(150, 195, 300, 45);
        btnMovimientos.addActionListener(e -> {
            VentanaMovimientos ventana = new VentanaMovimientos();
            ventana.setVisible(true);
        });

        // Botón reportes
        btnReportes = new JButton("REPORTES");
        btnReportes.setBounds(150, 260, 300, 45);
        btnReportes.addActionListener(e -> {
            VentanaReportes ventana = new VentanaReportes();
            ventana.setVisible(true);
        });

        // Boton de historial de movimientos
        btnHistorial = new JButton("HISTORIAL DE MOVIMIENTOS");
        btnHistorial.setBounds(150, 325, 300, 45);

        btnHistorial.addActionListener(e -> {
            VentanaHistorialMovimientos ventana =
                    new VentanaHistorialMovimientos();

            ventana.setVisible(true);
        });

        // Botón registrar usuarios (solo ADMIN)
        if ("ADMIN".equalsIgnoreCase(rolUsuario)) {

            btnRegistrarUsuario = new JButton("REGISTRAR USUARIOS");
            btnRegistrarUsuario.setBounds(150, 390, 300, 40);

            btnRegistrarUsuario.addActionListener(e -> {
                VentanaRegistroUsuario ventana =
                        new VentanaRegistroUsuario(nombreUsuario);

                ventana.setVisible(true);
            });

            panel.add(btnRegistrarUsuario);
            btnAdministrarUsuarios = new JButton("ADMINISTRAR USUARIOS");
            btnAdministrarUsuarios.setBounds(150, 440, 300, 40);

            btnAdministrarUsuarios.addActionListener(e -> {

                VentanaUsuarios ventana =
                        new VentanaUsuarios(nombreUsuario);

                ventana.setVisible(true);
            });

            panel.add(btnAdministrarUsuarios);
        }

        // Botón cerrar sesión
        btnCerrarSesion = new JButton("CERRAR SESIÓN");
        btnCerrarSesion.setBounds(150, 510, 300, 40);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        // Agregar componentes
        panel.add(lblTitulo);
        panel.add(lblBienvenida);
        panel.add(btnProductos);
        panel.add(btnMovimientos);
        panel.add(btnReportes);
        panel.add(btnHistorial);
        panel.add(btnCerrarSesion);

        // Agregar panel a la ventana
        add(panel);
    }
    private void cerrarSesion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            VentanaLogin login = new VentanaLogin();

            login.setVisible(true);

            this.dispose();
        }
    }


}
