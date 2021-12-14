package es.upv.etsit.aatt.paco.trabajoaatt.ui.cartelera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CarteleraViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CarteleraViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Fragmento Inicio");
    }

    public LiveData<String> getText() {
        return mText;
    }
}