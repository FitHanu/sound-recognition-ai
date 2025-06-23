package org.fit.sra.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.fit.sra.CsvDetailActivity;
import org.fit.sra.DangerLevel;
import org.fit.sra.R;
import org.fit.sra.constant.AppConst;
import org.fit.sra.service.FileLoggerService;
import org.fit.sra.util.CommonUtils;

public class RecognitionHistoryFragment extends Fragment {

  private File storagePath;
  private final List<CsvFileMeta> csvFileMeta = new ArrayList<>();

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
    this.storagePath = new File(context.getFilesDir(), "logs");

    this.loadCsvFiles();

    CsvFileAdapter adapter = new CsvFileAdapter(this.csvFileMeta, file -> {
      // Handle file click (replace with navigation or fragment swap as needed)
//      Toast.makeText(context, "Clicked: " + file.getName(), Toast.LENGTH_SHORT).show();
      CsvDetailActivity.start(requireContext(), file.getAbsolutePath());
    });

    recyclerView.setAdapter(adapter);

    return view;
  }

  private void loadCsvFiles() {
    if (this.storagePath.exists()
        && this.storagePath.isDirectory()) {

      File[] files = storagePath.listFiles((dir, name) -> name.endsWith(".csv"));
      List<CsvFileMeta> listMeta = new ArrayList<>();
      if (!CommonUtils.isArrayNullOrEmpty(files)) {

        // Decode filename
        for (File file : files) {
          String fileNameWOExtension = file.getName().replace(".csv", "");
          String[] parts = fileNameWOExtension.split(FileLoggerService.DE);

          if (parts.length != 3) {
            throw new IllegalStateException("File name format is not correct,"
                + " expecting %s_%s_%s.csv format, got: " + file.getName());
          }

          String startTimeStr       = parts[0];
          String highestSeverityStr = parts[1];
          String durationStr        = parts[2];

          ZonedDateTime startTime = CommonUtils
              .parseFormattedDatetimeStr(startTimeStr, AppConst.LOG_DATETIME_FORMAT);
          long durationInSeconds = 0;
          try {
            durationInSeconds = Long.parseLong(durationStr);
          } catch (NumberFormatException e) {
            Log.w("", "Fail converting " + durationStr + " to type long");
          }
          DangerLevel highestSeverity = DangerLevel.createFromStr(highestSeverityStr);


          listMeta.add(new CsvFileMeta(
              startTime,
              durationInSeconds,
              highestSeverity,
              file)
          );
        }

        listMeta.sort((fm1, fm2) -> {
          long n1 = fm1.getDuration();
          long n2 = fm2.getDuration();
          return Long.compare(n2, n1); // Descending
        });

        this.csvFileMeta.addAll(listMeta);
      }
    }
  }

  // RecyclerView Adapter
  private static class CsvFileAdapter extends RecyclerView.Adapter<CsvFileAdapter.ViewHolder> {
    interface OnItemClickListener {
      void onItemClick(File file);
    }

    private final List<CsvFileMeta> files;
    private final OnItemClickListener listener;

    public CsvFileAdapter(List<CsvFileMeta> files, OnItemClickListener listener) {
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
      CsvFileMeta fileMeta = files.get(position);
      holder.bind(fileMeta, listener);
    }

    @Override
    public int getItemCount() {
      return files.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
      TextView startTimeTv;
      TextView durationTv;
      ImageView highestSeverityIv;
      CardView cardView;
      View itemView;

      ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        startTimeTv = itemView.findViewById(R.id.tv_log_start_time);
        durationTv = itemView.findViewById(R.id.tv_history_duration);
        highestSeverityIv = itemView.findViewById(R.id.iv_highest_severity);
        cardView = (CardView) itemView;
      }

      void bind(CsvFileMeta fileMeta, OnItemClickListener listener) {
        startTimeTv.setText(CommonUtils.getFormattedDatetimeStr(
            fileMeta.getStartTime(), AppConst.LOG_DATETIME_DISPLAY_FORMAT));
        durationTv.setText(CommonUtils.getFormatedDuration(fileMeta.getDuration()));
        DangerLevel highestDangerLevel = fileMeta.getHighestDangerLevel();

        switch (highestDangerLevel) {
          case NONE:
            highestSeverityIv.setBackground(
                ContextCompat.getDrawable(this.itemView.getContext(),
                    R.drawable.baseline_circle_ok_16)
            );
            break;
          case LOW:
            highestSeverityIv.setBackground(
                ContextCompat.getDrawable(this.itemView.getContext(),
                    R.drawable.baseline_circle_low_16)
            );
            break;
          case MEDIUM:
            highestSeverityIv.setBackground(
                ContextCompat.getDrawable(this.itemView.getContext(),
                    R.drawable.baseline_circle_med_16)
            );
            break;
          case HIGH:
            highestSeverityIv.setBackground(
                ContextCompat.getDrawable(this.itemView.getContext(),
                    R.drawable.baseline_circle_hi_16)
            );
            break;
        }

        cardView.setOnClickListener(v -> listener.onItemClick(fileMeta.getCsvFile()));
      }
    }
  }

  private static class CsvFileMeta {

    private final ZonedDateTime startTime;
    private final long duration;
    private final DangerLevel highestDangerLevel;
    private final File csvFile;

    public CsvFileMeta(ZonedDateTime startTime, long duration,
        DangerLevel highestDangerLevel, File csvFile) {
      this.startTime = startTime;
      this.duration = duration;
      this.highestDangerLevel = highestDangerLevel;
      this.csvFile = csvFile;
    }

    public ZonedDateTime getStartTime() {
      return startTime;
    }

    public long getDuration() {
      return duration;
    }

    public DangerLevel getHighestDangerLevel() {
      return highestDangerLevel;
    }

    public File getCsvFile() {
      return csvFile;
    }
  }
}
