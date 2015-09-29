package br.com.javace.javou.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import br.com.javace.javou.R;
import br.com.javace.javou.adapter.ParticipantAdapter;
import br.com.javace.javou.interfaces.OnItemClickListener;
import br.com.javace.javou.interfaces.OnItemLongClickListener;
import br.com.javace.javou.interfaces.OnScrollListener;
import br.com.javace.javou.model.participant.Participant;
import br.com.javace.javou.model.raffle.Raffle;
import br.com.javace.javou.task.ParticipantDeleteTask;
import br.com.javace.javou.task.ParticipantPresenceTask;
import br.com.javace.javou.task.ParticipantSendTask;
import br.com.javace.javou.task.ParticipantTask;
import br.com.javace.javou.ui.activity.MainActivity;
import br.com.javace.javou.ui.activity.NewParticipantActivity;
import br.com.javace.javou.ui.activity.ParticipantDetailActivity;
import br.com.javace.javou.ui.activity.RaffleActivity;
import br.com.javace.javou.ui.base.BaseActivity;
import br.com.javace.javou.ui.base.BaseFragment;
import br.com.javace.javou.util.Constant;
import br.liveo.searchliveo.SearchLiveo;
import br.liveo.searchliveo.interfaces.OnSearchListener;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Rudsonlive on 10/07/15.
 */
public class ParticipantFragment extends BaseFragment implements OnSearchListener {

    private boolean isActionMode;
    private boolean isSearchView;
    private ProgressDialog mDialog;
    private int mPositionActionMode;

    private OnScrollListener mEndlessListener;
    private ArrayList<Participant> mParticipants;
    private ParticipantAdapter mParticipantAdapter;

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.searchLiveo) SearchLiveo mSearchLiveo;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    @Bind(R.id.floatAdd) FloatingActionButton mBtnFloatAdd;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout mSwipeRefreshLayout;

    public static ParticipantFragment newInstance(){
        return new ParticipantFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participant, container, false);
        ButterKnife.bind(this, view);

        mToolbar.setTitle(getString(R.string.participant));
        mToolbar.setNavigationIcon(R.drawable.ic_face_unlock_white_24dp);
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(15);
        }

        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(manager);
        mEndlessListener = new OnScrollListener(manager, mBtnFloatAdd) {
            @Override
            public void onLoadMore(int currentPage) {
            }

            @Override
            public void onScroll(RecyclerView recyclerView, int dx, int dy, boolean onScroll) {
            }
        };

        mRecyclerView.addOnScrollListener(mEndlessListener);

        mSearchLiveo.with(getActivity(), this).build();
        mBtnFloatAdd.setOnClickListener(onClickFloatAdd);

        mSwipeRefreshLayout.setOnRefreshListener(onRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.shirtSizePColor, R.color.shirtSizeMColor, R.color.shirtSizeGColor, R.color.shirtSizeGGColor);

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null){
            loadParticipant();
        }else{
            isActionMode = savedInstanceState.getBoolean(Constant.ACTION_MODE);
            mParticipants = savedInstanceState.getParcelableArrayList(Constant.PARTICIPANT);

            if (getParticipants() != null){
                resultAdapter(getParticipants());
            }

            if (isActionMode){
                mPositionActionMode = savedInstanceState.getInt(Constant.POSITION);
                showActionMode(mPositionActionMode);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.ACTION_MODE, isActionMode);
        outState.putInt(Constant.POSITION, mPositionActionMode);
        outState.putParcelableArrayList(Constant.PARTICIPANT, getParticipants());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        menu.findItem(R.id.menu_discart).setVisible(false);
        menu.findItem(R.id.menu_delete).setVisible(false);
        isSearchView = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.menu_search:
                isSearchView = true;
                mSearchLiveo.show();
                break;
            case R.id.menu_raffle:
                raffleParticipant();
                break;
            case R.id.menu_send:
                generateSendingFile();
                break;
        }
        return true;
    }

    private void loadParticipant(){
        mEndlessListener.resetEndlessRecyclerView();
        new ParticipantTask(getActivity()){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mSwipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(ArrayList<Participant> participants) {
                super.onPostExecute(participants);

                if (participants != null && participants.size() > 0){
                    mParticipants = new ArrayList<>();
                    mParticipants = participants;

                    resultAdapter(mParticipants);
                }else{
                    //Implementar warning
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute();

    }

    private SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadParticipant();
        }
    };

    private void resultAdapter(ArrayList<Participant> participants){
        mParticipantAdapter = new ParticipantAdapter(getActivity(), participants);
        getParticipantAdapter().setOnClickListener(onClickListener);
        getParticipantAdapter().setOnItemClickListener(onItemClickListener);
        getParticipantAdapter().setOnItemLongClickListener(onItemLongClickListener);
        mRecyclerView.setAdapter(getParticipantAdapter());
    }

    private void raffleParticipant() {
        if (mParticipants != null && mParticipants.size() > 0) {
            Raffle raffle = new Raffle(mParticipants);
            if(raffle.isValid()){
                Intent intent = new Intent(getActivity(), RaffleActivity.class);
                intent.putExtra(Constant.PARTICIPANT, raffle.getFortunate());
                startActivityForResult(intent, 0, BaseActivity.ActivityAnimation.SLIDE_LEFT);
            }else{
                Toast.makeText(getActivity(), R.string.warning_not_participante_fortunate, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), R.string.warning_not_participante_fortunate, Toast.LENGTH_SHORT).show();
        }


    }

    private OnItemClickListener onClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            Intent intent = new Intent(getActivity(), ParticipantDetailActivity.class);
            intent.putExtra(Constant.PARTICIPANT, mParticipants.get(position));
            startActivityForResult(intent, 1, BaseActivity.ActivityAnimation.SLIDE_LEFT);
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {

            if (!isActionMode) {
                confirmUndoPresence(getParticipants().get(position), position);
            }else{
                hideActionMode();
                ((MainActivity)getActivity()).hideActionMode();
            }

            hideSearch();
        }
    };

    private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View v, int position) {
            showActionMode(position);
        }
    };

    private void confirmUndoPresence(final Participant participant, final int position){

        new ParticipantPresenceTask(getActivity(), participant){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog(!participant.isAttend());
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result){
                    if (isSearchView) {
                        loadParticipant();
                        isSearchView = false;
                    } else {
                        getParticipantAdapter().readAttendParticipant(position, !participant.isAttend());
                    }
                }else{
                    Toast.makeText(getActivity(), R.string.error_confirmed_presence, Toast.LENGTH_LONG).show();
                }

                hideDialog();
            }
        }.execute();
    }

    private View.OnClickListener onClickFloatAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideSearch();
            startActivityForResult(new Intent(getActivity(), NewParticipantActivity.class), 0, BaseActivity.ActivityAnimation.SLIDE_LEFT);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                if (mSearchLiveo != null) {
                    mSearchLiveo.resultVoice(requestCode, resultCode, data);
                }
            }else{
                loadParticipant();
            }
        }
    }

    private void showDialog(boolean attend) {
        mDialog = ProgressDialog.show(getActivity(), getString(R.string.wait),
                getString(attend ? R.string.warning_wait_confirmed_presence : R.string.warning_wait_undo_confirmed_presence));
    }

    private void showSendFileDialog() {
        mDialog = ProgressDialog.show(getActivity(), getString(R.string.wait),
                getString(R.string.warning_send_participante_attend));
    }

    private void showDeleteDialog() {
        mDialog = ProgressDialog.show(getActivity(), getString(R.string.wait),
                getString(R.string.warning_delete_wait_participante));
    }

    private void hideDialog(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }

    public void showActionMode(int position){

        if (isActionMode){
            hideActionMode();
        }

        if (getParticipantAdapter() != null) {
            isActionMode = true;
            hideSearch();
            mPositionActionMode = position;
            getParticipantAdapter().setChecked(mPositionActionMode + 1, true);
            ((MainActivity) getActivity()).showActionMode(getParticipants().get(mPositionActionMode).getName());
        }
    }

    public void hideActionMode(){
        hideSearch();

        if (getParticipantAdapter() != null) {
            isActionMode = false;
            getParticipantAdapter().resetarCheck();
            ((MainActivity) getActivity()).hideActionMode();
        }
    }

    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // TODO Auto-generated method stub
            if (mode != null){
                hideActionMode();
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // TODO Auto-generated method stub
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {

                case R.id.menu_delete:
                    confirmDelete(mPositionActionMode);
                    break;
            }

            mode.finish();
            return false;
        }
    };

    private void deleteParticipant(final Participant participant){

        new ParticipantDeleteTask(getActivity(), participant.getId()){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDeleteDialog();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (result) {
                    getParticipantAdapter().remove(mPositionActionMode);

                    if (participant.isGroup()){
                        loadParticipant();
                    }

                    Toast.makeText(getActivity(), R.string.warning_delete_participante, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.error_delete_participante, Toast.LENGTH_SHORT).show();
                }

                hideDialog();
            }
        }.execute();
    }

    private void confirmDelete(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.warning_participant_excluded));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                deleteParticipant(getParticipants().get(position));
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

    public ArrayList<Participant> getParticipants() {
        return mParticipants;
    }

    public ParticipantAdapter getParticipantAdapter() {
        return mParticipantAdapter;
    }

    @Override
    public void changedSearch(CharSequence text) {
        if (mParticipantAdapter != null && !isActionMode){
            mParticipantAdapter.searchParticipantes(text);
        }
    }

    private void hideSearch() {
        if (mSearchLiveo.isActive()) {
            mSearchLiveo.hide();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideSearch();
    }

    private void generateSendingFile(){

        if (mParticipants != null && mParticipants.size() > 0) {
            new ParticipantSendTask(mParticipants) {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    showSendFileDialog();
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);

                    if (aBoolean) {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("message/rfc822");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"handersonbf@gmail.com"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.subject));
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.text));
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Constant.PATH_FILE_JAVOU)));
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
                    } else {
                        Toast.makeText(getActivity(), R.string.warning_not_participante_attend, Toast.LENGTH_SHORT).show();
                    }

                    hideDialog();
                }
            }.execute();
        }else{
            Toast.makeText(getActivity(), R.string.warning_not_participante_attend, Toast.LENGTH_SHORT).show();
        }
    }
}
