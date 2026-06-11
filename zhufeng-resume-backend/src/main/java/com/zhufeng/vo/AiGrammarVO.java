package com.zhufeng.vo;

import lombok.Data;
import java.util.List;

@Data
public class AiGrammarVO {
    private List<GrammarError> errors;

    @Data
    public static class GrammarError {
        private int position;
        private int length;
        private String text;
        private String suggestion;
        private String type; // "typo" | "punctuation"
    }
}
