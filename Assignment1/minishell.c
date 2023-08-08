
/*********************************************************************
   Program  : miniShell                   Version    : 1.3
 --------------------------------------------------------------------
   skeleton code for linix/unix/minix command line interpreter
 --------------------------------------------------------------------
   File			: minishell.c
   Compiler/System	: gcc/linux

********************************************************************/

#include <sys/types.h>
#include <sys/wait.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>
#include <signal.h>

#define NV 20			/* max number of command tokens */
#define NL 100			/* input buffer size */
char            line[NL];	/* command input buffer */
int             job_id = 1;
int             job_queue_size = 0;
char            newLine[NL];

typedef struct {
  int job_id;
  int pid;
  char *command;
} JobInfo;
JobInfo job_queue[NV]; 

char            *v[NV];	/* array of pointers to command line tokens */
char            *new_v[NV];  /* array of pointers to new line tokens */

/*
	shell prompt
 */

void prompt(void)
{
  // fprintf(stdout, "\n msh> ");
  fflush(stdout);
  return;
}

void enqueue_job(int job_id, int pid, const char *command) {
  if (job_queue_size < NV) {
    job_queue[job_queue_size].job_id = job_id;
    job_queue[job_queue_size].pid = pid;
    job_queue[job_queue_size].command = strdup(command); // copy to the array

    job_queue_size++;
  }
}

void dequeue_job() {
  if (job_queue_size > 0) {
    for (int i = 1; i < job_queue_size; i++) {
      job_queue[i - 1] = job_queue[i];
    }
    job_queue_size--;
    // printf("dequeue   --  ");
  }
}

void sigchld_handler(int signum) {
  int status;
  
  // Wait for any terminated child process
  while ((waitpid(-1, &status, WNOHANG)) > 0) {
    if (WIFEXITED(status)) {
      for (int x = 0; x < job_queue_size; x++ ){
        printf("[%d]+ Done        ", job_queue[x].job_id);
        // printf("%s\n", job_queue[x].command);
        // Print the command without "&" if it exists
        char *command = strdup(job_queue[x].command); // Make a copy to modify
        size_t command_length = strlen(command);
        command[command_length - 2] = '\0'; // Remove "&"
        
        printf("%s\n", command);
        free(command); // Free the allocated memory

        dequeue_job();
      }
    }
  }
}

int main(int argk, char *argv[], char *envp[])
/* argk - number of arguments */
/* argv - argument vector from command line */
/* envp - environment pointer */

{
  int             frkRtnVal;	/* value returned by fork sys call */
  #pragma GCC diagnostic ignored "-Wunused-but-set-variable"
  // int             wpid;		/* value returned by wait */
  // char           *v[NV];	/* array of pointers to command line tokens */
  char           *sep = " \t\n";/* command line token separators    */
  int             i;		/* parse index */

  /* prompt for and process one command line at a time  */
  signal(SIGCHLD, sigchld_handler);

  while (1) {			/* do Forever */
    prompt();
    fgets(line, NL, stdin);
    fflush(stdin);
    int bg_execution = 0;

    memcpy(newLine, line, sizeof(line));

    if (feof(stdin)) {		/* non-zero on EOF  */
      exit(0);
    }
    
    if (line[0] == '#' || line[0] == '\n' || line[0] == '\000')
      continue;			/* to prompt */

    v[0] = strtok(line, sep);

    for (i = 1; i < NV; i++) {
      v[i] = strtok(NULL, sep);
      if (v[i] == NULL)
	      break;
    }

    /* assert i is number of tokens + 1 */

    // calculate v length
    int v_length = 0;
    while (v[v_length] != NULL) {
      v_length++;
    }

    // handle ‘&’ at the end
    if (v_length > 0 && strcmp(v[v_length - 1], "&") == 0){
      bg_execution = 1;
      v[v_length - 1] = NULL;     // remove "&" at the end
    }

    // handle cd
    if (strcmp(v[0], "cd") == 0) {
      // using chdir() to change current path
      if (i >= 2) {
        if (chdir(v[1]) != 0) {
          perror("cd");
        }
      } else {
        fprintf(stderr, "format error: cd + path\n");
      }
      continue;
    }

    /* fork a child process to exec the command in v[0] */

    switch (frkRtnVal = fork()) {
      case -1:			            /* fork returns error to parent process */
        {
          break;
        }
      case 0:			              /* code executed only by child process */
        {
          // execvp(v[0], v);
          execvp(v[0], v);
          perror("execvp");
          exit(1);
        }
      default:			            /* code executed only by parent process */
        {
          if (bg_execution == 0) {
            // if the bg_execution == 0, it will be finished in foreground
            // wpid = wait(0);
            waitpid(frkRtnVal, NULL, 0);
          } else {
            // Print the job number and PID for background process
            printf("[%d] %d\n", job_id, frkRtnVal);
            enqueue_job(job_id, frkRtnVal, newLine);  
            job_id++;
          }
          break;
        }
      }				/* switch */
  }				/* while */
  return 0;
}				/* main */