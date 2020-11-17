#include "syscall.h"
#include "stdlib.h"
#include "stdio.h"
#define n 1024
#define bign 4096

char xtl[n];

int main(void) {
int i,a,b,c,d;
a=creat("1.txt");
if(a==-1){
printf("Error:Create\n");
return 0;
}
printf("Success:Create\n");
close(a);
printf("Success:Close\n");
unlink("1.txt");
printf("Success:Unlink\n");

a=creat("2.txt");
for(i=0;i<10;i++) {
xtl[i]=i+'0';
}
b=write(a,xtl,10);
if(b==-1){
printf("Error:Write\n");
return 0;
}
printf("Success:Write\n");
b=read(a,xtl,10);
if(b==-1){
printf("Error:Read\n");
return 0;
}
printf("Success:Read\n");
close(a);
unlink("1.txt");

halt();
return 0;
}
