package learn.datasource.entity.jk;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2021/1/18 2:18 下午
 */
@Data
public class QuestionDetail {

    @TableId(type = IdType.NONE)
    private Long questionId;

    private String question;

    private String selections;

    private String answer;

    private String other;

    private String controversy;

    private String baseline;

    private String parse;

    private String extra;

    private Long materialId;

    private Long parseVideoId;

    private String parseVideoUrl;

    private Integer parseVideoVersion;

    private long createdAt;

    private long updatedAt;

    private Boolean status;
}
