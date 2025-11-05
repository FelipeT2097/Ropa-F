package modelo;

/**
 Gestión de sesión del empleado logueado
 */
public class Usuario_Sesion {
    
    private static Usuario_Sesion instancia = null;
    
    // Datos del empleado logueado
    private Integer idUsuario;
    private String nombreCompleto;
    private String nombreUsuario;
    private String rol;
    private boolean sesionActiva;
    
    // Constructor privado
    private Usuario_Sesion() {
        this.sesionActiva = false;
    }
    
    /**
     * Obtener la única instancia
     */
    public static Usuario_Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Usuario_Sesion();
        }
        return instancia;
    }
    
    /**
     * Iniciar sesión del empleado
     */
    public void iniciarSesion(Integer id, String nombreCompleto, String nombreUsuario, String rol) {
        this.idUsuario = id;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
        this.sesionActiva = true;
        
        System.out.println("✅ Sesión iniciada: " + nombreUsuario + " - Rol: " + rol);
    }
    
    /**
     * Cerrar sesión
     */
    public void cerrarSesion() {
        System.out.println("❌ Sesión cerrada: " + this.nombreUsuario);
        this.idUsuario = null;
        this.nombreCompleto = null;
        this.nombreUsuario = null;
        this.rol = null;
        this.sesionActiva = false;
    }
    
    /**
     * Verificar si hay sesión activa
     */
    public boolean isSesionActiva() {
        return sesionActiva;
    }
    
    /**
     * Verificar si es ADMIN
     */
    public boolean esAdmin() {
        return rol != null && rol.equalsIgnoreCase("admin");
    }
    
    /**
     * Verificar si es VENDEDOR
     */
    public boolean esVendedor() {
        return rol != null && rol.equalsIgnoreCase("vendedor");
    }
    
    /**
     * Verificar si es ALMACENISTA
     */
    public boolean esAlmacenista() {
        return rol != null && rol.equalsIgnoreCase("almacenista");
    }
    
    // Getters
    public Integer getIdUsuario() { return idUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getRol() { return rol; }
}