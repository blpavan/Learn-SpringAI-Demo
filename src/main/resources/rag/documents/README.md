Place mixed knowledge-base documents in this folder to index them with
TikaDocumentDataLoader at application startup.

Supported examples:
- product-manual.pdf
- refund-policy.docx
- support-faq.txt
- release-notes.html

TikaDocumentReader extracts text from these file types, TokenTextSplitter chunks
the extracted text, and VectorStore stores the chunks in Qdrant.

Keep PDF-only experiments in ../pdfs if you want to compare PagePdfDocumentReader
against TikaDocumentReader. Put PDFs here when you want the generic Tika path.
