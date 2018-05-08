package dk.localghost.hold17.transport;

import dk.localghost.hold17.dto.Definitions;
import dk.localghost.hold17.dto.Examples;
import dk.localghost.hold17.dto.Synonyms;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
//        WordService.getDefinitionAsync("dog").enqueue(new Callback<Definitions>() {
//            @Override
//            public void onResponse(Call<Definitions> call, Response<Definitions> response) {
//                Definitions def = response.body();
//                System.out.println("Definition: " + def.getDefinitions().get(0).getDefinition());
//            }
//
//            @Override
//            public void onFailure(Call<Definitions> call, Throwable throwable) {
//                System.err.println("Callback failed.");
//            }
//        });
//
//        WordService.getExampleAsync("dog").enqueue(new Callback<Examples>() {
//            @Override
//            public void onResponse(Call<Examples> call, Response<Examples> response) {
//                Examples examples = response.body();
//                System.out.println("Example: " + examples.getExamples().get(0));
//            }
//
//            @Override
//            public void onFailure(Call<Examples> call, Throwable throwable) {
//                System.err.println("Callback failed.");
//            }
//        });
//
//        WordService.getSynonymsAsync("dog").enqueue(new Callback<Synonyms>() {
//            @Override
//            public void onResponse(Call<Synonyms> call, Response<Synonyms> response) {
//                Synonyms synonyms = response.body();
//                System.out.println("Synonyms: {\n");
//                for(int i = 0; i < synonyms.getSynonyms().size(); i++) {
//                    System.out.print(synonyms.getSynonyms().get(i) + "\n");
//                }
//                System.out.println("}");
//            }
//
//            @Override
//            public void onFailure(Call<Synonyms> call, Throwable throwable) {
//
//            }
//        });

        try {
            URL url = new URL("https://db.localghost.dk/words.txt");
            Scanner scanner = new Scanner(url.openStream());
            List<String> words = new ArrayList<>();
            while(scanner.hasNextLine()) {
                words.add(scanner.nextLine());
            }
            for(int i = 0; i < words.size(); i++) {
                System.out.println(words.get(i));
            }
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }
}
