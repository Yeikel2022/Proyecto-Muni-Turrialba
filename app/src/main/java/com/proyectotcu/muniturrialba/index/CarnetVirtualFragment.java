package com.proyectotcu.muniturrialba.index;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarnetVirtualFragment extends Fragment {

    //Variables globales para esta clase.
    ImageView carnetQR, fotoPerfil;
    TextView txtMensaje;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* A diferencia de las actividades, al ser esto un fragmento, no se -
     * puede usar el ViewBinding para acceder a los recursos de una forma -
     * más sencilla, por lo que se hizo de la manera manual respectivamente. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Lo primero seria obtener el fragmento que esta relacionado -
         * al carnet virtual y también los botones que estan relacionados -
         * al QR del carnet virtual, y la foto de perfil dentro de la -
         * aplicación móvil respectivamente.
         *
         * Además del texto que contiene el mensaje de error o de negación -
         * relacionada a la autorización de permisos. Como también de ocultar -
         * los botones para evitar cualquier situación de que alguien logre -
         * saltarse la validación de los roles. */
        View view = inflater.inflate(R.layout.fragment_carnet_virtual, container, false);
        carnetQR = view.findViewById(R.id.img_CarnetQR);
        fotoPerfil = view.findViewById(R.id.img_carnetFotoPerfil);
        txtMensaje = view.findViewById(R.id.txt_MensajeCarnet);

        carnetQR.setVisibility(View.INVISIBLE);
        fotoPerfil.setVisibility(View.INVISIBLE);
        txtMensaje.setVisibility(View.GONE);

        try {
            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getActivity().getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            /* Aqui lo que se esta haciendo es crear una lista con todas las partes -
             * separadas que contiene el token recibido por parte del API. Esto por -
             * medio del comando: "split("\\.")", el cual nos ayuda a separar dicho -
             * token por los puntos que contiene, de ahi el \\., ya que es una expre-
             * sión regular que nos permite realizar esa acción respectivamente. */
            String[] partesToken = tokenGuardado.split("\\.");

            /* Aqui lo que se esta haciendo es decodificar la parte que contiene los -
             * claims del token, esto porque se necesita saber el rol y los permisos -
             * de dicho usuario. */
            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
             * para poder almacenar correctamente los datos que fueron gu -
             * ardados en la variable: "cuerpoToken". Esto porque el token -
             * en si, esta en un formato JSON respectivamente. */
            JSONObject json = new JSONObject(cuerpoToken);

            /* Después de crear ese objeto, entonces lo que se haria es -
             * guardar el nombre, los apellidos, el correo, el rol y los -
             * permisos del usuario que inicio sesión en el campo respectivo, -
             * de modo que así se puedan utilizar para validar cuales opciones -
             * puede acceder y también para verificar si dicho usuario tiene la -
             * autorización necesaria respectivamente. */
            String campoNombre = json.optString("nombre");
            String campoApellidos = json.optString("primer_Apellido") + " " + json.optString("segundo_Apellido");
            String campoCorreo = json.optString("correo");
            Integer campoRol = Integer.parseInt(json.optString("rol"));

            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));

            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador", -
             * "Administrador" o "Empleado". Y si no, entonces no lo dejaria acceder a las -
             * opciones del perfil. */
            if (campoRol == 1 || campoRol == 2 || campoRol == 3) {
                /* Una vez hecho eso, lo que seguiria seria validar los permisos del usuario -
                 * que ha iniciado sesión. Y, si luego de eso, la respuesta que trae es un 4, -
                 * entonces quiere decir que si esta autorizado para acceder al carnet virtual. */
                Integer respuestaPermisos = ValidarPermisos(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 4) {
                    carnetQR.setVisibility(View.VISIBLE);
                    fotoPerfil.setVisibility(View.VISIBLE);
                    MostrarQR(campoNombre, campoApellidos, campoCorreo, tokenGuardado);
                }

                /* Asimismo, también se hace otra validación, ya que, si la respuesta que trae -
                 * es un 1, quiere decir que ese usuario que inicio sesión no contiene el permiso -
                 * de leer, por lo que no esta autorizado para visualizar el carnet respectivamente. */
                if (respuestaPermisos == 1) {
                    carnetQR.setVisibility(View.GONE);
                    fotoPerfil.setVisibility(View.GONE);
                }
            }
        } catch (Exception error) {
            Toast.makeText(getActivity(), "¡Lo sentimos, pero parece que hubo un problema técnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" + "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
        }

        return view;
    }


    /* Metodo que sirve para mostrar el QR del usuario, esto dentro -
     * de la aplicación móvil respectivamente. */
    private void MostrarQR(String nombreUsuario, String apellidosUsuario, String correoUsuario, String tokenUsuario) {
        /* Aquí se llama la conexión del API. Además de indicarle -
         * también que en este fragmento (y clase) se esta pidiendo -
         * esa conexión del API como tal, de ahí el porque se esta -
         * pasando lo que tiene la variable: "nombreActividad".
         *
         * NOTA: Se uso un getActivity() en la variable: "nombreActividad" -
         * porque es la manera en la cual se puede saber cual es la actividad -
         * que esta haciendo uso de los fragmentos (que este caso es el menu -
         * principal), haciendo que la aplicacion puede saber donde se esta -
         * haciendo el llamado. Osea una instancia de la clase. */
        Activity nombreActividad = getActivity();
        usuarioInterface = ConexionAPI.Conexion_API(nombreActividad);

        /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
         * así poder enviar el nombre, los apellidos, el correo y el token para que el API -
         * lo pueda recibir y obtener el QR del usuario. Esto por medio de la interfaz respectiva. */
        Call<String> codigoQR = usuarioInterface.obtenerCodigoQR(nombreUsuario, apellidosUsuario, correoUsuario, tokenUsuario);

        /* Además, también se hace otra petición con el metodo respectivo, para así poder enviar -
         * el correo y el token para que el API lo pueda recibir y obtener los datos del usuario. -
         * Esto por medio de la interfaz respectiva. */
        Call<UsuarioEntitie> usuario = usuarioInterface.obtenerUsuario(correoUsuario, tokenUsuario);


        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
         * el metodo GET.
         *
         * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
         * podemos saber la posible respuesta que pudo brindar el API como tal. */
        codigoQR.enqueue(new Callback<String>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    /* Aqui lo que se está haciendo es colocar el código QR que representaria -
                     * al carnet virtual, pero si sucede alguna situación con el QR, entonces -
                     * en su lugar se pondra un logo por defecto. */
                    try {
                        String CodigoQR = response.body().toString().trim();
                        byte[] lista = Base64.decode(CodigoQR, Base64.DEFAULT);
                        Bitmap mapeo = BitmapFactory.decodeByteArray(lista, 0, lista.length);

                        carnetQR.setImageBitmap(mapeo);
                    } catch (Exception error) {
                        carnetQR.setImageResource(R.drawable.rounded_id_card_24);
                    }
                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();

                        /* Aqui lo que se hace es colocar el logo del carnet por defecto.
                         * Esto por temas de buenas prácticas. */
                        carnetQR.setImageResource(R.drawable.rounded_id_card_24);

                        /* Esto es para imprimir los mensajes de error. */
                        Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Pero en este momento no es posible ver su carnet virtual debido a que " + error, Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();

                        /* Este sirve solo para el logcat:
                         * System.out.println(error); */
                    } catch (Exception error) {
                        /* El printStackTrace(), sirve para aspectos de depuración.
                         * Esto debido a que ayuda a entender donde y porque ocurrio -
                         * un error durante la ejecución del proyecto. En este caso -
                         * las excepciones respectivamente.
                         *
                         * error.printStackTrace(); */

                        /* Aqui lo que se hace es colocar el logo del carnet por defecto.
                         * Esto por temas de buenas prácticas. */
                        carnetQR.setImageResource(R.drawable.rounded_id_card_24);

                        Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Pero no es posible visualizar su carnet virtual en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            /* Aqui es para saber si hubo un fallo en dar la respuesta -
             * por parte del API. */
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                 * y también para ver en que fallo en el API.
                 *
                 * NOTA: Este comando es para ver que fallo, el usuario no lo debe -
                 * ver:
                 * Toast.makeText(getActivity(), t.getLocalizedMessage(),
                 * Toast.LENGTH_SHORT).show(); */
                carnetQR.setImageResource(R.drawable.rounded_id_card_24);

                Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Pero no es posible visualizar su carnet virtual en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
            }
        });


        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
         * el metodo GET.
         *
         * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
         * podemos saber la posible respuesta que pudo brindar el API como tal. */
        usuario.enqueue(new Callback<UsuarioEntitie>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                if (response.isSuccessful()) {
                    /* Aqui lo que se está haciendo es colocar la imagen del perfil, pero -
                     * siempre y cuando el usuario haya colocado una imagen, si no entonces -
                     * en su lugar se pondra un logo de perfil por defecto.
                     *
                     * Esto es asi porque en el registro no se pide una imagen como tal, de -
                     * ahi que se pone aqui un try catch para detectar si esta nulo la imagen -
                     * para luego hacer esa acción. */
                    try {
                        String FotoPerfil = response.body().getImagen_Perfil().toString().trim();
                        byte[] lista = Base64.decode(FotoPerfil, Base64.DEFAULT);
                        Bitmap mapeo = BitmapFactory.decodeByteArray(lista, 0, lista.length);

                        fotoPerfil.setImageBitmap(mapeo);
                    } catch (Exception error) {
                        fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);
                    }
                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();

                        /* Aqui lo que se hace es colocar el logo de imagen por defecto.
                         * Esto por temas de buenas prácticas. */
                        fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);

                        /* Esto es para imprimir los mensajes de error. */
                        Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Pero en este momento no es posible ver la información debido a que " + error, Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();

                        /* Este sirve solo para el logcat:
                         * System.out.println(error); */
                    } catch (Exception error) {
                        /* El printStackTrace(), sirve para aspectos de depuración.
                         * Esto debido a que ayuda a entender donde y porque ocurrio -
                         * un error durante la ejecución del proyecto. En este caso -
                         * las excepciones respectivamente.
                         *
                         * error.printStackTrace(); */

                        /* Aqui lo que se hace es colocar el logo de imagen por defecto.
                         * Esto por temas de buenas prácticas. */
                        fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);

                        Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Pero no es posible visualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            /* Aqui es para saber si hubo un fallo en dar la respuesta -
             * por parte del API. */
            @Override
            public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                 * y también para ver en que fallo en el API.
                 *
                 * NOTA: Este comando es para ver que fallo, el usuario no lo debe -
                 * ver:
                 * Toast.makeText(getActivity(), t.getLocalizedMessage(),
                 * Toast.LENGTH_SHORT).show(); */

                /* Aqui lo que se hace es colocar el logo de imagen por defecto.
                 * Esto por temas de buenas prácticas. */
                fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);

                Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Pero no es posible visualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
            }
        });

    }


    /* Metodo que sirve para validar los permisos del usuario -
     * que ha iniciado sesión, esto para poder continuar con -
     * la navegación dentro de la aplicación móvil respectivamente. */
    private Integer ValidarPermisos(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        /* Aqui valida si todos los permisos de ese usuario son falsos -
         * (dando a entender que no esta autorizado). Y si entra, entonces -
         * se ocultaria todas las opciones del carnet virtual y se enviaria -
         * un mensaje mencionando que no tiene la autorización suficiente -
         * para poder continuar. */
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            carnetQR.setVisibility(View.GONE);
            fotoPerfil.setVisibility(View.GONE);

            txtMensaje.setVisibility(View.VISIBLE);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_carnet_virtual.xml), esto porque es una forma dinamica de hacerlo. */

            Toast.makeText(getActivity(), "¡No tienes la autorización necesaria para visualizar esta información!", Toast.LENGTH_LONG).show();
            return 0;
        }

        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso el carnet virtual). Y si entra, entonces -
         * se ocultaria todas las opciones del carnet virtual y devolveria -
         * un 1, indicando que no puede ver dichas opciones. */
        if(Leer == false) {
            Toast.makeText(getActivity(), "¡No tienes la autorización necesaria para visualizar esta información!", Toast.LENGTH_LONG).show();
            return 1;
        }

        //El 4 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 4;
    }


}