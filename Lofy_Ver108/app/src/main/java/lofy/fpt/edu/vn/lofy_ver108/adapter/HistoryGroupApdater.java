package lofy.fpt.edu.vn.lofy_ver108.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.business.MapMethod;
import lofy.fpt.edu.vn.lofy_ver108.entity.Group;

public class HistoryGroupApdater extends BaseAdapter {
    ArrayList<Group> alGroup;
    private LayoutInflater inflater;
    Context mContext;
    MapMethod mapMethod;

    public HistoryGroupApdater(Context context, ArrayList<Group> alGroup) {
        mContext = context;
        this.alGroup = alGroup;
        inflater = LayoutInflater.from(context);
        mapMethod = new MapMethod(mContext);
    }

    @Override
    public int getCount() {
        return alGroup.size();
    }

    @Override
    public Object getItem(int i) {
        return alGroup.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MyViewHolder myViewHolder;
        if (view == null) {
            myViewHolder = new MyViewHolder();
            view = inflater.inflate(R.layout.item_group_history, viewGroup, false);
            myViewHolder.tvDate = (TextView) view.findViewById(R.id.tv_it_history_date);
            myViewHolder.tvName = (TextView) view.findViewById(R.id.tv_it_history_name);
            myViewHolder.tvTrack = (TextView) view.findViewById(R.id.tv_it_history_track);
            myViewHolder.ivDel = (ImageView) view.findViewById(R.id.iv_it_history_delete);
//            myViewHolder.ivDel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ((ListView)viewGroup).performItemClick(view,i,0);
//                }
//            });
            view.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) view.getTag();
        }
        try {
            Group group = alGroup.get(i);
            myViewHolder.tvName.setText(group.getGroupName());
            myViewHolder.tvDate.setText(group.getStart_Date() + " - " + group.getEnd_Date());
//           myViewHolder.tvTrack.setText("(" + group.getStart_Lat() + group.getStart_Long() + ") -- ( " + group.getEnd_Lat() + group.getEnd_Long() + ")");

            String addressStart = mapMethod.getAddress(group.getStart_Lat(), group.getStart_Long(), mContext);
            String addressEnd = mapMethod.getAddress(group.getEnd_Lat(), group.getEnd_Long(), mContext);
            myViewHolder.tvTrack.setText(addressStart + " - " + addressEnd);
            myViewHolder.ivDel.performClick();


        } catch (Exception e) {

        }

        return view;
    }

    private class MyViewHolder {
        TextView tvDate;
        TextView tvName;
        TextView tvTrack;
        ImageView ivDel;
    }
}
