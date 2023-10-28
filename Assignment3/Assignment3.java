import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Assignment3 {
    public static void main(String[] args) {
        String ipAddress = "localhost";

        // get port from terminal
        int port = 12345;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-l") && i + 1 < args.length) {
                port = Integer.parseInt(args[i + 1]);
                break;
            }
        }

        // get pattern from terminal input
        String pattern = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p") && i + 1 < args.length) {
                pattern = args[i + 1];
                break;
            }
        }

        int book_id = 1;
        // establish a shared list for all clients
        SharedData sharedData = new SharedData();

        // create the analysis threads and run them
        final Semaphore semaphore = new Semaphore(1);
        int interval = 2000; // adjust this as required, 2000 corresponds to 2 seconds
        Thread thread1 = new Thread(new AnalysisThread(sharedData, pattern, semaphore, "Thread 1", interval));
        Thread thread2 = new Thread(new AnalysisThread(sharedData, pattern, semaphore, "Thread 2", interval));
        thread1.start();
        thread2.start();

        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ipAddress));
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Accept a connection from the client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                // Create a new thread for each client
                Thread clientThread = new Thread(new ClientHandler(clientSocket, sharedData, book_id));
                // call start() to run this thread, run() method will execute
                clientThread.start();

                // After the thread is closed, increase the book_id
                book_id++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// to store information sent from client to server, each node corresponds to 1 data packet
class Node {
    public Node next;
    public String data;
    public int node_book_id; // to know which book this Node belongs to 
    public boolean searched = false; // to know if this book has undergone searching by the analysis threads

    public Node() {
    }

    public Node(String data, int node_book_id) {
        this.data = data;
        this.node_book_id = node_book_id;
    }
}

// to store information of the book, which will be part of the final result
class BookInfo {
    public int book_id;
    public String bookName;
    public int count; // number of times the pattern appears

    public BookInfo(int book_id, int count, String bookName) {
        this.book_id = book_id;
        this.bookName = bookName;
        this.count = count;
    }

    public void addCount(int moreCount) {
        this.count += moreCount;
    }
}

// class SharedData defines all shared data contained in the server
class SharedData {
    public LinkedList<Node> sharedList = new LinkedList<Node>(); // shared linked list of nodes
    public ArrayList<BookInfo> result = new ArrayList<BookInfo>(); // shared final result after searching and counting

    // this method will be called by the Client Handler threads, synchronisation is required
    public void addNode(Node node) {
        synchronized (sharedList) {
            sharedList.add(node);
        }
    }

    // helper method to count number of string occurence
    private int countPattern(String substring, String mainString) {
        int count = 0;
        int fromIndex = 0;

        while ((fromIndex = mainString.indexOf(substring, fromIndex)) != -1) {
            // Found an occurrence, update the starting index for the next search
            fromIndex = fromIndex + substring.length();
            count++;
        }
        return count;
    }

    // helper method to add to result
    private void addToResult(int book_id, String data, int count) {
        if (this.result.isEmpty() | searchBookInResult(book_id) < 0) {// new book
            String title = data.split("\n")[0]; // get the book title
            BookInfo newBook = new BookInfo(book_id, count, title);
            result.add(newBook);
        
        } else { // book already exists inside result
            result.get(searchBookInResult(book_id)).addCount(count);
        }
    }

    // helper method to search for a book in the result
    private int searchBookInResult (int book_id){
        int order = -999;
        for (int i = 0; i < result.size(); i++){
            BookInfo book = result.get(i);
            if (book.book_id == book_id){
                order = i;
                break;
            }
        }
        return order;
    }

    // helper method to sort the result
    private void sortResult() {
        Collections.sort(result, new Comparator<BookInfo>() {
            @Override
            public int compare(BookInfo book1, BookInfo book2) {
                // Compare in descending order based on count
                return Integer.compare(book2.count, book1.count);
            }
        });
    }

    // helper method to print out the result
    private void printResult(String pattern) {
        // System.out.println("Search result for: " + pattern);
        for (int i = 0; i < result.size(); i++) {
            BookInfo book = result.get(i);
            System.out.println("Book: " + book.bookName);
            System.out.println("      \"" + pattern + "\" appears " + book.count + " times");
        }
    }

    // this method will be called by the analysis threads, synchronisation is required
    public void searchInNode(String pattern) {
        synchronized (sharedList) {
            if (!sharedList.isEmpty()) {
                for (int i = 0; i < sharedList.size(); i++) {
                    Node node = sharedList.get(i);
                    if (node.searched == false) {
                        int count = countPattern(pattern, node.data);
                        addToResult(node.node_book_id, node.data, count);
                        node.searched = true;
                    }
                }
                sortResult();
                printResult(pattern);
            } else {
                System.out.println("No data to analyse");
            }
        }
    }
}

// class ClientHandler is to handle client connection and receive client data
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private SharedData sharedData;
    private int book_id;

    public ClientHandler(Socket clientSocket, SharedData sharedData, int book_id) {
        this.clientSocket = clientSocket;
        this.sharedData = sharedData;
        this.book_id = book_id;
    }

    private void printBook(int book_id) { // call this method to print out books
        // traverse through the thread list
        String bookFileName = "book_" + String.format("%02d", book_id) + ".txt";

        // print the book into a txt file
        try (PrintWriter writer = new PrintWriter(bookFileName, "UTF-8")) {
            for (int i = 0; i < sharedData.sharedList.size(); i++) {
                Node tmp = sharedData.sharedList.get(i);
                if (tmp.node_book_id == book_id) {
                    writer.println(tmp.data);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() { // running the thread
        try {
            InputStream inputStream = clientSocket.getInputStream();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Handle the client's request (customize this part)
            out.println("Hello, client!");

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // System.out.println("thread for book " + book_id + " is running");

                // Process the received message from the client
                String receivedData = new String(buffer, 0, bytesRead);

                // Create a new Node and encapsulate the clientMessage
                Node newNode = new Node(receivedData, book_id);
                // Add the new Node to the shared list
                // updateSharedList(newNode);
                sharedData.addNode(newNode);
                // Continue to listen for the next data packet
                buffer = new byte[1024];
            }
            System.out.println("============printing book===========");
            this.printBook(book_id);

            // Close the client socket
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// class AnalysisThread is to create analysis threads
class AnalysisThread implements Runnable {
    private SharedData sharedData;
    private String pattern;
    private final Semaphore semaphore;
    private final String name;
    private int interval;

    public AnalysisThread(SharedData sharedData, String pattern, Semaphore semaphore, String name, int interval) {
        this.sharedData = sharedData;
        this.pattern = pattern;
        this.semaphore = semaphore;
        this.name = name;
        this.interval = interval;
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphore.acquire();
                // do something
                System.out.println("---Analysis " + this.name + " is running---");
                sharedData.searchInNode(pattern);
                // sleep again
                Thread.sleep(interval);
                semaphore.release();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}