package com.pe.inventoryapp.backend.user.service;

import com.pe.inventoryapp.backend.user.model.request.ProfileRequest;
import com.pe.inventoryapp.backend.user.model.response.DetailUserResponse;

public interface ProfileService {
  DetailUserResponse findUserById(Long id);

  void updateUserProfileById(Long id, ProfileRequest profileRequest);
}
