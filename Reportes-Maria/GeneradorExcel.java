
package sistemadeinventariog2.reportes;

/**
 * Desarrollado por: Maria
 * Descripción: Clase encargada de generar y exportar los reportes 
 * del sistema de inventario a un archivo Excel 
 */

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorExcel {

    public static void generarReporte(
            JTable tabla,
            String titulo,
            String resumen,
            String rutaArchivo) throws IOException {

        try (Workbook libro = new XSSFWorkbook()) {

            Sheet hoja = libro.createSheet("Reporte");

            // Estilo del título
            CellStyle estiloTitulo = libro.createCellStyle();
            Font fuenteTitulo = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            estiloTitulo.setFont(fuenteTitulo);

            // Estilo de encabezados
            CellStyle estiloEncabezado = libro.createCellStyle();
            Font fuenteEncabezado = libro.createFont();
            fuenteEncabezado.setBold(true);
            estiloEncabezado.setFont(fuenteEncabezado);

            // Estilo para precios
            CellStyle estiloPrecio = libro.createCellStyle();
            DataFormat formato = libro.createDataFormat();
            estiloPrecio.setDataFormat(
                    formato.getFormat("$#,##0.00")
            );

            // Título
            Row filaTitulo = hoja.createRow(0);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue(titulo);
            celdaTitulo.setCellStyle(estiloTitulo);

            // Fecha de generación
            Row filaFecha = hoja.createRow(1);
            filaFecha.createCell(0).setCellValue(
                    "Generado: " +
                            LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy HH:mm"
                                    )
                            )
            );

            // Encabezados
            TableModel modelo = tabla.getModel();
            Row filaEncabezados = hoja.createRow(3);

            for (int columna = 0;
                 columna < modelo.getColumnCount();
                 columna++) {

                Cell celda = filaEncabezados.createCell(columna);

                celda.setCellValue(
                        modelo.getColumnName(columna)
                );

                celda.setCellStyle(estiloEncabezado);
            }

            // Datos de la tabla
            for (int fila = 0;
                 fila < modelo.getRowCount();
                 fila++) {

                Row nuevaFila = hoja.createRow(fila + 4);

                for (int columna = 0;
                     columna < modelo.getColumnCount();
                     columna++) {

                    Cell celda = nuevaFila.createCell(columna);

                    Object valor = modelo.getValueAt(
                            fila, columna
                    );

                    if (valor instanceof Number numero) {

                        celda.setCellValue(numero.doubleValue());

                        // La columna Precio es la número 4
                        if (columna == 4) {
                            celda.setCellStyle(estiloPrecio);
                        }

                    } else {

                        celda.setCellValue(
                                valor != null ? valor.toString() : ""
                        );
                    }
                }
            }

            // Resumen del reporte
            int filaResumen = modelo.getRowCount() + 5;

            Row nuevaFilaResumen = hoja.createRow(filaResumen);

            nuevaFilaResumen.createCell(0).setCellValue(
                    resumen != null ? resumen : ""
            );

            // Ajustar ancho de columnas
            for (int columna = 0;
                 columna < modelo.getColumnCount();
                 columna++) {

                hoja.autoSizeColumn(columna);
            }

            // Guardar archivo Excel
            try (FileOutputStream archivo =
                         new FileOutputStream(rutaArchivo)) {

                libro.write(archivo);
            }
        }
    }
}

