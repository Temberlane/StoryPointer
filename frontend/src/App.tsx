import React, { useEffect, useState } from 'react';
import axios from 'axios';

type Segment = {
  start: number;
  end: number;
  text: string;
  speaker: string;
};

type Transcript = {
  videoId: string;
  language: string;
  segments: Segment[];
  durationSec: number;
  diarization: boolean;
};

type Prediction = {
  videoId: string;
  modelVersion: string;
  prediction: string;
  score: number;
  highlights: { text: string; start: number; end: number }[];
};

type QaResult = {
  answer: string;
  citations: { text: string; start: number; end: number }[];
};

const App: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [sourceUrl, setSourceUrl] = useState('');
  const [videoId, setVideoId] = useState('');
  const [status, setStatus] = useState('');
  const [transcript, setTranscript] = useState<Transcript | null>(null);
  const [prediction, setPrediction] = useState<Prediction | null>(null);
  const [qaQuestion, setQaQuestion] = useState('');
  const [qaResult, setQaResult] = useState<QaResult | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!videoId || status === 'ready' || status === 'error') {
      return;
    }
    const interval = setInterval(async () => {
      const res = await axios.get(`/videos/${videoId}/status`);
      setStatus(res.data.status);
    }, 2000);
    return () => clearInterval(interval);
  }, [videoId, status]);

  useEffect(() => {
    const fetchTranscript = async () => {
      if (videoId && status === 'ready') {
        try {
          const res = await axios.get(`/videos/${videoId}/transcript`);
          setTranscript(res.data);
        } catch (err) {
          console.error(err);
        }
      }
    };
    fetchTranscript();
  }, [videoId, status]);

  const uploadFile = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    setLoading(true);
    const res = await axios.post('/videos', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    setVideoId(res.data.videoId);
    setStatus(res.data.status);
    setTranscript(null);
    setPrediction(null);
    setLoading(false);
  };

  const uploadUrl = async () => {
    if (!sourceUrl) return;
    setLoading(true);
    const res = await axios.post('/videos', { sourceUrl });
    setVideoId(res.data.videoId);
    setStatus(res.data.status);
    setTranscript(null);
    setPrediction(null);
    setLoading(false);
  };

  const runPredict = async () => {
    if (!videoId) return;
    const res = await axios.post('/predict', { videoId });
    setPrediction(res.data);
  };

  const runQa = async () => {
    if (!videoId || !qaQuestion) return;
    const res = await axios.post('/qa', { videoId, question: qaQuestion });
    setQaResult(res.data);
  };

  return (
    <>
      <header>
        <h1>StoryPointer</h1>
        <p>Upload a video, transcribe, and predict with ONNX.</p>
      </header>
      <main>
        <section className="card">
          <h2>1. Upload</h2>
          <div>
            <input type="file" accept="video/*" onChange={(e) => setFile(e.target.files?.[0] || null)} />
            <button onClick={uploadFile} disabled={!file || loading}>Upload file</button>
          </div>
          <div style={{ marginTop: '1rem' }}>
            <input
              type="url"
              placeholder="https://example.com/video.mp4"
              value={sourceUrl}
              onChange={(e) => setSourceUrl(e.target.value)}
            />
            <button onClick={uploadUrl} disabled={!sourceUrl || loading} style={{ marginTop: '0.5rem' }}>
              Submit URL
            </button>
          </div>
          {videoId && (
            <p>
              Video ID: <strong>{videoId}</strong> – Status: <strong>{status}</strong>
            </p>
          )}
        </section>

        {transcript && (
          <section className="card">
            <h2>2. Transcript</h2>
            <div className="transcript">
              {transcript.segments.map((segment, idx) => (
                <div key={idx} className="segment">
                  <strong>{segment.speaker}</strong> [{segment.start.toFixed(2)}s – {segment.end.toFixed(2)}s]
                  <div>{segment.text}</div>
                </div>
              ))}
            </div>
          </section>
        )}

        {videoId && (
          <section className="card">
            <h2>3. Predict</h2>
            <button onClick={runPredict} disabled={status !== 'ready'}>
              Run prediction
            </button>
            {prediction && (
              <div style={{ marginTop: '1rem' }}>
                <p>
                  Prediction: <strong>{prediction.prediction}</strong> ({(prediction.score * 100).toFixed(1)}%)
                </p>
                <h3>Highlights</h3>
                <ul className="highlight-list">
                  {prediction.highlights.map((highlight, idx) => (
                    <li key={idx}>
                      <div>{highlight.text}</div>
                      <small>
                        {highlight.start.toFixed(2)}s – {highlight.end.toFixed(2)}s
                      </small>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </section>
        )}

        {videoId && (
          <section className="card">
            <h2>4. Ask a question</h2>
            <textarea
              rows={3}
              placeholder="What was the sentiment?"
              value={qaQuestion}
              onChange={(e) => setQaQuestion(e.target.value)}
            />
            <button onClick={runQa} disabled={!qaQuestion}>Ask</button>
            {qaResult && (
              <div style={{ marginTop: '1rem' }}>
                <p>{qaResult.answer}</p>
                <h3>Citations</h3>
                <ul className="highlight-list">
                  {qaResult.citations.map((citation, idx) => (
                    <li key={idx}>
                      <div>{citation.text}</div>
                      <small>
                        {citation.start.toFixed(2)}s – {citation.end.toFixed(2)}s
                      </small>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </section>
        )}
      </main>
    </>
  );
};

export default App;
