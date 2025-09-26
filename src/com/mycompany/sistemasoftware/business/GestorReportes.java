package com.mycompany.sistemasoftware.business;

// Imports de iTextPDF
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Imports de modelos
import com.mycompany.sistemasoftware.model.ModeloCliente;
import com.mycompany.sistemasoftware.model.ModeloEmpresa;
import com.mycompany.sistemasoftware.model.ModeloDatosPdfVentaDTO;
import com.mycompany.sistemasoftware.model.ModeloDetalleVenta;
import com.mycompany.sistemasoftware.model.ModeloProductoSimple;
import com.mycompany.sistemasoftware.model.ModeloVentaVendedorDTO;

// Imports de Java y otras librerías
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

// PATRÓN SINGLETON
public class GestorReportes {

    private static GestorReportes instancia;

    private GestorReportes() {}

    public static synchronized GestorReportes getInstance() {
        if (instancia == null) {
            instancia = new GestorReportes();
        }
        return instancia;
    }

    public void generarReporteProductosExcel() {
        // ... (Este método ya estaba bien, no necesita cambios) ...
        List<ModeloProductoSimple> productos = GestorProducto.getInstance().listarProductosParaReporte();

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay productos para generar el reporte.");
            return;
        }

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Productos");

        try {
            // --- Lógica de formato del Excel (sin cambios) ---
            InputStream is = new FileInputStream("src/imagenes/logoC.png");
            byte[] bytes = IOUtils.toByteArray(is);
            int imgIndex = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            is.close();
 
            CreationHelper help = book.getCreationHelper();
            Drawing draw = sheet.createDrawingPatriarch();
 
            ClientAnchor anchor = help.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(1);
            Picture pict = draw.createPicture(anchor, imgIndex);
            pict.resize(1, 3);
 
            CellStyle tituloEstilo = book.createCellStyle();
            tituloEstilo.setAlignment(HorizontalAlignment.CENTER);
            tituloEstilo.setVerticalAlignment(VerticalAlignment.CENTER);
            org.apache.poi.ss.usermodel.Font fuenteTitulo = book.createFont();
            fuenteTitulo.setFontName("Arial");
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            tituloEstilo.setFont(fuenteTitulo);
 
            Row filaTitulo = sheet.createRow(1);
            Cell celdaTitulo = filaTitulo.createCell(1);
            celdaTitulo.setCellStyle(tituloEstilo);
            celdaTitulo.setCellValue("Reporte de Productos");
 
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 3));
 
            String[] cabecera = new String[]{"Código", "Nombre", "Precio", "Existencia"};
 
            CellStyle headerStyle = book.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
 
            org.apache.poi.ss.usermodel.Font font = book.createFont();
            font.setFontName("Arial");
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 12);
            headerStyle.setFont(font);
 
            Row filaEncabezados = sheet.createRow(4);
 
            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEnzabezado = filaEncabezados.createCell(i);
                celdaEnzabezado.setCellStyle(headerStyle);
                celdaEnzabezado.setCellValue(cabecera[i]);
            }

            // --- Llenado de datos (ahora desde la lista) ---
            int numFilaDatos = 5;
            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderBottom(BorderStyle.THIN);

            for (ModeloProductoSimple producto : productos) {
                Row filaDatos = sheet.createRow(numFilaDatos++);
                
                Cell celdaCodigo = filaDatos.createCell(0);
                celdaCodigo.setCellStyle(datosEstilo);
                celdaCodigo.setCellValue(producto.getCodigo());

                Cell celdaNombre = filaDatos.createCell(1);
                celdaNombre.setCellStyle(datosEstilo);
                celdaNombre.setCellValue(producto.getNombre());

                Cell celdaPrecio = filaDatos.createCell(2);
                celdaPrecio.setCellStyle(datosEstilo);
                celdaPrecio.setCellValue(producto.getPrecio());

                Cell celdaStock = filaDatos.createCell(3);
                celdaStock.setCellStyle(datosEstilo);
                celdaStock.setCellValue(producto.getStock());
            }

            // --- Finalización del archivo (sin cambios) ---
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            
            sheet.setZoom(150);
            String fileName = "productos";
            String home = System.getProperty("user.home");
            File file = new File(home + "/Downloads/" + fileName + ".xlsx");
            FileOutputStream fileOut = new FileOutputStream(file);
            book.write(fileOut);
            fileOut.close();
            Desktop.getDesktop().open(file);
            JOptionPane.showMessageDialog(null, "Reporte Generado en Descargas.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al generar el reporte Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generarGraficoVentasDelDia(String fecha) {
        // ... (Este método ya estaba bien, no necesita cambios) ...
        List<ModeloVentaVendedorDTO> datosGrafico = GestorVenta.getInstance().listarVentasPorVendedorParaGrafico(fecha);

        if (datosGrafico.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontraron ventas para la fecha seleccionada.");
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (ModeloVentaVendedorDTO dato : datosGrafico) {
            dataset.setValue(dato.getVendedor(), dato.getTotalVendido());
        }

        JFreeChart jf = ChartFactory.createPieChart("Reporte de Ventas por Vendedor (" + fecha + ")", dataset);
        ChartFrame f = new ChartFrame("Total de Ventas por Vendedor", jf);
        f.setSize(1000, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public void generarPdfVenta(ModeloDatosPdfVentaDTO datos) {
        try {
            FileOutputStream archivo;
            File file = new File("src/pdf/venta" + datos.getIdVenta() + ".pdf");
            archivo = new FileOutputStream(file);
            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            // --- Encabezado ---
            Image img = Image.getInstance("src/imagenes/logo_pdf.png");
            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            fecha.add(Chunk.NEWLINE);
            fecha.add("Factura: " + datos.getIdVenta() + "\nFecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\n\n");

            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);
            encabezado.getDefaultCell().setBorder(0);
            float[] columnaEncabezado = new float[]{20f, 30f, 70f, 40f};
            encabezado.setWidths(columnaEncabezado);
            encabezado.setHorizontalAlignment(PdfPTable.ALIGN_LEFT); // CORREGIDO
            encabezado.addCell(img);

            ModeloEmpresa conf = datos.getDatosEmpresa();
            encabezado.addCell("");
            encabezado.addCell("Ruc: " + conf.getRuc() + "\nNombre: " + conf.getRepresentanteLegal() + "\nTeléfono: " + conf.getTelefono() + "\nDirección: " + conf.getDireccion() + "\nRazón: " + conf.getRazonSocial());
            encabezado.addCell(fecha);
            doc.add(encabezado);

            // --- Datos del Cliente ---
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);
            cli.add("Datos del Cliente\n\n");
            doc.add(cli);

            PdfPTable tablaCli = new PdfPTable(4);
            tablaCli.setWidthPercentage(100);
            tablaCli.getDefaultCell().setBorder(0);
            float[] columnaCli = new float[]{20f, 50f, 30f, 40f};
            tablaCli.setWidths(columnaCli);
            tablaCli.setHorizontalAlignment(PdfPTable.ALIGN_LEFT); // CORREGIDO
            
            PdfPCell cl1 = new PdfPCell(new Phrase("Dni/Ruc", negrita));
            PdfPCell cl2 = new PdfPCell(new Phrase("Nombre", negrita));
            PdfPCell cl3 = new PdfPCell(new Phrase("Teléfono", negrita));
            PdfPCell cl4 = new PdfPCell(new Phrase("Dirección", negrita));
            cl1.setBorder(0); cl2.setBorder(0); cl3.setBorder(0); cl4.setBorder(0);
            tablaCli.addCell(cl1); tablaCli.addCell(cl2); tablaCli.addCell(cl3); tablaCli.addCell(cl4);
            
            ModeloCliente cliente = datos.getDatosCliente();
            tablaCli.addCell(String.valueOf(cliente.getDni()));
            tablaCli.addCell(cliente.getNombre());
            tablaCli.addCell(String.valueOf(cliente.getTelefono()));
            tablaCli.addCell(cliente.getDireccion());
            doc.add(tablaCli);

            // --- Productos ---
            doc.add(Chunk.NEWLINE);
            PdfPTable tablaPro = new PdfPTable(4);
            tablaPro.setWidthPercentage(100);
            float[] columnapro = new float[]{10f, 50f, 15f, 20f};
            tablaPro.setWidths(columnapro);
            tablaPro.setHorizontalAlignment(PdfPTable.ALIGN_LEFT); // CORREGIDO
            
            PdfPCell pro1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell pro2 = new PdfPCell(new Phrase("Descripción", negrita));
            PdfPCell pro3 = new PdfPCell(new Phrase("Precio U.", negrita));
            PdfPCell pro4 = new PdfPCell(new Phrase("Precio T.", negrita));
            pro1.setBorder(0); pro2.setBorder(0); pro3.setBorder(0); pro4.setBorder(0);
            pro1.setBackgroundColor(BaseColor.DARK_GRAY);
            pro2.setBackgroundColor(BaseColor.DARK_GRAY);
            pro3.setBackgroundColor(BaseColor.DARK_GRAY);
            pro4.setBackgroundColor(BaseColor.DARK_GRAY);
            tablaPro.addCell(pro1); tablaPro.addCell(pro2); tablaPro.addCell(pro3); tablaPro.addCell(pro4);
            
            for (ModeloDetalleVenta det : datos.getDetallesVenta()) {
                tablaPro.addCell(String.valueOf(det.getCantidad()));
                String nombreProducto = GestorProducto.getInstance().buscarProductoPorCodigo(det.getCod_pro()).getNombre();
                tablaPro.addCell(nombreProducto);
                tablaPro.addCell(String.format("%.2f", det.getPrecio()));
                tablaPro.addCell(String.format("%.2f", det.getPrecio() * det.getCantidad()));
            }
            doc.add(tablaPro);

            // --- Total ---
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE); // CORREGIDO
            info.add("Total a pagar: " + String.format("%.2f", datos.getTotalVenta()));
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);

            // --- Firma y mensaje final ---
            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.add("Cancelación y Firma\n\n");
            firma.add("-----------------------\n");
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            
            Paragraph mensaje = new Paragraph();
            mensaje.add(Chunk.NEWLINE);
            mensaje.add("Gracias por su compra");
            mensaje.setAlignment(Element.ALIGN_CENTER);
            doc.add(mensaje);

            doc.close();
            archivo.close();
            Desktop.getDesktop().open(file);

        } catch (DocumentException | IOException e) {
            System.err.println("Error al generar PDF: " + e.toString());
            JOptionPane.showMessageDialog(null, "Error al generar el PDF.");
            e.printStackTrace();
        }
    }
}