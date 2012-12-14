package org.wwscc.android.Results;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

public class DataListFragment extends SherlockFragment implements OnItemSelectedListener, Interface.DataDest
{
	private static final String[] TYPES =  new String[] {"event", "champ", "pax", "net"};
	
    private Spinner classes;
	private Spinner types;
	private ListView display;
	private JSONArrayAdapter currentAdapter;
	private int currentType;
	private Interface.DataSource retriever;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View main = inflater.inflate(R.layout.fragment_list, container, false);

		classes = (Spinner)main.findViewById(R.id.classselect);
        types = (Spinner)main.findViewById(R.id.typeselect);
        display = (ListView)main.findViewById(R.id.datalist);
        
        ArrayAdapter<String> classList = new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic);
        String available[] = getActivity().getSharedPreferences(null, 0).getString("CLASSES", "").split(",");
        for (String s : available)
        	classList.add(s);
        classes.setAdapter(classList);
        classes.setOnItemSelectedListener(this);

        ArrayAdapter<String> typesList = new ArrayAdapter<String>(getActivity(), R.layout.spinner_basic);
        for (String s : TYPES)
        	typesList.add(s);
        types.setAdapter(typesList);
        types.setOnItemSelectedListener(this);
        
        currentAdapter = null;
        currentType = 0;
        return main;
	}
	
	public void setDataSource(Interface.DataSource d)
	{
		Log.e("TEST", "set data source " + d);
		retriever = d;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		Log.e("TEST", "start " + this);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		Log.e("TEST", "stop " + this);
		if (retriever != null)
			retriever.stopListening(this);
	}
	
	@Override
	public void updateData(JSONArray newData)
	{
		currentAdapter.updateData(newData);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		Log.e("TEST", "item selected " + view + ", " + position);
		if (parent == types)
		{
			String type = (String)types.getSelectedItem();
			if (type.equals("event")) {
				currentAdapter = new A.EventListAdapter(getActivity());
				currentType = DataRetriever.EVENTRESULT;
			} else if (type.equals("champ")) {
				currentAdapter = new A.ChampListAdapter(getActivity());
				currentType = DataRetriever.CHAMPRESULT;
			} else if (type.equals("pax")) {
				currentAdapter = new A.PaxListAdapter(getActivity());
				currentType = DataRetriever.TOPNET;
			} else if (type.equals("net")) {
				currentAdapter = new A.RawListAdapter(getActivity());
				currentType = DataRetriever.TOPRAW;
			}
			display.setAdapter(currentAdapter);
		}
	
		if ((retriever != null) && (currentType > 0))
		{
			retriever.startListening(this, currentType, (String)classes.getSelectedItem());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}


}
