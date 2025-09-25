package com.example.storypointer.ml;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.ValueInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class OnnxInferenceRunner implements InferenceRunner {

    private static final Logger log = LoggerFactory.getLogger(OnnxInferenceRunner.class);

    private final OnnxModelLoader loader;
    private final OrtEnvironment environment;
    private final String modelVersion;

    public OnnxInferenceRunner(Path modelPath, String modelVersion) {
        this.loader = new OnnxModelLoader();
        this.environment = OrtEnvironment.getEnvironment();
        this.modelVersion = modelVersion;
        try {
            loader.load(modelPath);
        } catch (Exception e) {
            log.error("Failed to load ONNX model", e);
        }
    }

    @Override
    public PredictionResult predict(String text) {
        OrtSession session = loader.getSession();
        if (session == null) {
            throw new IllegalStateException("ONNX model not loaded");
        }
        String inputName = session.getInputNames().iterator().next();
        try {
            try (OnnxTensor tensor = OnnxTensor.createTensor(environment, new String[]{text})) {
                Result result = session.run(Map.of(inputName, tensor));
                String label = extractLabel(result);
                double score = extractScore(result);
                result.close();
                return new PredictionResult(label, score);
            }
        } catch (OrtException e) {
            throw new IllegalStateException("ONNX inference failed", e);
        }
    }

    private String extractLabel(Result result) {
        for (String outputName : result.getOutputNames()) {
            ValueInfo info = loader.getSession().getOutputInfo().get(outputName);
            if (info != null && info.getInfo() instanceof ai.onnxruntime.NodeInfo nodeInfo) {
                // fallthrough to handle generically
            }
        }
        return result.stream()
                .map(output -> {
                    try {
                        Object value = output.getValue();
                        if (value instanceof String[] arr) {
                            return arr[0];
                        }
                        if (value instanceof String[][] arr) {
                            return arr[0][0];
                        }
                    } catch (OrtException e) {
                        log.debug("Unable to read label", e);
                    }
                    return null;
                })
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse("unknown");
    }

    private double extractScore(Result result) {
        return result.stream().map(output -> {
            try {
                Object value = output.getValue();
                if (value instanceof float[][] arr) {
                    return (double) arr[0][0];
                }
                if (value instanceof float[] arr) {
                    return (double) arr[0];
                }
                if (value instanceof double[][] arr) {
                    return arr[0][0];
                }
                if (value instanceof double[] arr) {
                    return arr[0];
                }
            } catch (OrtException e) {
                log.debug("Unable to read score", e);
            }
            return Double.NaN;
        }).filter(d -> !Double.isNaN(d)).findFirst().orElse(0.0);
    }

    @Override
    public String modelVersion() {
        return modelVersion;
    }

    @Override
    public boolean isLoaded() {
        return loader.getSession() != null;
    }
}
