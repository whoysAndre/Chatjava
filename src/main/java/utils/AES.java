
package utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String cifrar(String message, String key) throws Exception {
        // Convertir la clave en bytes
        byte[] keyBytes = key.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        // Crear el cifrador
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Cifrar el mensaje
        byte[] encryptedBytes = cipher.doFinal(message.getBytes("UTF-8"));

        // Codificar en Base64
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String descifrar(String ciphertext, String key) throws Exception {
        // Convertir la clave en bytes
        byte[] keyBytes = key.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        // Crear el descifrador
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decodificar Base64
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);

        // Descifrar
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        return new String(decryptedBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            String clave = "1234567890123456"; // Debe ser de 16 caracteres
            String mensaje = "admin";

            String cifrado = cifrar(mensaje, clave);
            System.out.println("Cifrado: " + cifrado);

            String descifrado = descifrar(cifrado, clave);
            System.out.println("Descifrado: " + descifrado);

        } catch (Exception e) {
        }
    }
}
