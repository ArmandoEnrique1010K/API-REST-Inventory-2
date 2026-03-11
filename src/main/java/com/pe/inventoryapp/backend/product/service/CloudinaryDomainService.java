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

        try {
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar imagen", e);
        }
    }
}
