Place PDF files in this folder to index them into Qdrant at application startup.

PdfDataLoader reads files matching classpath:/rag/pdfs/*.pdf, splits the extracted
text into chunks, and stores the chunks in the configured VectorStore.
