package com.phloxinc.whereworks.fragments.managment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.phloxinc.whereworks.R;
import com.phloxinc.whereworks.adapters.ContactAdapter;
import com.phloxinc.whereworks.bo.Member;
import com.phloxinc.whereworks.process.Process;
import com.phloxinc.whereworks.process.ProcessRequest;
import com.phloxinc.whereworks.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements View.OnClickListener, ProcessRequest.RequestListener<Object>, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private ContactAdapter<Member> adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(Utils.getColorScheme());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        fab.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Member> memberList = new ArrayList<>();
        try {
            memberList = Member.all();
            if (!memberList.isEmpty()) {
                Member member = new Member();
                member.setMemberId(-1);
                memberList.add(member);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        adapter = new ContactAdapter<>(memberList);
        recyclerView.setAdapter(adapter);

        if (Utils.IsInternetAvailable(getContext())) {
            swipeContainer.setRefreshing(true);
            new ProcessRequest<>(Process.MEMBER_TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.fab) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Add a Contact");
            View dialogLayout = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_add_contact, null);
            final EditText nameField = (EditText) dialogLayout.findViewById(R.id.name);
            final EditText emailField = (EditText) dialogLayout.findViewById(R.id.email);
            final EditText numberField = (EditText) dialogLayout.findViewById(R.id.number);
            builder.setView(dialogLayout);
            builder.setPositiveButton("SEND INVITE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    String name = nameField.getText().toString();
                    String email = emailField.getText().toString();
                    String number = numberField.getText().toString();

                    if (!name.isEmpty()) {
                        if (!email.isEmpty()) {
                            if (Utils.IsInternetAvailable(view.getContext())) {
                                new ProcessRequest<>(Process.MEMBER_INVITATION, ContactsFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email, name);
                                dialog = new ProgressDialog(getActivity());
                                dialog.setMessage("Sending Invite");
                                dialog.show();
                            } else {
                                Utils.showDialog(getContext(), "No internet");
                            }
                        } else if (!number.isEmpty()) {
                            if (Utils.IsInternetAvailable(view.getContext())) {
                                new ProcessRequest<>(Process.MEMBER_INVITATION, ContactsFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, email, name, number);
                                dialog = new ProgressDialog(getActivity());
                                dialog.setMessage("Sending Invite");
                                dialog.show();
                            } else {
                                Utils.showDialog(getContext(), "No internet");
                            }
                        } else {
                            Utils.showDialog(getContext(), "Email / Phone Number is Missing");
                        }
                    } else {
                        Utils.showDialog(getContext(), "Name is Missing");
                    }

                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });

            builder.show();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(String process, Object result) {
        switch (process) {
            case Process.MEMBER_TEAM_LIST:
                if (result != null) {
                    List<Member> memberList = Member.all();
                    if (!memberList.isEmpty()) {
                        Member member = new Member();
                        member.setMemberId(-1);
                        memberList.add(member);
                        adapter.setFeeds(memberList);
                    }
                }
                swipeContainer.setRefreshing(false);
                break;

            case Process.MEMBER_INVITATION:
                dialog.dismiss();
                if (Utils.IsInternetAvailable(getContext())) {
                    swipeContainer.setRefreshing(true);
                    new ProcessRequest<>(Process.MEMBER_TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
        }
    }

    @Override
    public void onFailure(String process) {
        swipeContainer.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        if (Utils.IsInternetAvailable(getContext())) {
            new ProcessRequest<>(Process.MEMBER_TEAM_LIST, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
