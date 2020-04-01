package dae.mob123.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//omdat data lokaal zal worden opgeslaan (Room) -> overerven van AndroidViewModel
public class MuralViewModel extends AndroidViewModel {

    private LiveData<List<Mural>> murals;
    private ArrayList<Mural> artworkList;
    private MuralDatabase database;
    private final Application mApplication;
    public ExecutorService threadExecutor = Executors.newFixedThreadPool(4);

    public MuralViewModel(@NonNull Application application) {
    //in constructor aangeven in welke applicatie
        super(application);
        mApplication = application;
        database = MuralDatabase.getInstance(application);

        this.murals = new MutableLiveData<>();
        murals = database.getRepoDao().getAllMurals();
    }

    //Opvragen mural list
    public LiveData<List<Mural>> getMurals(){
        fetchMurals();
        return murals;
    }

    //Data binnenhalen van de web service!
    //Alle murals fetchen
    private void fetchMurals(){
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://bruxellesdata.opendatasoft.com/api/records/1.0/search/?dataset=comic-book-route&rows=58")
                        .get()
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();

                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonRecordsArray = jsonObject.getJSONArray("records");

                    int arraySize = jsonRecordsArray.length();
                    int i = 0;

                    artworkList = new ArrayList<>();

                    while(i < arraySize){
                        String jsonID = jsonRecordsArray.getJSONObject(i).getString("recordid");
                        JSONObject jsonArtwork = jsonRecordsArray.getJSONObject(i).getJSONObject("fields");

                        Mural currentMural = new Mural(
                                jsonID,
                                jsonArtwork.getString("auteur_s"),
                                jsonArtwork.getString("filename"),
                                jsonArtwork.getString("personnage_s"),
                                jsonArtwork.getString("annee"), new LatLng(jsonArtwork.getJSONArray("coordonnees_geographiques").getDouble(0),
                                                                                 jsonArtwork.getJSONArray("coordonnees_geographiques").getDouble(1))
                        );
                        artworkList.add(currentMural);
                        i++;
                    }

                } catch (IOException e){
                    e.printStackTrace();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void insertMural(final Mural m){
        MuralDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.getRepoDao().insertMural(m);
            }
        });
    }

    public void updateMural(final Mural m){
        MuralDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.getRepoDao().updateMural(m);
            }
        });
    }

    public void deleteMural(final Mural m){
        MuralDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.getRepoDao().deleteMural(m);
            }
        });
    }
}
