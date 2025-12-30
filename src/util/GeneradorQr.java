/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generador de Códigos QR para facturas electrónicas Compatible con Java 8
 *
 * Genera códigos QR con el CUFE y URL de validación de la DIAN Usa la librería
 * ZXing (Google)
 *
 * DEPENDENCIAS (Maven):
 * <dependency>
 * <groupId>com.google.zxing</groupId>
 * <artifactId>core</artifactId>
 * <version>3.5.0</version>
 * </dependency>
 * <dependency>
 * <groupId>com.google.zxing</groupId>
 * <artifactId>javase</artifactId>
 * <version>3.5.0</version>
 * </dependency>
 *
 * /**
 *
 * @author piper
 */
public class GeneradorQr {

    // Constantes de configuración
    public static final int TAMANIO_DEFECTO = 300;
    public static final int TAMANIO_PEQUENO = 150;
    public static final int TAMANIO_MEDIANO = 300;
    public static final int TAMANIO_GRANDE = 500;

    // URL base para validación en la DIAN
    private static final String URL_BASE_DIAN = "https://catalogo-vpfe.dian.gov.co/document/searchqr?documentkey=";

    /**
     * Genera un código QR con el CUFE de la factura
     *
     * @param cufe Código Único de Factura Electrónica
     * @param tamanio Tamaño del QR en píxeles (recomendado 300)
     * @return BufferedImage con el código QR generado
     * @throws WriterException Si hay error al generar el QR
     */
    public static BufferedImage generarCodigoQR(String cufe, int tamanio) throws WriterException {
        // URL completa para validación
        String urlValidacion = URL_BASE_DIAN + cufe;

        // Configuración del QR
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // Alta corrección de errores
        hints.put(EncodeHintType.MARGIN, 1); // Margen mínimo

        // Generar matriz del QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(urlValidacion, BarcodeFormat.QR_CODE, tamanio, tamanio, hints);

        // Convertir matriz a imagen
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        return qrImage;
    }

    /**
     * Genera un código QR con tamaño por defecto (300x300)
     *
     * @param cufe Código Único de Factura Electrónica
     * @return BufferedImage con el código QR
     * @throws WriterException Si hay error al generar el QR
     */
    public static BufferedImage generarCodigoQR(String cufe) throws WriterException {
        return generarCodigoQR(cufe, TAMANIO_DEFECTO);
    }

    /**
     * Genera un código QR y lo guarda en un archivo
     *
     * @param cufe Código Único de Factura Electrónica
     * @param rutaArchivo Ruta donde guardar el archivo (ej: "qr_factura.png")
     * @param tamanio Tamaño del QR en píxeles
     * @throws WriterException Si hay error al generar el QR
     * @throws IOException Si hay error al guardar el archivo
     */
    public static void generarYGuardarQR(String cufe, String rutaArchivo, int tamanio)
            throws WriterException, IOException {
        BufferedImage qrImage = generarCodigoQR(cufe, tamanio);
        File archivoQR = new File(rutaArchivo);
        ImageIO.write(qrImage, "PNG", archivoQR);
    }

    /**
     * Genera un código QR personalizado con colores
     *
     * @param cufe Código Único de Factura Electrónica
     * @param tamanio Tamaño del QR en píxeles
     * @param colorQR Color del QR (Color.BLACK por defecto)
     * @param colorFondo Color de fondo (Color.WHITE por defecto)
     * @return BufferedImage con el código QR personalizado
     * @throws WriterException Si hay error al generar el QR
     */
    public static BufferedImage generarCodigoQRConColores(
            String cufe,
            int tamanio,
            Color colorQR,
            Color colorFondo) throws WriterException {

        String urlValidacion = URL_BASE_DIAN + cufe;

        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(urlValidacion, BarcodeFormat.QR_CODE, tamanio, tamanio, hints);

        // Crear imagen con colores personalizados
        BufferedImage qrImage = new BufferedImage(tamanio, tamanio, BufferedImage.TYPE_INT_RGB);
        qrImage.createGraphics();

        Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
        graphics.setColor(colorFondo);
        graphics.fillRect(0, 0, tamanio, tamanio);
        graphics.setColor(colorQR);

        for (int i = 0; i < tamanio; i++) {
            for (int j = 0; j < tamanio; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        return qrImage;
    }

    /**
     * Convierte una imagen BufferedImage a array de bytes Útil para almacenar
     * en base de datos o enviar por red
     *
     * @param imagen Imagen a convertir
     * @param formato Formato de imagen ("PNG", "JPG", etc.)
     * @return Array de bytes con la imagen
     * @throws IOException Si hay error en la conversión
     */
    public static byte[] imagenABytes(BufferedImage imagen, String formato) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, formato, baos);
        return baos.toByteArray();
    }

    /**
     * Genera código QR con logo/imagen en el centro
     *
     * @param cufe Código Único de Factura Electrónica
     * @param tamanio Tamaño del QR en píxeles
     * @param rutaLogo Ruta del archivo del logo a insertar
     * @param tamanioLogo Tamaño del logo (recomendado: 20% del tamaño del QR)
     * @return BufferedImage con el código QR y logo
     * @throws WriterException Si hay error al generar el QR
     * @throws IOException Si hay error al leer el logo
     */
    public static BufferedImage generarCodigoQRConLogo(
            String cufe,
            int tamanio,
            String rutaLogo,
            int tamanioLogo) throws WriterException, IOException {

        // Generar QR base
        BufferedImage qrImage = generarCodigoQR(cufe, tamanio);

        // Cargar logo
        BufferedImage logo = ImageIO.read(new File(rutaLogo));

        // Redimensionar logo si es necesario
        BufferedImage logoRedimensionado = new BufferedImage(
                tamanioLogo,
                tamanioLogo,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logoRedimensionado.createGraphics();
        g2d.drawImage(logo, 0, 0, tamanioLogo, tamanioLogo, null);
        g2d.dispose();

        // Insertar logo en el centro del QR
        Graphics2D graphics = qrImage.createGraphics();
        int posX = (tamanio - tamanioLogo) / 2;
        int posY = (tamanio - tamanioLogo) / 2;
        graphics.drawImage(logoRedimensionado, posX, posY, null);
        graphics.dispose();

        return qrImage;
    }

    /**
     * Valida que un CUFE sea válido antes de generar el QR
     *
     * @param cufe CUFE a validar
     * @return true si es válido
     */
    public static boolean validarCUFE(String cufe) {
        if (cufe == null || cufe.isEmpty()) {
            return false;
        }

        // El CUFE debe tener 96 caracteres (SHA-384)
        if (cufe.length() != 96) {
            return false;
        }

        // Solo debe contener caracteres hexadecimales
        return cufe.matches("[0-9a-fA-F]+");
    }

    /**
     * Genera URL completa de validación para un CUFE
     *
     * @param cufe Código Único de Factura Electrónica
     * @return URL completa para validación en DIAN
     */
    public static String generarURLValidacion(String cufe) {
        return URL_BASE_DIAN + cufe;
    }

    // MÉTODOS PARA INTEGRACIÓN CON JASPERREPORTS
    /**
     * Genera QR optimizado para JasperReports Tamaño y formato adecuados para
     * incluir en reportes PDF
     *
     * @param cufe Código Único de Factura Electrónica
     * @return BufferedImage listo para usar en JasperReports
     */
    public static BufferedImage generarQRParaJasper(String cufe) {
        try {
            // Tamaño optimizado para reportes (300x300)
            return generarCodigoQR(cufe, TAMANIO_DEFECTO);
        } catch (WriterException e) {
            throw new RuntimeException("Error al generar QR para JasperReports: " + e.getMessage(), e);
        }
    }

    /**
     * Genera QR como objeto java.awt.Image (compatible con JasperReports)
     *
     * @param cufe Código Único de Factura Electrónica
     * @return java.awt.Image listo para pasar como parámetro a JasperReports
     */
    public static java.awt.Image generarQRComoImage(String cufe) {
        return generarQRParaJasper(cufe);
    }

    // CLASE DE EJEMPLO DE USO
    // Ejemplo de uso del generador de códigos QR
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     GENERADOR DE CÓDIGOS QR - DIAN COLOMBIA");
        System.out.println("=================================================\n");

        try {
            // Generar CUFE de ejemplo
            String cufe = GeneradorCUFE.generarCUFEEjemplo();

            System.out.println("CUFE generado para prueba:");
            System.out.println(cufe);
            System.out.println();

            // Validar CUFE
            System.out.println("Validando CUFE...");
            boolean cufeValido = validarCUFE(cufe);
            System.out.println("CUFE válido: " + cufeValido);
            System.out.println();

            if (!cufeValido) {
                System.out.println("ERROR: CUFE inválido. No se puede generar QR.");
                return;
            }

            //Generar QR básico
            System.out.println("EJEMPLO 1: Generando QR básico...");
            BufferedImage qrBasico = generarCodigoQR(cufe);
            String rutaQRBasico = "qr_basico.png";
            ImageIO.write(qrBasico, "PNG", new File(rutaQRBasico));
            System.out.println("✓ QR básico guardado en: " + rutaQRBasico);
            System.out.println("  Tamaño: " + qrBasico.getWidth() + "x" + qrBasico.getHeight() + " píxeles");
            System.out.println();

            //Generar QR con tamaño personalizado
            System.out.println("EJEMPLO 2: Generando QR grande (500x500)...");
            String rutaQRGrande = "qr_grande.png";
            generarYGuardarQR(cufe, rutaQRGrande, TAMANIO_GRANDE);
            System.out.println("✓ QR grande guardado en: " + rutaQRGrande);
            System.out.println();

            //Generar QR con colores personalizados
            System.out.println("EJEMPLO 3: Generando QR con colores...");
            BufferedImage qrColores = generarCodigoQRConColores(
                    cufe,
                    TAMANIO_DEFECTO,
                    new Color(0, 51, 102), // Azul oscuro
                    Color.WHITE
            );
            String rutaQRColores = "qr_colores.png";
            ImageIO.write(qrColores, "PNG", new File(rutaQRColores));
            System.out.println("✓ QR con colores guardado en: " + rutaQRColores);
            System.out.println();

            //Convertir a bytes (para BD)
            System.out.println("EJEMPLO 4: Convirtiendo QR a bytes...");
            byte[] qrBytes = imagenABytes(qrBasico, "PNG");
            System.out.println("✓ QR convertido a " + qrBytes.length + " bytes");
            System.out.println("  (Listo para guardar en base de datos)");
            System.out.println();

            //URL de validación
            System.out.println("EJEMPLO 5: URL de validación DIAN");
            String urlValidacion = generarURLValidacion(cufe);
            System.out.println("URL generada:");
            System.out.println(urlValidacion);
            System.out.println();

            //Para JasperReports
            System.out.println("EJEMPLO 6: QR para JasperReports");
            System.out.println("Código para usar en tu reporte:");
            System.out.println("──────────────────────────────────────────");
            System.out.println("// En tu clase Java:");
            System.out.println("BufferedImage qr = GeneradorQR.generarQRParaJasper(cufe);");
            System.out.println("parametros.put(\"qrCodeImage\", qr);");
            System.out.println();
            System.out.println("// En tu archivo .jrxml:");
            System.out.println("<parameter name=\"qrCodeImage\" class=\"java.awt.Image\"/>");
            System.out.println("<image>");
            System.out.println("    <reportElement x=\"20\" y=\"50\" width=\"100\" height=\"100\"/>");
            System.out.println("    <imageExpression><![CDATA[$P{qrCodeImage}]]></imageExpression>");
            System.out.println("</image>");
            System.out.println("──────────────────────────────────────────");
            System.out.println();

            System.out.println("=================================================");
            System.out.println("Todos los ejemplos ejecutados exitosamente");
            System.out.println("Revisa los archivos PNG generados");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
