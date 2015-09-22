package br.liveo.searchliveo;

/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import br.liveo.searchliveo.interfaces.OnSearchListener;

public class SearchLiveo extends FrameLayout {

    private Activity mContext;

    private EditText mEdtSearch;
    private ImageView mImgBackSearch;
    private ImageView mImgVoiceSearch;
    private RecyclerView mRecyclerView;
    private RelativeLayout mViewSearch;

    private int mColorPrimaryDark;
    private boolean active = false;
    private int mColorStatusBarHide = -1;
    private int mColorStatusBarShow = -1;
    private OnSearchListener mSearchListener;

    public static int REQUEST_CODE_SPEECH_INPUT = 7777;
    private static String SEARCH_TEXT = "SEARCH_TEXT";
    private static String STATE_TO_SAVE = "stateToSave";
    private static String INSTANCE_STATE = "instanceState";

    public SearchLiveo with(Context activity) {

        if (this.mContext == null) {
            try {
                this.mContext = (Activity) activity;
                this.mSearchListener = (OnSearchListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(mContext.getString(R.string.warning_listener));
            }
        }else{
            build();
        }

        return this;
    }

    public SearchLiveo with(Activity getActivity, OnSearchListener context) {

        if (this.mContext == null) {
            try {
                this.mContext = getActivity;
                this.mSearchListener = context;
            } catch (ClassCastException e) {
                throw new ClassCastException(mContext.getString(R.string.warning_fragment_listener));
            }
        }else{
            build();
        }

        return this;
    }

    public SearchLiveo build(){

        if (this.mSearchListener == null){
            throw new ClassCastException(mContext.getString(R.string.warning_listener));
        }

        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Resources.Theme theme = this.mContext.getTheme();
                TypedArray typedArray = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark});
                setColorPrimaryDark(typedArray.getResourceId(0, 0));
            }
        }catch (Exception e){
            e.getStackTrace();
        }

        return this;
    }

    public SearchLiveo(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) {
            init(context);
        }
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.search_liveo, this, true);

        mEdtSearch = (EditText) view.findViewById(R.id.edtSearch);
        mViewSearch = (RelativeLayout) view.findViewById(R.id.viewSearch);
        mImgVoiceSearch = (ImageView) view.findViewById(R.id.imgVoiceSearch);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerSearchView);

        mImgBackSearch = (ImageView) view.findViewById(R.id.imgBackSearch);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View toolbarShadow = view.findViewById(R.id.toolbarShadow);
            toolbarShadow.setVisibility(View.VISIBLE);
        }

        mViewSearch.setVisibility(View.INVISIBLE);
        mEdtSearch.setOnKeyListener(onKeyListener);

        mImgBackSearch.setOnClickListener(onClickSearchBack);
        mRecyclerView.setOnClickListener(onClickRecyclerView);
        mImgVoiceSearch.setOnClickListener(onClickVoiceSearch);

        mEdtSearch.setOnEditorActionListener(onEditorActionListener);
        mEdtSearch.addTextChangedListener(new OnTextWatcherEdtSearch());
    }

    public SearchLiveo colorIcon(int color){
        mImgBackSearch.setColorFilter(ContextCompat.getColor(mContext, color));
        mImgVoiceSearch.setColorFilter(ContextCompat.getColor(mContext, color));
        return this;
    };

    public SearchLiveo textColor(int color){
        mEdtSearch.setTextColor(ContextCompat.getColor(mContext, color));
        return this;
    };

    public SearchLiveo hintColor(int color){
        mEdtSearch.setHintTextColor(ContextCompat.getColor(mContext, color));
        return this;
    };

    public SearchLiveo colorIconBack(int color){
        mImgBackSearch.setColorFilter(ContextCompat.getColor(mContext, color));
        return this;
    };

    public SearchLiveo colorIconVoice(int color){
        mImgVoiceSearch.setColorFilter(ContextCompat.getColor(mContext, color));
        return this;
    };

    public SearchLiveo setText(String text){
        mEdtSearch.setText(text);
        return this;
    };

    public SearchLiveo setHint(String text){
        mEdtSearch.setHint(text);
        return this;
    };

    public SearchLiveo colorStatusBarHide(int color){
        mColorStatusBarHide = color;
        return this;
    }

    public SearchLiveo colorStatusBarShow(int color){
        mColorStatusBarShow = color;
        return this;
    }

    public SearchLiveo hideVoice(){
        mImgVoiceSearch.setVisibility(View.INVISIBLE);
        return this;
    }

    public SearchLiveo showVoice(){
        mImgVoiceSearch.setVisibility(View.VISIBLE);
        return this;
    }

    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    hide();
                    return true;
                }
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hide();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);
                    }
                });
                return true;
            }
            return false;
        }
    };

    private OnClickListener onClickVoiceSearch = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mEdtSearch.getText().toString().length() != 0) {
                mEdtSearch.setText("");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });
            } else {
                startVoice(mEdtSearch);
            }
        }
    };

    private OnClickListener onClickSearchBack = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
        }
    };

    private OnClickListener onClickRecyclerView = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
        }
    };

    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    private class OnTextWatcherEdtSearch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (mEdtSearch.getText().toString().length() == 0) {
                    mImgVoiceSearch.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
                } else {
                    mImgVoiceSearch.setImageResource(R.drawable.ic_close_grey600_24dp);
                }

                mSearchListener.changedSearch(mEdtSearch.getText().toString());
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void hide() {
        try {
            hideAnimation();
            setActive(false);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public SearchLiveo show() {
        setActive(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            try {
                showAnimation();
            } catch (ClassCastException e) {
                throw new ClassCastException(mContext.getString(R.string.warning_with));
            }

        } else {

            Animation mFadeIn = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_in);
            mViewSearch.setEnabled(true);
            mViewSearch.setVisibility(View.VISIBLE);
            mViewSearch.setAnimation(mFadeIn);

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                            toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });
        }

        mEdtSearch.requestFocus();
        return this;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAnimation(){
        try {

            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, mColorStatusBarShow != -1 ?
                    mColorStatusBarShow : R.color.colorSearchLiveoPrimaryDark));

            final Animator animator = ViewAnimationUtils.createCircularReveal(mViewSearch,
                    mViewSearch.getWidth() - (int) dpToPixel(24, this.mContext),
                    (int) dpToPixel(23, this.mContext), 0,
                    (float) Math.hypot(mViewSearch.getWidth(), mViewSearch.getHeight()));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                    toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.setDuration(300);
            animator.start();
        }catch (Exception e){
            e.getStackTrace();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                            toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            });
        }

        mViewSearch.setVisibility(View.VISIBLE);
    }

    private SearchLiveo hideAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, mColorStatusBarHide != -1 ? mColorStatusBarHide : getColorPrimaryDark()));

            final Animator animatorHide = ViewAnimationUtils.createCircularReveal(mViewSearch,
                    mViewSearch.getWidth() - (int) dpToPixel(24, mContext),
                    (int) dpToPixel(23, mContext),
                    (float) Math.hypot(mViewSearch.getWidth(), mViewSearch.getHeight()), 0);
            animatorHide.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                                    hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);
                        }
                    });
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewSearch.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorHide.setDuration(200);
            animatorHide.start();

        } else {

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);
                }
            });

            Animation mFadeOut = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_out);
            mViewSearch.setAnimation(mFadeOut);
            mViewSearch.setVisibility(View.INVISIBLE);
        }

        mEdtSearch.setText("");
        mViewSearch.setEnabled(false);
        return this;
    }

    private int getColorPrimaryDark() {
        return mColorPrimaryDark;
    }

    private void setColorPrimaryDark(int mColorPrimaryDark) {
        this.mColorPrimaryDark = mColorPrimaryDark;
    }

    private float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBoolean(STATE_TO_SAVE, this.isActive());

        if (!mEdtSearch.getText().toString().trim().equals("")){
            bundle.putString(SEARCH_TEXT, mEdtSearch.getText().toString());
        }

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.setActive(bundle.getBoolean(STATE_TO_SAVE));

            String text = bundle.getString(SEARCH_TEXT, "");
            if (!text.trim().equals("")){
                mEdtSearch.setText(text);
            }

            if (this.isActive()){
                show();
            }

            state = bundle.getParcelable(INSTANCE_STATE);
        }

        super.onRestoreInstanceState(state);
    }

    private void startVoice(EditText editText) {
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(editText.getWindowToken(), 0);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mContext.getString(R.string.searchview_voice));
        try {
            mContext.startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(mContext.getApplicationContext(), R.string.not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    public void resultVoice(int requestCode, int resultCode, Intent data){
        if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mEdtSearch.setText(result.get(0));
            }
        }
    }
}
