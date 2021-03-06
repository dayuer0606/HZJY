package com.android.weischool.dialog;


import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.weischool.R;
import com.android.weischool.adapter.VoteResultAdapter;
import com.android.weischool.imageload.GlideImageLoader;
import com.talkfun.sdk.module.BriefVoteEntity;
import com.talkfun.sdk.module.VotePubEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class VoteResultDialogFragment extends VoteBaseDialogFragment {
    private static final String TAG = "tag";
    private List<BriefVoteEntity> briefVoteEntityList = new ArrayList<>();
    private VoteResultAdapter voteResultAdapter;
    private VotePubEntity votePubEntity;

    private static final String VOTE_PUB_KEY = "vote_pub_key";
    public static VoteResultDialogFragment create(VotePubEntity votePubEntity) {
        VoteResultDialogFragment voteResultFragment = new VoteResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VOTE_PUB_KEY, votePubEntity);
        voteResultFragment.setArguments(bundle);
        return voteResultFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Serializable item = bundle.getSerializable(VOTE_PUB_KEY);
        this.votePubEntity =  item != null ? (VotePubEntity) item : null;
        if (votePubEntity != null && votePubEntity.getBriefVoteEntityList() != null) {
            briefVoteEntityList = votePubEntity.getBriefVoteEntityList();
            voteResultAdapter = new VoteResultAdapter(getActivity());
            voteResultAdapter.setItems(briefVoteEntityList);

        }
        setStyle(STYLE_NO_FRAME, R.style.htVoteStyle);
        setCancelable(false);
    }

    @Override
    void initViewEvent() {
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showResult(votePubEntity);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (llVoteBody != null)
            llVoteBody.setOrientation(newConfig.orientation);
    }

    private void showResult(VotePubEntity votePubEntity) {
        if (voteResultAdapter == null) {
            briefVoteEntityList = votePubEntity.getBriefVoteEntityList();
            voteResultAdapter = new VoteResultAdapter(getActivity());
            voteResultAdapter.setItems(briefVoteEntityList);
        }
        chooseLv.setAdapter(voteResultAdapter);
        voteBtn.setVisibility(View.GONE);
        String title = votePubEntity.getTitle();
        if (!TextUtils.isEmpty(title) && !title.equals("null")) {
            titleTv.setText(title);
        } else if (!TextUtils.isEmpty(votePubEntity.getLabel())) {
            titleTv.setText(votePubEntity.getLabel());
        }


        //???????????????????????????
        String answer = votePubEntity.getAnswer();
        if (!TextUtils.isEmpty(answer)) {
            answerCorrect.setVisibility(View.VISIBLE);
            answerCorrect.setText("???????????????:" + transformAsciiToChar(answer, 0));

            //???????????????
            String options = votePubEntity.getOptions();
            if (!TextUtils.isEmpty(options)) {
                answerSelf.setVisibility(View.VISIBLE);
                options = options.substring(1, options.length() - 1);
                answerSelf.setText("???????????????:" + transformAsciiToChar(options, 1));
            }
        }

        tvPublishTime.setText(String.format("%s %s %s",votePubEntity.getNickname(),votePubEntity.getStartTime(),getString(R.string.runs_vote)));
        //??????
        if (!TextUtils.isEmpty(votePubEntity.getImageUrl())) {
            ivVoteImage.setVisibility(View.VISIBLE);
//            Glide.with(getActivity()).load(votePubEntity.getImageUrl()).into(ivVoteImage);
            GlideImageLoader.create(ivVoteImage).load(votePubEntity.getImageUrl());
        } else {
            ivVoteImage.setVisibility(View.GONE);
        }
    }


    /**
     * ???ascii??????????????????
     */
    public String transformAsciiToChar(String answer, int l) {
        String correctAnswer = "";
        String[] answers;
        if (TextUtils.isEmpty(answer))
            return null;
        answer = answer.trim();
        if (!answer.contains(",")) {
            answers = new String[1];
            answers[0] = answer;
        } else
            answers = answer.split(",");

        String item = "";
        for (int i = 0; i < answers.length; i++) {
            item = answers[i];
            if (TextUtils.isEmpty(item)) continue;
            item = item.trim();
            char c = (char) (Integer.parseInt(item) + 65 - l);
            if (i != 0)
                correctAnswer += "," + c;
            else
                correctAnswer += c;
        }
        return correctAnswer;
    }
}
