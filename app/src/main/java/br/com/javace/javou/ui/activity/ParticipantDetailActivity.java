package br.com.javace.javou.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    @Bind(R.id.txt_email) TextView mTxtEmail;
    @Bind(R.id.txt_phone) TextView mTxtPhone;
    @Bind(R.id.txt_attend) TextView mTxtAttend;
    @Bind(R.id.img_photo) ImageView mImgPhoto;
    @Bind(R.id.txt_shirt_size) TextView mTxtShirtSize;
    @Bind(R.id.float_edit) FloatingActionButton mFloatEdit;
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

        mFloatEdit.setOnClickListener(onClickEdit);
        mParticipant = getIntent().getExtras().getParcelable(Constant.PARTICIPANT);
        mCollapsingToolbar.setTitle(mParticipant != null ? mParticipant.getName() : getString(R.string.app_name));

        if (mParticipant != null){
            mTxtEmail.setText(mParticipant.getEmail());
            mTxtPhone.setText(mParticipant.getPhone());

            GradientDrawable gradientDrawable = (GradientDrawable) mTxtShirtSize.getBackground();
            gradientDrawable.setColor(ContextCompat.getColor(this, Util.shirtSizeColor[mParticipant.getShirtSize()]));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_send).setVisible(false);
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

    private View.OnClickListener onClickEdit  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), NewParticipantActivity.class);
            intent.putExtra(Constant.PARTICIPANT, mParticipant);
            startActivityForResult(intent, 1, BaseActivity.ActivityAnimation.SLIDE_LEFT);
        }
    };


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
