# Labyrinth

> Project created for the "Programmazione Avanzata Java e C" *(Advanced Java and C Programming)* exam of the Degree in Computer Science and Engineering at the University of Brescia (UniBS), in collaboration with [Tafa Hamit](https://github.com/tafah).

This is a Java-based implementation of the classic Labyrinth board game. Players navigate a maze, collect treasures, and try to be the first to return to their starting position. This version includes features for both local and online multiplayer gameplay.

![Game Example](/resource/readme/game.gif)

## Features

*   **Local Multiplayer:** Play with friends on the same computer.
*   **Online Multiplayer:** Play against other players over the network.
*   **Bot Opponents:** Play against AI-controlled opponents.
*   **Power-ups:** Use special abilities to gain an advantage.
*   **Graphical User Interface:** A user-friendly GUI for an immersive experience.

## Technologies

*   **Java:** The core programming language for the game.
*   **Swing:** Used for creating the graphical user interface.
*   **Maven:** For project management and building.

## Prerequisites

*   **Java Development Kit (JDK) 21 or higher**
*   **Apache Maven**

## Environment Configuration

The project uses a `.env` file to configure server settings. You can find or create a `.env` file in the project root with the following variables:

```
SERVER_IP=localhost
SERVER_PORT=2235
```

- `SERVER_IP`: The IP address where the server will run or connect.
- `SERVER_PORT`: The port number used for server communication.

Adjust these values as needed for your environment.

## How to Run

### Running the Client

To run the game client, use the following command:

```bash
mvn exec:java@client
```

### Running the Server

To run the game server for online play, use the following command:

```bash
mvn exec:java@server
```

## Project Structure

The project is organized into the following main packages:

*   `it.unibs.pajc.labyrinth.client`: Contains the client-side code, including the GUI, controllers, and animations.
*   `it.unibs.pajc.labyrinth.core`: Contains the core game logic, including the game board, cards, players, and game rules.
*   `it.unibs.pajc.labyrinth.server`: Contains the server-side code for handling online multiplayer games.
