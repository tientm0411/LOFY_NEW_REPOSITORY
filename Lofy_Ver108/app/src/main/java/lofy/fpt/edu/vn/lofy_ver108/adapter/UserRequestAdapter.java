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

public class UserRequestAdapter extends BaseAdapter {
    ArrayList<User> alUsers;
    private LayoutInflater inflater;
    Context mContext;

    public UserRequestAdapter(Context context, ArrayList<User> alUsers) {
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
        MyViewHolder myViewHolder;
        if (view == null) {
            myViewHolder = new MyViewHolder();
            view = inflater.inflate(R.layout.item_user_request,viewGroup,false);
            myViewHolder.imageAva =(ImageView) view.findViewById(R.id.img_item_request_user_ava);
            myViewHolder.tvName =(TextView) view.findViewById(R.id.tv_item_request_user_name);
            myViewHolder.btnAcc = (Button) view.findViewById(R.id.btn_item_request_user_accecpt);
            myViewHolder.btnDeny = (Button) view.findViewById(R.id.btn_item_request_user_deny);
            myViewHolder.btnAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView)viewGroup).performItemClick(v,i,0);
                }
            });
            myViewHolder.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListView)viewGroup).performItemClick(v,i,0);
                }
            });
            view.setTag(myViewHolder);
        }else{
            myViewHolder =(MyViewHolder) view.getTag();
        }
        User user = alUsers.get(i);
        Picasso.with(mContext).load(user.getUrlAvatar()).into(myViewHolder.imageAva);
        myViewHolder.tvName.setText(user.getUserName());

        return view;
    }

    private class MyViewHolder {
        ImageView imageAva;
        TextView tvName;
        Button btnAcc;
        Button btnDeny;
    }
}
