package net.ridhoperdana.Frierun.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import net.ridhoperdana.Frierun.R;
import net.ridhoperdana.Frierun.pojo_class.User;

import java.util.Collections;
import java.util.List;

/**
 * Created by RIDHO on 12/21/2016.
 */

public class CustomAdapterFriendList extends RecyclerView.Adapter<CustomViewHolderFriendList> {

    List<User> list = Collections.emptyList();
    Context context;

    @Override
    public CustomViewHolderFriendList onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_friend_list, parent, false);
        CustomViewHolderFriendList holder = new CustomViewHolderFriendList(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolderFriendList holder, int position) {
        User user = list.get(position);
        Picasso.with(context).load(user.getUrlFoto()).into(holder.fotoProfil);
        holder.namaPengguna.setText(user.getNama());
        holder.emailPengguna.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public CustomAdapterFriendList(List<User> list, Context context) {
        this.list = list;
        this.context = context;
    }
}
