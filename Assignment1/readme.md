
Objectives

Refresh/practice some basic C programming
Familiarise yourself with the Unix system call mechanism
Learn about processes and signals
Get you thinking about parallel processes, as well as what the unix shell really does.
 

This assignment consist of two tasks: 

## Task 1  (30%)

Write a C program called even.c that takes the input parameter n and prints the first “n” even numbers. We want the program to run slowly, so that it executes long enough to receive a signal.  To achieve this, you should place a sleep(5) after every print statement.  Compile and run to test it works ok.

Most Unix systems will understand SIGHUP and SIGINT.  Every signal has a default action associated with it, for example the default action for SIGINT is to terminate the process. 

You should modify your even number program to handle the signals as follows:

When it receives a HUP signal, it should print "Ouch!" and continue.
When it receives a INT signal, it should print "Yeah!" and continue.
 

## Task 2  Improve a basic command line shell  (70%)

Read, study and completely understand the minishell.c code provided for this assignment. 

Modify that minishell to do the following:

put commands ended by an “&”  into background mode and report when they finish. 
properly interpret the shell cd command
include an appropriate perror statement after each and every system call.
fix the minishell so that if the exec system call fails, the child process is correctly terminated.
 

The minishell should follow the POSIX API, thus you may develop you code on a Linux machine. We will provide some simple test for testing 
