package dk.localghost.hold17.transport;
import dk.localghost.hold17.dto.Definitions;
import dk.localghost.hold17.dto.Examples;
import dk.localghost.hold17.dto.Synonyms;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordService {
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("https://wordsapiv1.p.mashape.com")
            .addConverterFactory(GsonConverterFactory.create());

    public static Call<Definitions> getDefinitionAsync(final String word) {
        final Retrofit retrofit = builder.build();

        IWordClient wordClient = retrofit.create(IWordClient.class);
        return wordClient.getDefinition(word);
    }

    public static Call<Examples> getExampleAsync(final String word) {
        final Retrofit retrofit = builder.build();

        IWordClient wordClient = retrofit.create(IWordClient.class);
        return wordClient.getExample(word);
    }

    public static Call<Synonyms> getSynonymsAsync(final String word) {
        final Retrofit retrofit = builder.build();

        IWordClient wordClient = retrofit.create(IWordClient.class);
        return wordClient.getSynonyms(word);
    }

}
