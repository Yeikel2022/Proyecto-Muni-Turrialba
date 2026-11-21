package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.RegistroArchivoEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RegistroArchivoInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer todas los -
     * registros relacionados a los archivos -
     * del sistema. */
    @GET("api/archivos")
    Call<List<RegistroArchivoEntitie>> obtenerArchivos(@Query("tokenAcceso") String jwtToken);


    /* Metodo que sirve para traer el registro archivo -
     * que le fue compartido al usuario respectivamente. */
    @GET("api/obtenerArchivosCompartidos")
    Call<List<RegistroArchivoEntitie>> obtenerArchivosCompartidos(@Query("correoUsuario") String correoUsuario,
                                                                  @Query("tokenAcceso") String jwtToken);

    // |===========| Metodos de tipo: POST |===========|
    /* Metodo que sirve para registrar un nuevo registro -
     * de archivo respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearArchivos")
    Call<RegistroArchivoEntitie> crearRegistroArchivos(@Body RegistroArchivoEntitie registroArchivoEntitie,
                                                       @Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar un registro de -
     * archivo respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarArchivo")
    Call<Boolean> actualizarRegistroArchivo(@Body RegistroArchivoEntitie registroArchivoEntitie,
                                            @Query("correoEmpleado") String correoEmpleado,
                                            @Query("tokenAcceso") String jwtToken);


    /* Metodo que sirve para actualizar un el estado de -
     * un registro de archivo respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarEstadoArchivo")
    Call<Boolean> actualizarEstadoRegistroArchivo(@Query("estadoArchivo") String estadoArchivo,
                                                  @Query("fechaSubida") String fechaSubida,
                                                  @Query("correoEmpleado") String correoEmpleado,
                                                  @Query("tokenAcceso") String jwtToken);


    /* Metodo que sirve para compartir un registro de -
     * archivo respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/compartirArchivo")
    Call<Boolean> compartirRegistroArchivo(@Body RegistroArchivoEntitie registroArchivoEntitie,
                                           @Query("tokenAcceso") String jwtToken);


    // |===========| Metodos de tipo: DELETE |===========|
    /* Metodo que sirve para eliminar un registro de archivo -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @DELETE("api/eliminarArchivo")
    Call<Boolean> eliminarRegistroArchivo(@Query("correoEmpleado") String correoEmpleado,
                                          @Query("fechaSubida") String fechaSubida,
                                          @Query("tokenAcceso") String jwtToken);



}
