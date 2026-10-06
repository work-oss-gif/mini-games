# Mini Games

A tiny Android app with three games: Snake, 2048 and Memory Match. Best scores are saved on the phone.

- Plain Java, no libraries. Game rules are in `SnakeGame.java` and `Game2048.java` (no Android code, so they are easy to test); drawing and touch are in the `*View` / `*Screen` classes.
- The APK is built by GitHub Actions on every push: open the run, scroll to **Artifacts**, download the zip and install `app-debug.apk`.
