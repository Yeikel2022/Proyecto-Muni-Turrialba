package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.JWTEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer a todos los usuarios -
     * del sistema. */
    @GET("api/usuarios")
    Call<List<UsuarioEntitie>> obtenerUsuarios();

    /* Metodo que sirve para traer el usuario deseado, -
     * por medio del correo electronico. */
    @GET("api/usuario/{correo}")
    Call<UsuarioEntitie> obtenerUsuario (@Path("correo") String correo,
                                         @Query("tokenAcceso") String jwtToken);

    /* Metodo que sirve para enviar el codigo recibido -
     * respectivamente. */
    @GET("api/validarcodigo/{codigo}")
    Call<Boolean> enviarCodigo(@Path("codigo") String codigo);

    /* Metodo que sirve para obtener el codigo QR -
     * para el carnet virtual respectivamente. */
    @GET("api/obtenerQR")
    Call<String> obtenerCodigoQR(@Query("nombre") String nombre, @Query("apellidos") String apellidos,
                                 @Query("correo")String correo, @Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: POST |===========|

    /* Metodo que sirve para que el usuario pueda iniciar -
     * sesión respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/iniciarsesion")
    Call<JWTEntitie> iniciarSesion(@Body UsuarioEntitie usuarioEntitie);

    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearusuarios")
    Call<UsuarioEntitie> registrarUsuario(@Body UsuarioEntitie usuarioEntitie,
                                          @Query("tipo") boolean tipo);

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


    // |===========| Metodos de tipo: PUT |===========|

    /* Metodo que sirve para que el usuario pueda actualizar -
     * la contraseña de su cuenta respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarcontraseña")
    Call<Boolean> actualizarContraseña(@Body ExtensionUsuarioEntitie extensionUsuarioEntitie);

    /* Metodo que sirve para que el usuario pueda cambiar -
     * la imagen de perfil respectivamente. */
    @Multipart
    @PUT("api/cambiarFoto")
    Call<Boolean> cambiarFotoPerfil(@Part MultipartBody.Part foto,
                                    @Query("tokenAcceso") String jwtToken);

    /* Metodo que sirve para que el usuario pueda actualizar -
     * la contraseña de su cuenta respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarUsuario")
    Call<Boolean> actualizarUsuario(@Body UsuarioEntitie usuarioEntitie,
                                    @Query("cedula") String cedula,
                                    @Query("tokenAcceso") String jwtToken);


}
