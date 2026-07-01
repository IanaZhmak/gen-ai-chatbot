# Modular Spring AI Chatbot

A modular AI-powered chatbot built with **Java, Spring Boot, and Spring AI**.

The project demonstrates several AI backend workflows: **Simple Chat**, **Memory Chat**, **RAG Chat**, **Prompt-Based Chat**, and **Tools Chat**.

The goal of this project is to show how AI features can be integrated into a clean Spring Boot backend using **Spring AI**, **RAG**, **embeddings**, **PGVector**, **Ollama/OpenAI**, **chat memory**, **tool calling**, and REST APIs.

---

## Key Features

- **Simple Chat** — basic conversation with an LLM.
- **Memory Chat** — stores conversation history so the model can use previous messages as context.
- **RAG Chat** — allows users to upload documents and ask questions based on the document content.
- **Prompt-Based Chat** — uses structured prompts and system instructions to control model behavior.
- **Tools Chat** — connects the AI to an external weather API, so the model can call a real backend tool and return current weather data.
- **Claude-assisted UI** — frontend generated and customized with Claude, integrated with Spring Boot REST APIs.

---

## Tech Stack

**Backend:** Java, Spring Boot, Spring AI, Spring MVC, Spring Data JPA, Hibernate, REST APIs

**AI:** Spring AI, RAG, Embeddings, PGVector, Chat Memory, Prompt Engineering, Tool Calling, Ollama, OpenAI

**Database:** PostgreSQL, PGVector

**External API:** OpenWeatherMap API

**Tools:** Docker, Maven/Gradle, Postman, Git/GitHub

---

## Example Use Cases
**RAG Chat**

A user uploads a document, such as a PDF, and asks a question about it.

The backend extracts the text, creates embeddings, stores them in PGVector, searches for relevant context, and sends that context to the model.

**Tools Chat**

A user asks:
What is the weather in Miami?

The AI uses a backend weather tool. The backend calls the OpenWeatherMap API, receives current weather data, and the model uses that data to generate the final answer.

---

## What I Learned
- How to integrate LLMs into a Spring Boot backend using Spring AI
- How to build multiple AI chat modes in one application
- How to build RAG workflows with document upload, embeddings, PGVector, and similarity search
- How to support chat memory and multi-turn conversations
- How to use tool calling to connect AI with an external weather API
- How to organize AI backend logic with controllers, services, DTOs, configuration classes, entities, and repositories
- How to expose AI-powered features through REST APIs


