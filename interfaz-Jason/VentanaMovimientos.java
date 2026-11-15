package sistemadeinventariog2.interfaz;

import javax.swing.*;
import java.awt.*;
import sistemadeinventariog2.servicios.InventarioManager;

public class VentanaMovimientos extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtCantidad;
    private JComboBox<String> cmbTipo;

    private final InventarioManager inventarioManager =
            new InventarioManager();

    public VentanaMovimientos() {

        setTitle("Movimientos de Inventario");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // Código del producto
        panel.add(new JLabel("Código del producto:"));

        txtCodigo = new JTextField();
        panel.add(txtCodigo);

        // Tipo de movimiento
        panel.add(new JLabel("Tipo de movimiento:"));

        cmbTipo = new JComboBox<>(
                new String[]{"ENTRADA", "SALIDA"}
        );

        panel.add(cmbTipo);

        // Cantidad
        panel.add(new JLabel("Cantidad:"));

        txtCantidad = new JTextField();
        panel.add(txtCantidad);

        // Botón registrar
        JButton btnRegistrar = new JButton("Registrar movimiento");

        panel.add(new JLabel());
        panel.add(btnRegistrar);

        btnRegistrar.addActionListener(
                e -> registrarMovimiento()
        );

        add(panel);
    }

    private void registrarMovimiento() {

        try {

            String codigo = txtCodigo.getText().trim();
            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debes ingresar el código del producto.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String tipo = cmbTipo.getSelectedItem().toString();

            String textoCantidad = txtCantidad.getText().trim();

            if (textoCantidad.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debes ingresar la cantidad del movimiento.",
                        "Campo obligatorio",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int cantidad = Integer.parseInt(textoCantidad);

            inventarioManager.registrarMovimiento(
                    codigo,
                    tipo,
                    cantidad
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Movimiento registrado correctamente.",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            txtCodigo.setText("");
            txtCantidad.setText("");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe ser un número entero.",
                    "Dato incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error de validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar el movimiento: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}
