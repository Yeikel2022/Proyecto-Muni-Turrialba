package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtensionEmpleadoUsuarioEntitie {
    @SerializedName("nombre_Empleado")
    @Expose
    private String nombre_Empleado;
    @SerializedName("apellido_1_Empleado")
    @Expose
    private String apellido1_Empleado;
    @SerializedName("apellido_2_Empleado")
    @Expose
    private String apellido2_Empleado;
    @SerializedName("edad_Empleado")
    @Expose
    private Integer edad_Empleado;
    @SerializedName("cedula_Empleado")
    @Expose
    private String cedula_Empleado;
    @SerializedName("telefono_Empleado")
    @Expose
    private String telefono_Empleado;
    @SerializedName("correo_Electronico_Empleado")
    @Expose
    private String correo_Electronico_Empleado;
    @SerializedName("contraseña_Empleado")
    @Expose
    private String contraseña_Empleado;
    @SerializedName("nombre_Rol")
    @Expose
    private String nombre_Rol;
    @SerializedName("fecha_Creacion_Empleado")
    @Expose
    private String fecha_Creacion_Empleado;
    @SerializedName("departamento")
    @Expose
    private String departamento;
    @SerializedName("activo")
    @Expose
    private Boolean activo;


    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public ExtensionEmpleadoUsuarioEntitie(String nombre, String apellido_1, String apellido_2, int edad,
                                           String cedula, String telefono, String correo_Electronico,
                                           String contraseña, String nombre_Rol, String fecha_CreacionEmpleado,
                                           String departamento, boolean activo) {
        this.nombre_Empleado = nombre;
        this.apellido1_Empleado = apellido_1;
        this.apellido2_Empleado = apellido_2;
        this.edad_Empleado = edad;
        this.cedula_Empleado = cedula;
        this.telefono_Empleado = telefono;
        this.correo_Electronico_Empleado = correo_Electronico;
        this.contraseña_Empleado = contraseña;
        this.nombre_Rol = nombre_Rol;
        this.fecha_Creacion_Empleado = fecha_CreacionEmpleado;
        this.departamento = departamento;
        this.activo = activo;
    }


    public String getNombre_Empleado() {
        return nombre_Empleado;
    }

    public void setNombre_Empleado(String nombreEmpleado) {
        this.nombre_Empleado = nombreEmpleado;
    }

    public String getApellido1_Empleado() {
        return apellido1_Empleado;
    }

    public void setApellido1_Empleado(String apellido1Empleado) {
        this.apellido1_Empleado = apellido1Empleado;
    }

    public String getApellido2_Empleado() {
        return apellido2_Empleado;
    }

    public void setApellido2_Empleado(String apellido2Empleado) {
        this.apellido2_Empleado = apellido2Empleado;
    }

    public Integer getEdad_Empleado() {
        return edad_Empleado;
    }

    public void setEdad_Empleado(Integer edadEmpleado) {
        this.edad_Empleado = edadEmpleado;
    }

    public String getCedula_Empleado() {
        return cedula_Empleado;
    }

    public void setCedula_Empleado(String cedulaEmpleado) {
        this.cedula_Empleado = cedulaEmpleado;
    }

    public String getTelefono_Empleado() {
        return telefono_Empleado;
    }

    public void setTelefono_Empleado(String telefonoEmpleado) {
        this.telefono_Empleado = telefonoEmpleado;
    }

    public String getCorreo_Electronico_Empleado() {
        return correo_Electronico_Empleado;
    }

    public void setCorreo_Electronico_Empleado(String correoElectronicoEmpleado) {
        this.correo_Electronico_Empleado = correoElectronicoEmpleado;
    }

    public String getContraseña_Empleado() {
        return contraseña_Empleado;
    }

    public void setContraseña_Empleado(String contraseAEmpleado) {
        this.contraseña_Empleado = contraseAEmpleado;
    }

    public String getNombre_Rol() {
        return nombre_Rol;
    }

    public void setNombre_Rol(String nombreRol) {
        this.nombre_Rol = nombreRol;
    }

    public String getFecha_Creacion_Empleado() {
        return fecha_Creacion_Empleado;
    }

    public void setFecha_Creacion_Empleado(String fechaCreacionEmpleado) {
        this.fecha_Creacion_Empleado = fechaCreacionEmpleado;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}
