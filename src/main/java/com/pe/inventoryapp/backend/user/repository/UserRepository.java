package com.pe.inventoryapp.backend.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.pe.inventoryapp.backend.user.model.entity.User;


public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
        // Busca al usuario mediante su email, firstname, lastname y/o dni desde un
        // parametro
        // @Query("""
        // SELECT u
        // FROM User u
        // WHERE (
        // :name IS NULL OR
        // LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
        // ) ORDER BY u.id DESC
        // """)
        // Page<User> findAllByName(
        // @Param("name") String name,
        // Pageable pageable);

        // Tambien lo debe buscar por roles, si ha marcado 2 roles, debe asegurarse que
        // el usuario tenga esos 2 roles
        // @Query("""
        // SELECT u
        // FROM User u
        // JOIN u.roles r
        // WHERE (
        // :name IS NULL OR
        // LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
        // )
        // AND r.id IN :roleIds
        // GROUP BY u.id
        // HAVING COUNT(DISTINCT r.id) = :rolesCount AND u.active = true ORDER BY u.id
        // DESC
        // """)
        // Page<User> findAllByParamsAndHavingRoles(
        // @Param("name") String name,
        // @Param("roleIds") List<Long> roleIds,
        // @Param("rolesCount") long rolesCount,
        // Pageable pageable);

        /**
         * Sobrescribe el método findAll de JpaSpecificationExecutor
         * para agregar carga optimizada de la relación "roles".
         *
         * 🔥 EntityGraph:
         * - Evita el problema N+1 (múltiples queries)
         * - Hace un JOIN automático para traer roles en la misma consulta
         *
         * @param spec     filtro dinámico (puede ser null)
         * @param pageable paginación + ordenamiento
         * @return página de usuarios con roles ya cargados
         */
        @EntityGraph(attributePaths = { "roles" })
        @NonNull
        Page<User> findAll(
                        @Nullable Specification<User> spec,
                        @Nullable Pageable pageable);

                //* EN LA CONSOLA SE VE UN LEFT JOIN POR CADA RELACION DE CADA ENTIDAD, RECORDAR QUE EXISTE UNA TABLA INTERMEDIA ENTRE USER Y ROLES PORQUE ES UNA RELACION MANY TO MANY


        // Obtener un usuario por su email
        // Optional<User> findByEmail(String email);

        @Query("""
                        SELECT u FROM User u
                        JOIN FETCH u.roles
                        WHERE u.email = :email
                        """)
        Optional<User> findByEmailWithRoles(String email);

        // Obtener un usuario por ID con roles
        /* QUITE LEFT JOIN FETCH Y COLOQUE JOIN FETCH */
        @Query("""
                        SELECT u FROM User u
                        JOIN FETCH u.roles
                        WHERE u.id = :id
                        """)

        Optional<User> findByIdWithRoles(Long id);

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

        //* NO HAY SOLUCION AL PROBLEMA DE LAS 2 QUERIES AL UTILIZAR EL METODO FINDALL SOBREESCRITO DE ESTE REPOSITORIO, SIEMPRE SE HARAN 2 QUERIES, 1 PARA OBTENER LOS DATOS Y EL OTRO PARA CONTAR LA CANTIDAD DE LOS DATOS*/


        // Lista los primeros 10 usuarios que coincidan con el parametro de busqueda
        // @Query("""
        // SELECT u
        // FROM User u
        // JOIN u.roles r
        // WHERE u.active = true
        // AND (
        // :name IS NULL OR
        // LOWER(u.firstname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.lastname) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // LOWER(u.email) LIKE LOWER(CONCAT('%', :name, '%')) OR
        // CAST(u.dni AS string) LIKE CONCAT('%', :name, '%')
        // ) ORDER BY u.id DESC LIMIT 10
        // """)
        // List<User> findAllFirstTenUsersByName(String name);
}