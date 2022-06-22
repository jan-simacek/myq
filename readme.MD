# Opening project in IntelliJ
Clone this repository
```
git clone https://github.com/jan-simacek/myq.git
```
and open `build.gradle.kts` in IntelliJ, when prompted select "As a project"

# Running in IntelliJ
Open `src/main/kotlin/myq/cleaning/main.kt` and run the `main()` function in 
this file. It'll guide you about what cmdline parameters it needs.

# Build distribution zip
Run the gradle distZip target using gradlew wrapper. In the project root run
```
$ ./gradlew distZip
```
Find the resulting zip file in 
```
build/distributions/myqtest-1.0-SNAPSHOT.zip
```
unpack it into a folder and within it run
```
bin/myqtest
```
It'll guide what parameters to supply.

# Running tests
In the project root run
```
$ ./gradlew test
```