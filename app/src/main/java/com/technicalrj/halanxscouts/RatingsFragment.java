package com.technicalrj.halanxscouts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingsFragment extends Fragment {


    public RatingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_ratings, container, false);

        GridView gridView = view.findViewById(R.id.things_to_improve);

        String[] numbers = new String[] {"Manners" , "Language" , "Good", "Excellet"};
        ArrayAdapter<String > adapter = new ArrayAdapter<String>(getActivity(), R.layout.ratings_row, R.id.tag,numbers);
        gridView.setAdapter(adapter);



        return view;

    }

}
