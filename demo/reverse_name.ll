@.str = private unnamed_addr constant [5 x i8] c"%d  \00", align 1
@.str1 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
define void @reverse(i32 %x) nounwind {
entry:

  %x.addr = alloca i32, align 4
  store i32 %x, i32* %x.addr, align 4
  %p = alloca i32, align 4
  %0 = load i32* %x.addr, align 4
  store i32 %0, i32* %p, align 4
  %q = alloca i32, align 4
  br label %while.cond0

while.cond0:
  %1 = load i32* %p, align 4
  %cmp0 = icmp ne i32 %1, 0
  br i1 %cmp0, label %while.body0, label %while.end0


while.body0:
    %2 = load i32* %p, align 4
    %rem0 = srem i32 %2, 10
    store i32 %rem0, i32* %q, align 4
    %3 = load i32* %q, align 4
    %call0 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 %3)
    %4 = load i32* %p, align 4
    %div0 = sdiv i32 %4, 10
    store i32 %div0, i32* %p, align 4
  br label %while.cond0

while.end0:
  ret void
}
define void @main() nounwind {
entry:

call void @reverse(i32 123)
  ret void
}
declare i32 @printf(i8*, ...)
