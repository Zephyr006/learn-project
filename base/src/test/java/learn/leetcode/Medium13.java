package learn.leetcode;

/**
 * 2043. 简易银行系统
 * https://leetcode-cn.com/problems/simple-bank-system/
 *
 * @author Zephyr
 * @date 2022/3/20.
 */
public class Medium13 {

    class Bank {
        long[] balance;
        // 简单模拟即可 !! 注意每一个要操作的账户都需要判断账户是否存在！！
        public Bank(long[] balance) {
            this.balance = balance;
        }

        public boolean transfer(int account1, int account2, long money) {
            if (!legal(account1, money) || account2 > balance.length) {
                return false;
            }
            balance[account1 - 1] -= money;
            balance[account2 - 1] += money;
            return true;
        }

        // 存款
        public boolean deposit(int account, long money) {
            if (account > balance.length) {
                return false;
            }
            balance[account - 1] += money;
            return true;
        }

        //取
        public boolean withdraw(int account, long money) {
            if (!legal(account, money)) {
                return false;
            }
            balance[account - 1] -= money;
            return true;
        }

        private boolean legal(int account, long money) {
            return account <= balance.length && balance[account - 1] >= money;
        }
    }

}
