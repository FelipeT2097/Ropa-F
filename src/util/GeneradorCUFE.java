/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 *
 * @author piper
 */
public class GeneradorCUFE {

    // Constantes para el ambiente
    public static final int AMBIENTE_PRODUCCION = 1;
    public static final int AMBIENTE_PRUEBAS = 2;

    /**
     * Genera el CUFE completo para una factura electrónica
     *
     * @param numeroFactura Número de la factura (sin prefijo ni letras)
     * @param fechaFactura Fecha y hora de generación de la factura
     * @param valorFactura Valor total de la factura (con impuestos)
     * @param codigoImpuesto1 Código del primer impuesto (01=IVA, 02=Consumo,
     * 03=INC)
     * @param valorImpuesto1 Valor del primer impuesto
     * @param codigoImpuesto2 Código del segundo impuesto (puede ser null)
     * @param valorImpuesto2 Valor del segundo impuesto (puede ser 0)
     * @param codigoImpuesto3 Código del tercer impuesto (puede ser null)
     * @param valorImpuesto3 Valor del tercer impuesto (puede ser 0)
     * @param valorTotal Valor total de la factura con impuestos
     * @param nitOFE NIT del Obligado a Facturar Electrónicamente (sin DV)
     * @param tipoDocAdquiriente Tipo de documento del adquiriente (13=Cédula,
     * 31=NIT, etc.)
     * @param numDocAdquiriente Número de documento del adquiriente (sin DV)
     * @param claveTenica Clave técnica asignada por la DIAN
     * @param tipoAmbiente Ambiente de operación (1=Producción, 2=Pruebas)
     * @return String con el CUFE generado (SHA-384 en hexadecimal)
     */
    public static String generarCUFE(
            String numeroFactura,
            Date fechaFactura,
            double valorFactura,
            String codigoImpuesto1,
            double valorImpuesto1,
            String codigoImpuesto2,
            double valorImpuesto2,
            String codigoImpuesto3,
            double valorImpuesto3,
            double valorTotal,
            String nitOFE,
            String tipoDocAdquiriente,
            String numDocAdquiriente,
            String claveTenica,
            int tipoAmbiente) {

        try {
            // Formato DIAN obligatorio: yyyy-MM-dd'T'HH:mm:ss
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            
            String fechaFormateada = sdf.format(fechaFactura);

            // Formato de valores monetarios: sin separadores, con 2 decimales
            String valorFacturaStr = formatearValor(valorFactura);
            String valorImpuesto1Str = formatearValor(valorImpuesto1);
            String valorImpuesto2Str = formatearValor(valorImpuesto2);
            String valorImpuesto3Str = formatearValor(valorImpuesto3);
            String valorTotalStr = formatearValor(valorTotal);

            // Construcción de la cadena según especificación DIAN
            StringBuilder cadena = new StringBuilder();
            cadena.append(numeroFactura);
            cadena.append(fechaFormateada);
            cadena.append(valorFacturaStr);
            cadena.append(codigoImpuesto1 != null ? codigoImpuesto1 : "");
            cadena.append(valorImpuesto1Str);
            cadena.append(codigoImpuesto2 != null ? codigoImpuesto2 : "");
            cadena.append(valorImpuesto2Str);
            cadena.append(codigoImpuesto3 != null ? codigoImpuesto3 : "");
            cadena.append(valorImpuesto3Str);
            cadena.append(valorTotalStr);
            cadena.append(nitOFE);
            cadena.append(tipoDocAdquiriente);
            cadena.append(numDocAdquiriente);
            cadena.append(claveTenica);
            cadena.append(tipoAmbiente);

            // Generar hash SHA-384
            String cufe = generarHashSHA384(cadena.toString());

            return cufe;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar CUFE: " + e.getMessage(), e);
        }
    }

    /**
     * Método simplificado para generar CUFE con menos parámetros Útil cuando
     * solo tienes un impuesto (IVA)
     */
    public static String generarCUFESimple(
            String numeroFactura,
            Date fechaFactura,
            double valorSinImpuestos,
            double valorIVA,
            double valorTotal,
            String nitEmisor,
            String nitCliente,
            String claveTenica) {

        return generarCUFE(
                numeroFactura,
                fechaFactura,
                valorSinImpuestos,
                "01", // Código IVA
                valorIVA,
                null,
                0.0,
                null,
                0.0,
                valorTotal,
                nitEmisor,
                "31", // Tipo documento NIT
                nitCliente,
                claveTenica,
                AMBIENTE_PRODUCCION
        );
    }

    /**
     * Formatea un valor monetario según especificación DIAN Formato: sin
     * separadores de miles, con punto decimal y 2 decimales Ejemplo: 1234567.89
     */
    private static String formatearValor(double valor) {
        return String.format("%.2f", valor);
    }

    /**
     * Genera el hash SHA-384 de una cadena
     *
     * @param cadena Cadena a hashear
     * @return Hash en formato hexadecimal (96 caracteres)
     */
    private static String generarHashSHA384(String cadena) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-384");
        byte[] hashBytes = md.digest(cadena.getBytes());
        return bytesToHex(hashBytes);
    }

    /**
     * Convierte un array de bytes a su representación hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Valida que un CUFE tenga el formato correcto
     *
     * @param cufe CUFE a validar
     * @return true si el formato es válido
     */
    public static boolean validarFormatoCUFE(String cufe) {
        if (cufe == null || cufe.isEmpty()) {
            return false;
        }

        // El CUFE debe tener 96 caracteres hexadecimales (SHA-384)
        if (cufe.length() != 96) {
            return false;
        }

        // Verificar que todos sean caracteres hexadecimales
        return cufe.matches("[0-9a-fA-F]+");
    }

    /**
     * Genera un CUFE de ejemplo para pruebas
     */
    public static String generarCUFEEjemplo() {
        return generarCUFESimple(
                "FV001-00123",
                new Date(),
                1000000.00,
                190000.00,
                1190000.00,
                "900123456", // NIT emisor
                "900654321", // NIT cliente
                "clave_tecnica_ejemplo_2024"
        );
    }

    // ============================================================================
    // MÉTODOS DE UTILIDAD ADICIONALES
    // ============================================================================
    /**
     * Extrae el número de factura limpio (sin prefijo ni caracteres especiales)
     */
    public static String limpiarNumeroFactura(String numeroFactura) {
        if (numeroFactura == null) {
            return "";
        }
        // Eliminar todo excepto números
        return numeroFactura.replaceAll("[^0-9]", "");
    }

    /**
     * Calcula el dígito de verificación del NIT Útil para validaciones
     * adicionales
     */
    public static int calcularDigitoVerificacion(String nit) {
        int[] primos = {71, 67, 59, 53, 47, 43, 41, 37, 29, 23, 19, 17, 13, 7, 3};
        int suma = 0;
        int longitudNit = nit.length();

        for (int i = 0; i < longitudNit; i++) {
            int digito = Character.getNumericValue(nit.charAt(longitudNit - 1 - i));
            suma += digito * primos[i];
        }

        int residuo = suma % 11;

        if (residuo >= 2) {
            return 11 - residuo;
        } else {
            return residuo;
        }
    }

    /**
     * Formatea un NIT con su dígito de verificación
     */
    public static String formatearNIT(String nit) {
        if (nit == null || nit.isEmpty()) {
            return "";
        }

        String nitLimpio = nit.replaceAll("[^0-9]", "");
        int dv = calcularDigitoVerificacion(nitLimpio);

        return nitLimpio + "-" + dv;
    }

    // ============================================================================
    // CLASE DE EJEMPLO DE USO
    // ============================================================================
    /**
     * Ejemplo de uso del generador de CUFE
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   GENERADOR DE CUFE - FACTURACIÓN ELECTRÓNICA");
        System.out.println("=================================================\n");

        // Ejemplo 1: CUFE Simple
        System.out.println("EJEMPLO 1: CUFE Simple (solo IVA)");
        System.out.println("---------------------------------");

        String cufeSimple = generarCUFESimple(
                "FV001-00123", // Número de factura
                new Date(), // Fecha actual
                1000000.00, // Valor sin impuestos
                190000.00, // IVA 19%
                1190000.00, // Total con IVA
                "900123456", // NIT emisor
                "900654321", // NIT cliente
                "tu_clave_tecnica_dian" // Clave técnica DIAN
        );

        System.out.println("CUFE Generado: " + cufeSimple);
        System.out.println("Longitud: " + cufeSimple.length() + " caracteres");
        System.out.println("Válido: " + validarFormatoCUFE(cufeSimple));
        System.out.println();

        // Ejemplo 2: CUFE Completo con múltiples impuestos
        System.out.println("EJEMPLO 2: CUFE Completo (IVA + INC)");
        System.out.println("-------------------------------------");

        String cufeCompleto = generarCUFE(
                "FV001-00124", // Número de factura
                new Date(), // Fecha
                2000000.00, // Valor sin impuestos
                "01", // Código impuesto 1 (IVA)
                380000.00, // Valor IVA 19%
                "03", // Código impuesto 2 (INC - Impoconsumo)
                160000.00, // Valor INC 8%
                null, // Sin tercer impuesto
                0.0, // Valor tercer impuesto
                2540000.00, // Total
                "900123456", // NIT emisor
                "31", // Tipo doc cliente (NIT)
                "800456789", // Número doc cliente
                "tu_clave_tecnica_dian", // Clave técnica
                AMBIENTE_PRODUCCION // Ambiente
        );

        System.out.println("CUFE Generado: " + cufeCompleto);
        System.out.println("Longitud: " + cufeCompleto.length() + " caracteres");
        System.out.println("Válido: " + validarFormatoCUFE(cufeCompleto));
        System.out.println();

        // Ejemplo 3: Utilidades
        System.out.println("EJEMPLO 3: Utilidades");
        System.out.println("---------------------");

        String nit = "900123456";
        System.out.println("NIT sin DV: " + nit);
        System.out.println("Dígito Verificación: " + calcularDigitoVerificacion(nit));
        System.out.println("NIT Formateado: " + formatearNIT(nit));
        System.out.println();

        String numeroFacturaConPrefijo = "FV001-00125";
        System.out.println("Número Factura Original: " + numeroFacturaConPrefijo);
        System.out.println("Número Limpio: " + limpiarNumeroFactura(numeroFacturaConPrefijo));
        System.out.println();

        // Ejemplo 4: Validación
        System.out.println("EJEMPLO 4: Validación de CUFE");
        System.out.println("------------------------------");

        String cufeInvalido1 = "abc123";  // Muy corto
        String cufeInvalido2 = "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"; // Caracteres inválidos

        System.out.println("CUFE válido: " + validarFormatoCUFE(cufeSimple));
        System.out.println("CUFE inválido (muy corto): " + validarFormatoCUFE(cufeInvalido1));
        System.out.println("CUFE inválido (caracteres incorrectos): " + validarFormatoCUFE(cufeInvalido2));
        System.out.println();

        System.out.println("=================================================");
        System.out.println("  IMPORTANTE: Reemplaza 'tu_clave_tecnica_dian'");
        System.out.println("  con la clave técnica real asignada por la DIAN");
        System.out.println("=================================================");
    }
}
