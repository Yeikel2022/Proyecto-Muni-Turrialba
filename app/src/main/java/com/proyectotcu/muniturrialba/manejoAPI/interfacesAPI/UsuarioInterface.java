package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioInterface {

    /* Metodo que sirve para traer a todos los usuarios -
     * del sistema. */
    @GET("api/usuarios")
    Call<List<UsuarioEntitie>> obtenerUsuarios();

    /* Metodo que sirve para traer el usuario deseado, -
     * por medio del correo electronico. */
    @GET("/api/usuario/{correo}")
    Call<UsuarioEntitie> obtenerUsuario (@Path("correo") String correo);

    /* Metodo que sirve para enviar el codigo recibido -
     * respectivamente. */
    @GET("api/validarcodigo/{codigo}")
    Call<Boolean> enviarCodigo(@Path("codigo") String codigo);


    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearusuarios")
    Call<UsuarioEntitie> enviarUsuario(@Body UsuarioEntitie usuarioEntitie);

    /* Metodo que sirve para enviar el correo electronico -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/enviarcorreo/{correo}")
    Call<UsuarioEntitie> enviarCorreo(@Path("correo") String correo);

    /* Metodo que sirve para enviar el correo electronico -
     * respectivamente.*/
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarcontraseña")
    Call<Boolean> actualizarContraseña(@Body ExtensionUsuarioEntitie extensionUsuarioEntitie);


}
