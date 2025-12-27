package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  
  // Busca al usuario mediante su email, firstname, lastname y/o dni desde un parametro
  
  // Tambien lo debe buscar por roles, si ha marcado 2 roles, debe asegurarse que el usuario tenga esos 2 roles
  @Query(
    """
   SELECT u
          FROM User u
          WHERE (:name IS NULL OR LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) 
          OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) 
          OR LOWER (u.email) LIKE LOWER(CONCAT('%', :name, '%')) 
          OR u.dni LIKE CONCAT('%', :name, '%'))
            AND (:role IS NULL OR u.category.id = :categoryId)
            AND p.status = true 
    """
    )
  Page<User> findAllByParams(@Param("name") String name, @Param("role") List<Long> roleIds)
  
    Optional<User> findByEmail(String email);
}