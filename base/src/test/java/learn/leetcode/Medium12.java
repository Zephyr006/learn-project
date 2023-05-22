package learn.leetcode;

/**
 * 393. UTF-8 编码验证
 *
 * UTF-8 中的一个字符可能的长度为 1 到 4 字节，遵循以下的规则：
 *
 * 对于 1 字节 的字符，字节的第一位设为 0 ，后面 7 位为这个符号的 unicode 码。
 * 对于 n 字节 的字符 (n > 1)，第一个字节的前 n 位都设为1，第 n+1 位设为 0 ，后面字节的前两位一律设为 10 。剩下的没有提及的二进制位，全部为这个符号的 unicode 码。
 *
 * @author Zephyr
 * @date 2022/3/16.
 */
public class Medium12 {


    public static void main(String[] args) {
        assert  new Medium12().validUtf8(new int[]{197, 130, 1});
        assert new Medium12().validUtf8(new int[]{230, 136, 145});
        assert ! new Medium12().validUtf8(new int[]{235,140,4});
        assert ! new Medium12().validUtf8(new int[]{255});
        assert new Medium12().validUtf8(new int[]{145});

    }

    /**
     * 自己写的，思路：
     * 如果当前数字高位是以 1 开头，则去寻找数组的后面有几个以 '10'开头的数字，然后回头来判断当前数字是否是以n个1作为开头，不是的话就不合法
     * bug：如果当前数字以1开头，但是数组后面已经没有更多元素了，那么在判断的过程中会导致数组下标越界
     */
    public boolean validUtf8(int[] data) {
        int mark = 1 << 7;                //10000000
        int mark11000000 = (1 << 7) | (1 << 6); //11000000
        int count = 1;
        for (int i = 0; i < data.length; i++) {
            // 当前字符是1字节的字符
            if ((mark & data[i]) == mark) {
                count = 1;

                if (i + count >= data.length) {
                    return false;
                }
                // 数字以二进制10开头
                while ((mark11000000 & data[i + count]) == mark) {
                    count++;
                    if (i + count >= data.length) {
                        return false;
                    }
                }
                int offset = count - 1;

                //第 n+1 位设为 0
                if (((data[i] >> (7 - count)) & 1) == 1) {
                    return false;
                }
                //第一个字节的前 n 位都设为1
                while (count > 0) {
                    // 校验n字节字符是否以n个1作为开头
                    if (((data[i] >> (8 - count--)) & 1) == 0) {
                        return false;
                    }
                }
                i += offset;
            }
        }
        return true;
    }




    /**
     * 正确的解，UTF-8 中的一个字符可能的长度为 1 到 4 字节,因此只判断编码中的前1-5bits value.
     *
     * 简单的来说可以分为5种情况:
     *
     * 首字节（4种情况）：0xxxxxxx, 110xxxxx, 1110xxxx, 11110xxx, 后面字节（1种情况）：10xxxxxx
     *
     * 只需要对这5种情况分别进行讨论我们便知道所给的编码是否合法.
     *
     * 判断时采用右移法
     *
     * 作者：su-yin-d
     * 链接：https://leetcode-cn.com/problems/utf-8-validation/solution/c-by-su-yin-d-380h/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public boolean validUtf8Right(int[] data) {
        int bitIndex = 0;//用于判断data首字节后一共有几个字节
        for(int num : data) {
            if(bitIndex == 0) {
                if((num >> 5) == 0b110) bitIndex = 1;
                else if((num >> 4) == 0b1110) bitIndex = 2;
                else if((num >> 3) == 0b11110) bitIndex = 3;
                else if((num >> 7) == 0b1) return false;
            } else {
                if((num >> 6) != 0b10) return false;
                --bitIndex;
            }
        }
        return bitIndex == 0;

    }

}
