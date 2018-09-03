package lofy.fpt.edu.vn.lofy_ver108.controller;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import lofy.fpt.edu.vn.lofy_ver108.R;

public class GuidesActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       showIntro();
        setColorSkipButton(getResources().getColor(R.color.black));
        setSkipText("Bỏ qua");
        setColorDoneText(getResources().getColor(R.color.black));
        setDoneText("Hoàn Thành");
    }
    private void showIntro() {
        SliderPage sliderPage=new SliderPage();
        sliderPage.setTitle("Trang cá nhân");
        sliderPage.setDescription("Trang cá nhân là nơi bạn có thể cập nhật thông tin, xem lại những hành trình đã trải qua và đăng xuất khỏi ứng dụng.");
        sliderPage.setImageDrawable(R.drawable.guidesa);
        sliderPage.setBgColor(getResources().getColor(R.color.app2));
        sliderPage.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        SliderPage sliderPage2=new SliderPage();
        sliderPage2.setTitle("Tạo nhóm");
        sliderPage2.setDescription("Đây là nơi giúp bạn tạo và quản lý nhóm, phê duyệt thành viên, đặt lộ trình và tận hưởng chuyến đi của mình.");
        sliderPage2.setImageDrawable(R.drawable.guidesb);
        sliderPage2.setBgColor(getResources().getColor(R.color.app2));
        sliderPage2.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage2.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3=new SliderPage();
        sliderPage3.setTitle("Đặt lộ trình");
        sliderPage3.setDescription("Có bạn bè xung quanh, có một nơi để đến? Vậy thì cùng nhau đặt lộ trình, xách balo lên và đi thôi!!!");
        sliderPage3.setImageDrawable(R.drawable.guidesd);
        sliderPage3.setBgColor(getResources().getColor(R.color.app2));
        sliderPage3.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage3.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4=new SliderPage();
        sliderPage4.setTitle("Tham gia nhóm");
        sliderPage4.setDescription("Là cánh cửa dẫn bạn tham gia vào hành trình của những người khác, cùng chia sẻ với nhau những điều tuyệt vời.");
        sliderPage4.setImageDrawable(R.drawable.guidesc);
        sliderPage4.setBgColor(getResources().getColor(R.color.app2));
        sliderPage4.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage4.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        SliderPage sliderPage5=new SliderPage();
        sliderPage5.setTitle("Bảng cảnh báo");
        sliderPage5.setDescription("Gặp cảnh đẹp trên đường, có sự kiện xảy ra, hoặc đơn giản chỉ là nhắc nhở nhau về vài điều thú vị? Bảng cảnh báo sẽ giúp bạn truyền tải thông điệp một cách nhanh chóng nhất!");
        sliderPage5.setImageDrawable(R.drawable.guidese);
        sliderPage5.setBgColor(getResources().getColor(R.color.app2));
        sliderPage5.setTitleColor(getResources().getColor(R.color.fontText));
        sliderPage5.setDescColor(getResources().getColor(R.color.fontText));
        addSlide(AppIntroFragment.newInstance(sliderPage5));
    }
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),InformationAppActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent=new Intent(getApplicationContext(),InformationAppActivity.class);
        startActivity(intent);
        finish();
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
