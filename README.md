To run: ./gradlew run  
to build: ./gradlew build  
to image: ./gradlew jlink  
to package: ./gradlew jpackage (if needed--info or --debug)  

TODO:
- Enrich E2E test
- Refactoring: implement IDs for characters, etc... To handle it on repository
  - for now, we will melt summons and characters --> beurk
- Aggregate invocations to the spell caster
- Add toggle on character name to show details
- Add a test for isControlledByAi mobs
- Add tests for friendly fire
- Couleur des graphs par perso et pas par position
- Refacto duplicated code
- Toggle friendly fire

Known issues:
- Bug Sadi poison & Xelor
- Compute des heals HoT
- Check computing Feca glyph --> Aucune indication autre que le cast
