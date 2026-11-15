package sistemadeinventariog2.interfaz;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import sistemadeinventariog2.modelo.Producto;
import sistemadeinventariog2.servicios.InventarioManager;

public class VentanaProductos extends JFrame {

    // Campos del producto
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDescripcion;
    private JTextField txtCategoria;
    private JTextField txtPrecio;
    private JTextField txtCantidad;

    // Búsqueda
    private JTextField txtBuscar;
    private JComboBox<String> cmbBuscarPor;

    // Tabla
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;

    // Botones
    private JButton btnRegistrar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;
    private JButton btnRegresar;

    private final InventarioManager inventarioManager =
            new InventarioManager();

    public VentanaProductos() {

        setTitle("Gestión de Productos - Sistema de Inventario G2");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        panelPrincipal.setBackground(Color.WHITE);

        // =================================
        // TÍTULO
        // =================================

        JLabel lblTitulo = new JLabel(
                "GESTIÓN DE PRODUCTOS",
                SwingConstants.CENTER
        );

        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // =================================
        // FORMULARIO
        // =================================

        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 8, 8));
        panelFormulario.setBorder(
                BorderFactory.createTitledBorder("Datos del producto")
        );

        txtCodigo = new JTextField();
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(new Color(230, 230, 230));
        txtNombre = new JTextField();
        txtDescripcion = new JTextField();
        txtCategoria = new JTextField();
        txtPrecio = new JTextField();
        txtCantidad = new JTextField();

        panelFormulario.add(new JLabel("Código:"));
        panelFormulario.add(txtCodigo);

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Descripción:"));
        panelFormulario.add(txtDescripcion);

        panelFormulario.add(new JLabel("Categoría:"));
        panelFormulario.add(txtCategoria);

        panelFormulario.add(new JLabel("Precio:"));
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Cantidad:"));
        panelFormulario.add(txtCantidad);

        btnRegistrar = new JButton("Registrar");
        btnModificar = new JButton("Modificar");


        panelFormulario.add(btnRegistrar);
        panelFormulario.add(btnModificar);

        // =================================
        // BÚSQUEDA
        // =================================

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelBusqueda.setBorder(
                BorderFactory.createTitledBorder("Buscar productos")
        );

        cmbBuscarPor = new JComboBox<>(
                new String[]{"Código", "Nombre", "Categoría"}
        );

        txtBuscar = new JTextField(18);

        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProducto());

        btnMostrarTodos = new JButton("Mostrar todos");


        panelBusqueda.add(new JLabel("Buscar por:"));
        panelBusqueda.add(cmbBuscarPor);
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);

        // =================================
        // TABLA DE PRODUCTOS
        // =================================

        String[] columnas = {
                "Código",
                "Nombre",
                "Descripción",
                "Categoría",
                "Precio",
                "Cantidad"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);

        scrollTabla.setBorder(
                BorderFactory.createTitledBorder("Inventario de productos")

        );
        // Cargar datos al seleccionar un producto de la tabla
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                cargarProductoSeleccionado();
            }

        });


        // =================================
        // BOTONES INFERIORES
        // =================================

        JPanel panelInferior = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnLimpiar = new JButton("Limpiar campos");
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnRegresar = new JButton("Regresar al menú");
        btnRegresar.addActionListener(e -> dispose());

        panelInferior.add(btnEliminar);
        panelInferior.add(btnLimpiar);
        panelInferior.add(btnRegresar);

        // =================================
        // ORGANIZAR PANELES
        // =================================

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

        panelCentral.add(panelFormulario, BorderLayout.WEST);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        panelCentral.add(panelBusqueda, BorderLayout.NORTH);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Mostrar todos los productos
        btnMostrarTodos.addActionListener(
                e -> cargarProductos()
        );

// Registrar producto
        btnRegistrar.addActionListener(
                e -> registrarProducto()
        );
        // Modificar producto
        btnModificar.addActionListener(
                e -> modificarProducto()
        );

// Cargar productos al abrir la ventana
        cargarProductos();
    }
    private void cargarProductos() {

        try {

            // Limpiar la tabla
            modeloTabla.setRowCount(0);

            // Obtener productos desde MongoDB
            for (Producto producto : inventarioManager.listarProductos()) {

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

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void buscarProducto() {

        try {

            String criterio = cmbBuscarPor.getSelectedItem().toString();
            String valor = txtBuscar.getText().trim();

            if (valor.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debes escribir un dato para buscar.",
                        "Campo vacío",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            modeloTabla.setRowCount(0);

            if (criterio.equals("Código")) {

                Producto producto = inventarioManager.consultarProducto(valor);

                agregarProductoTabla(producto);

            } else if (criterio.equals("Nombre")) {

                for (Producto producto : inventarioManager.buscarPorNombre(valor)) {
                    agregarProductoTabla(producto);
                }

            } else if (criterio.equals("Categoría")) {

                for (Producto producto : inventarioManager.buscarPorCategoria(valor)) {
                    agregarProductoTabla(producto);
                }
            }

            if (modeloTabla.getRowCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No se encontraron productos.",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Búsqueda",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al buscar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void agregarProductoTabla(Producto producto) {

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
    private void cargarProductoSeleccionado() {

        int fila = tablaProductos.getSelectedRow();

        if (fila != -1) {

            txtCodigo.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtDescripcion.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtCategoria.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtPrecio.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtCantidad.setText(modeloTabla.getValueAt(fila, 5).toString());

            // Bloquear la cantidad al seleccionar un producto
            txtCantidad.setEditable(false);
        }
    }
    private void registrarProducto() {
        // Habilitar cantidad para registrar un producto nuevo
        txtCantidad.setEditable(true);

        try {

            // Obtener los datos de los campos
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String categoria = txtCategoria.getText().trim();

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );

            int cantidad = Integer.parseInt(
                    txtCantidad.getText().trim()
            );

            // Registrar el producto mediante InventarioManager
            Producto producto = inventarioManager.registrarProducto(
                    nombre,
                    descripcion,
                    categoria,
                    precio,
                    cantidad
            );

            // Mostrar el código generado automáticamente
            txtCodigo.setText(producto.getCodigo());

            // Actualizar la tabla
            cargarProductos();

            // Mostrar confirmación
            JOptionPane.showMessageDialog(
                    this,
                    "Producto registrado correctamente.\n" +
                            "Código generado: " + producto.getCodigo(),
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "El precio debe ser decimal y la cantidad debe ser un número entero.",
                    "Datos incorrectos",
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
                    "No se pudo registrar el producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void limpiarCampos() {

        // Limpiar todos los campos
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");

        // Habilitar nuevamente la cantidad
        txtCantidad.setEditable(true);

        // Quitar selección de la tabla
        tablaProductos.clearSelection();

        // Colocar el cursor en el nombre
        txtNombre.requestFocus();
    }
    private void modificarProducto() {

        try {

            // Obtener el código del producto seleccionado
            String codigo = txtCodigo.getText().trim();

            // Comprobar que exista un producto seleccionado
            if (codigo.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debes seleccionar un producto de la tabla.",
                        "Producto no seleccionado",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Obtener los nuevos datos
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String categoria = txtCategoria.getText().trim();

            double precio = Double.parseDouble(
                    txtPrecio.getText().trim()
            );


            // Modificar el producto mediante InventarioManager
            boolean actualizado = inventarioManager.modificarProducto(
                    codigo,
                    nombre,
                    descripcion,
                    categoria,
                    precio
            );
            if (actualizado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Producto modificado correctamente.",
                        "Modificación exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // Actualizar la tabla
                cargarProductos();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudieron guardar los cambios.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "La cantidad debe ser un número entero.",
                    "Datos incorrectos",
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
                    "No se pudo modificar el producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }
    private void eliminarProducto() {

        try {

            String codigo = txtCodigo.getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debes seleccionar un producto de la tabla.",
                        "Producto no seleccionado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de eliminar el producto " + codigo + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta == JOptionPane.YES_OPTION) {

                boolean eliminado = inventarioManager.eliminarProducto(codigo);

                if (eliminado) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Producto eliminado correctamente.",
                            "Eliminación exitosa",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    limpiarCampos();
                    cargarProductos();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar el producto.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se puede eliminar",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al eliminar el producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}
