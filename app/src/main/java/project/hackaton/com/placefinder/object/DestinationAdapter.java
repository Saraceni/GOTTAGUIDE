package project.hackaton.com.placefinder.object;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import project.hackaton.com.placefinder.R;

/**
 * Created by rafaelgontijo on 11/8/15.
 */
public class DestinationAdapter extends ArrayAdapter<Destination> {

    private final Context context;
    private final List<Destination> destinations;

    public DestinationAdapter(Context context, List<Destination> vals)
    {
        super(context, -1, vals);
        this.context = context;
        destinations = vals;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        final ViewHolder viewHolder;

        if(view == null)
        {
            view = View.inflate(getContext(), R.layout.adapter_destination, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.adapter_destination_name);
            viewHolder.city = (TextView) view.findViewById(R.id.adapter_destination_city);
            viewHolder.country = (TextView) view.findViewById(R.id.adapter_destination_country);
            viewHolder.state = (TextView) view.findViewById(R.id.adapter_destination_state);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        Destination dest = destinations.get(position);
        viewHolder.name.setText(dest.getName());
        viewHolder.city.setText(dest.getCity());
        viewHolder.country.setText(dest.getCountry());
        viewHolder.state.setText(dest.getState());

        return view;
    }

    static class ViewHolder
    {
        TextView name;
        TextView city;
        TextView country;
        TextView state;
    }
}
