package sistemadeinventariog2.interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import sistemadeinventariog2.modelo.Movimiento;
import sistemadeinventariog2.servicios.InventarioManager;

public class VentanaHistorialMovimientos extends JFrame {

    private JTable tablaMovimientos;
    private DefaultTableModel modeloTabla;

    private JButton btnActualizar;
    private JButton btnRegresar;

    private JLabel lblTotal;

    private final InventarioManager inventarioManager =
            new InventarioManager();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public VentanaHistorialMovimientos() {

        setTitle("Historial de Movimientos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Título
        JLabel lblTitulo = new JLabel(
                "HISTORIAL DE MOVIMIENTOS",
                SwingConstants.CENTER
        );

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));

        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Código del producto",
                        "Tipo de movimiento",
                        "Cantidad",
                        "Fecha"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaMovimientos = new JTable(modeloTabla);

        JScrollPane scrollTabla = new JScrollPane(tablaMovimientos);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Total de movimientos: 0");

        JPanel panelBotones = new JPanel(new FlowLayout());

        btnActualizar = new JButton("Actualizar");
        btnRegresar = new JButton("Regresar");

        panelBotones.add(btnActualizar);
        panelBotones.add(btnRegresar);

        panelInferior.add(lblTotal, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        // Eventos
        btnActualizar.addActionListener(e -> cargarMovimientos());

        btnRegresar.addActionListener(e -> dispose());

        // Agregar componentes
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Cargar historial al abrir
        cargarMovimientos();
    }

    private void cargarMovimientos() {

        try {

            modeloTabla.setRowCount(0);

            List<Movimiento> movimientos =
                    inventarioManager.obtenerHistorialMovimientos();

            for (Movimiento movimiento : movimientos) {

                Object[] fila = {
                        movimiento.getCodigoProducto(),
                        movimiento.getTipo(),
                        movimiento.getCantidad(),
                        movimiento.getFecha().format(formatoFecha)
                };

                modeloTabla.addRow(fila);
            }

            lblTotal.setText(
                    movimientos.isEmpty()
                            ? "No hay movimientos registrados."
                            : "Total de movimientos: " + movimientos.size()
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar el historial: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}
