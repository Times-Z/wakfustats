To run: ./gradlew run
to build: ./gradlew build
to image: ./gradlew jlink
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- Aggregate invocations to the spell caster
- Compute shields
- Avoid friendly fire
- Add a test for isControlledByAi mobs
- Refacto sealed interface on Damages, Heals, Shields then use it with pattern matching
- Refacto Map KEYS for Damages, Heals, Shields
- Couleur des graphs par perso et pas par position

Known issues:
- Bug Sadi poison & Xelor
- Compute des heals HoT
- Check computing Feca glyph --> Aucune indication autre que le cast
