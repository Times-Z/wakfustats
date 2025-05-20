To run: ./gradlew run
to build: ./gradlew build
to image: ./gradlew jlink
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- Aggregate invocations to the spell caster
- Avoid friendly fire
  - compute -> DONE
  - add button to toggle
- Add toggle on character name to show details
- Add a test for isControlledByAi mobs
- Add tests for friendly fire
- Refacto sealed interface on Damages, Heals, Shields then use it with pattern matching
- Refacto Map KEYS for Damages, Heals, Shields
- Refacto duplicated code
- Couleur des graphs par perso et pas par position

Known issues:
- Bug Sadi poison & Xelor
- Compute des heals HoT
- Check computing Feca glyph --> Aucune indication autre que le cast
