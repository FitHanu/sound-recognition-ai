package org.fit.sra.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.fit.sra.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecognitionHistoryFragment extends Fragment {

  private File storagePath;
  private final List<File> csvFiles = new ArrayList<>();

  public RecognitionHistoryFragment() {
    // Required empty public constructor
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_history_list, container, false);

    RecyclerView recyclerView = view.findViewById(R.id.recycler_csv_files);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    Context context = requireContext();
    storagePath = new File(context.getFilesDir(), "logs");

    loadCsvFiles();

    CsvFileAdapter adapter = new CsvFileAdapter(csvFiles, file -> {
      // Handle file click (replace with navigation or fragment swap as needed)
      Toast.makeText(context, "Clicked: " + file.getName(), Toast.LENGTH_SHORT).show();
      // TODO: You can pass `file.getAbsolutePath()` to a detail fragment here
    });

    recyclerView.setAdapter(adapter);

    return view;
  }

  private void loadCsvFiles() {
    if (storagePath.exists() && storagePath.isDirectory()) {
      File[] files = storagePath.listFiles((dir, name) -> name.endsWith(".csv"));
      if (files != null) {
        List<File> sortedFiles = Arrays.stream(files)
            .sorted((f1, f2) -> {
              long n1 = Long.parseLong(f1.getName().replace(".csv", ""));
              long n2 = Long.parseLong(f2.getName().replace(".csv", ""));
              return Long.compare(n2, n1); // descending
            })
            .collect(Collectors.toList());
        csvFiles.addAll(sortedFiles);
      }
    }
  }

  // RecyclerView Adapter
  private static class CsvFileAdapter extends RecyclerView.Adapter<CsvFileAdapter.ViewHolder> {
    interface OnItemClickListener {
      void onItemClick(File file);
    }

    private final List<File> files;
    private final OnItemClickListener listener;

    public CsvFileAdapter(List<File> files, OnItemClickListener listener) {
      this.files = files;
      this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.list_item_history, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      File file = files.get(position);
      holder.bind(file, listener);
    }

    @Override
    public int getItemCount() {
      return files.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      TextView filenameView;
      CardView cardView;

      ViewHolder(@NonNull View itemView) {
        super(itemView);
        filenameView = itemView.findViewById(R.id.tv_csv_filename);
        cardView = (CardView) itemView;
      }

      void bind(File file, OnItemClickListener listener) {
        filenameView.setText(file.getName());
        cardView.setOnClickListener(v -> listener.onItemClick(file));
      }
    }
  }
}
