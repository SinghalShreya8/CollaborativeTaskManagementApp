package com.example.taskmanagement.ui.main;
import com.example.taskmanagement.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class Ongoing_task extends Fragment {

    ArrayList<WonderModel> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    int  Images[] = {R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};
    String title[]={"title 1","title 2","title 3","title 4","title 5","title 6","title 7"};
    String date[]={"01-01-2021","01-01-2021","01-01-2021","01-01-2021","01-01-2021","01-01-2021","01-01-2021"};
    String assigned[]={"Mr. ABC","Mr. ABC","Mr. ABC","Mr. ABC","Mr. ABC","Mr. ABC","Mr. ABC"};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeList();
    }


    public void initializeList() {
        listitems.clear();

        for(int i =0;i<7;i++){


            WonderModel item = new WonderModel();
            item.setImageResourceId(Images[i]);
            item.setTitle(title[i]);
            item.setAssigned_by(assigned[i]);
            item.setDate(date[i]);
            listitems.add(item);

        }

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ongoing_task, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<WonderModel> list;

        public MyAdapter(ArrayList<WonderModel> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final String arr[]={list.get(position).getTitle(),list.get(position).getDate(),list.get(position).getAssigned_by()};

            holder.titleTextView.setText(arr[0]);
            holder.dateTextView.setText(arr[1]);
            holder.assignedByTextView.setText(arr[2]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),Taskdetail.class);
                    i.putExtra("taskobject",arr);
                    v.getContext().startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends com.example.taskmanagement.ui.main.MyViewHolder {

        public TextView titleTextView;
        public TextView assignedByTextView;
        public  TextView dateTextView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            assignedByTextView = v.findViewById(R.id.assignedByTextView);
            dateTextView = (TextView) v.findViewById(R.id.dateTextView);



        }
    }

}
