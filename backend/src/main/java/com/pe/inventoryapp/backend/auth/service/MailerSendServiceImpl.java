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

    ms.setToken(apiKey);

    Email email = new Email();
    email.setFrom("TuApp Inventory",
        testDomain);
    email.addRecipient("", toEmail);
    email.setSubject("Recuperar contraseña");

    String text = "Tu código para restablecer contraseña es: " + token;
    String html = "<p>Tu código para restablecer contraseña es: <strong>" + token + "</strong></p>";

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