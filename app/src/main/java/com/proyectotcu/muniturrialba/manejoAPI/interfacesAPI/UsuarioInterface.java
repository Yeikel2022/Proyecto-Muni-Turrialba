package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.JWTEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoEntitie;
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
    @GET("api/usuario/{correo}")
    Call<UsuarioEntitie> obtenerUsuario (@Path("correo") String correo);

    /* Metodo que sirve para enviar el codigo recibido -
     * respectivamente. */
    @GET("api/validarcodigo/{codigo}")
    Call<Boolean> enviarCodigo(@Path("codigo") String codigo);


    /* Metodo que sirve para que el usuario pueda inciar -
     * sesión respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/iniciarsesion")
    Call<JWTEntitie> iniciarSesion(@Body UsuarioEntitie usuarioEntitie);

    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearusuarios")
    Call<UsuarioEntitie> registrarUsuario(@Body UsuarioEntitie usuarioEntitie);

    /* Metodo que sirve para crearle los permisos al usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("pendiente")
    Call<PermisoEntitie> crearPermisoUsuario(@Body PermisoEntitie permisosEntitie);

    /* Metodo que sirve para enviar el correo electronico -
     * del usuario que esta intentando recuperar la contra-
     * seña y cuenta respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/enviarcorreo/{correo}")
    Call<Boolean> enviarCorreo(@Path("correo") String correo);


    /* Metodo que sirve para que el usuario pueda actualizar -
     * la contraseña de su cuenta respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarcontraseña")
    Call<Boolean> actualizarContraseña(@Body ExtensionUsuarioEntitie extensionUsuarioEntitie);


}
