README for JJ Project (jjProj) ATM Machine

----------------------
Synopsis
----------------------
The JJ Project is an ATM Machine transaction processing application. The aplication is a standalone Java jar executable.
The application will take one or more ATM transaction processing data files and produce the resultant output.
The application works by reading in data input file(s) and writing output files with the results of the processing.

----------------------
Installation
----------------------
Once the application is built the executable jar can be placed in a location when the user wants the application to run.
The application will always create the data processing directories from the directory where the application is run from.

The application processing directrories include the following:
-input (where ata input files should be placed for processing)
-output (where output data files will be placed after processing)
-processed (Where the imput files will be moved once processing is complete)

Files placed in the output and processed directories will be named with a datestamp with the current date.

-----------------------------------------------
Supported Deployment/Installation Environment
------------------------------------------------
Microsoft Windows 7 operating system
Java 1.8.0_121

---------------------------------
Build Environment
---------------------------------
Microsoft Windows 7 operating system
Java 1.8.0_121
Maven 3.2.5

---------------------------------
Building the application
---------------------------------
To build the application the user will need access to a Maven repository that has holds  the 3rd party 
dependencies specified in this README.

To build the application go the the directory where the application's pom.xml is located "jjProj-master\atmMachine\" then run 
the maven build command below. The application's executable jar will be produced in the target directory. 

mvn clean install

-------------------------------------
Running ATM Machine from command line
-------------------------------------
To run the application you can use the Java virtual machine to run the executable jar as below. The executable jar can be placed 
into any directory location where is can create it's own directories and files.

java -jar atmMachine-1.0-SNAPSHOT.jar

The application will always create the data processing directories below at the same level as the executable jar.
-input 
-output 
-processed 

By default all files with a ".data" extension in the input directory will be considered data input files for processing.

When the application is run there will be some usefull outputs to the user console to allow the 
user to see what that application is doing. 
The application will terminate once the processing is complete. The output files in the output directory can be 
taken for inspection.

----------------------
Command Line Arguments
----------------------
The application has 2 optional commnad line arguments.

-file <file name>
(optional) Specify Path to single file to process. If this option if specified then the this will be the only file processed
by the application, any data files in the input directory will not be processed. The file specifies here does not need to be 
in the input directory.

-i <true|false>
(optional) Move Input file after processing to the proessed directory. Default is true. By default files will be moved
to the processed directory, setting this argument to false leaves the input file in place.

Examples:
java -jar atmMachine-1.0-SNAPSHOT.jar
java -jar atmMachine-1.0-SNAPSHOT.jar -i false
java -jar atmMachine-1.0-SNAPSHOT.jar -file C:\jj_proj\jjProj\atmMachine\target\input\ATM_data_test_OK_1.data
java -jar atmMachine-1.0-SNAPSHOT.jar -1 false -file C:\jj_proj\jjProj\atmMachine\target\input\ATM_data_test_OK_1.data

----------------------------------
3rd Party Jars reqired for Build 
----------------------------------
aopalliance-1.0.jar
commons-cli-1.3.jar
commons-logging-1.2.jar
hamcrest-core-1.3.jar
javassist-3.18.0-GA.jar
junit-4.11.jar
mockito-all-1.9.5.jar
objenesis-1.2.jar
powermock-api-mockito-1.5.2.jar
powermock-api-support-1.5.2.jar
powermock-core-1.5.2.jar
powermock-module-junit4-1.5.2.jar
powermock-module-junit4-common-1.5.2.jar
powermock-reflect-1.5.2.jar
spring-aop-4.2.3.RELEASE.jar
spring-beans-4.2.3.RELEASE.jar
spring-context-4.2.3.RELEASE.jar
spring-core-4.2.3.RELEASE.jar
spring-expression-4.2.3.RELEASE.jar
spring-test-4.2.3.RELEASE.jar

-------------------------------------------
Tests / Business requirement Validation
-------------------------------------------
The application uses Junit test cases to validate the application.

Please see the test input data files @ /src/test/resources/*.data in the code base for the Junit test case 
inputs that validate the applacation's operation. Junit test cases validate that each test input file produces the required output. 

The Junit test case "testprocessInputFile_ATM_data_test_OK_1()" in "AtmEngineServiceImplTest.java" validates the application 
against the "ATM_data_test_OK_1.data" file which is the input test data provided in the requirements specification.

The application currently has a line coverage of 78% for it's Junit test cases.

-------------------------
Performance & Profiling 
-------------------------
Not performed.

----------------------------------
License
----------------------------------
This is freeware, no license required.
