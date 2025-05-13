🎬 MovieDB2025

MovieDB2025 is a Kotlin-based Android app that showcases movies from The Movie Database (TMDB) API. 
Users can browse popular, top-rated, and saved movies with detailed information.

---

📱 Features

- Fetch and display popular and top-rated movies
- View movie details including poster, release date, and description
- Save favorite movies locally using Room database
- Offline support with Room caching
- Watch trailers using the YouTube Player API
- Responsive UI using Jetpack Compose
- Dropdown menu to switch between categories
- Adaptive icon support with custom SVG graphics

---

🧰 Tech Stack

- Kotlin
- Jetpack Compose
- ViewModel + LiveData
- Room (local cache)
- Retrofit + OkHttp (network)
- TMDB API
- Android YouTube Player
- WorkManager for background syncing

---

🚀 Getting Started

1. Clone this repository
2. Add your TMDB API key in 'app/src/main/java/com/example/moviedb2025/utils':
   const val API_KEY = "your_key_here"
3. Sync the Gradle project and run the app on an emulator or Android device.

---

📦 Environment

Minimum SDK: API 21 (Android 5.0 Lollipop)
Target SDK: API 34 (Android 14)
Build Tools Version: 34.0.0
Gradle Plugin Version: 8.3.0
Kotlin Version: 1.9.x
Tested on: Android Studio Meerkat | 2024.3.1 Patch 2