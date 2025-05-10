# Genius Music Platform 🎵

![Genius Logo](https://images.genius.com/e68b4981a04ce6f714f0d0d44973a02c.300x300x1.jpg)

A Java-based music platform inspired by Genius.com with lyric editing and artist management features.

## Table of Contents
- [✨ Features](#features)
- [🚀 Installation](#installation)
- [🎮 Usage](#usage)
- [📂 Project Structure](#project-structure)
- [🔧 Technologies](#technologies)
- [🌐 API Integration](#api-integration)
- [🤝 Contributing](#contributing)
- [📜 License](#license)

## ✨ Features

### 🎵 Music Discovery
- Browse popular songs and artists
- View top charts
- Search functionality

### 👥 User Accounts
- Three distinct roles: User, Artist, Admin
- Secure authentication with password hashing
- Artist verification system

### ✏️ Content Interaction
- Suggest lyric edits (requires artist approval)
- Comment on songs
- Follow favorite artists

### 🎤 Artist Features
- Create and manage songs/albums
- Approve/reject lyric edits
- View artist statistics

### 🛠️ Admin Tools
- Verify new artists
- Moderate content
- Manage user accounts

## 🚀 Installation

1. **Prerequisites**:
   - Java JDK 17+
   - Maven
   - Genius API token (set as `GENIUS_API_TOKEN` environment variable)

2. **Clone and build**:
   ```bash
   git clone https://github.com/yourusername/genius-music-platform.git
   cd genius-music-platform
   mvn clean install

3. **Run the application**:

       java -jar target/genius-music-platform.jar

### 🎮 Usage

Command Line Interface

=== Welcome to Genius Music Platform ===

    --- Main Menu (Guest) ---
    1. Login
    2. Register
    3. Browse Songs
    4. Browse Artists
    5. Top Charts
    6. Search
    0. Exit
    
Key Functionalities

**Role**	Capabilities
**User**	View lyrics, suggest edits, comment, follow artists
**Artist**	Manage songs/albums, approve edits, view stats
**Admin**	Verify artists, moderate content, manage accounts

### 📂 Project Structure
    
    src/
    ├── main/
    │   ├── java/
    │   │   ├── com/genius/
    │   │   │   ├── model/          # Data models
    │   │   │   ├── service/        # Business logic
    │   │   │   ├── util/           # Utilities
    │   │   │   ├── view/           # CLI interface
    │   │   │   └── App.java        # Main class
    │   ├── resources/              # Config files
    └── test/                       # Test code


### 🔧 Technologies

* Core: Java 17

* Build: Gradle

* Security: PBKDF2 password hashing

* API: Genius API

* JSON: Gson

* Concurrency: ExecutorService

### 🌐 API Integration

1. Get a Genius API token

2. Set environment variable:

        export GENIUS_API_TOKEN=your_token_here
### 🤝 Contributing

1. Fork the repository

2. Create your feature branch (git checkout -b feature/amazing-feature)

3. Commit your changes (git commit -m 'Add some amazing feature')

4. Push to the branch (git push origin feature/amazing-feature)

5. Open a Pull Request

### 📜 License

Distributed under the MIT License. See LICENSE for more information.
