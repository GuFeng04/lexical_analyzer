datasg segment
tem db 6,7 dup  (0)
sum dw 0
mul dw 0
i dw 0
j dw 0
T1 dw 0
T2 dw 0
T3 dw 0
T4 dw 0
datasg ends
codesg segment
assume cs:codesg,ds:datasg
start:
MOV AX,datasg
MOV DS,AX
L1: mov AX, 0
mov sum, AX
L2: mov AX, 1
mov mul, AX
L3: mov AX, 1
mov i, AX
L4: mov AX, i
sub AX, 11
L5: jnc TheEnd
L6: mov AX, i
mov j, AX
L7: mov AX, 0
sub AX, j
L8: jnc L13
L9: mov AX, mul
mov BX,j
mul BX
mov T3, AX
L10: mov AX, T3
mov mul, AX
L12: jmp L7
L13: mov AX, sum
add AX, mul
mov T4, AX
L14: mov AX, T4
mov sum, AX
L15: mov AX, i
add AX, 1
mov i, AX
L16: jmp L4
TheEnd:nop
mov ax,4c00h; int 21h的4ch号中断，安全退出程序！
int 21h;调用系统中断！
codesg ends
end start
