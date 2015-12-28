@.str = private unnamed_addr constant [5 x i8] c"%d  \00", align 1
@.str1 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
define void @main() nounwind {
entry:

  %n = alloca i32, align 4
  store i32 9, i32* %n, align 4
  %a = alloca [9 x i32], align 4
  %arrayidx0 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 0

  store i32 33, i32* %arrayidx0, align 4
  %arrayidx1 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 1

  store i32 77, i32* %arrayidx1, align 4
  %arrayidx2 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 2

  store i32 12, i32* %arrayidx2, align 4
  %arrayidx3 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 3

  store i32 57, i32* %arrayidx3, align 4
  %arrayidx4 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 4

  store i32 103, i32* %arrayidx4, align 4
  %arrayidx5 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 5

  store i32 66, i32* %arrayidx5, align 4
  %arrayidx6 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 6

  store i32 22, i32* %arrayidx6, align 4
  %arrayidx7 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 7

  store i32 45, i32* %arrayidx7, align 4
  %arrayidx8 = getelementptr inbounds [9 x i32]* %a, i32 0, i32 8

  store i32 11, i32* %arrayidx8, align 4
  %b = alloca i32, align 4
  %0 = load i32* %n, align 4
  %sum0 = sub nsw i32 %0, 1
  store i32 %sum0, i32* %b, align 4
%arraydecay0= getelementptr inbounds [9 x i32]* %a, i32 0, i32 0
  %1 = load i32* %b, align 4
call void @sort(i32* %arraydecay0, i32 0, i32 %1)
%arraydecay1= getelementptr inbounds [9 x i32]* %a, i32 0, i32 0
call void @show_array(i32* %arraydecay1, i32 9)
  ret void
}
define void @show_array(i32* %a, i32 %n) nounwind {
entry:

  %a.addr = alloca i32*, align 4
  store i32* %a, i32** %a.addr, align 4
  %n.addr = alloca i32, align 4
  store i32 %n, i32* %n.addr, align 4
  %i = alloca i32, align 4
  store i32 0, i32* %i, align 4
  br label %while.cond0

while.cond0:
  %0 = load i32* %i, align 4
  %1 = load i32* %n.addr, align 4
  %cmp0 = icmp slt i32 %0, %1
  br i1 %cmp0, label %while.body0, label %while.end0


while.body0:
    %p = alloca i32, align 4
    %2 = load i32* %i, align 4
    %3 = load i32** %a.addr, align 4
    %arrayidx0 = getelementptr inbounds i32* %3, i32 %2
    %4 = load i32* %arrayidx0, align 4
    store i32 %4, i32* %p, align 4
    %5 = load i32* %p, align 4
    %call0 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 %5)
    %6 = load i32* %i, align 4
    %add0 = add nsw i32 %6, 1
    store i32 %add0, i32* %i, align 4
  br label %while.cond0

while.end0:
  ret void
}
define void @sort(i32* %a, i32 %l, i32 %r) nounwind {
entry:

  %a.addr = alloca i32*, align 4
  store i32* %a, i32** %a.addr, align 4
  %l.addr = alloca i32, align 4
  store i32 %l, i32* %l.addr, align 4
  %r.addr = alloca i32, align 4
  store i32 %r, i32* %r.addr, align 4
  %i = alloca i32, align 4
  %0 = load i32* %l.addr, align 4
  store i32 %0, i32* %i, align 4
  %j = alloca i32, align 4
  %1 = load i32* %r.addr, align 4
  store i32 %1, i32* %j, align 4
  %tmp = alloca i32, align 4
  %2 = load i32* %l.addr, align 4
  %3 = load i32* %r.addr, align 4
  %add0 = add nsw i32 %2, %3
  store i32 %add0, i32* %tmp, align 4
  %tmp2 = alloca i32, align 4
  %4 = load i32* %tmp, align 4
  %div0 = sdiv i32 %4, 2
  store i32 %div0, i32* %tmp2, align 4
  %x = alloca i32, align 4
  %5 = load i32* %tmp2, align 4
  %6 = load i32** %a.addr, align 4
  %arrayidx0 = getelementptr inbounds i32* %6, i32 %5
  %7 = load i32* %arrayidx0, align 4
  store i32 %7, i32* %x, align 4
  %y = alloca i32, align 4
  br label %while.cond0

while.cond0:
  %8 = load i32* %i, align 4
  %9 = load i32* %j, align 4
  %cmp0 = icmp sle i32 %8, %9
  br i1 %cmp0, label %while.body0, label %while.end0


while.body0:
    br label %while.cond1

  while.cond1:
    %10 = load i32* %i, align 4
    %11 = load i32** %a.addr, align 4
    %arrayidx1 = getelementptr inbounds i32* %11, i32 %10
    %12 = load i32* %x, align 4
    %13 = load i32* %arrayidx1, align 4
    %cmp1 = icmp slt i32 %13, %12
    br i1 %cmp1, label %while.body1, label %while.end1


  while.body1:
      %14 = load i32* %i, align 4
      %add1 = add nsw i32 %14, 1
      store i32 %add1, i32* %i, align 4
    br label %while.cond1

  while.end1:
    br label %while.cond2

  while.cond2:
    %15 = load i32* %x, align 4
    %16 = load i32* %j, align 4
    %17 = load i32** %a.addr, align 4
    %arrayidx2 = getelementptr inbounds i32* %17, i32 %16
    %18 = load i32* %arrayidx2, align 4
    %cmp2 = icmp slt i32 %15, %18
    br i1 %cmp2, label %while.body2, label %while.end2


  while.body2:
      %19 = load i32* %j, align 4
      %sum0 = sub nsw i32 %19, 1
      store i32 %sum0, i32* %j, align 4
    br label %while.cond2

  while.end2:
    %20 = load i32* %i, align 4
    %21 = load i32* %j, align 4
    %cmp3 = icmp sle i32 %20, %21
    br i1 %cmp3, label %if.then0, label %if.end0


  if.then0:
    %22 = load i32* %i, align 4
    %23 = load i32** %a.addr, align 4
    %arrayidx3 = getelementptr inbounds i32* %23, i32 %22
    %24 = load i32* %arrayidx3, align 4
    store i32 %24, i32* %y, align 4
    %25 = load i32* %i, align 4
    %26 = load i32** %a.addr, align 4
    %arrayidx4 = getelementptr inbounds i32* %26, i32 %25
    %27 = load i32* %j, align 4
    %28 = load i32** %a.addr, align 4
    %arrayidx5 = getelementptr inbounds i32* %28, i32 %27
    %29 = load i32* %arrayidx5, align 4
    store i32 %29, i32* %arrayidx4, align 4
    %30 = load i32* %j, align 4
    %31 = load i32** %a.addr, align 4
    %arrayidx6 = getelementptr inbounds i32* %31, i32 %30
    %32 = load i32* %y, align 4
    store i32 %32, i32* %arrayidx6, align 4
    %33 = load i32* %i, align 4
    %add2 = add nsw i32 %33, 1
    store i32 %add2, i32* %i, align 4
    %34 = load i32* %j, align 4
    %sum1 = sub nsw i32 %34, 1
    store i32 %sum1, i32* %j, align 4
    br label %if.end0
  if.end0:
  br label %while.cond0

while.end0:
  %35 = load i32* %l.addr, align 4
  %36 = load i32* %j, align 4
  %cmp4 = icmp slt i32 %35, %36
  br i1 %cmp4, label %if.then1, label %if.end1


if.then1:
  %tmp1 = alloca i32, align 4
  %37 = load i32* %j, align 4
  %sum2 = sub nsw i32 %37, 1
  store i32 %sum2, i32* %tmp1, align 4
  %38 = load i32* %l.addr, align 4
  %39 = load i32* %tmp1, align 4
call void @sort(i32* %a, i32 %38, i32 %39)
  br label %if.end1
if.end1:
  %40 = load i32* %i, align 4
  %41 = load i32* %r.addr, align 4
  %cmp5 = icmp slt i32 %40, %41
  br i1 %cmp5, label %if.then2, label %if.end2


if.then2:
  %tmps = alloca i32, align 4
  %42 = load i32* %i, align 4
  %add3 = add nsw i32 %42, 1
  store i32 %add3, i32* %tmps, align 4
  %43 = load i32* %tmps, align 4
  %44 = load i32* %r.addr, align 4
call void @sort(i32* %a, i32 %43, i32 %44)
  br label %if.end2
if.end2:
  ret void
}
declare i32 @printf(i8*, ...)
