package com.pe.inventoryapp.backend.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class MailerSendServiceImpl implements MailerSendService {

  private static final Logger log = LoggerFactory.getLogger(MailerSendServiceImpl.class);

  @Override
  public void sendResetPasswordToken(String toEmail, String token) {
    // Configuración de MailerSend
    MailerSend ms = new MailerSend();
    
    Dotenv dotenv = Dotenv.load();

    String apiKey = dotenv.get("MAILERSEND_API_TOKEN");
    String testDomain = dotenv.get("MAILERSEND_TEST_DOMAIN");

    // Comprueba el contenido de las variables de entorno
    // System.out.println(apiKey);
    // System.out.println(testDomain);

    ms.setToken(apiKey);

    Email email = new Email();
    email.setFrom("Inventory App 2",
        testDomain);
    email.addRecipient("", toEmail);
    email.setSubject("Recuperar contraseña");

    String text = "Tu código para restablecer contraseña es: " + token;
    String html = "<p>Tu código de 6 digitos para restablecer contraseña es: <strong>" + token
        + "</strong>. Recuerda que tienes 10 minutos para restablecer tu contraseña antes que el código expire</p>";

    email.setPlain(text);
    email.setHtml(html);

    try {
      MailerSendResponse resp = ms.emails().send(email);
      System.out.println("Email enviado, messageId: " + resp.messageId);
    } catch (MailerSendException e) {
      // Hay un problema con la asignación de variables de entorno:
      // Se almacenan en memoria el ultimo valor asignado en el archivo .env, aunque no exista
      // Ejemplo: Si comentas o eliminas una variable de entorno, se queda con el ultimo valor asignado
      // La unica solución es reasignar el valor de la variable de entorno
      log.error("Error al enviar el correo: " + e.getMessage());
      throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR, "El servicio de envio de emails no ha respondido");
    }
  }

}
