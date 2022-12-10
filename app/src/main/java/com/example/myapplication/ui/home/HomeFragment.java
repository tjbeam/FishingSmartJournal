package com.example.myapplication.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.DBHelper;
import com.example.myapplication.R;
import com.example.myapplication.Report;
import com.example.myapplication.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    DBHelper db;
    private FragmentHomeBinding binding;
    Spinner locationSpinner;
    EditText numberFishNumber;
    EditText tideLevelNumber;
    EditText timeFishedNumber;
    Spinner ebbFSpinner;
    Spinner timeOfDaySpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //prepare and load database
        db = new DBHelper(getActivity());
        Report.setReports(db.getAllReports());

        //set up fragment
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        //set up submit button and on click listener
        Button submitButton = (Button) root.findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for valid inputs
                if(locationSpinner.getSelectedItemPosition() <= 1){
                    Toast.makeText(getContext(), "Pick a valid location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String locationString = locationSpinner.getSelectedItem().toString();

                int numFish = 0;
                try {
                    numFish = Integer.parseInt(numberFishNumber.getText().toString());
                }catch(NumberFormatException nfe){
                    Toast.makeText(getContext(), "Enter a number of fish caught", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (numFish<0){
                    Toast.makeText(getContext(), "You can't catch negative fish!", Toast.LENGTH_SHORT).show();
                    return;
                }

                float tideLevel = 1;
                try {
                    tideLevel = Float.parseFloat(tideLevelNumber.getText().toString());
                }catch(NumberFormatException nfe){
                    Toast.makeText(getContext(), "Enter a number for tide level", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tideLevel < -100 || tideLevel > 100 ){
                    Toast.makeText(getContext(), "You can't catch negative fish!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isEbb = ebbFSpinner.getSelectedItemPosition() == 0;

                int timeOfDay = 0;
                switch(timeOfDaySpinner.getSelectedItemPosition()){
                    case 0:
                        timeOfDay = Report.TIME_DAWN;
                        break;
                    case 1:
                        timeOfDay = Report.TIME_EARLY_DAY;
                        break;
                    case 2:
                        timeOfDay = Report.TIME_LATE_DAY;
                        break;
                    case 3:
                        timeOfDay = Report.TIME_DUSK;
                        break;
                    case 4:
                        timeOfDay = Report.TIME_NIGHT;
                        break;
                }


                float timeFished = 0;
                try {
                    timeFished = Float.parseFloat(timeFishedNumber.getText().toString());
                }catch(NumberFormatException nfe){
                    Toast.makeText(getContext(), "Enter a number for time fished", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (timeFished < 0){
                    Toast.makeText(getContext(), "You can't fish negative hours!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create a new report
                new Report(locationString, numFish, tideLevel, isEbb, timeOfDay, timeFished);

                //insert into database
                db.insertReport(locationString, numFish, tideLevel, isEbb, timeOfDay, timeFished);


                numberFishNumber.setText("");
                tideLevelNumber.setText("");
                timeFishedNumber.setText("");

                Toast.makeText(getContext(),"Submitted Report. Good Job" , Toast.LENGTH_SHORT).show();

            }
        });

        //set up clear button and on click listener
        Button clearButton = (Button) root.findViewById(R.id.ClearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numberFishNumber.setText("");
                tideLevelNumber.setText("");
                timeFishedNumber.setText("");
            }
        });

        //set up ebb/flood spinner
        ebbFSpinner = root.findViewById(R.id.EbbFSpinner);
        String[] ebbFOptions = {"Ebb/Outgoing", "Flood/Incoming"};
        ArrayAdapter ebbFSpinnerAdapter
                = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, ebbFOptions);
        ebbFSpinnerAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        ebbFSpinner.setAdapter(ebbFSpinnerAdapter);

        //set up time of day spinner
        timeOfDaySpinner = root.findViewById(R.id.TimeSpinner);
        String[] timeOfDayOptions = {"Dawn", "Early Day", "Late Day", "Dusk", "Night"};
        ArrayAdapter timeOfDayAdapter
                = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, timeOfDayOptions);
        timeOfDayAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        timeOfDaySpinner.setAdapter(timeOfDayAdapter);

        //set up location spinner
        locationSpinner = root.findViewById(R.id.LocationSpinner);
        ArrayList<String> spinnerList = new ArrayList<>();
        spinnerList.add("Select Location");
        spinnerList.add("Add New Location");
        spinnerList.addAll(Report.locations);
        ArrayAdapter <String> locationAdapter
                = new ArrayAdapter <String> (getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        locationAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        //set up listener for adding new location
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //add new location selected, show dialogue
                if (position == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("New Location");


                    final EditText input = new EditText(getActivity());

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add new location
                            Report.locations.add(input.getText().toString());
                            spinnerList.add(input.getText().toString());
                            locationSpinner.setSelection(spinnerList.size() - 1);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        numberFishNumber = (EditText) root.findViewById(R.id.NumberFishNumber);
        tideLevelNumber = (EditText) root.findViewById(R.id.TideLevelNumber);
        timeFishedNumber  = (EditText) root.findViewById(R.id.TimeFishedNumber);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}