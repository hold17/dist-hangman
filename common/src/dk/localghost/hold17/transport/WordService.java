package dk.localghost.hold17.transport;
import dk.localghost.hold17.dto.Results;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordService {

    private static Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://wordsapiv1.p.mashape.com")
                .addConverterFactory(GsonConverterFactory.create());

    public static Call<Results> getResults(final String word) throws IndexOutOfBoundsException {

        final Retrofit retrofit = builder.build();

     IWordClient wordClient = retrofit.create(IWordClient.class);
     return wordClient.getResults(word);
    }
}
