package com.pe.inventoryapp.backend.product.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
  String uploadImage(MultipartFile file);
}