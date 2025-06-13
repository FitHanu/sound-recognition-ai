package org.fit.sra.model;

import org.fit.sra.DangerLevel;

import java.util.List;
import java.util.Map;

/**
 *
 * Not yet used
 *
 */
public class ClassifierModelConfig {

    private final String modelName;
    private final List<String> classNames;
    private final Map<Integer, DangerLevel> severityConfig;


    public ClassifierModelConfig(String modelName,
                                 List<String> classNames,
                                 Map<Integer, DangerLevel> severityConfig) {
        this.modelName = modelName;
        this.classNames = classNames;
        this.severityConfig = severityConfig;
    }

    public String getModelName() {
        return modelName;
    }

    public List<String> getClassNames() {
        return classNames;
    }

    public Map<Integer, DangerLevel> getSeverityConfig() {
        return severityConfig;
    }

}
