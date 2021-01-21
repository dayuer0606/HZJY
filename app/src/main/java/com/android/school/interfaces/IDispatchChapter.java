package com.android.school.interfaces;


import com.talkfun.sdk.module.ChapterEntity;

import java.util.List;

/**
 */
public interface IDispatchChapter {
    void getChapterList(List<ChapterEntity> chapterEntityList);

    void switchToChapter();

}
