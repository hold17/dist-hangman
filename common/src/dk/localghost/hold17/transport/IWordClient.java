package dk.localghost.hold17.transport;
import dk.localghost.hold17.dto.Results;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface IWordClient {
    @Headers({ "X-Mashape-Key: 1zkuLTuZIImshpTySOozqGWnihfvp1zv5bTjsnSdZuCu31mnQ3" })
    @GET("words/{word}")
    Call<Results> getResults(@Path("word") String word);
}
