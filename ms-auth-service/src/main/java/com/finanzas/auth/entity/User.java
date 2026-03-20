package com.finanzas.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 50, message = "El primer nombre no puede exceder 50 caracteres")
    @Column(name = "primer_nombre", nullable = false)
    private String primerNombre;

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no puede exceder 50 caracteres")
    @Column(name = "primer_apellido", nullable = false)
    private String primerApellido;

    @Size(max = 50, message = "El segundo nombre no puede exceder 50 caracteres")
    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @Size(max = 50, message = "El segundo apellido no puede exceder 50 caracteres")
    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @NotBlank(message = "El número de celular es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "El número de celular no es válido")
    @Column(name = "celular", unique = true, nullable = false)
    private String celular;

    @Column(name = "email_verificado", nullable = false)
    private Boolean emailVerificado = false;

    @Column(name = "celular_verificado", nullable = false)
    private Boolean celularVerificado = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructores
    public User() {}

    public User(String username, String email, String password, String primerNombre, 
               String primerApellido, String celular) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.celular = celular;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        StringBuilder nombreCompleto = new StringBuilder();
        nombreCompleto.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.trim().isEmpty()) {
            nombreCompleto.append(" ").append(segundoNombre);
        }
        nombreCompleto.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
            nombreCompleto.append(" ").append(segundoApellido);
        }
        return nombreCompleto.toString().trim();
    }

    public String getIniciales() {
        String iniciales = primerNombre.substring(0, 1).toUpperCase();
        iniciales += primerApellido.substring(0, 1).toUpperCase();
        return iniciales;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Boolean getEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(Boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public Boolean getCelularVerificado() {
        return celularVerificado;
    }

    public void setCelularVerificado(Boolean celularVerificado) {
        this.celularVerificado = celularVerificado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", primerNombre='" + primerNombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", celular='" + celular + '\'' +
                ", emailVerificado=" + emailVerificado +
                ", celularVerificado=" + celularVerificado +
                ", createdAt=" + createdAt +
                '}';
    }
}
