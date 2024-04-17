package entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeveloperTest {

    @Test
    void testGetter() {
        String name = "Name";
        Developer developer = new Developer();

        developer.setName(name);

        assertThat(developer.getName()).isEqualTo(name);
    }
}
