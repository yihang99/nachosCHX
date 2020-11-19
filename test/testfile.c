#include "syscall.h"
#include "stdlib.h"
#include "stdio.h"
#define n 1024
#define sn 16

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

char file1[n]="a.coff";
char file2[n]="b.coff";
int args = 0;
char* argv[sn];
char x1[]="dasasdas";
char x2[]="ewqqweqweq";
char x3[]="fhghghgh";
char x4[]="uiuiuiuiui";
char x5[]="nmnmnmnmn";
argv[0]=x1;
argv[1]=x2;
argv[2]=x3;
argv[3]=x4;
argv[4]=x5;

int y1=exec(file1,0,argv);
printf("Success:Execution\n");
int y2=exec(file2,1,argv);
int y3;
int y4=join(y1,&y3);
printf("Success:Join\n");



halt();
return 0;
}
