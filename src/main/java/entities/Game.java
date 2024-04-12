package entities;

import lombok.Data;

@Data
public class Game {
    private String title;
    private Developer developer;
    private int yearReleased;
    private String genre;
}
