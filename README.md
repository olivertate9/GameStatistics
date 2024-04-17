# Statistics Program

This program is designed to collect statistics from JSON files within a specified folder and generate XML reports based on attributes such as "developer", "yearReleased", and "genre".

### Main entity: Game

Attributes:

1. Title
2. Developer: references another entity Developer
3. Year Released
4. Genre: Strings separated by comma

### Secondary entity: Developer

Attributes:

1. Name

## Prerequisites

Before running this program, ensure you have the following installed on your system:

* Java Development Kit (JDK) version 17 or higher
* Apache Maven (for building the project)

## Installation

1. Clone the repository to your local machine:

    `https://github.com/olivertate9/GameStatistics.git`
2. Navigate to the project directory and open terminal here

3. Build the project using Maven:

   `mvn clean install`

## Usage

To use this program, you have two options:

1. Open Run/Debug configuration in Intellij IDEA and pass program arguments here.
  `src\main\resources\json genre`
  
![CLIarguments](https://github.com/olivertate9/GameStatistics/assets/104451170/eef46d74-4808-4017-8148-a5752899ee58)


2. Navigate to the project directory and open terminal here then run:

   `java -jar target/GameStatistics-1.0.jar src\main\resources\json genre`

**_Note_** XML file would be saved to `src/main/resources` package.

**_Note_** If you want to use your own folder with JSON files somewhere on your computer provide the absolute path as first argument. Example: `D:\json`

**_Note_** Possible attributes is : `developer, genre, yearReleased`.

**_Note_** Adjust the `/` or `\` depending on your operating system.

## Error Handling

If you provide incorrect or insufficient command-line arguments, the program will throw an IllegalArgumentException with an error message indicating the issue.

If the specified folder does not exist or is not a valid directory, the program will throw an InvalidFolderException.

If the specified attribute is not one of the supported fields (developer, yearReleased, genre), the program will throw an InvalidAttributeException.

## Examples:
Input file example are located in:

`src/main/resources/json`

Output file example are located in(run the program at least once):
`src/main/resources`


## ThreadPool comparison(average of 3 attempts each):
Test data: 100 JSON files, each one contains 100000 objects.

Number of threads - 1: 123471 milliseconds

Number of threads - 2: 70124 milliseconds

Number of threads - 4: 56750 milliseconds

Number of threads - 8: 53581 milliseconds
