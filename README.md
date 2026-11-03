# PAGES Android

PAGES is an Android book and article reader with Kyutai Pocket TTS **english_18m_refl_2026-10 (18.2M parameters)** running on-device through ONNX Runtime Web/WASM inside a local Android WebView.

## Model packaging

The GitHub Actions workflow downloads and packages the pinned model bundle into the APK. The APK contains the ONNX graphs, SentencePiece tokenizer, preset voice states, ONNX Runtime Web + WASM runtime, and the PDF.js text extractor. Speech inference runs locally after installation and does not call a TTS server.

Model assets: `maxime/pocket-tts-18m-onnx`, pinned at revision `9b874ef8e0be4a95a352a0fe0455e064e75404e0`. Original Kyutai model weights are CC BY 4.0; see `MODEL_ATTRIBUTION.txt` in the app assets.

## Build

GitHub Actions builds a debug-signed installable APK on pushes to `main` and publishes it as the `pages-v1.0` release. The build artifact is also retained in Actions for 30 days.

## Notes

- Eight bundled English voices: Alba, Azelma, Cosette, Eponine, Fantine, Javert, Jean, and Marius.
- CPU inference is intentionally limited to one WASM thread to reduce contention on entry-level devices.
- PDF text extraction requires selectable text; scanned PDFs need OCR, which is not included.
- RSS refresh requires an internet connection; saved article text and images are stored locally.
