package com.pe.inventoryapp.backend.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

// import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class CloudinaryConfig {

  @Value("${cloudinary.cloud.name}")
  private String cloudName;
  @Value("${cloudinary.api.key}")
  private String apiKey;
  @Value("${cloudinary.api.secret}")
  private String apiSecret;

  @Bean
  Cloudinary cloudinary() {
    // Dotenv dotenv = Dotenv.load();

    // String cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
    // String apiKey = dotenv.get("CLOUDINARY_API_KEY");
    // String apiSecret = dotenv.get("CLOUDINARY_API_SECRET");

    // Map<String, String> config = new HashMap<>();

    // config.put("cloud_name", cloudName);
    // config.put("api_key", apiKey);
    // config.put("api_secret", apiSecret);

    // return new Cloudinary(config);

    return new Cloudinary(ObjectUtils.asMap(
        "cloud_name", cloudName,
        "api_key", apiKey,
        "api_secret", apiSecret
    ));


  }

}
