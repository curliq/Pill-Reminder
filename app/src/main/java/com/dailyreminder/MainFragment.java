package com.dailyreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    View v;
    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RemindersAdapter adapter;
    ArrayList<ReminderView> arrayList;
    FloatingActionButton addButton;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int count;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity)getActivity()).changeTitle(getString(R.string.app_name), false, 0, 0);
        recyclerView = (RecyclerView)v.findViewById(R.id.mainList);
        toolbar = (Toolbar)getActivity().findViewById(R.id.mToolbar);
        addButton = (FloatingActionButton)v.findViewById(R.id.addfab);
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = prefs.edit();
        count = prefs.getInt("i", 1);

        arrayList = new ArrayList<>();
        for (int k = count; k >= 0; k--) {
            if (prefs.getString("name"+k, null) != null) {
                ReminderView view = new ReminderView(getActivity(),
                        prefs.getString("name"+k, ""),
                        prefs.getString("frequency"+k, ""),
                        prefs.getString("notes"+k, ""),
                        prefs.getInt("hora"+k, 8),
                        prefs.getInt("minuto"+k, 0),
                        k);
                arrayList.add(view);
            }
        }

        adapter = new RemindersAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                    addButton.hide();
                else
                    addButton.show();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).toFragmentNewReminder();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings_toolbar:
                        ((MainActivity)getActivity()).toSettings();
                        break;
                }
                return true;
            }
        });

        return v;
    }


    @Subscribe
    public void onAdapterAdd(final AddEvent event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                arrayList.add(0, new ReminderView(getActivity(), event.name, event.hour, event.notes, event.hourita, event.minute, count));
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        editor.putString("name"+count, event.name);
                        editor.putString("frequency"+count, event.hour);
                        editor.putString("notes"+count, event.notes);
                        editor.putInt("hora"+count, event.hourita);
                        editor.putInt("minuto"+count, event.minute);
                        editor.putInt("i", count);
                        editor.apply();
                        makeNotification(count, event.name, event.hourita, event.minute);
                    }
                }, 700);
            }
        }, 300);
    }

    @Subscribe
    public void onAdapterRemove(final RemoveEvent event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arrayList.remove(event.listPosition);
                adapter.notifyItemRemoved(event.listPosition);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        editor.putString("name"+event.position, null);
                        editor.putString("frequency"+event.position, null);
                        editor.putString("notes"+event.position, null);
                        editor.putInt("hora"+event.position, 0);
                        editor.putInt("minuto"+event.position, 0);
                        editor.apply();

                        //Call first notification immediately
                        Intent intent = new Intent(getActivity(), Broadcast_Notification.class);
                        intent.putExtra("cancel", true);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), event.position, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                        }

                    }
                }, 700);
            }
        }, 300);
    }


    public void makeNotification(int count, String name, int hours, int minutes) {

        Bundle bd = new Bundle();
        bd.putInt("count", count);
        bd.putString("name", name);
        bd.putInt("hours", hours);
        bd.putInt("minutes", minutes);


        Intent intent = new Intent(getActivity(), Broadcast_Notification.class);
        intent.putExtras(bd);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), count, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);

    }




    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }



    /**
     **** adapter
     */

    class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.MyViewHolder>  {

        private ArrayList<ReminderView> remindersList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, time, notes;
            public FrameLayout root;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.titletext);
                time = (TextView) view.findViewById(R.id.hoursText);
                notes = (TextView) view.findViewById(R.id.notestext);
                root = (FrameLayout) view.findViewById(R.id.reminderroot);

            }
        }

        public RemindersAdapter(ArrayList<ReminderView> list) {
            this.remindersList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reminder_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            ReminderView movie = remindersList.get(position);
            holder.title.setText(movie.getTitle());
            holder.time.setText(movie.getTime());
            holder.notes.setText(movie.getNotes());

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)getActivity()).toFragmentInfo(
                            remindersList.get(position).getTitle(),
                            remindersList.get(position).getNotes(),
                            remindersList.get(position).getHour(),
                            remindersList.get(position).getMinute(),
                            remindersList.get(position).getCount(),
                            position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return remindersList.size();
        }


    }
}
