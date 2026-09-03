package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExtensionInicioSesionEntitie implements Parcelable {
    @SerializedName("nombre")
    @Expose
    private String nombre;

    @SerializedName("apellido_1")
    @Expose
    private String apellido_1;

    @SerializedName("apellido_2")
    @Expose
    private String apellido_2;

    @SerializedName("cedula")
    @Expose
    private String cedula;

    @SerializedName("correo_Electronico")
    @Expose
    private String correo_Electronico;

    @SerializedName("departamento")
    @Expose
    private String departamento;

    @SerializedName("nombre_Rol")
    @Expose
    private String nombre_Rol;

    @SerializedName("fecha_Creacion")
    @Expose
    private String fecha_Creacion;

    @SerializedName("fecha_Inicio_Sesion")
    @Expose
    private String fecha_Inicio_Sesion;

    @SerializedName("ultima_Conexion")
    @Expose
    private String ultima_Conexion;

    protected ExtensionInicioSesionEntitie(Parcel tipo) {
        nombre = tipo.readString();
        apellido_1 = tipo.readString();
        apellido_2 = tipo.readString();
        cedula = tipo.readString();
        correo_Electronico = tipo.readString();
        departamento = tipo.readString();
        nombre_Rol = tipo.readString();
        fecha_Creacion = tipo.readString();
        fecha_Inicio_Sesion = tipo.readString();
        ultima_Conexion = tipo.readString();
    }

    public static final Creator<ExtensionInicioSesionEntitie> CREATOR = new Creator<ExtensionInicioSesionEntitie>() {
        @Override
        public ExtensionInicioSesionEntitie createFromParcel(Parcel source) {
            return new ExtensionInicioSesionEntitie(source);
        }

        @Override
        public ExtensionInicioSesionEntitie[] newArray(int size) {
            return new ExtensionInicioSesionEntitie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(apellido_1);
        dest.writeString(apellido_2);
        dest.writeString(cedula);
        dest.writeString(correo_Electronico);
        dest.writeString(departamento);
        dest.writeString(nombre_Rol);
        dest.writeString(fecha_Creacion);
        dest.writeString(fecha_Inicio_Sesion);
        dest.writeString(ultima_Conexion);
    }

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

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo_Electronico() {
        return correo_Electronico;
    }

    public void setCorreo_Electronico(String correoElectronico) {
        this.correo_Electronico = correoElectronico;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNombre_Rol() {
        return nombre_Rol;
    }

    public void setNombre_Rol(String nombreRol) {
        this.nombre_Rol = nombreRol;
    }

    public String getFecha_Creacion() {
        return fecha_Creacion;
    }

    public void setFecha_Creacion(String fechaCreacion) {
        this.fecha_Creacion = fechaCreacion;
    }

    public String getFecha_Inicio_Sesion() {
        return fecha_Inicio_Sesion;
    }

    public void setFecha_Inicio_Sesion(String fechaInicioSesion) {
        this.fecha_Inicio_Sesion = fechaInicioSesion;
    }

    public String getUltima_Conexion() {
        return ultima_Conexion;
    }

    public void setUltima_Conexion(String ultimaConexion) {
        this.ultima_Conexion = ultimaConexion;
    }


}
