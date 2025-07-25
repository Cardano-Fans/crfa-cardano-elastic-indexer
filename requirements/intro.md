🧪 Proof of Concept (PoC): Cardano Node to Elasticsearch Indexer

✅ Objective
Develop a PoC application that reads transactions from a local Cardano node (via socket), deserializes them using cardano-client-lib, and indexes the result as JSON documents into a local Elasticsearch instance.

⚙️ Tech Stack
Language: Java 24

Framework: Quarkus

Build Tool: Gradle

Indexing: Elasticsearch (latest)

Cardano Libraries:

cardano-client-lib (bloxbean)
yaci-core (bloxbean)

PoC Node Source: Socket connection to local node at lubawa.lan:3001

Environment: Docker + Docker Compose

CI/CD: GitHub Actions (Docker-based builds)

🧩 Requirements
Project Setup

Quarkus-based Java 24 application.

Native Docker image build enabled from day one.

Docker Compose setup for:

Latest Elasticsearch

Latest Kibana

Our application, wired into the above.

Indexing Pipeline

Use Yaci (especially node-to-socket / Chain Sync protocol) to connect to the node.

Use cardano-client-lib to:

Deserialize transactions from CBOR.

Convert deserialized transactions to pretty-printed JSON.

Push this JSON into Elasticsearch.

Development Environment

Docker Compose stack includes:

Elasticsearch

Kibana (connected to ES)

Local application (connected to lubawa.lan:3001)

CI/CD

GitHub Actions should:

Build Docker image.

Run tests (if any).

Validate that the image can start and connect to the ES cluster.

🔍 Learning and Integration Targets
Please explore and understand the following repositories before implementation:

🔗 Bloxbean Yaci
Focus: Node-to-socket protocols (e.g. Chain Sync)

🔗 Cardano Client Lib
Focus: Deserializing CBOR transactions, converting to JSON

🔗 CRFA Block Monitor
Focus: Quarkus integration, Docker Compose setup examples

🏁 Goal of PoC
A minimal but working Quarkus application that:

Connects to a local Cardano node (lubawa.lan:3001)

Deserializes and converts transactions to JSON

Indexes them to a local Elasticsearch instance

Can be viewed and searched via Kibana

