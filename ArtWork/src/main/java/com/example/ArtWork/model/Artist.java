package com.example.ArtWork.model;

import java.util.Objects;

import javax.persistence.*;

@Entity
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String artName;
    
    @Column(nullable = false)
    private Integer score;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "judge_id", nullable = false )
    private Judge judge;
    
    public Artist() {}
    
    public Artist(String name, String artName, Integer score, Judge judge) {
        this.name = name;
        this.artName = artName;
        this.score = score;
        this.judge = judge;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getArtName() { return artName; }
    public void setArtName(String artName) { this.artName = artName; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Judge getJudge() { return judge; }
    public void setJudge(Judge judge) { this.judge = judge; }

    @Override
    public String toString() {
        return "Artist [id=" + id + ", name=" + name + ", artName=" + artName + ", score=" + score + ", judge=" + judge + "]";
    }
    
    @Override
    public boolean equals(Object obj) {    	
        if (this == obj) 
        	return true;
        if (obj == null || getClass() != obj.getClass()) 
        	return false;
        Artist artist = (Artist) obj;
        return Objects.equals(id, artist.id)
            && Objects.equals(name, artist.name)
            && Objects.equals(artName, artist.artName)
            && Objects.equals(score, artist.score)
            && Objects.equals(judge, artist.judge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, artName, score, judge);
    }

}