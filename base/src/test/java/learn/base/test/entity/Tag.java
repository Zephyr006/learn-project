package learn.base.test.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Zephyr
 * @date 2021/4/13.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    Long id;
    Long parentId;
    String name;


    @Getter
    public static class TagTreeNode extends Tag {
        //String name;
        //Long id;
        //Long parentId;
        List<TagTreeNode> childNodes = new ArrayList<>(5);

        public TagTreeNode(final String name, final Long id, final Long parentId) {
            this.name = name;
            this.id = id;
            this.parentId = parentId;
        }

        public boolean grow(List<Tag> tags) {
            if (CollectionUtils.isEmpty(tags)) {
                return false;
            }

            boolean grew = false;
            if (CollectionUtils.isEmpty(childNodes)) { //叶子节点
                for (Tag tag : tags) {
                    if (tag.getParentId().compareTo(id) == 0) {
                        childNodes.add(new TagTreeNode(tag.getName(), tag.getId(), tag.getParentId()));
                        grew = true;
                    }
                }
            } else {
                for (TagTreeNode tagNode : childNodes) {
                    grew = Boolean.logicalOr(grew, tagNode.grow(tags));
                }
            }
            return grew;
        }

        public static List<Tag.TagTreeNode> getAllLeafTag(TagTreeNode root) {
            // 只有根节点，返回自己
            if (root.childNodes.isEmpty()) {
                return Collections.singletonList(root);
            }
            List<Tag.TagTreeNode> leafTagIds = new ArrayList<>();
            for (TagTreeNode childNode : root.childNodes) {
                if (childNode.childNodes.isEmpty()) {
                    leafTagIds.add(childNode);
                } else {
                    leafTagIds.addAll(TagTreeNode.getAllLeafTag(childNode));
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
