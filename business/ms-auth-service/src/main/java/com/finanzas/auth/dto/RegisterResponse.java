package com.finanzas.auth.dto;

/**
 * Data Transfer Object (DTO) para respuesta de registro de usuarios.
 * 
 * Esta clase encapsula la respuesta del sistema después de intentar
 * registrar un nuevo usuario. Proporciona información estructurada
 * sobre el resultado del proceso sin exponer datos sensibles
 * como contraseñas o información interna del sistema.
 * 
 * Es utilizada para mantener una interfaz consistente con el frontend
 * y facilitar el manejo de respuestas tanto exitosas como de error.
 * 
 * @author Sistema de Finanzas Personales
 * @version 1.0
 * @since 2024-01-01
 */
public class RegisterResponse {

    /**
     * Indica si el proceso de registro fue exitoso.
     * true: Usuario registrado correctamente
     * false: Error durante el registro
     */
    private boolean success;

    /**
     * Mensaje descriptivo del resultado del registro.
     * Proporciona información clara al usuario sobre el estado
     * de la operación (éxito o motivo del error).
     */
    private String message;

    /**
     * Nombre de usuario registrado.
     * Solo incluido en respuestas exitosas.
     */
    private String username;

    /**
     * Correo electrónico del usuario registrado.
     * Solo incluido en respuestas exitosas.
     */
    private String email;

    /**
     * Nombre completo del usuario registrado.
     * Calculado concatenando nombres y apellidos.
     * Solo incluido en respuestas exitosas.
     */
    private String nombreCompleto;

    /**
     * Iniciales del nombre completo del usuario.
     * Útil para display en interfaces de usuario.
     * Solo incluido en respuestas exitosas.
     */
    private String iniciales;

    /**
     * Constructor por defecto requerido por frameworks.
     */
    public RegisterResponse() {
    }

    /**
     * Constructor para respuestas exitosas.
     * Incluye toda la información relevante del usuario registrado.
     * 
     * @param message Mensaje de éxito
     * @param username Nombre de usuario registrado
     * @param email Correo electrónico registrado
     * @param nombreCompleto Nombre completo del usuario
     * @param iniciales Iniciales del nombre completo
     */
    public RegisterResponse(String message, String username, String email, 
                           String nombreCompleto, String iniciales) {
        this.success = true;
        this.message = message;
        this.username = username;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.iniciales = iniciales;
    }

    /**
     * Constructor para respuestas de error.
     * Solo incluye información básica del error.
     * 
     * @param message Mensaje descriptivo del error
     */
    public RegisterResponse(String message) {
        this.success = false;
        this.message = message;
    }

    /**
     * Factory method para crear respuesta de éxito.
     * Método estático conveniente para crear respuestas exitosas
     * con datos de usuario ya formateados.
     * 
     * @param username Nombre de usuario
     * @param email Correo electrónico
     * @param primerNombre Primer nombre
     * @param primerApellido Primer apellido
     * @param segundoNombre Segundo nombre (opcional)
     * @param segundoApellido Segundo apellido (opcional)
     * @return RegisterResponse con datos de éxito
     */
    public static RegisterResponse success(String username, String email,
                                         String primerNombre, String primerApellido,
                                         String segundoNombre, String segundoApellido) {
        // Construir nombre completo
        StringBuilder nombreCompleto = new StringBuilder();
        nombreCompleto.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.trim().isEmpty()) {
            nombreCompleto.append(" ").append(segundoNombre);
        }
        nombreCompleto.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.trim().isEmpty()) {
            nombreCompleto.append(" ").append(segundoApellido);
        }

        // Calcular iniciales
        String iniciales = primerNombre.substring(0, 1).toUpperCase() +
                          primerApellido.substring(0, 1).toUpperCase();

        return new RegisterResponse(
            "Usuario registrado exitosamente",
            username,
            email,
            nombreCompleto.toString().trim(),
            iniciales
        );
    }

    /**
     * Factory method para crear respuesta de error.
     * Método estático conveniente para crear respuestas de error
     * con mensaje personalizado.
     * 
     * @param errorMessage Mensaje de error descriptivo
     * @return RegisterResponse con información de error
     */
    public static RegisterResponse error(String errorMessage) {
        return new RegisterResponse(errorMessage);
    }

    // Getters y Setters con documentación

    /**
     * Verifica si el registro fue exitoso.
     * 
     * @return true si el registro fue exitoso, false si hubo error
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Establece el estado de éxito del registro.
     * 
     * @param success true para éxito, false para error
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Obtiene el mensaje descriptivo del resultado.
     * 
     * @return Mensaje del resultado del registro
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje descriptivo del resultado.
     * 
     * @param message Mensaje descriptivo
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Obtiene el nombre de usuario registrado.
     * 
     * @return Nombre de usuario o null si hubo error
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario registrado.
     * 
     * @param username Nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * 
     * @return Email del usuario o null si hubo error
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     * 
     * @param email Correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene el nombre completo del usuario.
     * 
     * @return Nombre completo formateado o null si hubo error
     */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    /**
     * Establece el nombre completo del usuario.
     * 
     * @param nombreCompleto Nombre completo formateado
     */
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    /**
     * Obtiene las iniciales del nombre completo.
     * 
     * @return Iniciales en mayúsculas o null si hubo error
     */
    public String getIniciales() {
        return iniciales;
    }

    /**
     * Establece las iniciales del nombre completo.
     * 
     * @param iniciales Iniciales en mayúsculas
     */
    public void setIniciales(String iniciales) {
        this.iniciales = iniciales;
    }

    /**
     * Representación en texto del objeto RegisterResponse.
     * Proporciona información clara del resultado del registro.
     * 
     * @return Representación string del objeto
     */
    @Override
    public String toString() {
        return "RegisterResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", iniciales='" + iniciales + '\'' +
                '}';
    }
}
