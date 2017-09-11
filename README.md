README for JJ Project (jjProj) ATM Machine

----------------------
Synopsis
----------------------

----------------------
Installation
----------------------


----------------------------------
API Reference
----------------------------------


----------------------------------
Tests
----------------------------------

----------------------------------
Contributors
----------------------------------

----------------------------------
License
----------------------------------
This is freeware, no license required.


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

---------------------------------
Supported Build Environment
---------------------------------
Microsoft Windows 7 operating system
Java 1.8.0_121
Maven 3.2.5

---------------------------------
Supported Deployment/Installation Environment
---------------------------------
Microsoft Windows 7 operating system
Java 1.8.0_121

-------------------------------------
Running ATM Machine from command line
-------------------------------------
java -jar

----------------------
Command Line Arguments
----------------------

Example:  ./word-count -b -sort SelectionSort -suf < textfile

-b | -a | -s
  (required) Specifies the type of tree for storing (word, count) pair
  possible trees are Binary search tree, Avl tree, and Splay tree

    -b - Count frequencies using an unbalanced binary search tree 
    -a - Count frequencies using an AVL tree 
    -s - Count frequencies using a splay tree 

-sort SelectionSort | MergeSort | HeapSort
  (optional) Specifies the type of sort.  
  If -sort is omitted, HeapSort is used

-suf
  (optional) Turns on suffix checker

-------------------------
Design Decisions & Issues
-------------------------

-------------------------
Performance & Profiling 
-------------------------
Not performed

