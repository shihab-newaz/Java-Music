---

# Java Music 🎵

Java Music is a JavaFX-based application that allows users to manage and play music, discover trending songs, and download tracks using the Shazam API.

## Features
- **Music Playback**: Play, pause, and stop tracks seamlessly.
- **Playlist Management**: Create, edit, and organize playlists.
- **Shazam API Integration**: Search for songs and download them directly.
- **Trending Songs Viewer**: Stay updated with the latest trending music.
- **User-Friendly JavaFX Interface**: Intuitive and modern GUI for enhanced user experience.

## Getting Started

### Prerequisites
To run the project, ensure you have the following installed:
- [Java Development Kit (JDK) 8+](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
- [JavaFX SDK](https://openjfx.io/)
- [Maven](https://maven.apache.org/) (optional)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/shihab-newaz/Java-Music.git
   cd Java-Music
   ```
2. Set up JavaFX:
   - Download and install the JavaFX SDK.
   - Add the JavaFX SDK `lib` folder to your project classpath.

3. Compile and run the project:
   ```bash
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/**/*.java
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin com.example.Main
   ```

### API Configuration
To use the Shazam API:
1. Obtain an API key from [Shazam's API](https://rapidapi.com/apidojo/api/shazam).
2. Configure the API key in your application code:
   ```java
   String apiKey = "YOUR_API_KEY";
   ```

## Usage
1. Launch the application.
2. Browse and play your local music files.
3. Use the trending viewer to discover popular tracks.
4. Download songs using the integrated Shazam API.

## Development

### Project Structure
- `src/`: Contains the source code.
- `bin/`: Compiled bytecode.
- `docs/`: Generated documentation.

### Contributing
1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/YourFeature
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/YourFeature
   ```
5. Open a pull request.

## Documentation
Generate documentation using Javadoc:
```bash
javadoc --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d docs -sourcepath src -subpackages com.example
```
View the documentation in the `docs` folder.

## License
This project is licensed under the [MIT License](LICENSE).
