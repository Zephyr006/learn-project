- 一旦涉及出现次数，需要用到散列表
- 涉及子串，考虑滑动窗口


- **整数数字反转**
```java 
//result的变量类型声明为long， 如果反转后结果产生溢出，则强转为int后其值会发生变化（高位被舍弃）
long result = 0;
//（十进制）常规数据，从个位开始处理，逐位取出，每次取出的数字都将result整体向左移动一位，
//就是将原始数据从右向左逐位取出，然后向result的尾部（右侧）追加
while (x != 0) {
    result = result * 10 + x % 10;
    x = x / 10;
}
return  (int) result == result ? (int) result : 0;
```

- **获取整数最高位**
```java
//int dev = 1;
//while (x / dev >= 10) {
//    dev *= 10;
//}
long dev = (long)Math.pow(10, (int)Math.log10(112));
long top = x / dev;
```