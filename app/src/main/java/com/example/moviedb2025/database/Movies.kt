package com.example.moviedb2025.database

import com.example.moviedb2025.models.Movie

class Movies {
    fun getMovies(): List<Movie> {
        return listOf(
            Movie(
                1,
                "A Minecraft Movie",
                "/yFHHfHcUgGAxziP1C3lLt0q2T4s.jpg",
                "/2Nti3gYAX513wvhp8IiLL6ZDyOm.jpg",
                "2025-03-31",
                "Four misfits find themselves struggling with ordinary problems when they are suddenly pulled through a mysterious portal into the Overworld: a bizarre, cubic wonderland that thrives on imagination. To get back home, they'll have to master this world while embarking on a magical quest with an unexpected, expert crafter, Steve.",
                listOf("Family", "Comedy", "Adventure", "Fantasy"),
                "https://www.minecraft-movie.com",
                "tt3566834"
            ),
            Movie(
                2,
                "Captain America: Brave New World",
                "/pzIddUEMWhWzfvLI3TwxUG2wGoi.jpg",
                "/ce3prrjh9ZehEl5JinNqr4jIeaB.jpg",
                "2025-02-12",
                "When a group of radical activists take over an energy company's annual gala, seizing 300 hostages, an ex-soldier turned window cleaner suspended 50 storeys up on the outside of the building must save those trapped inside, including her younger brother.",
                listOf("Action", "Thriller", "Science Fiction"),
                "https://www.marvel.com/movies/captain-america-brave-new-world",
                "tt14513804"
            ),
            Movie(
                3,
                "Moana 2",
                "/aLVkiINlIeCkcZIzb7XHzPYgO6L.jpg",
                "/zo8CIjJ2nfNOevqNajwMRO6Hwka.jpg",
                "204-11-21",
                "After receiving an unexpected call from her wayfinding ancestors, Moana journeys alongside Maui and a new crew to the far seas of Oceania and into dangerous, long-lost waters for an adventure unlike anything she's ever faced.",
                listOf("Animation", "Adventure", "Family", "Comedy"),
                "https://movies.disney.com/moana-2",
                "tt13622970"
            ),
            Movie(
                4,
                "Mufasa: The Lion King",
                "/lurEK87kukWNaHd0zYnsi3yzJrs.jpg",
                "/1w8kutrRucTd3wlYyu5QlUDMiG1.jpg",
                "204-12-18",
                "Mufasa, a cub lost and alone, meets a sympathetic lion named Taka, the heir to a royal bloodline. The chance meeting sets in motion an expansive journey of a group of misfits searching for their destiny.",
                listOf("Adventure", "Family", "Animation"),
                "https://movies.disney.com/mufasa-the-lion-king",
                "tt13186482"
            ),
            Movie(
                5,
                "The Hard Hit",
                "/whkFbOZTamHeugEG95jvQehSzAH.jpg",
                "/fzv87rT0jlAkh5Uf9PpIlUj6Nj8.jpg",
                "203-10-20",
                "An Interpol agent hunting the head of a global crime syndicate tracks his target to Las Vegas, but when the criminal organization kills his wife and daughter, he goes above the law to get his revenge.",
                listOf("Action", "Thriller", "Crime"),
                "",
                "tt10676048"
            )

        )
    }
}