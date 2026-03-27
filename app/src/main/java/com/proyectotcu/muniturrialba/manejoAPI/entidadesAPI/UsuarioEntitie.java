package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsuarioEntitie {
    /* Estas son las variables que nos ayudaran -
     * para la entidad usuario. Además de que -
     * ya están serializadas para el formato: -
     * JSON respectivamente. */
    @SerializedName("nombre")
    @Expose
    private String nombre;

    @SerializedName("apellido_1")
    @Expose
    private String apellido_1;

    @SerializedName("apellido_2")
    @Expose
    private String apellido_2;

    @SerializedName("edad")
    @Expose
    private Integer edad;

    @SerializedName("cedula")
    @Expose
    private String cedula;

    @SerializedName("telefono")
    @Expose
    private String telefono;

    @SerializedName("correo_Electronico")
    @Expose
    private String correo_Electronico;

    @SerializedName("contraseña")
    @Expose
    private String contraseña;

    @SerializedName("fecha_Creacion")
    @Expose
    private String fecha_Creacion;

    @SerializedName("imagen_Perfil")
    @Expose
    private String imagen_Perfil;

    @SerializedName("id_Rol")
    @Expose
    private Integer id_Rol;

    /* Este constructor parametrizado, nos ayudara -
     * a realizar aquellos procesos donde se necesita -
     * enviar, actualizar o eliminar información de los -
     * usuarios. Además de indicarle al sistema, donde -
     * debe guardar la información respectivamente. */
    public UsuarioEntitie(String nombre, String apellido_1, String apellido_2,
                          int edad, String cedula, String telefono, String correo_Electronico,
                          String contraseña, String imagen_Perfil, int id_Rol) {
        this.nombre = nombre;
        this.apellido_1 = apellido_1;
        this.apellido_2 = apellido_2;
        this.edad = edad;
        this.cedula = cedula;
        this.telefono = telefono;
        this.correo_Electronico = correo_Electronico;
        this.contraseña = contraseña;
        this.imagen_Perfil = imagen_Perfil;
        this.id_Rol = id_Rol;
    }

    //Estos son los métodos GET y SET de las variables anteriores.
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido_1() {
        return apellido_1;
    }

    public void setApellido_1(String apellido1) {
        this.apellido_1 = apellido1;
    }

    public String getApellido_2() {
        return apellido_2;
    }

    public void setApellido_2(String apellido2) {
        this.apellido_2 = apellido2;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo_Electronico() {
        return correo_Electronico;
    }

    public void setCorreo_Electronico(String correoElectronico) {
        this.correo_Electronico = correoElectronico;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getFecha_Creacion() {
        return fecha_Creacion;
    }

    public void setFecha_Creacion(String fechaCreacion) {
        this.fecha_Creacion = fechaCreacion;
    }

    public String getImagen_Perfil() {
        return imagen_Perfil;
    }

    public void setImagen_Perfil(String imagenPerfil) {
        this.imagen_Perfil = imagenPerfil;
    }

    public Integer getId_Rol() {
        return id_Rol;
    }

    public void setId_Rol(Integer idRol) {
        this.id_Rol = idRol;
    }
}
