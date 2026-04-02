package com.pe.inventoryapp.backend.product.service;

import java.io.File;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.pe.inventoryapp.backend.common.model.response.CloudinaryImageResponse;

@Service
public class CloudinaryDomainService {
    private final Cloudinary cloudinary;

    public CloudinaryDomainService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public CloudinaryImageResponse uploadImage(MultipartFile file) {
        try {
            // Map<String, Object> uploadResult = cloudinary.uploader().upload(
            //         file.getInputStream(),
            //         Map.of());

            // Convierte el MultipartFile a File temporal y súbelo así.
            // Cloudinary siempre reconoce File. 
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    tempFile,
                    // Ruta hacia una carpeta que se encuentra en Cloudinary
                    Map.of(
                        "folder", "inventory/models"
                    ));

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();
            
            tempFile.delete(); // limpiar archivo temporal

            System.out.println(imageUrl);
            System.out.println(publicId);

            return new CloudinaryImageResponse(imageUrl, publicId);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen a Cloudinary", e);
        }
    }

    public void deleteImage(String publicId) {

        // 1. VALIDACIÓN REAL
        if (publicId == null || publicId.isBlank()) {
            return; // no haces nada, evitas error
        }

        try {
            Map result = cloudinary.uploader().destroy(publicId, Map.of());

            String status = (String) result.get("result");

            // 2. LOGICA SEGURA
            if ("ok".equals(status)) {
                System.out.println("Imagen eliminada correctamente");
            } else if ("not found".equals(status)) {
                System.out.println("La imagen no existía en Cloudinary");
            } else {
                System.out.println("Resultado inesperado: " + status);
            }

        } catch (Exception e) {
            // 3. NO ROMPAS TODO EL PROCESO POR ESTO
            System.out.println("Error al eliminar imagen: " + e.getMessage());
        }
    }
}
