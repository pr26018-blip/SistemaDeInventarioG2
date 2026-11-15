
package sistemadeinventariog2.interfaz;

/**
 * Desarrollado por: Maria
 * Descrpción: interfaz gráfica encargada de visualizar y gestionar
 * la exportación de reportes del inventario a formatos Excel y PDF */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

import sistemadeinventariog2.modelo.Producto;
import sistemadeinventariog2.servicios.ReportesManager;
import sistemadeinventariog2.reportes.GeneradorExcel;
import sistemadeinventariog2.reportes.GeneradorPDF;

public class VentanaReportes extends JFrame {

    private JComboBox<String> cmbTipoReporte;
    private JTextField txtMinimo;
    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;
    private JLabel lblResultado;

    private JButton btnGenerar;
    private JButton btnExportarExcel;
    private JButton btnExportarPDF;
    private JButton btnRegresar;

    private boolean reporteGenerado = false;

    private final ReportesManager reportesManager =
            new ReportesManager();

    public VentanaReportes() {

        setTitle("Reportes de Inventario");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Panel superior
        JPanel panelSuperior = new JPanel(new FlowLayout());

        cmbTipoReporte = new JComboBox<>(new String[]{
                "Inventario general",
                "Productos sin existencias",
                "Existencias bajas",
                "Valor total del inventario"
        });

        txtMinimo = new JTextField(5);
        txtMinimo.setText("5");

        btnGenerar = new JButton("Generar reporte");

        panelSuperior.add(new JLabel("Tipo de reporte:"));
        panelSuperior.add(cmbTipoReporte);
        panelSuperior.add(new JLabel("Mínimo de existencias:"));
        panelSuperior.add(txtMinimo);
        panelSuperior.add(btnGenerar);

        // Tabla
        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "Código",
                        "Nombre",
                        "Descripción",
                        "Categoría",
                        "Precio",
                        "Cantidad"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaReportes = new JTable(modeloTabla);

        JScrollPane scrollTabla = new JScrollPane(tablaReportes);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());

        lblResultado = new JLabel("Selecciona un reporte.");

        btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarExcel.setEnabled(false);

        btnExportarPDF = new JButton("Exportar a PDF");
        btnExportarPDF.setEnabled(false);

        btnRegresar = new JButton("Regresar al menú");

        JPanel panelBotones = new JPanel(new FlowLayout());

        panelBotones.add(btnExportarExcel);
        panelBotones.add(btnExportarPDF);
        panelBotones.add(btnRegresar);

        panelInferior.add(lblResultado, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);

        // Eventos
        btnGenerar.addActionListener(e -> generarReporte());

        btnExportarExcel.addActionListener(e -> exportarExcel());

        btnExportarPDF.addActionListener(e -> exportarPDF());

        btnRegresar.addActionListener(e -> dispose());

        // Agregar componentes
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void generarReporte() {

        try {

            modeloTabla.setRowCount(0);

            reporteGenerado = false;
            btnExportarExcel.setEnabled(false);
            btnExportarPDF.setEnabled(false);

            String tipo = cmbTipoReporte.getSelectedItem().toString();

            if (tipo.equals("Inventario general")) {

                List<Producto> productos =
                        reportesManager.obtenerInventarioGeneral();

                mostrarProductos(productos);

                lblResultado.setText(
                        "Total de productos: " + productos.size()
                );

            } else if (tipo.equals("Productos sin existencias")) {

                List<Producto> productos =
                        reportesManager.obtenerProductosSinExistencias();

                mostrarProductos(productos);

                lblResultado.setText(
                        productos.isEmpty()
                                ? "No hay productos sin existencias."
                                : "Productos sin existencias: " + productos.size()
                );

            } else if (tipo.equals("Existencias bajas")) {

                String textoMinimo = txtMinimo.getText().trim();

                if (textoMinimo.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Debes ingresar el mínimo de existencias.",
                            "Campo obligatorio",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int minimo = Integer.parseInt(textoMinimo);

                List<Producto> productos =
                        reportesManager.obtenerProductosConExistenciasBajas(
                                minimo
                        );

                mostrarProductos(productos);

                lblResultado.setText(
                        "Productos con existencias bajas: " + productos.size()
                );

            } else if (tipo.equals("Valor total del inventario")) {

                double total =
                        reportesManager.calcularValorTotalInventario();

                List<Producto> productos =
                        reportesManager.obtenerInventarioGeneral();

                mostrarProductos(productos);

                lblResultado.setText(
                        String.format(
                                "Valor total del inventario: $%.2f || Total de artículos: %d",
                                total,
                                productos.size()
                        )
                );
            }

            // El reporte se generó correctamente
            reporteGenerado = true;
            btnExportarExcel.setEnabled(true);
            btnExportarPDF.setEnabled(true);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El mínimo debe ser un número entero.",
                    "Dato incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar el reporte: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void mostrarProductos(List<Producto> productos) {

        for (Producto producto : productos) {

            Object[] fila = {
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getCategoria(),
                    producto.getPrecio(),
                    producto.getCantidad()
            };

            modeloTabla.addRow(fila);
        }
    }

    // Exportar reporte a Excel
    private void exportarExcel() {

        if (!reporteGenerado) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes generar un reporte.",
                    "Reporte no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar reporte de Excel");
        selector.setSelectedFile(
                new File("Reporte_Inventario.xlsx")
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {

            File archivo = selector.getSelectedFile();

            // Agregar extensión .xlsx si no fue escrita
            if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {

                archivo = new File(
                        archivo.getParentFile(),
                        archivo.getName() + ".xlsx"
                );
            }

            // Confirmar si el archivo ya existe
            if (archivo.exists()) {

                int confirmar = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo ya existe. ¿Deseas reemplazarlo?",
                        "Confirmar reemplazo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmar != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {

                GeneradorExcel.generarReporte(
                        tablaReportes,
                        cmbTipoReporte.getSelectedItem().toString(),
                        lblResultado.getText(),
                        archivo.getAbsolutePath()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "El reporte Excel se generó correctamente.",
                        "Exportación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al exportar el reporte: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // Exportar reporte a PDF
    private void exportarPDF() {

        if (!reporteGenerado) {
            JOptionPane.showMessageDialog(
                    this,
                    "Primero debes generar un reporte.",
                    "Reporte no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser selector = new JFileChooser();

        selector.setDialogTitle("Guardar reporte en PDF");
        selector.setSelectedFile(
                new File("Reporte_Inventario.pdf")
        );

        int resultado = selector.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {

            File archivo = selector.getSelectedFile();

            // Agregar extensión .pdf si no fue escrita
            if (!archivo.getName().toLowerCase().endsWith(".pdf")) {

                archivo = new File(
                        archivo.getParentFile(),
                        archivo.getName() + ".pdf"
                );
            }

            // Confirmar si el archivo ya existe
            if (archivo.exists()) {

                int confirmar = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo ya existe. ¿Deseas reemplazarlo?",
                        "Confirmar reemplazo",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmar != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {

                GeneradorPDF.generarReporte(
                        tablaReportes,
                        cmbTipoReporte.getSelectedItem().toString(),
                        lblResultado.getText(),
                        archivo.getAbsolutePath()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "El reporte PDF se generó correctamente.",
                        "Exportación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error al exportar el reporte PDF: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}


