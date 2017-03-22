package com.littlechoc.olddriver.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.littlechoc.olddriver.R;
import com.littlechoc.olddriver.contract.HistoryContract;
import com.littlechoc.olddriver.model.RecordModel;
import com.littlechoc.olddriver.presenter.HistoryPresenter;
import com.littlechoc.olddriver.ui.adapter.HistoryAdapter;
import com.littlechoc.olddriver.ui.base.BaseActivity;
import com.littlechoc.olddriver.ui.view.DividerItemDecoration;
import com.littlechoc.olddriver.ui.view.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Junhao Zhou 2017/3/17
 */

public class HistoryActivity extends BaseActivity implements HistoryContract.View {

  @BindView(R.id.title_bar)
  public Toolbar titleBar;

  @BindView(R.id.history_list)
  public RecyclerView historyList;

  @BindView(R.id.empty_view)
  public EmptyView emptyView;

  private HistoryAdapter adapter;

  private final List<RecordModel> records = new ArrayList<>();

  private HistoryContract.Presenter historyPresenter;

  @Override
  public int getRootView() {
    return R.layout.activity_history;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
    initView();
  }

  private void init() {
    new HistoryPresenter(this);
  }

  private void initView() {
    setSupportActionBar(titleBar);
    titleBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    adapter = new HistoryAdapter(records);
    historyList.setLayoutManager(new LinearLayoutManager(this));
    historyList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    historyList.setAdapter(adapter);
    adapter.setOnHistoryItemClickListener(new HistoryAdapter.OnHistoryItemClickListener() {
      @Override
      public void onClickHistoryItem(int position) {
        historyPresenter.openDisplayActivity(records.get(position).getName());
      }
    });

    emptyView.setEmptyText("没有记录");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_history_activity, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.clear:
        historyPresenter.clear();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (records.size() == 0) {
      historyPresenter.loadData();
    }
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public void setPresenter(HistoryContract.Presenter presenter) {
    historyPresenter = presenter;
    historyPresenter.bindData(records);
  }

  @Override
  public void updateList() {
    adapter.notifyDataSetChanged();
  }
}
