package es.upv.etsit.aatt.paco.trabajoaatt.ui.busqueda;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BusquedaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BusquedaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragmento ajustes");
    }

    public LiveData<String> getText() {
        return mText;
    }
}