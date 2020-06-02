package com.example.taskmanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.taskmanagement.MainActivity;
import com.example.taskmanagement.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Accept_Reject extends Fragment {

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
            //Wonder item = new WonderModel();
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
        View view = inflater.inflate(R.layout.fragment_accept_reject, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.CardView2);
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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<WonderModel> list;
        public MyAdapter(ArrayList<WonderModel> list){
            this.list=list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.acc_rej,viewGroup,false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
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

            holder.acc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i("message","Accept");
                }
            });

            holder.rej.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent i= new Intent(v.getContext(),Reason.class);
                    i.putExtra("taskobject",arr);
                    v.getContext().startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView titleTextView;
            public TextView assignedByTextView;
            public  TextView dateTextView;
            public Button acc;
            public Button rej ;

            MyViewHolder(View v){
                super(v);
                titleTextView = (TextView) v.findViewById(R.id.titleTextView);
                assignedByTextView = v.findViewById(R.id.assignedByTextView);
                dateTextView = (TextView) v.findViewById(R.id.dateTextView);
                acc = v.findViewById(R.id.accept);
                rej = (Button) v.findViewById(R.id.reject);
            }
        }
    }

}
