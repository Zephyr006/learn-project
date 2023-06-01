package learn.leetcode;


import java.util.ArrayDeque;


/**
 * 100. 相同的树
 *
 * 给定两个二叉树，编写一个函数来检验它们是否相同。
 *
 * 如果两个树在结构上相同，并且节点具有相同的值，则认为它们是相同的。
 *
 * 示例 1:
 *
 * 输入:       1         1
 *           / \       / \
 *          2   3     2   3
 *
 *         [1,2,3],   [1,2,3]
 *
 * 输出: true
 * 示例 2:
 *
 * 输入:      1          1
 *           /           \
 *          2             2
 *
 *         [1,2],     [1,null,2]
 *
 * 输出: false
 * 示例 3:
 *
 * 输入:       1         1
 *           / \       / \
 *          2   1     1   2
 *
 *         [1,2,1],   [1,1,2]
 *
 * 输出: false
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/same-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/19.
 */
public class Easy100 {

    /**
     * 递归解法
     * @param p
     * @param q
     * @return
     */
    public boolean isSameTree(LeetCodeHelper.TreeNode p, LeetCodeHelper.TreeNode q) {
        // one of p and q is null, another one is not null
        if ((p == null && q != null) || (p != null && q == null)) {
            return false;
        }

        // p and q are null
        if (p == null && q == null) {  // it's can be simplified to `if (p == null)` here
            return true;
        }

        if (p.val != q.val)
            return false;

        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }



    /**
     * 方法二：迭代
     *
     * 从根开始，每次迭代将当前结点从双向队列中弹出。然后，进行方法一中的判断：
     *
     * p 和 q 不是 None,
     *
     * p.val 等于 q.val,
     *
     * 若以上均满足，则压入子结点。
     */
    public boolean isSameTreeOfficial(LeetCodeHelper.TreeNode p, LeetCodeHelper.TreeNode q) {
        if (p == null && q == null) return true;
        if (!check(p, q)) return false;

        // init deques
        ArrayDeque<LeetCodeHelper.TreeNode> deqP = new ArrayDeque<LeetCodeHelper.TreeNode>();
        ArrayDeque<LeetCodeHelper.TreeNode> deqQ = new ArrayDeque<LeetCodeHelper.TreeNode>();
        deqP.addLast(p);
        deqQ.addLast(q);

        while (!deqP.isEmpty()) {
            p = deqP.removeFirst();
            q = deqQ.removeFirst();

            if (!check(p, q)) return false;
            if (p != null) {
                // in Java nulls are not allowed in Deque
                if (!check(p.left, q.left)) return false;
                if (p.left != null) {
                    deqP.addLast(p.left);
                    deqQ.addLast(q.left);
                }
                if (!check(p.right, q.right)) return false;
                if (p.right != null) {
                    deqP.addLast(p.right);
                    deqQ.addLast(q.right);
                }
            }
        }
        return true;
    }
    // 检查当前两个节点是否相等：都为空、或都不为空并且val相等，则为“相等”，返回true
    // 一个节点为空，另一个不为空，则返回false
    public boolean check(LeetCodeHelper.TreeNode p, LeetCodeHelper.TreeNode q) {
        // p and q are null
        if (p == null && q == null) return true;
        // one of p and q is null
        if (q == null || p == null) return false;
        if (p.val != q.val) return false;
        return true;
    }



}
