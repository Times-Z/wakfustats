To run: ./gradlew run
to build: ./gradlew build
to image: ./gradlew jlink
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- Opacity control
- Identify Character with ULID instead of name
- Compute shields
- Option to aggregate invocations to the spell caster
- Check computing Feca glyph
- Bug Sadi poison & Xelor

Known issues:
- Reset stats during fight could create an Unknown character (caused by the fetchCharacter function)
- Compute des heals HoT
- Couleur des graphs n'augmentent pas dans l'écran des heals
- Couleur des graphs n'augmentent pas dans l'écran des shields