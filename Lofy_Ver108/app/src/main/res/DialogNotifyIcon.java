package lofy.fpt.edu.vn.floatactionbuttondemo;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Window;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogNotifyIcon extends Dialog {


    public DialogNotifyIcon(@NonNull Context context) {
        super(context);
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_notify_icon);
    }

}
