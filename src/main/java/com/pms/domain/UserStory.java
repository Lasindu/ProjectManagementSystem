package com.pms.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Upulie on 4/14/2015.
 */
@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="UserStory")
public class UserStory {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int userStoryId;
    private String name;
    private String description;
    private int priority;
    private String date;
    private String preRequisits;
    private String dependancy;
    private String domain;
    private String assignedSprint;
    private String releasedDate;
    private String state;
    private boolean CR;



    @ManyToOne
    @JoinColumn(name="projectId")
    private Project project;

    @OneToMany(mappedBy="userStory",fetch=FetchType.LAZY,cascade=CascadeType.ALL,orphanRemoval=true)
    private Collection<Task> userStoryTasks= new ArrayList<Task>();










    //getters and setters
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Collection<Task> getUserStoryTasks() {
        return userStoryTasks;
    }

    public void setUserStoryTasks(Collection<Task> userStoryTasks) {
        this.userStoryTasks = userStoryTasks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(int userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPreRequisits() {
        return preRequisits;
    }

    public void setPreRequisits(String preRequisits) {
        this.preRequisits = preRequisits;
    }

    public String getDependancy() {
        return dependancy;
    }

    public void setDependancy(String dependancy) {
        this.dependancy = dependancy;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAssignedSprint() {
        return assignedSprint;
    }

    public void setAssignedSprint(String assignedSprint) {
        this.assignedSprint = assignedSprint;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public boolean isCR() {
        return CR;
    }

    public void setCR(boolean CR) {
        this.CR = CR;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
