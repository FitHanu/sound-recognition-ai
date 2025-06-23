package org.fit.sra;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.fit.sra.model.CsvRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvDetailActivity extends AppCompatActivity {

  private CsvDetailAdapter adapter;
  private final List<CsvRow> allRows = new ArrayList<>();
  private int currentIndex = 0;
  private static final int BATCH_SIZE = 50;

  private File csvFile;

  public static void start(Context context, String filePath) {
    Intent intent = new Intent(context, CsvDetailActivity.class);
    intent.putExtra("csv_path", filePath);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history_details);
    Toolbar toolbar = findViewById(R.id.activity_history_toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    String path = getIntent().getStringExtra("csv_path");
    if (path == null) {
      Toast.makeText(this, "CSV path missing", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    csvFile = new File(path);

    RecyclerView recycler = findViewById(R.id.recycler_csv_detail);
    recycler.setLayoutManager(new LinearLayoutManager(this));

    adapter = new CsvDetailAdapter();
    recycler.setAdapter(adapter);

    loadAllRows();
    loadNextBatch();

    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        if (layoutManager != null &&
            layoutManager.findLastVisibleItemPosition() >= adapter.getItemCount() - 1) {
          loadNextBatch();
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void loadAllRows() {
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] tokens = line.split(",");
        if (tokens.length == 4) {
          allRows.add(new CsvRow(tokens[0], tokens[1], tokens[2], tokens[3]));
        }
      }
    } catch (IOException e) {
      Log.e("CsvDetailActivity", "Failed to read CSV", e);
      Toast.makeText(this, "Failed to read CSV", Toast.LENGTH_SHORT).show();
    }
  }

  private void loadNextBatch() {
    int end = Math.min(currentIndex + BATCH_SIZE, allRows.size());
    if (currentIndex < end) {
      adapter.addRows(allRows.subList(currentIndex, end));
      currentIndex = end;
    }
  }

  public static class CsvDetailAdapter
      extends RecyclerView.Adapter<CsvDetailAdapter.RowViewHolder> {
    private final List<CsvRow> rows = new ArrayList<>();

    public void addRows(List<CsvRow> newRows) {
      int start = rows.size();
      rows.addAll(newRows);
      notifyItemRangeInserted(start, newRows.size());
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.list_item_detail_history_row, parent, false);
      return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
      CsvRow row = rows.get(position);
      holder.time.setText(row.time);
      holder.label.setText(row.label);
      holder.category.setText(row.category);
      holder.confidence.setText(row.confidence);
    }

    @Override
    public int getItemCount() {
      return rows.size();
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {
      TextView time, label, category, confidence;

      public RowViewHolder(View itemView) {
        super(itemView);
        time = itemView.findViewById(R.id.cell_time);
        label = itemView.findViewById(R.id.cell_label);
        category = itemView.findViewById(R.id.cell_category);
        confidence = itemView.findViewById(R.id.cell_confidence);
      }
    }
  }
}
