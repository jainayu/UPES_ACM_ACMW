package org.upesacm.acmacmw.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.fragment.homepage.HierarchyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminConsoleFragment extends Fragment implements AdapterView.OnItemClickListener {

    ListView adminConsoleListView;

    public AdminConsoleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_admin_console, container, false);
        adminConsoleListView = view.findViewById(R.id.listview_admin_console);

        List<String> consoleOptList=new ArrayList<>();
        consoleOptList.add("Members");
        consoleOptList.add("Posts");
        consoleOptList.add("Alumni");
        consoleOptList.add("Hierarchy");
        consoleOptList.add("Study Material");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),R.layout.admin_console_opts_list,
                R.id.text_view_admin_opts,consoleOptList);
        adminConsoleListView.setAdapter(adapter);

        adminConsoleListView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        System.out.println("item positon : "+position);
        switch (position) {
            case 0 :
        }
    }
}
