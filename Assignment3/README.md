# OSPrac3

# Run the server using makefile
1. Compile the server:

```
make
```

2. Run the server:

```
java Assignment3 -l 12345 -p "happy"
```
    
replace `12345` with the port number, replace `"the"` with the pattern you want to search

# Run the client
Put the `.txt` file in the the `resources` folder of this repository, then in a new terminal, enter:

```
nc localhost 12345 -i 1 < resources/test.txt
```
    
replace `test.txt` with the file you want to send to the server

# How to interpret the search result
One of the analysis threads will print out the list of book every 2 seconds (can be manually adjusted inside the code). It print the book titles, ranked by the highest occurence of the pattern to the least.
    
The server will search in all data node by node. Therefore, if there are any case of a certain String being split between 2 nodes of the same book, search result will not be able to detect it. For example,
- search pattern is "happy" 
- node 1 ends with "hap"
- node 2 starts with "py".
- -> The search result will not count this as 1 occurence of the pattern "happy".

    
# Autograder output:
1. Load/Compile (Java)
    Java implementation found and compiled successfully.

2. PartA_Network (20/20)
    Test passed; well done!

3. PartA_Scale (15/15)
    Test passed!

4. PartA_Log (15/15)
    Test passed!

5. PartA_Files (30/30)
    Test passed!

Part B will be marked manually.