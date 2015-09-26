package br.com.javace.javou.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.javace.javou.R;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.util.Constant;
import br.com.javace.javou.util.Util;
import butterknife.Bind;
import butterknife.ButterKnife;


public class ParticipantDetailActivity extends BaseActivity {

    private ProgressDialog mDialog;
    private Participant mParticipant;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.txtEmail) TextView mTxtEmail;
    @Bind(R.id.txtPhone) TextView mTxtPhone;
    @Bind(R.id.txtAttend) TextView mTxtAttend;
    @Bind(R.id.imgPhoto) ImageView mImgPhoto;
    @Bind(R.id.txtShirtSize) TextView mTxtShirtSize;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_detail);

        ButterKnife.bind(this);
        this.setSupportActionBar(mToolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mParticipant = getIntent().getExtras().getParcelable(Constant.PARTICIPANT);
        mCollapsingToolbar.setTitle(mParticipant != null ? mParticipant.getName() : getString(R.string.app_name));

        if (mParticipant != null){
            mTxtEmail.setText(mParticipant.getEmail());
            mTxtPhone.setText(mParticipant.getPhone());

            GradientDrawable gradientDrawable = (GradientDrawable) mTxtShirtSize.getBackground();
            gradientDrawable.setColor(getResources().getColor(Util.shirtSizeColor[mParticipant.getShirtSize()]));
            mTxtShirtSize.setText(getString(Util.shirtSize[mParticipant.getShirtSize()]));

            if (mParticipant.isAttend()) {
                mTxtAttend.setText(getString(R.string.attended_event));
                mTxtAttend.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green_18dp, 0, 0, 0);
            }

            if (mParticipant.getPhoto() != null && !mParticipant.getPhoto().equals("")) {
                Glide.with(this).load(mParticipant.getPhoto()).centerCrop().into(mImgPhoto);
            }else{
                Glide.with(this).load(R.drawable.ic_suricate).centerCrop().into(mImgPhoto);
            }
        }

        colorPrimaryIcon();
    }

    private void colorPrimaryIcon(){
        final ImageView imgEmail = (ImageView) findViewById(R.id.imgEmail);
        final ImageView imgPhone = (ImageView) findViewById(R.id.imgPhone);
        final ImageView imgAttend = (ImageView) findViewById(R.id.imgAttend);
        final ImageView imgShirtSize = (ImageView) findViewById(R.id.imgShirtSize);

        imgEmail.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        imgPhone.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        imgAttend.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        imgShirtSize.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_raffle).setVisible(false);
        menu.findItem(R.id.menu_discart).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(ActivityAnimation.SLIDE_RIGHT);
                break;

            case R.id.menu_delete:
                confirmDelete();
                break;
        }
        return true;
    }

//    private void deleteParticipant(){
//        showDialog();
//        ParseObject gameScore = new ParseObject(Constant.PARTICIPANT);
//        gameScore.setObjectId(mParticipant.getObjectId());
//        gameScore.deleteInBackground(new DeleteCallback() {
//            @Override
//            public void done(ParseException error) {
//                if (error == null) {
//                    Intent intent = new Intent();
//                    intent.putExtra(Constant.PARTICIPANT, true);
//                    setResult(0, intent);
//                    finish();
//                    Toast.makeText(getApplicationContext(), R.string.warning_delete_participante, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.error_delete_participante, Toast.LENGTH_SHORT).show();
//                }
//
//                hideDialog();
//            }
//        });
//    }

    private void showDialog() {
        mDialog = ProgressDialog.show(this, getString(R.string.wait),
                getString(R.string.warning_delete_wait_participante));
    }

    private void hideDialog(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    private void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.warning_participant_excluded));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                //deleteParticipant();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(ActivityAnimation.SLIDE_RIGHT);
    }
}
