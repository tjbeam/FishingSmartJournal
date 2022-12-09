package com.example.myapplication.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ForecastViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ForecastViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Spot Forecaster");
    }

    public LiveData<String> getText() {
        return mText;
    }
}