#include <stdio.h>
#include <signal.h>
#include <unistd.h>
#include <stdlib.h> 

volatile int should_continue = 1;

// HUP signal
void handle_hup_signal(int signum) {
    printf("Ouch!\n");
}

// INT signal
void handle_int_signal(int signum) {
    printf("Yeah!\n");
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("Usage: %s n\n", argv[0]);
        return 1;
    }

    int n = atoi(argv[1]);
    if (n <= 0) {
        printf("Please provide a positive integer.\n");
        return 1;
    }

    signal(SIGHUP, handle_hup_signal);
    signal(SIGINT, handle_int_signal);

    int even_number = 0;
    int count = 0;

    while (should_continue) {
        if (even_number % 2 == 0) {                 // determine whether it is an even number
            printf("%d\n", even_number);
            fflush(stdout);
            count++;
            if (count == n) {
                break;
            }
        }
        even_number++;
        sleep(5);
    }

    return 0;
}
