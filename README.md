To run: ./gradlew run

TODO:
- Opacity control
- Handle reset screen in better way
- Reboot deduplication but in domain
- Identify Character with ULID instead of name
- Add Unit tests
- Compute heals
- Compute shields

Known issues:
- Reset stats during fight could create an Unknown character (caused by the fetchCharacter function)