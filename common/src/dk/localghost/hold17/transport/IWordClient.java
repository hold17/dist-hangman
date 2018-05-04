package dk.localghost.hold17.transport;
import dk.localghost.hold17.dto.Definitions;
import dk.localghost.hold17.dto.Examples;
import dk.localghost.hold17.dto.Synonyms;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

import java.util.List;

public interface IWordClient {
    @Headers({ "X-Mashape-Key: 1zkuLTuZIImshpTySOozqGWnihfvp1zv5bTjsnSdZuCu31mnQ3" })
    @GET("words/{word}/definitions")
    Call<Definitions> getDefinition(@Path("word") String word);

    @Headers({ "X-Mashape-Key: 1zkuLTuZIImshpTySOozqGWnihfvp1zv5bTjsnSdZuCu31mnQ3" })
    @GET("words/{word}/synonyms")
    Call<Synonyms> getSynonyms(@Path("word") String word);

    @Headers({ "X-Mashape-Key: 1zkuLTuZIImshpTySOozqGWnihfvp1zv5bTjsnSdZuCu31mnQ3" })
    @GET("words/{word}/examples")
    Call<Examples> getExample(@Path("word") String word);
}
