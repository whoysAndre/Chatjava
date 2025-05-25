
package utils;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptJava {
  public static String hashPassword(String plainText) {
    return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
  }

  public static boolean checkPassword(String plainText, String hashedPassword) {
    return BCrypt.checkpw(plainText, hashedPassword);
  }

  public static void main(String[] args) {
    String textPlano = "1234";
    String hash = BcryptJava.hashPassword(textPlano);
    System.out.println(hash);

    boolean isPasswordCorrect = BcryptJava.checkPassword(textPlano, hash);
    System.out.println("Is password correct? " + isPasswordCorrect);
  }
}
