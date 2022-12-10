package com.example.myapplication.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.Report;
import com.example.myapplication.databinding.FragmentHistoryBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        historyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, getHistory());

        ListView listView = (ListView) root.findViewById(R.id.HistoryList);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public ArrayList<String> getHistory(){
        ArrayList<String> historyList = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.##");
        for (Report r:Report.reports){
            String theString = r.location + "            Fish Caught:" + r.numFish + "\nTime Fished:"
                    + r.timeFished + "      Fish Per Hour:" + df.format(r.numFish/r.timeFished);
            historyList.add(theString);
        }


        return historyList;
    }
}