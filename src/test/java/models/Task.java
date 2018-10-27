package models;


import java.util.Arrays;

public class Task {
    private String title;
    private String[] tags;

    public Task(String title, String[] tags){
        this.title = title;
        this.tags = tags;
    }

    @Override
    public String toString(){
        return "Task[title:'" + title + "'; tags:[" + Arrays.toString(tags) + "]";
    }

    public String getTitle() {
        return this.title;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
