package net.ridhoperdana.Frierun.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.ridhoperdana.Frierun.R;

/**
 * Created by RIDHO on 12/21/2016.
 */

public class CustomViewHolderFriendList extends RecyclerView.ViewHolder {

    ImageView fotoProfil, statusOnline;
    TextView namaPengguna, emailPengguna;

    public CustomViewHolderFriendList(View itemView) {
        super(itemView);
        fotoProfil = (ImageView)itemView.findViewById(R.id.foto_profil);
        namaPengguna = (TextView)itemView.findViewById(R.id.nama_pengguna_friend_list);
        emailPengguna = (TextView)itemView.findViewById(R.id.email_pengguna_friend_list);
        statusOnline = (ImageView)itemView.findViewById(R.id.icon_status_online);
    }
}
