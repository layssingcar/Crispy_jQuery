package com.mcp.crispy.comment.service;

import com.vane.badwordfiltering.BadWordFiltering;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public class BadWordFilteringHelper {

    public static BadWordFiltering getBadWordFiltering() {
        com.vane.badwordfiltering.BadWordFiltering badWordFiltering1 = new com.vane.badwordfiltering.BadWordFiltering();
        String[] badWords = new String[]{"에이라이퉷", "에이퉷", "시바"};
        badWordFiltering1.addAll(Arrays.asList(badWords));
        badWordFiltering1.remove("공지");
        badWordFiltering1.remove("공지사항");
        return badWordFiltering1;
    }
}
