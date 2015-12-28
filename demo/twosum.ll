@.str = private unnamed_addr constant [5 x i8] c"%d  \00", align 1
@.str1 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
define void @twosum(i32* %nums, i32 %target, i32 %len) nounwind {
entry:

  %nums.addr = alloca i32*, align 4
  store i32* %nums, i32** %nums.addr, align 4
  %target.addr = alloca i32, align 4
  store i32 %target, i32* %target.addr, align 4
  %len.addr = alloca i32, align 4
  store i32 %len, i32* %len.addr, align 4
  %i = alloca i32, align 4
  store i32 0, i32* %i, align 4
  %j = alloca i32, align 4
  store i32 0, i32* %j, align 4
  br label %while.cond0

while.cond0:
  %0 = load i32* %i, align 4
  %1 = load i32* %len.addr, align 4
  %cmp0 = icmp slt i32 %0, %1
  br i1 %cmp0, label %while.body0, label %while.end0


while.body0:
    br label %while.cond1

  while.cond1:
    %2 = load i32* %j, align 4
    %3 = load i32* %len.addr, align 4
    %cmp1 = icmp slt i32 %2, %3
    br i1 %cmp1, label %while.body1, label %while.end1


  while.body1:
      %4 = load i32* %target.addr, align 4
      %5 = load i32* %i, align 4
      %6 = load i32** %nums.addr, align 4
      %arrayidx0 = getelementptr inbounds i32* %6, i32 %5
      %7 = load i32* %arrayidx0, align 4
      %8 = load i32* %j, align 4
      %9 = load i32** %nums.addr, align 4
      %arrayidx1 = getelementptr inbounds i32* %9, i32 %8
	  %10 = load i32* %arrayidx1, align 4
      %add0 = add nsw i32 %7, %10
      %cmp2 = icmp eq i32 %4, %add0
      br i1 %cmp2, label %if.then0, label %if.end0


    if.then0:
      %11 = load i32* %i, align 4
      %call0 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 %11)
      %12 = load i32* %j, align 4
      %call1 = call i32 (i8*, ...)* @printf(i8* getelementptr inbounds ([5 x i8]* @.str,     i32 0, i32 0), i32 %12)
      br label %if.end0
    if.end0:
      %13 = load i32* %j, align 4
      %add1 = add nsw i32 %13, 1
      store i32 %add1, i32* %j, align 4
    br label %while.cond1

  while.end1:
    %14 = load i32* %i, align 4
    %add2 = add nsw i32 %14, 1
    store i32 %add2, i32* %i, align 4
    store i32 0, i32* %j, align 4
  br label %while.cond0

while.end0:
  ret void
}
define void @main() nounwind {
entry:

  %nums = alloca [5 x i32], align 4
  %arrayidx0 = getelementptr inbounds [5 x i32]* %nums, i32 0, i32 0

  store i32 1, i32* %arrayidx0, align 4
  %arrayidx1 = getelementptr inbounds [5 x i32]* %nums, i32 0, i32 1

  store i32 2, i32* %arrayidx1, align 4
  %arrayidx2 = getelementptr inbounds [5 x i32]* %nums, i32 0, i32 2

  store i32 3, i32* %arrayidx2, align 4
  %arrayidx3 = getelementptr inbounds [5 x i32]* %nums, i32 0, i32 3

  store i32 4, i32* %arrayidx3, align 4
  %arrayidx4 = getelementptr inbounds [5 x i32]* %nums, i32 0, i32 4

  store i32 5, i32* %arrayidx4, align 4
  %target = alloca i32, align 4
  store i32 9, i32* %target, align 4
  %len = alloca i32, align 4
  store i32 5, i32* %len, align 4
%arraydecay0= getelementptr inbounds [5 x i32]* %nums, i32 0, i32 0
  %0 = load i32* %target, align 4
  %1 = load i32* %len, align 4
call void @twosum(i32* %arraydecay0, i32 %0, i32 %1)
  ret void
}
declare i32 @printf(i8*, ...)
