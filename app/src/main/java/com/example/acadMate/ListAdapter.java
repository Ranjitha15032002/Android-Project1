package com.example.acadMate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.example.profile_page.R;

import java.util.List;


public class ListAdapter extends ArrayAdapter<FacultyMember> {
    private final Context context;
    private final List<FacultyMember> facultyList;

    public ListAdapter(Context context, List<FacultyMember> facultyList) {
        super(context, R.layout.list_faculty, facultyList);
        this.context = context;
        this.facultyList = facultyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_faculty, parent, false);

        TextView nameTextView = rowView.findViewById(R.id.fnaem);
        TextView depTextView = rowView.findViewById(R.id.fdep);
        TextView emailTextView = rowView.findViewById(R.id.fmail);

        FacultyMember faculty = facultyList.get(position);

        nameTextView.setText(faculty.getName());
        depTextView.setText(faculty.getDesignation());
        emailTextView.setText(faculty.getEmail());

        return rowView;
    }
}
