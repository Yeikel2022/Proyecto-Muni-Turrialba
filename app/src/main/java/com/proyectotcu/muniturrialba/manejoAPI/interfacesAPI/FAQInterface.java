package com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI;

import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.FAQEntitie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface FAQInterface {

    // |===========| Metodos de tipo: GET |===========|

    /* Metodo que sirve para traer todas las preguntas -
     * y respuestas del sistema. */
    @GET("api/obtenerFAQs")
    Call<List<FAQEntitie>> obtenerFAQs(@Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: POST |===========|
    /* Metodo que sirve para registrar a un nuevo usuario -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @POST("api/crearFAQ")
    Call<FAQEntitie> crearFAQ(@Body FAQEntitie faqEntitie,
                              @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: PUT |===========|
    /* Metodo que sirve para actualizar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @PUT("api/actualizarFAQ")
    Call<Boolean> actualizarFAQ(@Body FAQEntitie faqEntitie,
                                @Query("preguntaActual") String preguntaRecorrida,
                                @Query("tokenAcceso") String jwtToken);



    // |===========| Metodos de tipo: DELETE |===========|
    /* Metodo que sirve para eliminar una pregunta y respuesta -
     * respectivamente. */
    @Headers("Content-Type: application/json")
    @DELETE("api/eliminarFAQ")
    Call<Boolean> eliminarFAQ(@Query("preguntaFAQ") String preguntaFAQ,
                              @Query("tokenAcceso") String jwtToken);



}
