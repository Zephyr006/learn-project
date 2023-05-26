package learn.base.test.business.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Zephyr
 * @since 2021-4-13.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagOrKnowledge {

    Long id;
    Long parentId;
    String name;


    @Getter
    public static class TreeNode extends TagOrKnowledge {

        List<TreeNode> childNodes = new ArrayList<>(4);

        public TreeNode(final String name, final Long id, final Long parentId) {
            this.name = name;
            this.id = id;
            this.parentId = parentId;
        }

        public boolean grow(List<TagOrKnowledge> tags) {
            if (CollectionUtils.isEmpty(tags)) {
                return false;
            }

            boolean grew = false;
            if (CollectionUtils.isEmpty(childNodes)) { //叶子节点
                for (TagOrKnowledge tag : tags) {
                    if (tag.getParentId().compareTo(id) == 0) {
                        childNodes.add(new TreeNode(tag.getName(), tag.getId(), tag.getParentId()));
                        grew = true;
                    }
                }
            } else {
                for (TreeNode tagNode : childNodes) {
                    grew = Boolean.logicalOr(grew, tagNode.grow(tags));
                }
            }
            return grew;
        }

        public static List<TagOrKnowledge> getAllLeafTag(TreeNode root) {
            // 只有根节点，返回自己
            if (root.childNodes.isEmpty()) {
                return Collections.singletonList(root);
            }
            List<TagOrKnowledge> leafTagIds = new ArrayList<>();
            for (TreeNode childNode : root.childNodes) {
                if (childNode.childNodes.isEmpty()) {
                    leafTagIds.add(childNode);
                } else {
                    leafTagIds.addAll(TreeNode.getAllLeafTag(childNode));
                }
            }
            return leafTagIds;
        }

        @Override
        public String toString() {
            return "TagTreeNode{" +
                    "name='" + name + '\'' +
                    ", childNodes.size=" + childNodes.size() +
                    //", id=" + id +
                    '}';
        }
    }


}
