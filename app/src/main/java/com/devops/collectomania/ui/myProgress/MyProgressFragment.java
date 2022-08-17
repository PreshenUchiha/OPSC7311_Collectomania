package com.devops.collectomania.ui.myProgress;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.devops.collectomania.Categorie;
import com.devops.collectomania.CategorieAdapter;
import com.devops.collectomania.Item;
import com.devops.collectomania.ItemAdapter;
import com.devops.collectomania.R;
import com.devops.collectomania.TempUser;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyProgressFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    BarChart BarProgress;
    //PieChart PieProgress;
    ListView ProgressListView;
    List<Categorie> categoryModelList = new ArrayList<>();
    List<Item> itemModelList = new ArrayList<>();
    List<String> GoalModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_progress, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        BarProgress = view.findViewById(R.id.barChart);
        //PieProgress = view.findViewById(R.id.pieChart);
        ProgressListView = view.findViewById(R.id.listviewGoal);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> barEntriesLabel = new ArrayList<>();
        barEntriesLabel.add("Dummy");
        barEntriesLabel.add("");


        ArrayList<BarEntry> barEntries1 = new ArrayList<>();

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Category").child(TempUser.encodedEmail());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryModelList.clear();

                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    categoryModelList.add(snapshot1.getValue(Categorie.class));
                }

                int g = 2;
                for (Categorie c :categoryModelList) {
                    float position = (float) (g-0.15);
                    barEntriesLabel.add(c.getCatagory());
                    barEntriesLabel.add("");

                    BarEntry barEntry1 = new BarEntry(position,c.goal);
                    barEntries1.add(barEntry1);

                    g= g+2;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Recent Item").child(TempUser.encodedEmail());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemModelList.clear();

                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            itemModelList.add(snapshot1.getValue(Item.class));
                        }

                        int i=1;
                        int c =0;
                        for (Categorie cat :categoryModelList) {

                            float qty=0,percentage;
                            for (Item item: itemModelList) {

                                if (item.getCategory().equals(cat.getCatagory())){
                                    qty = qty + item.getQuantity();
                                }
                            }
                                percentage = (float) Math.round((qty/categoryModelList.get(c).getGoal())*100);
                                BarEntry barEntry = new BarEntry(i,qty);
                                //PieEntry pieEntry = new PieEntry(qty,cat.getCatagory()+" "+percentage+"% to Goal");
                                GoalModelList.add(
                                        "Category: "+ cat.getCatagory() +" ,Current Qty: "+ qty
                                        + "\nGoal: "+categoryModelList.get(c).getGoal()
                                        + "\nPercentage to goal: "+percentage+"% ");

                                barEntries.add(barEntry);
                                //pieEntries.add(pieEntry);

                                c++;
                                i=i+2;
                        }

                        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1,GoalModelList);
                        ProgressListView.setAdapter(arrayAdapter);




                        //Bar data Initialize
                        BarDataSet barDataSet = new BarDataSet(barEntries,"My Categories");
                        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Goal");
                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        barDataSet1.setColor(Color.GRAY);
                        barDataSet.setDrawValues(false);
                        barDataSet1.setDrawValues(false);

                        BarData barData = new BarData();
                        barData.addDataSet(barDataSet);
                        barData.addDataSet(barDataSet1);

                        BarProgress.setData(barData);
                        BarProgress.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barEntriesLabel));
                        BarProgress.animateY(3000);
                        BarProgress.getDescription().setText("Progress Chart");
                        BarProgress.getDescription().setTextColor(Color.BLUE);
                        BarProgress.getDescription().setEnabled(false);

                        //Pie data Initialize
                        //PieDataSet pieDataSet = new PieDataSet(pieEntries,"Collection");
                        //pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                       // pieDataSet.setValueTextSize(20);
                       // PieProgress.setEntryLabelTextSize(10);
                       // PieProgress.setEntryLabelColor(Color.BLACK);
                       // PieProgress.getDescription().setText("Items in category");
                       // PieProgress.setData(new PieData(pieDataSet));
                       // PieProgress.animateXY(3000,3000);




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TempUser.Fullname = "User database unread";
            }
        });


    }
}