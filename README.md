To run: ./gradlew run
to build: ./gradlew build
to image: ./gradlew jlink
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- Identify Character with ULID instead of name
- Compute shields
- Aggregate invocations to the spell caster
- Refacto sealed interface on Damages, Heals, Shields then use it with pattern matching
- Couleur des graphs par perso et pas par position

Known issues:
- Reset stats during fight could create an Unknown character (caused by the fetchCharacter function)
- Compute des heals HoT
- Check computing Feca glyph --> Aucune indication autre que le cast
- Bug Sadi poison & Xelor