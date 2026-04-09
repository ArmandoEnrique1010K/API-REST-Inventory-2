package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pe.inventoryapp.backend.user.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  // Busca al usuario mediante su email, firstname, lastname y/o dni desde un parametro
    // @Query("""
    //             SELECT u
    //             FROM User u
    //             WHERE (
    //                 :name IS NULL OR
    //                 LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
    //             ) ORDER BY u.id DESC
    //         """)
    // Page<User> findAllByName(
    //         @Param("name") String name,
    //         Pageable pageable);

  // Tambien lo debe buscar por roles, si ha marcado 2 roles, debe asegurarse que el usuario tenga esos 2 roles
    // @Query("""
    //             SELECT u
    //             FROM User u
    //             JOIN u.roles r
    //             WHERE (
    //                 :name IS NULL OR
    //                 LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
    //             )
    //             AND r.id IN :roleIds
    //             GROUP BY u.id
    //             HAVING COUNT(DISTINCT r.id) = :rolesCount AND u.active = true ORDER BY u.id DESC
    //         """)
    // Page<User> findAllByParamsAndHavingRoles(
    //         @Param("name") String name,
    //         @Param("roleIds") List<Long> roleIds,
    //         @Param("rolesCount") long rolesCount,
    //         Pageable pageable);


    // Obtener un usuario por su email
    Optional<User> findByEmail(String email);

    // “¿Existe algún usuario que tenga el rol con nombre name y cuyo id sea
    // distinto al id dado?”
    @Query("""
                SELECT COUNT(u) > 0
                FROM User u
                JOIN u.roles r
                WHERE r.name = :name
                AND u.id <> :id
            """)
    boolean existsByRoleNameAndIdNot(@Param("name") String name,
            @Param("id") Long id);

    boolean existsByEmail(String email);

    // Lista los primeros 10 usuarios que coincidan con el parametro de busqueda
    // @Query("""
    //             SELECT u
    //             FROM User u
    //             JOIN u.roles r
    //             WHERE u.active = true 
    //             AND (
    //                 :name IS NULL OR
    //                 LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
    //                 CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
    //             ) ORDER BY u.id DESC LIMIT 10
    //         """)
    // List<User> findAllFirstTenUsersByName(String name);
}