package com.pe.inventoryapp.backend.auth.service;

import org.springframework.stereotype.Service;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class MailerSendServiceImpl implements MailerSendService {
  // Envia el token de restablecimiento de contraseña (este método no interactua
  // con la base de datos)
  @Override
  public void sendResetPasswordToken(String toEmail, String token) {
    // Configuración de MailerSend
    MailerSend ms = new MailerSend();

    Dotenv dotenv = Dotenv.load();
    String apiKey = dotenv.get("MAILERSEND_API_TOKEN");
    String testDomain = dotenv.get("MAILERSEND_TEST_DOMAIN");

    System.out.println(apiKey);
    System.out.println(testDomain);

    ms.setToken(apiKey);

    Email email = new Email();
    email.setFrom("Inventory App 2",
        testDomain);
    email.addRecipient("", toEmail);
    email.setSubject("Recuperar contraseña");

    String text = "Tu código para restablecer contraseña es: " + token;
    String html = "<p>Tu código de 6 digitos para restablecer contraseña es: <strong>" + token
        + "</strong>. Recuerda que tienes 5 minutos para restablecer tu contraseña antes que el código expire</p>";

    email.setPlain(text);
    email.setHtml(html);

    try {
      MailerSendResponse resp = ms.emails().send(email);
      System.out.println("Email enviado, messageId: " + resp.messageId);
    } catch (MailerSendException e) {
      e.printStackTrace();
    }

  }

}