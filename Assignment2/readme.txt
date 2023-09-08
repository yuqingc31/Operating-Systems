There is no need to modify Memsim.java or MMU.java. To operate with these files, you will need to adhered to the interface of MMU.java.
In addition, the 3 replacement strategies should be implemented by EscMMU.java, LruMMU.java, RandMMU.java.

terminal - testing:
    - lru - done :
        javac Memsim.java
        java Memsim trace1 4 lru debug - pass
        java Memsim trace1 8 lru debug - pass 
        java Memsim trace2 6 lru debug - pass
        java Memsim trace3 4 lru debug - pass
        java Memsim trace3 12 lru debug - pass
    - Esc:
        java Memsim trace1 4 esc debug
    - rand
    - FIFO - progress:
        javac Memsim.java
        java Memsim trace1 4 fifo debug - pass
