package dev.keeptogether.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTest {

    @Test
    void moduleDependenciesAreValid() {
        ApplicationModules.of(KeepTogetherBackend.class).verify();
    }
}
