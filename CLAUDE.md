# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Proof of Concept (PoC) for a Cardano Node to Elasticsearch Indexer. The application reads transactions from a local Cardano node via socket connection, deserializes them using cardano-client-lib, and indexes the results as JSON documents into Elasticsearch.

## Tech Stack

- **Language**: Java 24
- **Framework**: Quarkus
- **Build Tool**: Gradle
- **Indexing**: Elasticsearch (latest)
- **Visualization**: Kibana
- **Cardano Libraries**: 
  - cardano-client-lib (bloxbean)
  - yaci-core (bloxbean)
- **Environment**: Docker + Docker Compose
- **CI/CD**: GitHub Actions (Docker-based builds)

## Architecture

The application implements an indexing pipeline that:

1. **Connection Layer**: Uses Yaci (node-to-socket / Chain Sync protocol) to connect to a Cardano node at `lubawa.lan:3001`
2. **Deserialization Layer**: Uses cardano-client-lib to deserialize transactions from CBOR format
3. **Transformation Layer**: Converts deserialized transactions to pretty-printed JSON
4. **Indexing Layer**: Pushes JSON documents into Elasticsearch for searching and analysis
5. **Visualization Layer**: Kibana interface for viewing and searching indexed transactions

## Development Environment

The development stack should include:
- Elasticsearch (latest)
- Kibana (connected to ES)
- Application container (connected to lubawa.lan:3001)

## Key Dependencies and Libraries

### Cardano Integration
- **Yaci**: Focus on node-to-socket protocols, especially Chain Sync protocol implementation
- **Cardano Client Lib**: Used for CBOR transaction deserialization and JSON conversion

### Reference Projects
When implementing features, consider patterns from:
- Bloxbean Yaci repository (node-to-socket protocols)
- Cardano Client Lib repository (CBOR handling)
- CRFA Block Monitor (Quarkus integration and Docker Compose examples)

## Project Status

This is currently a greenfield project with only requirements documentation. The codebase will need to be built from scratch following the specifications in `requirements/intro.md`.