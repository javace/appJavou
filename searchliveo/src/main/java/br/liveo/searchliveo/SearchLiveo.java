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

    private ImageView mImgArrowSearch;
    private ImageView mImgVoiceSearch;
    private ImageView mImgCloseSearch;

    private RecyclerView mRecyclerView;
    private RelativeLayout mViewSearch;

    private int mColorPrimaryDark;
    private boolean active = false;
    private boolean isVoice = true;

    private int mColorIcon = -1;
    private int mColorIconArrow = -1;
    private int mColorIconVoice = -1;
    private int mColorIconClose = -1;

    private int mStatusBarHideColor = -1;
    private int mStatusBarShowColor = -1;
    private OnSearchListener mSearchListener;

    private static String SEARCH_TEXT = "SEARCH_TEXT";
    public static int REQUEST_CODE_SPEECH_INPUT = 7777;
    private static String STATE_TO_SAVE = "STATE_TO_SAVE";
    private static String INSTANCE_STATE = "INSTANCE_STATE";

    /**
     * Start context and the listener Search Live library.
     * Use this method when you are using an Activity
     *
     * @param context - Context Activity
     */
    public SearchLiveo with(Context context) {

        if (this.mContext == null) {
            try {
                this.mContext = (Activity) context;
                this.mSearchListener = (OnSearchListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(mContext.getString(R.string.warning_listener));
            }
        }else{
            build();
        }

        return this;
    }

    /**
     * Start context and the listener Search Live library.
     * Use this method when you are using an Fragment
     *
     * @param getActivity - Context Fragment
     * @param context - Listener
     */
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

    public void build(){

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
    }

    public SearchLiveo(Context context) {
        this(context, null);
    }

    public SearchLiveo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchLiveo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) {
            init(context);
            initAttribute(context, attrs, defStyleAttr);
        }
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.search_liveo, this, true);

        mEdtSearch = (EditText) view.findViewById(R.id.edtSearch);
        mViewSearch = (RelativeLayout) view.findViewById(R.id.viewSearch);

        mImgArrowSearch = (ImageView) view.findViewById(R.id.imgArrowSearch);
        mImgVoiceSearch = (ImageView) view.findViewById(R.id.imgVoiceSearch);

        mImgCloseSearch = (ImageView) view.findViewById(R.id.imgCloseSearch);
        mImgCloseSearch.setVisibility(isVoice() ? View.GONE : View.VISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerSearchView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View toolbarShadow = view.findViewById(R.id.toolbarShadow);
            toolbarShadow.setVisibility(View.VISIBLE);
        }

        mViewSearch.setVisibility(View.INVISIBLE);
        mEdtSearch.setOnKeyListener(onKeyListener);

        mImgArrowSearch.setOnClickListener(onClickSearchArrow);
        mRecyclerView.setOnClickListener(onClickRecyclerView);
        mImgVoiceSearch.setOnClickListener(onClickVoiceSearch);
        mImgCloseSearch.setOnClickListener(onClickCloseSearch);

        mEdtSearch.setOnEditorActionListener(onEditorActionListener);
        mEdtSearch.addTextChangedListener(new OnTextWatcherEdtSearch());
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.SearchLiveo, defStyleAttr, 0);
        if (attr != null) {
            try {

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoHint)) {
                    hint(attr.getString(R.styleable.SearchLiveo_searchLiveoHint));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoTextColor)) {
                    mEdtSearch.setTextColor(attr.getColor(R.styleable.SearchLiveo_searchLiveoTextColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoHintColor)) {
                    mEdtSearch.setHintTextColor(attr.getColor(R.styleable.SearchLiveo_searchLiveoHintColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorIcon)) {
                    setColorIcon(attr.getColor(R.styleable.SearchLiveo_searchLiveoColorIcon, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorArrow)) {
                    setColorIconArrow(attr.getColor(R.styleable.SearchLiveo_searchLiveoColorArrow, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorVoice)) {
                    setColorIconVoice(attr.getColor(R.styleable.SearchLiveo_searchLiveoColorVoice, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorClose)) {
                    setColorIconClose(attr.getColor(R.styleable.SearchLiveo_searchLiveoColorClose, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoBackground)) {
                    mViewSearch.setBackgroundColor(attr.getColor(R.styleable.SearchLiveo_searchLiveoBackground, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoStatusBarShowColor)) {
                    setStatusBarShowColor(attr.getColor(R.styleable.SearchLiveo_searchLiveoStatusBarShowColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoStatusBarHideColor)) {
                    setStatusBarHideColor(attr.getColor(R.styleable.SearchLiveo_searchLiveoStatusBarHideColor, -1));
                }
            } finally {
                attr.recycle();
            }
        }
    }

    /**
     * Set a new background color. If you do not use this method and standard color is white SearchLiveo.
     * In his layout.xml you can use the "app:searchLiveoBackground="@color/..."" attribute
     *
     * @param resId color attribute - colors.xml file
     */
    public SearchLiveo backgroundResource(int resId){
        mViewSearch.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set a new background color. If you do not use this method and standard color is white SearchLiveo.
     * In his layout.xml you can use the "app:searchLiveoBackground="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo backgroundColor(int color){
        mViewSearch.setBackgroundColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new text color.
     * In his layout.xml you can use the "app:searchLiveoTextColor="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo textColor(int color){
        mEdtSearch.setTextColor(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new hint color.
     * In his layout.xml you can use the "app:searchLiveoHintColor="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo hintColor(int color){
        mEdtSearch.setHintTextColor(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new text.
     * @param text "valeu"
     */
    public SearchLiveo text(String text){
        mEdtSearch.setText(text);
        return this;
    };

    /**
     * Set a new hint.
     * In his layout.xml you can use the "app:searchLiveoHint="value"" attribute
     * @param text "valeu"
     */
    public SearchLiveo hint(String text){
        mEdtSearch.setHint(text);
        return this;
    };

    /**
     * Set a new text.
     * @param text string attribute - string.xml file
     */
    public SearchLiveo text(int text){
        mEdtSearch.setText(mContext.getString(text));
        return this;
    };

    /**
     * Set a new hint.
     * In his layout.xml you can use the "app:searchLiveoHint="@string/..."" attribute
     * @param text string attribute - string.xml file
     */
    public SearchLiveo hint(int text){
        mEdtSearch.setHint(mContext.getString(text));
        return this;
    };

    /**
     * Set a new color for all icons (arrow, voice and close).
     * In his layout.xml you can use the "app:searchLiveoColorIcon="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIcon(int color){
        this.setColorIcon(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new color for back arrow
     * In his layout.xml you can use the "app:searchLiveoColorArrow="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconArrow(int color){
        this.setColorIconArrow(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new color for voice
     * In his layout.xml you can use the "app:searchLiveoColorVoice="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconVoice(int color){
        this.setColorIconVoice(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new color for close
     * In his layout.xml you can use the "app:searchLiveoColorClose="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconClose(int color){
        this.setColorIconClose(ContextCompat.getColor(mContext, color));
        return this;
    };

    /**
     * Set a new color for statusBar when the SearchLiveo is closed
     * In his layout.xml you can use the "app:searchLiveoStatusBarHideColor="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo statusBarHideColor(int color){
        setStatusBarHideColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for statusBar when the SearchLiveo for visible
     * In his layout.xml you can use the "app:searchLiveoStatusBarShowColor="@color/..."" attribute
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo statusBarShowColor(int color){
        setStatusBarShowColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Hide voice icon
     */
    public SearchLiveo hideVoice(){
        setIsVoice(false);
        mImgVoiceSearch.setVisibility(View.GONE);
        return this;
    }

    /**
     * Show voice icon
     */
    public SearchLiveo showVoice(){
        setIsVoice(true);
        mImgVoiceSearch.setVisibility(View.VISIBLE);
        return this;
    }

    private void colorIcon(){
        if (getColorIcon() != -1 && getColorIconArrow() == -1)  {
            mImgArrowSearch.setColorFilter(this.getColorIcon());
        }

        if (getColorIcon() != -1 && getColorIconVoice() == -1)  {
            mImgVoiceSearch.setColorFilter(this.getColorIcon());
        }

        if (getColorIcon() != -1 && getColorIconClose() == -1)  {
            mImgCloseSearch.setColorFilter(this.getColorIcon());
        }
    };

    private void colorIconArrow(){
        if (getColorIconArrow() != -1) {
            mImgArrowSearch.setColorFilter(this.getColorIconArrow());
        }
    };

    private void colorIconVoice(){
        if (this.getColorIconVoice() != -1) {
            mImgVoiceSearch.setColorFilter(this.getColorIconVoice());
        }else{
            mImgVoiceSearch.clearColorFilter();
        }
    };

    private void colorIconClose(){
        if (this.getColorIconClose() != -1) {
            mImgCloseSearch.setColorFilter(this.getColorIconClose());
        }else{
            mImgCloseSearch.clearColorFilter();
        }
    };

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
            startVoice(mEdtSearch);
        }
    };

    private OnClickListener onClickCloseSearch = new OnClickListener() {
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
            }
        }
    };

    private OnClickListener onClickSearchArrow = new OnClickListener() {
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

    public boolean isVoice() {
        return isVoice;
    }

    public void setIsVoice(boolean isVoice) {
        this.isVoice = isVoice;
    }

    public int getStatusBarHideColor() {
        return mStatusBarHideColor;
    }

    public void setStatusBarHideColor(int mStatusBarHideColor) {
        this.mStatusBarHideColor = mStatusBarHideColor;
    }

    public int getStatusBarShowColor() {
        return mStatusBarShowColor;
    }

    public void setStatusBarShowColor(int mStatusBarShowColor) {
        this.mStatusBarShowColor = mStatusBarShowColor;
    }

    private int getColorIcon() {
        return mColorIcon;
    }

    private void setColorIcon(int colorIcon) {
        this.mColorIcon = colorIcon;
        this.colorIcon();
    }

    public int getColorIconArrow() {
        return mColorIconArrow;
    }

    public void setColorIconArrow(int color) {
        this.mColorIconArrow = color;
        this.colorIconArrow();
    }

    public int getColorIconVoice() {
        return mColorIconVoice;
    }

    public void setColorIconVoice(int color) {
        this.mColorIconVoice = color;
        this.colorIconVoice();
    }

    public int getColorIconClose() {
        return mColorIconClose;
    }

    public void setColorIconClose(int color) {
        this.mColorIconClose = color;
        this.colorIconClose();
    }

    private class OnTextWatcherEdtSearch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                if (mEdtSearch.getText().toString().length() == 0) {
                    mImgCloseSearch.setVisibility(isVoice() ? View.GONE : View.VISIBLE);
                    mImgVoiceSearch.setVisibility(isVoice() ? View.VISIBLE : View.GONE);
                    mImgVoiceSearch.setImageResource(R.drawable.ic_keyboard_voice_color_24dp);
                    colorIconVoice();
                } else {
                    mImgVoiceSearch.setVisibility(View.GONE);
                    mImgCloseSearch.setVisibility(View.VISIBLE);
                    mImgCloseSearch.setImageResource(R.drawable.ic_close_color_24dp);
                    colorIconClose();
                }

                colorIcon();
                colorIconArrow();
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

            mContext.getWindow().setStatusBarColor(getStatusBarShowColor() != -1 ?
                    getStatusBarShowColor() : ContextCompat.getColor(mContext, R.color.colorSearchLiveoPrimaryDark));

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

            mContext.getWindow().setStatusBarColor(getStatusBarHideColor() != -1 ? getStatusBarHideColor() :
                    getColorPrimaryDark());

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
        this.mColorPrimaryDark = ContextCompat.getColor(mContext, mColorPrimaryDark);
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
        //setIsVoice(true);
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
                //setIsVoice(false);
            }
        }
    }
}
