package com.pain.green.ioc.domain;

import com.pain.green.ioc.annotation.Super;

@Super
public class SuperUser extends User {
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "SuperUser{" +
                "score=" + score +
                "} " + super.toString();
    }
}
