To run: ./gradlew run  
to build: ./gradlew build  
to image: ./gradlew jlink  
to package: ./gradlew jpackage (if needed--info or --debug)

TODO:
- Analyze all possible interactions in Dungeons ("cocons en sabléo" that auto take damages, ...)
- Toggle that shows the statistics details of the character (by spells)
- Toggle friendly fire

  LOVE:
- Refactor the multi accounting check
- Refactoring duplicated code
- Refactoring TheAnalyzer
- Add more tests

Known issues:
- Sadida's poisons (and DoTs / HoTs in general) could be badly compute sometimes (combining with Xelor big turns)
- For now, impossible to compute Feca's glyphs damages / heals / Shields.
- A lot of spells effects are not taken into account (like Steamer's "Modérateur d'énergie")
