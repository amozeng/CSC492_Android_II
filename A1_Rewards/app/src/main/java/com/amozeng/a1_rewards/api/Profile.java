package com.amozeng.a1_rewards.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable, Comparable<Profile>{
    private String username;
    private String password;
    private String firstName, lastName;
    private String department, position;
    private String story;
    private String points, pointsToAward;
    private String imageBytes;
    private String location;

    private List<Reward> rewardList = new ArrayList<Reward>();

    public Profile(String username) {
        this.username = username;
        this.points = "0";
    }

    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public String getFirstName() { return this.firstName; }
    public String getLastName() { return this.lastName; }
    public String getDepartment() { return this.department; }
    public String getPosition() { return this.position; }
    public String getStory() { return this.story; }
    public String getPoints() { return this.points; }
    public String getPointsToAward() { return this.pointsToAward; }
    public List<Reward> getReviewList() { return this.rewardList; }
    public String getImageBytes() { return this.imageBytes; }
    public String getLocation() { return this.location; }


    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String fName) { this.firstName = fName; }
    public void setLastName(String lName) { this.lastName = lName; }
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }
    public void setStory(String story) { this.story = story; }
    public void setPoints(String points) { this.points = points; }
    public void setPointsToAward(String pointsToAward) { this.pointsToAward = pointsToAward; }
    public void setImageBytes(String imageBytes) {this.imageBytes = imageBytes;};
    public void setReviewList(List<Reward> list) {this.rewardList = list;}
    public void setLocation(String loc) { this.location = loc;}

    @Override
    public int compareTo(Profile p) {
        int pointsInt = Integer.parseInt(points);
        int pointsInt_p = Integer.parseInt(p.getPoints());
        if(pointsInt > pointsInt_p) return -1;
        else if (pointsInt < pointsInt_p) return 1;

        return 0;
    }
}
