# GenerateExcel (Java + Apache POI)

Build and run the `GenerateExcel` utility which fills `DOC. REF. NO.`, `VERSION NO.`, and `DATE` into the provided template.

Prerequisites
- Java 11+
- Maven

Build

```bash
cd javaway
mvn package
```

This produces a shaded jar in `javaway/target/` named like `generate-excel-1.0.0.jar`.

Run

```bash
java -jar target/generate-excel-1.0.0.jar "IPACK-PAM-2026-001" "v1.0" "29-Jan-2026" "template/Remote_Access_and_PAM_Form(1).xlsx"
```

If you omit the template path the program will try `template/Remote_Access_and_PAM_Form(1).xlsx` by default.
## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
