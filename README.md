# panda-1

Script to convert txt files to a spreadsheet for cellphone(assuming) mast data

# Requirements

1. Java 7
2. Groovy 2.2.1
3. Gradle 1.11

# Usage

```Shell
    groovy file-convert.groovy -i $INPUT_DIR -o $OUTPUT_FILE -c $COMPLETED_DIR
```

# Distribution

To generate a distribution use:

```Shell
    gradle dZ
```

There should be a zip file: *build/distributions/panda1-dist.zip*

Unzip to the desired directory. From there go to the command line and navigate to the directory bin.

Use:

```Shell
    convert.sh -i /opt/data/input -o /opt/data/2014-02-25.csv -g /opt/data/completed
```
