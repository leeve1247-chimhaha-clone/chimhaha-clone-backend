package com.multirkh.chimhahaclone.controller;

import com.multirkh.chimhahaclone.category.PostCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostReceived {
    private PostCategory postCategory;
    private String content;
    private String title;
    private String user;
}
