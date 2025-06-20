package org.fit.sra.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.fit.sra.constant.ModelConst;
import android.util.Log;
import android.content.Context;



public class CsvUtils {
  public static Map<Integer, String> severityByIndex = new HashMap<>();
  public static void readSoundCsv(Context context) {
    try {
            Log.d("CSV", "Reading from assets...");
            String classCSVPath = "models/yamnet/"+ ModelConst.DEFAULT__CONFIG_CSV;
            InputStream is = context.getAssets().open(classCSVPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // skip header
                    continue;
                }
                String[] tokens = line.split(",");
                int index = Integer.parseInt(tokens[0].trim()); // e.g. "0"
                String label = tokens[1].trim();               // e.g. "Explosion"
                String severity = tokens[2].trim();  
                 Log.d("CSV", severity);          // e.g. "High"
                severityByIndex.put(index, severity);
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CSV", "Failed to read CSV", e);
        }
  }
}
