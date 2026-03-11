package com.pe.inventoryapp.backend.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CloudinaryConfig {
  @Bean
  Cloudinary cloudinary() {
    Dotenv dotenv = Dotenv.load();

    String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
    String apiKey = dotenv.get("CLOUDINARY_API_KEY");
    String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

    Map<String, String> config = new HashMap<>();

    config.put("cloud_name", cloudName);
    config.put("api_key", apiKey);
    config.put("api_secret", apiSecret);

    return new Cloudinary(config);
  }

}
