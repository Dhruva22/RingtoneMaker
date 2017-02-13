package com.example.ringtonemaker.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringtonemaker.R;
import com.example.ringtonemaker.database.DBHandler;
import com.example.ringtonemaker.model.Ringtones;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.ringtonemaker.utils.Constants.size;
import static com.example.ringtonemaker.utils.Util.getStoragePath;

/**
 * Created by Mishtiii on 08-02-2017.
 */

public class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.ViewHolder>
{
    private ArrayList<Ringtones> lst_ringtones;
    Context context;
    DBHandler dbHandler;
    MediaPlayer mediaPlayer;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView tvRingtoneName;
        ImageView ivShare,ivPlay,ivDelete;

        public ViewHolder(View v)
        {
            super(v);
            tvRingtoneName = (TextView) v.findViewById(R.id.tvRingtoneName);
            ivShare = (ImageView) v.findViewById(R.id.ivShare);
            ivPlay = (ImageView) v.findViewById(R.id.ivPlay);
            ivDelete = (ImageView) v.findViewById(R.id.ivDelete);

        }
    }

    public RingtoneAdapter(ArrayList<Ringtones> lst_ringtones, Context context)
    {
        this.lst_ringtones = lst_ringtones;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ringtone, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        dbHandler = new DBHandler(context);
        final Ringtones ringtones = lst_ringtones.get(position);
        holder.tvRingtoneName.setText(ringtones.getRingtone_name());
        holder.ivShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sharePath = getStoragePath(context) + ringtones.getRingtone_name();
                Uri uri = Uri.parse(sharePath);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM,uri);
                share.setType("audio/.mp3");
                context.startActivity(Intent.createChooser(share, "Share Sound File"));
            }
        });

        holder.ivPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mediaPlayer!= null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }
                String destinationFile = ringtones.getRingtone_source();
                try
                {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(destinationFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            mp.stop();
                            mediaPlayer = null;
                        }
                    });
                }
                catch (IOException e)
                {
                    Toast.makeText(context,"Hmmmmm. Can't play file",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,Cancel it!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                        {
                            @Override
                            public void onClick(SweetAlertDialog sDialog)
                            {
                                File fdelete = new File(ringtones.getRingtone_source());
                                if (fdelete.exists())
                                {
                                    fdelete.delete();
                                    size--;
                                    Log.i("size in del",size + "");
                                }
                                dbHandler.deleteRingtone(ringtones);
                                notifyDataSetChanged();
                                lst_ringtones.remove(position);
                                sDialog
                                        .setTitleText("Deleted!")
                                        .setContentText("Your ringtone file has been deleted!")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return lst_ringtones.size();
    }
}
