package com.example.myapplication.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.Report;
import com.example.myapplication.databinding.FragmentForecastBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    private FragmentForecastBinding binding;
    EditText tideNumber;
    Spinner ebbFSpinner;
    Spinner timeSpinner;
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //set up fragment
        ForecastViewModel forecastViewModel =
                new ViewModelProvider(this).get(ForecastViewModel.class);

        binding = FragmentForecastBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        forecastViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //set up ebb/flood spinner
        ebbFSpinner = root.findViewById(R.id.EbbFForecast);
        String[] ebbFOptions = {"Both","Ebb/Outgoing", "Flood/Incoming"};
        ArrayAdapter<String> ebbFSpinnerAdapter
                = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, ebbFOptions);
        ebbFSpinnerAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        ebbFSpinner.setAdapter(ebbFSpinnerAdapter);

        //set up item selected listener to filter results with input
        ebbFSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set up time spinner
        timeSpinner = root.findViewById(R.id.TimeForecastSpinner);
        String[] timeOfDayOptions = {"All","Dawn", "Early Day", "Late Day", "Dusk", "Night"};
        ArrayAdapter<String> timeOfDayAdapter
                = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, timeOfDayOptions);
        timeOfDayAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeOfDayAdapter);
        //set up item selected listener to filter results with input
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set up tide edit text and add listener to filter results after input
        tideNumber = (EditText) root.findViewById(R.id.TideLevelForecast);
        tideNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterResults();
            }
        });

        listView = (ListView) root.findViewById(R.id.ForecastList);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //goes through all reports, calculates scores, and updates listview
    public void filterResults(){

        //fetch tide level if entered
        float tideLevel = -1000;
        try {
            tideLevel = Float.parseFloat(tideNumber.getText().toString());
        }catch(NumberFormatException nfe){
            tideLevel = -1000; //set to indicate no tide level
        }

        //get ebb/flood value
        boolean isEbb = ebbFSpinner.getSelectedItemPosition() == 1;

        //get time of day value
        int timeOfDay = -1;
        switch(timeSpinner.getSelectedItemPosition()){
            case 0:
                timeOfDay = -1;
                break;
            case 1:
                timeOfDay = Report.TIME_DAWN;
                break;
            case 2:
                timeOfDay = Report.TIME_EARLY_DAY;
                break;
            case 3:
                timeOfDay = Report.TIME_LATE_DAY;
                break;
            case 4:
                timeOfDay = Report.TIME_DUSK;
                break;
            case 5:
                timeOfDay = Report.TIME_NIGHT;
                break;
        }



        ArrayList<String> stringResults = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        //for each location prepare totals
        for (String loc:Report.locations){
            int totalFish = 0;
            float totalHours = 0;
            int totalScore = 0;
            //go through each report
            for (Report r:Report.reports){
                int score = 1;
                //if the report matches the current location proceed
                if(r.location.equalsIgnoreCase(loc)){
                    totalFish += r.numFish;
                    totalHours += r.timeFished;
                    //adjust score multiplier by filter matches
                    if(r.tideLevel >= tideLevel - .5f && r.tideLevel <= tideLevel + .5f){
                        score += 2;
                    }
                    if(ebbFSpinner.getSelectedItemPosition() != 0 && r.isEbb == isEbb){
                        score += 2;
                    }
                    if(r.timeOfDay == timeOfDay){
                        score += 1;
                    }
                    //final score algorithm with score multiplier by average fish
                    totalScore += score * 10 * totalFish / totalHours;
                }


            }
            //build our string result and add it
            String theString = totalScore + " " +  loc + "  Fish/hr:" + df.format(totalFish/totalHours)
                    + "  Caught:" +  + totalFish;
            stringResults.add(theString);
        }

        //update list array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, stringResults);
        listView.setAdapter(adapter);

    }


}