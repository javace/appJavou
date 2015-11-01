package br.com.javace.javou.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Method;

import br.com.javace.javou.R;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Rudson Lima on 16/07/15.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public enum ActivityAnimation {
        SLIDE_LEFT, SLIDE_RIGHT
    }

    public void startActivity(Intent intent, final ActivityAnimation animation) {
        startActivity(intent);
        putAnimation(this, animation);
    }

    public void startActivityForResult(Intent intent, int requestCode, final ActivityAnimation animation) {
        startActivityForResult(intent, requestCode);
        putAnimation(this, animation);
    }

    public void finish(final ActivityAnimation animation) {
        finish();
        putAnimation(this, animation);
    }

    private static void putAnimation(final Activity source,
                                     final ActivityAnimation animation) {
        try {
            Method method = Activity.class.getMethod("overridePendingTransition", int.class, int.class);

            int[] animations = getAnimation(animation);
            method.invoke(source, animations[0], animations[1]);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private static int[] getAnimation(final ActivityAnimation animation) {
        int exitAnim;
        int enterAnim;

        switch (animation) {
            case SLIDE_RIGHT:
                enterAnim = R.anim.slide_right_enter;
                exitAnim = R.anim.slide_right_exit;
                break;

            case SLIDE_LEFT:
            default:
                enterAnim = R.anim.slide_left_enter;
                exitAnim = R.anim.slide_left_exit;
                break;
        }

        return new int[]{enterAnim, exitAnim};
    }
}
