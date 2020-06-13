package com.example.taskmanagement.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import com.example.taskmanagement.Popup;
import com.example.taskmanagement.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Accept_Reject extends Fragment {

    RecyclerView MyRecyclerView;
    FirestoreRecyclerAdapter adapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String emailId;
    String d1, t1;
    String diff;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private String getTimeDifference(Date deadlineDate, Date currentDate) {
        long diff = deadlineDate.getTime() - currentDate.getTime();
        long seconds = diff / 1000;
        int days = (int) (seconds / (24 * 60 * 60));
        seconds -= days * (24 * 60 * 60);
        long hours = (int) (seconds / (60 * 60));
        seconds -= hours * (60 * 60);
        int minutes = (int) (seconds / (60));
        seconds -= minutes * 60;

        String timeDifference = "";
        if (currentDate.before(deadlineDate)) {
            timeDifference = days + " Days " + hours + " hours " + minutes + " minutes";
        }
        return timeDifference;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_reject, container, false);
        fAuth = FirebaseAuth.getInstance();
        emailId = fAuth.getCurrentUser().getEmail();
        t1 = "";
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("users").document(emailId).collection("ongoingtask");
        FirestoreRecyclerOptions<WonderModel> item = new FirestoreRecyclerOptions.Builder<WonderModel>()
                .setQuery(query, WonderModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<WonderModel, AcceptTaskViewHolder>(item) {
            @NonNull
            @Override
            public AcceptTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.acc_rej, parent, false);
                return new AcceptTaskViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull AcceptTaskViewHolder holder, int position, @NonNull WonderModel model) {
                final String arr[] = {model.getTitle(), model.getDeadline_Date(), model.getassignedBy(), model.getDeadline_Time(), model.getDescription()};
                final WonderModel copy = model;
                d1 = arr[1] + " " + arr[3];
                try {

                    Date deadlineDate = dateFormat.parse(d1);
                    Date currentDate = (new Date());
                    t1 = getTimeDifference(deadlineDate, currentDate);

                } catch (ParseException e) {
                    t1 = "0 days";
                    e.printStackTrace();
                }
                holder.titleTextView.setText(arr[0]);
                holder.dateTextView.setText(t1);
                holder.assignedByTextView.setText(arr[2]);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), Taskdetail.class);
                        Bundle b = new Bundle();
                        b.putSerializable("taskObject", copy);
                        i.putExtras(b);
                        i.putExtra("time", t1);
                        v.getContext().startActivity(i);
                    }
                });
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.i("message", "Accept");
                    }
                });

                holder.reject.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Popup popUpClass = new Popup();
                        popUpClass.showPopupWindow(v);
                    }
                });
            }
        };
        MyRecyclerView = view.findViewById(R.id.CardView2);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        MyRecyclerView.setLayoutManager(MyLayoutManager);
        MyRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private class AcceptTaskViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView assignedByTextView;
        public TextView dateTextView;
        public Button accept;
        public Button reject;

        public AcceptTaskViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            assignedByTextView = v.findViewById(R.id.assignedByTextView);
            dateTextView = v.findViewById(R.id.dateTextView);
            accept = v.findViewById(R.id.accept);
            reject = v.findViewById(R.id.reject);

        }
    }

//    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
//        ArrayList<WonderModel> list;
//        public MyAdapter(ArrayList<WonderModel> list){
//            this.list=list;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//            View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.acc_rej,viewGroup,false);
//            return new MyViewHolder(v);
//        }

//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, int position) {
//            final String arr[]={list.get(position).getTitle(),list.get(position).getDate(),list.get(position).getAssigned_by()};
//
//            holder.titleTextView.setText(arr[0]);
//            holder.dateTextView.setText(arr[1]);
//            holder.assignedByTextView.setText(arr[2]);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(v.getContext(),Taskdetail.class);
//                    i.putExtra("taskobject",arr);
//                    v.getContext().startActivity(i);
//                }
//            });
//
//            holder.acc.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Log.i("message","Accept");
//                }
//            });
//
//            holder.rej.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                        Popup popUpClass = new Popup();
//                        popUpClass.showPopupWindow(v);
//                }
//            });
//
//        }

//        @Override
//        public int getItemCount() {
//            return list.size();
//        }

//        public class MyViewHolder extends RecyclerView.ViewHolder{
//
//            public TextView titleTextView;
//            public TextView assignedByTextView;
//            public  TextView dateTextView;
//            public Button acc;
//            public Button rej ;
//
//            MyViewHolder(View v){
//                super(v);
//                titleTextView = (TextView) v.findViewById(R.id.titleTextView);
//                assignedByTextView = v.findViewById(R.id.assignedByTextView);
//                dateTextView = (TextView) v.findViewById(R.id.dateTextView);
//                acc = v.findViewById(R.id.accept);
//                rej = (Button) v.findViewById(R.id.reject);
//            }
//        }
    //}

}
