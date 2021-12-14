package es.upv.etsit.aatt.paco.trabajoaatt.ui.filmoteca;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilmotecaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FilmotecaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragmento filmoteca");
    }

    public LiveData<String> getText() {
        return mText;
    }
}