
package sistemadeinventariog2.reportes;

/**
 * Desarrollado por: Maria
 * Descripción: Clase encargada de generar y exportar los reportes 
 * del sistema de inventario a un archivo PDF utilizando OpenPDF.
 */

import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Font;
import org.openpdf.text.Element;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPCell;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GeneradorPDF {

    public static void generarReporte(
            JTable tabla,
            String titulo,
            String resumen,
            String rutaArchivo) throws Exception {

        // Crear documento PDF en orientación horizontal
        Document documento = new Document(
                PageSize.A4.rotate(),
                25, 25, 30, 30
        );

        try (FileOutputStream archivo =
                     new FileOutputStream(rutaArchivo)) {

            PdfWriter.getInstance(documento, archivo);

            documento.open();

            // Fuente del título
            Font fuenteTitulo = new Font(
                    Font.HELVETICA,
                    16,
                    Font.BOLD
            );

            // Fuente de encabezados
            Font fuenteEncabezado = new Font(
                    Font.HELVETICA,
                    9,
                    Font.BOLD
            );

            // Fuente de datos
            Font fuenteDatos = new Font(
                    Font.HELVETICA,
                    8,
                    Font.NORMAL
            );

            // Título del reporte
            Paragraph parrafoTitulo = new Paragraph(
                    titulo,
                    fuenteTitulo
            );

            parrafoTitulo.setAlignment(Element.ALIGN_CENTER);
            parrafoTitulo.setSpacingAfter(10);

            documento.add(parrafoTitulo);

            // Fecha de generación
            String fecha = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm"
                    )
            );

            Paragraph parrafoFecha = new Paragraph(
                    "Generado: " + fecha,
                    fuenteDatos
            );

            parrafoFecha.setSpacingAfter(15);

            documento.add(parrafoFecha);

            // Obtener datos de la tabla
            TableModel modelo = tabla.getModel();

            int columnas = modelo.getColumnCount();

            // Crear tabla PDF
            PdfPTable tablaPDF = new PdfPTable(columnas);

            tablaPDF.setWidthPercentage(100);

            tablaPDF.setWidths(new float[]{
                    1.1f, 2.0f, 3.0f,
                    1.7f, 1.2f, 1.0f
            });

            // Encabezados
            for (int columna = 0;
                 columna < columnas;
                 columna++) {

                PdfPCell celda = new PdfPCell(
                        new Phrase(
                                modelo.getColumnName(columna),
                                fuenteEncabezado
                        )
                );

                celda.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                celda.setPadding(5);

                tablaPDF.addCell(celda);
            }

            // Filas de datos
            for (int fila = 0;
                 fila < modelo.getRowCount();
                 fila++) {

                for (int columna = 0;
                     columna < columnas;
                     columna++) {

                    Object valor = modelo.getValueAt(
                            fila,
                            columna
                    );

                    String texto = "";

                    if (valor != null) {

                        // Formato monetario para la columna Precio
                        if (columna == 4 && valor instanceof Number numero) {

                            texto = String.format(
                                    Locale.US,
                                    "$%.2f",
                                    numero.doubleValue()
                            );

                        } else {

                            texto = valor.toString();
                        }
                    }

                    PdfPCell celda = new PdfPCell(
                            new Phrase(texto, fuenteDatos)
                    );

                    celda.setPadding(5);

                    tablaPDF.addCell(celda);
                }
            }

            documento.add(tablaPDF);

            // Resumen del reporte
            Paragraph parrafoResumen = new Paragraph(
                    "\n" + resumen,
                    fuenteEncabezado
            );

            parrafoResumen.setSpacingBefore(10);

            documento.add(parrafoResumen);

            // Cerrar documento
            documento.close();
        }
    }
}

