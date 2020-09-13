# improved-commit-views

What is it?

The tool runs locally on your machine. Its objective is to create a commit patch HTML page with a different file ordering (other than the traditional alphabetical) and some extra elements, not provided in the traditional GitHub interface.
Works only for public Java repositories hosted on GitHub.


Before running:

- Make sure to edit display/src/main/resources/application.yml with one of your GitHub Auth Tokens so the application runs without authentication problems.
- The tool was tested on Windows and Ubuntu without problems, should support MAC OS but not tested.
- At least Java 12 required to run.
- Tool works only with GitHub public repositories and works over their pull requests.


How to run:

1) on api folder: mvn clean install
2) on core folder: mvn clean install
3) on display folder: mvn clean install
4) on display folder: mvn spring-boot:run
5) browse to (may need to clear browser cache for interface elements to work correctly): http://localhost:8080/api
