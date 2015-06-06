package com.pms.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Upulie on 4/13/2015.
 */

@Entity
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Table(name="Project")
public class Project{

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private int projectId;
    private String name;
    private String clientName;
    private String description;
    private String date;
    private String startDate;
    private String deliveredDate;

    @OneToMany(mappedBy="project",fetch=FetchType.LAZY,cascade=CascadeType.ALL,orphanRemoval=true)
    private Collection<UserStory> projectUserStories = new ArrayList<UserStory>();


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "projects")
    private Collection<User> users = new ArrayList<User>();










    //getters and setters

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<UserStory> getProjectUserStories() {
        return projectUserStories;
    }

    public void setProjectUserStories(Collection<UserStory> projectUserStories) {
        this.projectUserStories = projectUserStories;
    }
}
