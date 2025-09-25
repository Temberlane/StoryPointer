package com.example.storypointer.ml;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OnnxModelLoader implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(OnnxModelLoader.class);

    private final OrtEnvironment environment;
    private OrtSession session;

    public OnnxModelLoader() {
        this.environment = OrtEnvironment.getEnvironment();
    }

    public synchronized void load(Path modelPath) throws IOException, OrtException {
        byte[] bytes = Files.readAllBytes(modelPath);
        if (session != null) {
            session.close();
        }
        session = environment.createSession(bytes);
        log.info("ONNX model loaded from {}", modelPath);
    }

    public synchronized OrtSession getSession() {
        return session;
    }

    @Override
    public void close() throws Exception {
        if (session != null) {
            session.close();
        }
        environment.close();
    }
}
