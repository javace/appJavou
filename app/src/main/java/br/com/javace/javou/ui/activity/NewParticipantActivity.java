package br.com.javace.javou.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.task.ParticipantInsertTask;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;
import br.com.javace.javou.util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;

public class NewParticipantActivity extends BaseActivity{

    private int mShirtSize = 0;
    private ProgressDialog mDialog;

    @Bind(R.id.imgSex) ImageView mImgSex;
    @Bind(R.id.swSex) SwitchCompat mSwSex;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.edtName) EditText mEdtName;
    @Bind(R.id.edtEmail) EditText mEdtEmail;
    @Bind(R.id.edtPhone) EditText mEdtPhone;
    @Bind(R.id.edtCompany) EditText mEdtCompany;
    @Bind(R.id.swAttend) SwitchCompat mSwAttend;

    @Bind(R.id.txtShirtSizeP) TextView mTxtShirtSizeP;
    @Bind(R.id.txtShirtSizeM) TextView mTxtShirtSizeM;
    @Bind(R.id.txtShirtSizeG) TextView mTxtShirtSizeG;
    @Bind(R.id.txtShirtSizeGG) TextView mTxtShirtSizeGG;
    @Bind(R.id.txtShirtSizeEG) TextView mTxtShirtSizeEG;

    @Bind(R.id.layoutName) LinearLayout mLayoutName;
    @Bind(R.id.layoutEmail) LinearLayout mLayoutEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_participant);
        ButterKnife.bind(this);

        mToolbar.setTitle(getString(R.string.new_participant));
        mToolbar.setNavigationIcon(R.drawable.ic_done_white_24dp);
        this.setSupportActionBar(mToolbar);

        setupOnClickListener();
        if (savedInstanceState != null){
            mShirtSize = savedInstanceState.getInt(Constant.PARTICIPANT_shirtSize, 0);
        }

        colorDefaultShirtSize(mShirtSize);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_raffle).setVisible(false);
        menu.findItem(R.id.menu_delete).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_send).setVisible(false);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constant.PARTICIPANT_shirtSize, mShirtSize);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedAttend = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSwAttend.setCompoundDrawablesWithIntrinsicBounds(isChecked ? R.drawable.ic_check_green_18dp : 0, 0, 0, 0);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedSex = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSwSex.setText(isChecked ? R.string.female : R.string.male);
            mImgSex.setColorFilter(ContextCompat.getColor(NewParticipantActivity.this, isChecked ? R.color.shirtSizeGColor : R.color.shirtSizeMColor));
            mImgSex.setImageDrawable(ContextCompat.getDrawable(NewParticipantActivity.this, isChecked ? R.drawable.ic_favorite_grey600_24dp : R.drawable.ic_android_grey600_24dp));

            if (isChecked){
                YoYo.with(Techniques.Landing).playOn(mImgSex);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                    if (validation()){
                        saveParticipant();
                    }
                break;

            case R.id.menu_discart:
                finish(ActivityAnimation.SLIDE_RIGHT);
                break;
        }

        return true;
    }

    private void setupOnClickListener(){
        mSwAttend.setOnCheckedChangeListener(onCheckedAttend);
        mSwAttend.setChecked(true);

        mSwSex.setOnCheckedChangeListener(onCheckedSex);
        mSwSex.setChecked(false);

        mTxtShirtSizeP.setOnClickListener(onClickShirtSize);
        mTxtShirtSizeM.setOnClickListener(onClickShirtSize);
        mTxtShirtSizeG.setOnClickListener(onClickShirtSize);
        mTxtShirtSizeGG.setOnClickListener(onClickShirtSize);
        mTxtShirtSizeEG.setOnClickListener(onClickShirtSize);
    }

    private boolean validation(){
		boolean result = true;
        if (mEdtName.getText().toString().trim().equals("")){
            result = false;
            mEdtName.requestFocus();
            YoYo.with(Techniques.Shake).playOn(mLayoutName);
            Toast.makeText(getApplicationContext(), R.string.warning_no_name, Toast.LENGTH_SHORT).show();
        }else if (mEdtEmail.getText().toString().trim().equals("")){
            result = false;
            mEdtEmail.requestFocus();
            YoYo.with(Techniques.Shake).playOn(mLayoutEmail);
            Toast.makeText(getApplicationContext(), R.string.warning_no_email, Toast.LENGTH_SHORT).show();
        }

		return result;
	}

    private void saveParticipant(){
        showDialog();

        Participant participant = new Participant();
        participant.setName(mEdtName.getText().toString());
        participant.setPhone(mEdtPhone.getText().toString());
        participant.setEmail(mEdtEmail.getText().toString());
        participant.setShirtSize(mShirtSize);
        participant.setAttend(mSwAttend.isChecked());
        participant.setNameEvent("Javou #05 - 26/09/2015");
        participant.setBirthDate("");
        participant.setRaffled(false);
        participant.setSex(mSwSex.isChecked());
        participant.setCompany(mEdtCompany.getText().toString());

        new ParticipantInsertTask(this, participant){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result){
                    Intent intent = new Intent();
                    intent.putExtra(Constant.PARTICIPANT, true);
                    setResult(0, intent);
                    finish(ActivityAnimation.SLIDE_RIGHT);
                    Toast.makeText(getApplicationContext(), R.string.warning_save_participante, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.error_save_participante, Toast.LENGTH_SHORT).show();
                }

                hideDialog();
            }
        }.execute();
    }

    private View.OnClickListener onClickShirtSize = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetColorShirtSize();

            switch (v.getId()){

                case R.id.txtShirtSizeP:
                    mShirtSize = 0;
                    break;

                case R.id.txtShirtSizeM:
                    mShirtSize = 1;
                    break;

                case R.id.txtShirtSizeG:
                    mShirtSize = 2;
                    break;

                case R.id.txtShirtSizeGG:
                    mShirtSize = 3;
                    break;

                case R.id.txtShirtSizeEG:
                    mShirtSize = 4;
                    break;

            }

            colorDefaultShirtSize(mShirtSize);
        }
    };

    private void resetColorShirtSize(){
        GradientDrawable gradientDrawable = (GradientDrawable) mTxtShirtSizeP.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.dividerColor));

        gradientDrawable = (GradientDrawable) mTxtShirtSizeM.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.dividerColor));

        gradientDrawable = (GradientDrawable) mTxtShirtSizeG.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.dividerColor));

        gradientDrawable = (GradientDrawable) mTxtShirtSizeGG.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.dividerColor));

        gradientDrawable = (GradientDrawable) mTxtShirtSizeEG.getBackground();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.dividerColor));
    }

    private void colorDefaultShirtSize(int shirtSize){
        resetColorShirtSize();
        GradientDrawable gradientDrawable;

        switch (shirtSize) {
            case 0:
                gradientDrawable = (GradientDrawable) mTxtShirtSizeP.getBackground();
                YoYo.with(Techniques.Landing).withListener(new AnimatorShirtSize(gradientDrawable, shirtSize)).playOn(mTxtShirtSizeP);
                break;

            case 1:
                gradientDrawable = (GradientDrawable) mTxtShirtSizeM.getBackground();
                YoYo.with(Techniques.Landing).withListener(new AnimatorShirtSize(gradientDrawable, shirtSize)).playOn(mTxtShirtSizeM);
                break;

            case 2:
                gradientDrawable = (GradientDrawable) mTxtShirtSizeG.getBackground();
                YoYo.with(Techniques.Landing).withListener(new AnimatorShirtSize(gradientDrawable, shirtSize)).playOn(mTxtShirtSizeG);
                break;

            case 3:
                gradientDrawable = (GradientDrawable) mTxtShirtSizeGG.getBackground();
                YoYo.with(Techniques.Landing).withListener(new AnimatorShirtSize(gradientDrawable, shirtSize)).playOn(mTxtShirtSizeGG);
                break;

            case 4:
                gradientDrawable = (GradientDrawable) mTxtShirtSizeEG.getBackground();
                YoYo.with(Techniques.Landing).withListener(new AnimatorShirtSize(gradientDrawable, shirtSize)).playOn(mTxtShirtSizeEG);
                break;
        }
    }

    private class AnimatorShirtSize implements Animator.AnimatorListener{

        private int mPosition;
        private GradientDrawable mGradientDrawable;

        public AnimatorShirtSize(GradientDrawable gradientDrawable, int position){
            this.mPosition = position;
            this.mGradientDrawable = gradientDrawable;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (mGradientDrawable != null) {
                mGradientDrawable.setColor(ContextCompat.getColor(NewParticipantActivity.this, Util.shirtSizeColor[mPosition]));
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private void showDialog() {
        mDialog = ProgressDialog.show(this, getString(R.string.wait),
                getString(R.string.warning_save_wait_participante));
    }

    private void hideDialog(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }
}
