package com.summertaker.communitylistview.common;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "========== " + this.getClass().getSimpleName();
    }
}