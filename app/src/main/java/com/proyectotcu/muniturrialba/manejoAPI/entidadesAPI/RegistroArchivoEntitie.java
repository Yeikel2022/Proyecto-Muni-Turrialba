package com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegistroArchivoEntitie {
    @SerializedName("archivo")
    @Expose
    private String archivo;
    @SerializedName("nombre_Archivo")
    @Expose
    private String nombreArchivo;
    @SerializedName("formato_Archivo")
    @Expose
    private String formatoArchivo;
    @SerializedName("fecha_Subida")
    @Expose
    private String fechaSubida;
    @SerializedName("fecha_Actualizacion")
    @Expose
    private String fechaActualizacion;
    @SerializedName("fecha_Envio")
    @Expose
    private String fechaEnvio;
    @SerializedName("nombre_Originario")
    @Expose
    private String nombreOriginario;
    @SerializedName("apellido_1_Originario")
    @Expose
    private String apellido_1_Originario;
    @SerializedName("apellido_2_Originario")
    @Expose
    private String apellido_2_Originario;
    @SerializedName("correo_Electronico_Originario")
    @Expose
    private String correoElectronico_Originario;
    @SerializedName("departamento_Originario")
    @Expose
    private String departamentoOriginario;
    @SerializedName("nombre_Destinatario")
    @Expose
    private String nombreDestinatario;
    @SerializedName("apellido_1_Destinatario")
    @Expose
    private String apellido_1_Destinatario;
    @SerializedName("apellido_2_Destinatario")
    @Expose
    private String apellido_2_Destinatario;
    @SerializedName("correo_Electronico_Destinatario")
    @Expose
    private String correoElectronico_Destinatario;
    @SerializedName("departamento_Destinatario")
    @Expose
    private String departamentoDestinatario;
    @SerializedName("estado_Archivo")
    @Expose
    private String estadoArchivo;

    public RegistroArchivoEntitie(String archivo, String nombreArchivo, String formatoArchivo, String fechaSubida, String nombreOriginario,
                                  String apellido_1_Originario, String apellido_2_Originario, String correoElectronico_Originario,
                                  String departamentoOriginario, String nombreDestinatario, String apellido_1_Destinatario,
                                  String apellido_2_Destinatario, String correoElectronico_Destinatario, String departamentoDestinatario,
                                  String estadoArchivo) {
        this.archivo = archivo;
        this.nombreArchivo = nombreArchivo;
        this.formatoArchivo = formatoArchivo;
        this.fechaSubida = fechaSubida;
        this.nombreOriginario = nombreOriginario;
        this.apellido_1_Originario = apellido_1_Originario;
        this.apellido_2_Originario = apellido_2_Originario;
        this.correoElectronico_Originario = correoElectronico_Originario;
        this.departamentoOriginario = departamentoOriginario;
        this.nombreDestinatario = nombreDestinatario;
        this.apellido_1_Destinatario = apellido_1_Destinatario;
        this.apellido_2_Destinatario = apellido_2_Destinatario;
        this.correoElectronico_Destinatario = correoElectronico_Destinatario;
        this.departamentoDestinatario = departamentoDestinatario;
        this.estadoArchivo = estadoArchivo;
    }


    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getFormatoArchivo() {
        return formatoArchivo;
    }

    public void setFormatoArchivo(String formatoArchivo) {
        this.formatoArchivo = formatoArchivo;
    }

    public String getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(String fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getNombreOriginario() {
        return nombreOriginario;
    }

    public void setNombreOriginario(String nombreOriginario) {
        this.nombreOriginario = nombreOriginario;
    }

    public String getApellido_1_Originario() {
        return apellido_1_Originario;
    }

    public void setApellido_1_Originario(String apellido_1_Originario) {
        this.apellido_1_Originario = apellido_1_Originario;
    }

    public String getApellido_2_Originario() {
        return apellido_2_Originario;
    }

    public void setApellido_2_Originario(String apellido_2_Originario) {
        this.apellido_2_Originario = apellido_2_Originario;
    }

    public String getCorreoElectronico_Originario() {
        return correoElectronico_Originario;
    }

    public void setCorreoElectronico_Originario(String correoElectronico_Originario) {
        this.correoElectronico_Originario = correoElectronico_Originario;
    }

    public String getDepartamentoOriginario() {
        return departamentoOriginario;
    }

    public void setDepartamentoOriginario(String departamentoOriginario) {
        this.departamentoOriginario = departamentoOriginario;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getApellido_1_Destinatario() {
        return apellido_1_Destinatario;
    }

    public void setApellido_1_Destinatario(String apellido_1_Destinatario) {
        this.apellido_1_Destinatario = apellido_1_Destinatario;
    }

    public String getApellido_2_Destinatario() {
        return apellido_2_Destinatario;
    }

    public void setApellido_2_Destinatario(String apellido_2_Destinatario) {
        this.apellido_2_Destinatario = apellido_2_Destinatario;
    }

    public String getCorreoElectronico_Destinatario() {
        return correoElectronico_Destinatario;
    }

    public void setCorreoElectronico_Destinatario(String correoElectronico_Destinatario) {
        this.correoElectronico_Destinatario = correoElectronico_Destinatario;
    }

    public String getDepartamentoDestinatario() {
        return departamentoDestinatario;
    }

    public void setDepartamentoDestinatario(String departamentoDestinatario) {
        this.departamentoDestinatario = departamentoDestinatario;
    }

    public String getEstadoArchivo() {
        return estadoArchivo;
    }

    public void setEstadoArchivo(String estadoArchivo) {
        this.estadoArchivo = estadoArchivo;
    }
}
