package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.EmpleadoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionEmpleadoUsuarioEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface EmpleadoInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer todas las preguntas -
     * y respuestas del sistema. */
    @GET("api/empleados")
    Call<List<ExtensionEmpleadoUsuarioEntitie>> obtenerEmpleados(@Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: POST |===========|
    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearEmpleados")
    Call<EmpleadoEntitie> crearEmpleado(@Body EmpleadoEntitie empleadoEntitie,
                                        @Query("correoEmpleado") String correo,
                                        @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarEmpleado")
    Call<Boolean> actualizarEmpleado(@Body EmpleadoEntitie empleadoEntitie,
                                     @Query("correoEmpleado") String correo,
                                     @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: DELETE |===========|
    /* Metodo que sirve para eliminar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @DELETE("api/eliminarEmpleado")
    Call<Boolean> eliminarEmpleado(@Query("correoEmpleado") String correoEmpleado,
                              @Query("tokenAcceso") String jwtToken);



}
