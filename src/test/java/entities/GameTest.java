package entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameTest {

    @Test
    public void testGetters() {
        Developer developer = new Developer();
        Game game = new Game();

        game.setTitle("Example Game");
        game.setDeveloper(developer);
        game.setYearReleased(2022);
        game.setGenre("Action");

        assertThat(game.getTitle()).isEqualTo("Example Game");
        assertThat(game.getDeveloper()).isEqualTo(developer);
        assertThat(game.getYearReleased()).isEqualTo(2022);
        assertThat(game.getGenre()).isEqualTo("Action");
    }
}