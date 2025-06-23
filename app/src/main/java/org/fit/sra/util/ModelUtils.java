//package org.fit.sra.util;
//
//import android.content.Context;
//
//
//import com.google.gson.Gson;
//
//import org.fit.sra.constant.AppConst;
//import org.fit.sra.model.ClassifierModelConfig;
//
//import java.io.InputStreamReader;
//import java.util.List;
//
//public class ModelUtils {
//
//    private ModelUtils() {}
//
//    public static List<ClassifierModelConfig> getModels(Context context, String id) {
//        try (InputStreamReader reader = new InputStreamReader(
//                context.getAssets().open(AppConst.MODEL_INDEX_JSON))) {
//
//            Type listType = new TypeToken<List<ModelConfig>>(){}.getType();
//            return new Gson().fromJson(reader, listType);
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to load model configs", e);
//        }
//    }
//
//}
