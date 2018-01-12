package com.adionisio.practicas.editnote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Alejandro on 03/01/2018.
 */

public interface RestClient {

    @GET("/notas/{idNota}/")
    Call<Nota> getDataId(
            @Path("idNota") int idNota
    );
    @PUT("/notas/{idNota}/")
    Call<Nota> putNota(@Path("idNota") int idNota, @Body Nota nota);

}
