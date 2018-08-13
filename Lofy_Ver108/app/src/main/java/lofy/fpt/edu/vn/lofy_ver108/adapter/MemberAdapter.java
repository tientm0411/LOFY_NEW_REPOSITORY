package lofy.fpt.edu.vn.lofy_ver108.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lofy.fpt.edu.vn.lofy_ver108.R;
import lofy.fpt.edu.vn.lofy_ver108.entity.User;

public class MemberAdapter extends BaseAdapter {
    ArrayList<User> alUsers;
    private LayoutInflater inflater;
    Context mContext;

    public MemberAdapter(Context context, ArrayList<User> alUsers) {
        mContext = context;
        this.alUsers = alUsers;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return alUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        MemberAdapter.MyViewHolder myViewHolder;
        if (view == null) {
            myViewHolder = new MemberAdapter.MyViewHolder();
            view = inflater.inflate(R.layout.item_member,viewGroup,false);
            myViewHolder.imageAva =(ImageView) view.findViewById(R.id.img_item_member_ava);
            myViewHolder.tvName =(TextView) view.findViewById(R.id.tv_item_member_name);
            view.setTag(myViewHolder);
        }else{
            myViewHolder =(MemberAdapter.MyViewHolder) view.getTag();
        }
        User user = alUsers.get(i);
        Picasso.with(mContext).load(user.getUrlAvatar()).into(myViewHolder.imageAva);
        myViewHolder.tvName.setText(user.getUserName());

        return view;
    }

    private class MyViewHolder {
        ImageView imageAva;
        TextView tvName;
    }
}
