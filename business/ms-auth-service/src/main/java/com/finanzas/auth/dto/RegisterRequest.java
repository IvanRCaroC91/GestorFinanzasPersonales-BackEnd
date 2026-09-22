package com.finanzas.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para solicitud de registro de nuevos usuarios.
 * 
 * Esta clase encapsula los datos necesarios para registrar un nuevo usuario
 * en el sistema de gestión de finanzas personales. Incluye validaciones
 * Bean Validation para garantizar la integridad y calidad de los datos
 * antes de ser procesados por la capa de negocio.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0
 * @since 2024-01-01
 */
public class RegisterRequest {

    /**
     * Nombre de usuario único para el sistema.
     * Debe tener entre 3 y 50 caracteres, sin espacios.
     */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "El nombre de usuario solo puede contener letras, números y guiones bajos")
    private String username;

    /**
     * Correo electrónico único del usuario.
     * Será validado con formato de email estándar.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    /**
     * Contraseña del usuario.
     * Será encriptada con BCrypt antes de almacenarse.
     * Mínimo 6 caracteres para seguridad básica.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Size(max = 255, message = "La contraseña no puede exceder 255 caracteres")
    private String password;

    /**
     * Primer nombre del usuario.
     * Campo obligatorio para identificación personal.
     */
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 50, message = "El primer nombre no puede exceder 50 caracteres")
    private String primerNombre;

    /**
     * Primer apellido del usuario.
     * Campo obligatorio para identificación personal.
     */
    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no puede exceder 50 caracteres")
    private String primerApellido;

    /**
     * Segundo nombre opcional del usuario.
     * Campo no obligatorio para información adicional.
     */
    @Size(max = 50, message = "El segundo nombre no puede exceder 50 caracteres")
    private String segundoNombre;

    /**
     * Segundo apellido opcional del usuario.
     * Campo no obligatorio para información adicional.
     */
    @Size(max = 50, message = "El segundo apellido no puede exceder 50 caracteres")
    private String segundoApellido;

    /**
     * Número de celular único del usuario.
     * Formato internacional con código de país opcional.
     */
    @NotBlank(message = "El número de celular es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "El número de celular no es válido. Debe tener entre 10-15 dígitos")
    private String celular;

    /**
     * Constructor por defecto requerido por Spring Boot.
     */
    public RegisterRequest() {
    }

    /**
     * Constructor con parámetros principales.
     * Útil para pruebas y creación programática.
     * 
     * @param username Nombre de usuario único
     * @param email Correo electrónico del usuario
     * @param password Contraseña sin encriptar
     * @param primerNombre Primer nombre del usuario
     * @param primerApellido Primer apellido del usuario
     * @param celular Número de celular
     */
    public RegisterRequest(String username, String email, String password, 
                          String primerNombre, String primerApellido, String celular) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.celular = celular;
    }

    // Getters y Setters con documentación

    /**
     * Obtiene el nombre de usuario.
     * 
     * @return Nombre de usuario único
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     * 
     * @param username Nombre de usuario único
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * 
     * @return Email del usuario
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     * 
     * @param email Email válido del usuario
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return Contraseña sin encriptar
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     * Será encriptada antes de almacenarse.
     * 
     * @param password Contraseña con mínimo 6 caracteres
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el primer nombre del usuario.
     * 
     * @return Primer nombre
     */
    public String getPrimerNombre() {
        return primerNombre;
    }

    /**
     * Establece el primer nombre del usuario.
     * 
     * @param primerNombre Primer nombre del usuario
     */
    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    /**
     * Obtiene el primer apellido del usuario.
     * 
     * @return Primer apellido
     */
    public String getPrimerApellido() {
        return primerApellido;
    }

    /**
     * Establece el primer apellido del usuario.
     * 
     * @param primerApellido Primer apellido del usuario
     */
    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    /**
     * Obtiene el segundo nombre del usuario.
     * 
     * @return Segundo nombre o null si no está definido
     */
    public String getSegundoNombre() {
        return segundoNombre;
    }

    /**
     * Establece el segundo nombre del usuario.
     * 
     * @param segundoNombre Segundo nombre opcional
     */
    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    /**
     * Obtiene el segundo apellido del usuario.
     * 
     * @return Segundo apellido o null si no está definido
     */
    public String getSegundoApellido() {
        return segundoApellido;
    }

    /**
     * Establece el segundo apellido del usuario.
     * 
     * @param segundoApellido Segundo apellido opcional
     */
    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    /**
     * Obtiene el número de celular del usuario.
     * 
     * @return Número de celular
     */
    public String getCelular() {
        return celular;
    }

    /**
     * Establece el número de celular del usuario.
     * 
     * @param celular Número de celular válido
     */
    public void setCelular(String celular) {
        this.celular = celular;
    }

    /**
     * Representación en texto del objeto RegisterRequest.
     * Oculta la contraseña por seguridad.
     * 
     * @return Representación string del objeto
     */
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", primerNombre='" + primerNombre + '\'' +
                ", primerApellido='" + primerApellido + '\'' +
                ", segundoNombre='" + segundoNombre + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", celular='" + celular + '\'' +
                '}';
    }
}
