package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
  public static String formatearFecha(Date fecha) {
    if (fecha == null) {
      return "";
    }
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    return formatter.format(fecha);
  }

  public static int calcularEdad(Date fechaNacimiento) {
    if (fechaNacimiento == null) {
      return 0;
    }

    Date fechaActual = new Date();
    long diferenciaMilisegundos = fechaActual.getTime() - fechaNacimiento.getTime();
    long diasTranscurridos = diferenciaMilisegundos / (1000L * 60 * 60 * 24);

    return (int) (diasTranscurridos / 365);
  }
}
