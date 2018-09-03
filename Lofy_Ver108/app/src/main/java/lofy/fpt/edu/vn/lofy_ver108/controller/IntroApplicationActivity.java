package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class IntroApplicationActivity extends AppIntro implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String FILE_NAME = "Inital-Data-App-lofy-ver108.1";
    public static final String IS_FIST = "is fist";
    public static final String IS_LOGINED = "is logined";
    public static final String USER_ID = "userID";
    public static final String USER_NAME = "userName";
    public static final String USER_PHONE = "userPhone";
    public static final String USER_URL_AVATAR = "userAvatar";
    public static final String GROUP_ID = "groupID";
    public static final String GROUP_NAME = "groupName";
    public static final String IS_HOST = "isHost";
    public static final String GROUP_USER_ID = "groupUser_ID";
    public static final String GROUP_REQUEST = "group_request";
   // public static final String GROUP_USER_COLOR = "groupUser_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        chekFirstTime();
        setColorSkipButton(getResources().getColor(R.color.black));
        setSkipText("Bỏ qua");
        setColorDoneText(getResources().getColor(R.color.black));
        setDoneText("Hoàn Thành");
    }

    private void chekFirstTime() {
        if(AccessToken.getCurrentAccessToken()!=null){
            String gID = mSharedPreferences.getString(IntroApplicationActivity.GROUP_ID,"NA");
            if(gID.equals("NA") || gID.isEmpty() || gID.equals("")){
                Intent intent = new Intent(IntroApplicationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(IntroApplicationActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        }else if (mSharedPreferences.getBoolean(IS_FIST, true)) {
            editor.putBoolean(IS_FIST, false);
            editor.apply();
            showIntro();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        mSharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        editor = mSharedPreferences.edit();
    }
    private void showIntro() {
        SliderPage sliderPage=new SliderPage();
        sliderPage.setTitle("Bạn đang tìm một ứng dụng hỗ trợ chuyến du lịch sắp tới với bạn bè và người thân?");
        sliderPage.setDescription("”Không ai nhận ra rằng du lịch đẹp đến nhường nào cho đến khi họ về đến nhà và ngả đầu lên chiếc gối cũ kỹ, thân quen.” – Lâm Ngữ Đường");
        sliderPage.setImageDrawable(R.drawable.b);
        sliderPage.setBgColor(getResources().getColor(R.color.app2));
        sliderPage.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2=new SliderPage();
        sliderPage2.setTitle("Cùng nhau trải nghiệm những hành trình khó quên");
        sliderPage2.setDescription("”Một cuộc hành trình thực sự được tính không phải bằng dặm, mà bằng những người bạn.” - Tim Cahill");
        sliderPage2.setImageDrawable(R.drawable.a);
        sliderPage2.setBgColor(getResources().getColor(R.color.app1));
        sliderPage2.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage2.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3=new SliderPage();
        sliderPage3.setTitle("Nhắc cho nhau những sự kiện thú vị dọc cung đường ");
        sliderPage3.setDescription("”Người sống nhiều nhất không phải người sống lâu năm nhất mà là người có nhiều trải nghiệm phong phú nhất. ”-Jean Jacques Rousseau");
        sliderPage3.setImageDrawable(R.drawable.c);
        sliderPage3.setBgColor(getResources().getColor(R.color.app3));
        sliderPage3.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage3.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4=new SliderPage();
        sliderPage4.setTitle("Nghỉ ngơi, thư giãn, tiếp tục xách balo lên và đi... ");
        sliderPage4.setDescription("”Có một loại phép thuật, đó là đi xa hơn nữa, sau đó trở về, và hoàn toàn thay đổi.” – Kate Douglas Wiggin");
        sliderPage4.setImageDrawable(R.drawable.d);
        sliderPage4.setBgColor(getResources().getColor(R.color.app4));
        sliderPage4.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage4.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage4));
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
    @Override
    protected void onDestroy() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
    @Override
    public void setColorSkipButton(int colorSkipButton) {
        super.setColorSkipButton(colorSkipButton);
    }

    @Override
    public void setSkipText(@Nullable CharSequence text) {
        super.setSkipText(text);
    }

    @Override
    public void setColorDoneText(int colorDoneText) {
        super.setColorDoneText(colorDoneText);
    }

    @Override
    public void setDoneText(@Nullable CharSequence text) {
        super.setDoneText(text);
    }
}
