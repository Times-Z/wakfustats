To run: ./gradlew run
to build: ./gradlew build
to image: ./gradlew jlink
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- compute specific damages
  - Xelor distorsion
- Opacity control
- Handle reset screen in better way
- Identify Character with ULID instead of name
- Add Unit tests
- Compute heals
- Compute shields
- Option to aggregate invocations to the spell caster

Known issues:
- Reset stats during fight could create an Unknown character (caused by the fetchCharacter function)