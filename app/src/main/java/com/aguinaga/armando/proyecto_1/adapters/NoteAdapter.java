package com.aguinaga.armando.proyecto_1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aguinaga.armando.proyecto_1.models.Note;
import com.aguinaga.armando.proyecto_1.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by arman on 27/12/2017.
 */

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private List<Note> list;
    private int layout;

    public NoteAdapter(Context context, List<Note> boards, int layout){
        this.context = context;
        this.list = boards;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.description = convertView.findViewById(R.id.textViewNoteDescription);
            vh.createdAt = convertView.findViewById(R.id.textViewNoteCreateAt);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();

        }
        Note note = list.get(position);
        vh.description.setText(note.getDescripcion());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(note.getCreateDate());
        vh.createdAt.setText(date);

        return convertView;
    }

    public class ViewHolder{
        TextView description;
        TextView createdAt;
    }
}
