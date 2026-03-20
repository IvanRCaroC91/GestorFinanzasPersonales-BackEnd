package com.finanzas.auth.repository;

import com.finanzas.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Buscar un usuario por su nombre de usuario
     */
    Optional<User> findByUsername(String username);

    /**
     * Verificar si existe un usuario con el nombre de usuario especificado
     */
    boolean existsByUsername(String username);
}
