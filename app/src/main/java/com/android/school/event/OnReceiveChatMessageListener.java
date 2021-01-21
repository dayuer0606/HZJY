package com.android.school.event;

import com.talkfun.sdk.module.ChatEntity;

public interface OnReceiveChatMessageListener {
    public void onReceiveChatMessage(ChatEntity chatEntity);
}
